package ron.rdt;


import ron.*;

import java.util.Comparator;

import static ron.rdt.Lww.DELTA_UUID;

// Causal set, assumes causally consistent op delivery.
// Hence, no tombstones.
// You can either add or remove an atom/tuple.
// Equal elements possible.
public class CausalSet implements Reducer {
	private final IHeap heap;
	private boolean alwaysFull;

	private static final UUID CAUSAL_SET_UUID = UUID.newName("cas");

	public CausalSet(IHeap heap) {
		this.heap = heap;
	}
//
//	func (cs CausalSet) Features() int {
//		return ron.ACID_AID
//	}

	public static Comparator<Frame> setComparator() {

		return (af, bf) -> {
			var a = af.event();
			var b = bf.event();
			if (!af.ref().isZero()) {
				a = af.ref();
			}
			if (!bf.ref().isZero()) {
				b = bf.ref();
			}
			return -a.compareTo(b);
		};
	}

	public static Reducer makeCausalSetReducer() {
		return new CausalSet(FrameArrayHeap.makeFrameHeap(setComparator(), null, 16));
	}


	@Override
	public Frame reduce(Batch batch) {
		var ret = Frame.makeFrame(batch.frames.length);
		var ref = DELTA_UUID;
		if (this.alwaysFull || batch.hasFullState()) {
			ref = UUID.ZERO_UUID;
		}
		ret.appendStateHeader(Frame.newSpec(
				CAUSAL_SET_UUID,
				batch.frames[0].object(),
				batch.frames[batch.frames.length - 1].event(),
				ref
				));
		this.heap.putAll(batch);
		for (;!this.heap.eof();) {
			if (this.heap.current().ref().isZero() || !ref.isZero()) {
//				ret.appendReduced(*cs.heap.Current())
				ret.appendReduced(this.heap.current());
			}
			this.heap.nextPrim();
		}
		return ret;
	}

//	func init() {
//		ron.RDTYPES[CAUSAL_SET_UUID] = MakeCausalSetReducer
//	}
}
