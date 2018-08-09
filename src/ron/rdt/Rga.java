package ron.rdt;

import ron.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static ron.UUID.ZERO_UUID;

// RGA is a Replicated Growable Array data type, an ordered collection of anything
// (numbers, strings, objects). This algorithm is actually closer to Causal Trees,
// albeit that name may turn confusing (RG Arrays are actually trees, Causal Trees
// are actually arrays, but nevermind).
//
// Algorithmically, this differs from Operational Transforms by bruteforcing the
// problem: all the elements of an array have unique ids, so concurrent changes
// can't introduce confusion.
public class Rga implements Reducer {
	private IHeap active;	// active subtrees, a frame heap
	private Map<UUID, UUID> rms;	// removes
	private Frame[] ins;            // subtrees-to-insert, ordered by ref
	private Map<UUID, Integer> traps;// points to an offset at ins

	public static UUID RGA_UUID = UUID.newName("rga");
	private static UUID RM_UUID = UUID.newName("rm");

	public static Reducer makeRGAReducer() {
		var rga = new Rga();
		rga.active = FrameArrayHeap.makeFrameHeap(FrameArrayHeap.eventComparatorDesc(), FrameArrayHeap.refComparatorDesc(), 2);
		rga.rms = new HashMap<>();
		rga.ins = new Frame[32];
		rga.traps =  new HashMap<>();
		return rga;
	}

//	func (rga RGA) Features() int {
//		return ron.ACID_FULL
//	}
//
//// [x] multiframe handling: the O(N) multiframe merge
//// [ ] undo/redo

	public void addMax(Map<UUID, UUID> rmmap, UUID event, UUID target) {
		UUID rm = rmmap.get(target);
		boolean ok = rm != null;
		if (!ok || event.laterThan(rm)) {
			rmmap.put(target, event);
		}
	}

	public Comparator<Frame> refOrderedBatch() {
		return (i, j) -> {
			final boolean less = j.ref().laterThan(i.ref());
			return (less) ? -1 : 1;
		};
	}

	public Comparator<UUID> revOrderedUUIDSlice() {
		return (i, j) -> {
			final boolean less = i.laterThan(j);
			return (less) ? 1 : -1;
		};
	}

	// Reduce RGA frames
	@Override
	public Frame reduce(Batch batch) {
		var rdtype = batch.frames[0].type();
		var object = batch.frames[0].object();
		var event  = batch.frames[batch.frames.length - 1].event();
//		// multiframe parts must be atomically applied, hence same version id
		Spec spec = Frame.newSpec(rdtype, object, event, ZERO_UUID);
		Frame[] _produce = new Frame[4];
		Batch produce = new Batch(Frame.slice(_produce, 0, 0));
		Frame[] pending = Frame.slice(this.ins, 0, 0);

		for (var k = 0; k < batch.frames.length; k++) {
			var b = batch.frames[k];
			if (!b.isHeader()) {
				if (b.count() == 0) {
					addMax(this.rms, b.event(), b.ref());
				} else {
					pending = Frame.append(pending, b);
				}
			} else {
				if (b.ref().equals(RM_UUID)) { // rm batch, must be the last
					b.next();
					for (;!b.eof() && !b.isHeader();) {
						addMax(this.rms, b.event(), b.ref());
						b.next();
					}
				} else {
					pending = Frame.append(pending, b);
				}
			}
		}


		Arrays.sort(pending, refOrderedBatch());
//		System.out.println(Arrays.stream(pending).map(Frame::string).reduce((s, s2) -> s + "\n" + s2).get());
		for (var i = pending.length - 1; i >= 0; i--) {
			this.traps.put(pending[i].ref(),  i);
			// 	rga.traps[pending[i].Ref()] = i
//			System.out.printf("%s to %d\n", pending[i].ref().string(), i);
		}
//		System.out.println(this.traps);

		for (var i = 0; i < pending.length;) {
			final Frame result = Frame.makeFrame(1024);

			final UUID at = pending[i].ref();
			System.out.printf("At %s\n", at.string());
			for (; i < pending.length && !pending[i].eof() && pending[i].ref().equals(at); i++) {
				this.active.putFrame(pending[i]);
//				System.out.printf("put active %s, ref %s\n", pending[i], pending[i].ref().string());
			}
			this.traps.remove(at);
//			System.out.printf("remove trap %s\n", at.string());

			spec.setRef(at);
			spec.setEvent(event);
			result.appendStateHeader(spec);

			for (;!this.active.eof();) {
				Frame op = this.active.current();
				UUID ev = op.event();
				spec.setEvent(ev);
				UUID ref = op.ref();
				if (op.isRaw()) {
					ref = ZERO_UUID;
				}
				UUID rm = this.rms.get(ev);
				boolean ok = rm != null;
				if (ok) {
					if (rm.laterThan(ref)) {
						ref = rm;
					}
					this.rms.remove(ev);
				}

//				System.out.println("pushWithTerm " + op.string());
				result.appendReducedRef(ref, op);

				this.active.nextPrim();

				for (var t = this.traps.get(ev); (t != null) && t < pending.length; t++) {
					if (!pending[t].eof() && pending[t].ref().equals(ev)) {
						this.active.putFrame(pending[t]);
//						System.out.printf("put active %s, ref %s\n", pending[i], pending[i].ref().string());
					} else {
						break;
					}
				}

			}

			produce = new Batch(Frame.append(produce.frames, result.rewind()));

			for (;i < pending.length && pending[i].eof();) {
				i++;
			}
		}

		// a separate frame for all the removes we don't have a target for
		if (this.rms.size() > 0) {
			Frame result = Frame.makeFrame(1024);
			spec.setEvent(event);
			spec.setRef(RM_UUID);
			result.appendStateHeader(spec);
			// take removed event ids
			UUID[] refs = new UUID[this.rms.size()];
			int k = 0;
			for (var ref : this.rms.keySet()) {
//				refs = Frame.append(refs, ref);
				refs[k] = ref;
				k++;
			}
			Arrays.sort(refs, revOrderedUUIDSlice());
//			System.out.println(Arrays.toString(refs));
//			// scan, append
			for (var key : refs) {
				spec.setRef(key);
				spec.setEvent(this.rms.get(key));
				result.appendEmptyReducedOp(spec);
				this.rms.remove(key);
			}
			produce = new Batch(Frame.append(produce.frames, result.rewind()));
		}

		this.ins = Frame.slice(pending, 0 , 0); // reuse memory
//		for (var x : this.traps.keySet()) {
//			this.traps.remove(x);
//		}

		this.traps.clear();

		int l = produce.frames.length;
		for (var i = 0; i < pending.length; i++) {
			if (!pending[i].eof()) {
				produce = new Batch(Frame.append(produce.frames, pending[i].split().frames));
			}
		}

		if (produce.frames.length == 1) {
			return produce.frames[0];
		} else if (l == produce.frames.length) {
			return produce.join();
		} else {
			//for i:=0; i<len(produce); i++ {
			//	fmt.Printf("    %d %s\n", i, produce[i].String())
			//}
			produce.frames[0] = produce.frames[produce.frames.length - 1];
			produce.frames[produce.frames.length - 1] = produce.frames[0];
			return this.reduce(produce);
		}
		// [ ] TODO safety: ceil for inserted subtrees - unified sanity checker
	}

//
//	func init() {
//		ron.RDTYPES[RGA_UUID] = MakeRGAReducer
//	}

}
