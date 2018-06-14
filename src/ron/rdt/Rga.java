package ron.rdt;

import ron.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static ron.Frame.append;
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
	public static UUID RM_UUID = UUID.newName("rm");
	public static Atom[] NO_ATOMS = new Atom[]{};

	public static Reducer makeRGAReducer() {
		var rga = new Rga();
		rga.active = FrameArrayHeap.makeFrameHeap(FrameArrayHeap.eventComparatorDesc(), FrameArrayHeap.refComparatorDesc(), 2);
//		rga.rms = make(map[ron.UUID]ron.UUID)
		rga.rms = new HashMap<>();
		rga.ins = new Frame[32];
//		rga.traps = make(map[ron.UUID]int)
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
		return new Comparator<Frame>() {
			@Override
			public int compare(Frame i, Frame j) {
//				final boolean less = j.ref().laterThan(i.ref());
				final boolean less = j.ref().laterThan(i.ref());
//				return j.compare(i);
				return (less) ? -1 : 1;
			}
		};
	}

//	type RefOrderedBatch []*ron.Frame
//
//	func (b RefOrderedBatch) Len() int           { return len(b) }
//	func (b RefOrderedBatch) Swap(i, j int)      { b[i], b[j] = b[j], b[i] }
//	func (b RefOrderedBatch) Less(i, j int) bool { return b[j].Ref().LaterThan(b[i].Ref()) }
//
//	type RevOrderedUUIDSlice []ron.UUID
//
//	func (b RevOrderedUUIDSlice) Len() int           { return len(b) }
//	func (b RevOrderedUUIDSlice) Swap(i, j int)      { b[i], b[j] = b[j], b[i] }
//	func (b RevOrderedUUIDSlice) Less(i, j int) bool { return b[i].LaterThan(b[j]) }

	public Comparator<UUID> revOrderedUUIDSlice() {
		return new Comparator<UUID>() {
			@Override
			public int compare(UUID i, UUID j) {
				final boolean less = i.laterThan(j);
				return (less) ? 1 : -1;
//				return (less) ? -1 : 1;
			}
		};
	}
//
	// Reduce RGA frames
	@Override
	public Frame reduce(Batch batch) {
//		batch = batch.clone();
		var rdtype = batch.frames[0].type();
		var object = batch.frames[0].object();
		var event = batch.frames[batch.frames.length - 1].event();
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


//		sort.Sort(RefOrderedBatch(pending))
		Arrays.sort(pending, refOrderedBatch());
//		System.out.println(Arrays.toString(pending));
		for (var i = pending.length - 1; i >= 0; i--) {
			this.traps.put(pending[i].ref(),  i);
			// 	rga.traps[pending[i].Ref()] = i
		}

		for (var i = 0; i < pending.length;) {
			Frame result = Frame.makeFrame(1024);

			UUID at = pending[i].ref();
			for (; i < pending.length && !pending[i].eof() && pending[i].ref().equals(at); i++) {
				this.active.putFrame(pending[i]);
			}
			this.traps.remove(at);

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

				result.appendReducedRef(ref, op);
				this.active.nextPrim();

				for (var t = this.traps.get(ev); (t != null) && t < pending.length; t++) {
					if (!pending[t].eof() && pending[t].ref().equals(ev)) {
						this.active.putFrame(pending[t]);
					} else {
						break;
					}
				}

			}

//			produce.frames = Frame.append(produce.frames, result.rewind());
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
//			sort.Sort(RevOrderedUUIDSlice(refs))
			Arrays.sort(refs, revOrderedUUIDSlice());
			System.out.println(Arrays.toString(refs));
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

//	type TxtMapper struct {
//	}
//
//	func (txt TxtMapper) Map(batch ron.Batch) string {
//		if len(batch) == 0 {
//			return ""
//		}
//		rga := batch[0]
//		if rga.Type() != RGA_UUID || !rga.IsHeader() {
//			return ""
//		}
//		ret := []byte{}
//		for rga.Next(); !rga.EOF() && !rga.IsHeader(); rga.Next() {
//			if rga.Ref().IsZero() {
//				ret = append(ret, rga.RawString(0)...)
//			}
//		}
//		return string(ret)
//	}
//
//	func init() {
//		ron.RDTYPES[RGA_UUID] = MakeRGAReducer
//	}

}
