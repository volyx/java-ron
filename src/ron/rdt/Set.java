package ron.rdt;

import ron.*;

import java.util.Arrays;

// Set, fully commutative, with tombstones.
// You can either add or remove an atom/tuple.
// Equal elements possible.
public class Set implements Reducer {

	private final IHeap heap;

	private static final UUID SET_UUID = UUID.newName("set");

	public Set(IHeap heap) {
		this.heap = heap;
	}

	public static Reducer makeSetReducer() {
		return new Set(FrameArrayHeap.makeFrameHeap(CausalSet.setComparator(), FrameArrayHeap.refComparatorDesc(), 16));
	}

	@Override
	public Frame reduce(Batch batch) {
		Frame ret = Frame.makeFrame(batch.frames.length);
		var ref = Lww.DELTA_UUID;
		if (batch.hasFullState()) {
			ref = UUID.ZERO_UUID;
		}
		ret.appendStateHeader(Frame.newSpec(
											SET_UUID,
											batch.frames[0].object(),
											batch.frames[batch.frames.length - 1].event(),
											ref
											));
		this.heap.putAll(batch);
		for (;!this.heap.eof();) {
//			ret.appendReduced_ByRef(this.heap.current());
			final Frame current = this.heap.current();
//			System.out.println(current.string());
//			System.out.println(Arrays.toString(current.atoms));
			ret.appendReduced(current);
			this.heap.nextPrim();
		}
		return ret.rewind();
	}

/*
	func (set Set) Features() int {
		return ron.ACID_FULL
	}



	func init() {
		ron.RDTYPES[SET_UUID] = MakeSetReducer
	}
*/

}
