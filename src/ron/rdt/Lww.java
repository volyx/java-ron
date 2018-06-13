package ron.rdt;

import ron.*;

import static ron.UUID.ZERO_UUID;

// LWW is a last-write-wins replicated data type that may host a variety of user-land data types, like:
//
// 		* a dictionary,
// 		* a struct or
// 		* a simple 1D array (no splice, no index shifts),
// 		* a simple 2D array.
//
// This LWW employs client-side logical timestamps to decide which write wins, on a field-by-field basis.
// That is similar to e.g. Cassandra LWW.
//
public class Lww implements Reducer {

	private static final UUID LWW_UUID = UUID.newName("lww");
	private static final UUID DELTA_UUID = UUID.newName("d");

//	func (lww LWW) Features() int {
//		return ron.ACID_FULL
//	}

	@Override
	public Frame reduce(Batch inputs) {
		IHeap heap = FrameArrayHeap.makeFrameHeap(FrameArrayHeap.refComparator(), FrameArrayHeap.eventComparatorDesc(), inputs.frames.length);
		Spec spec = inputs.frames[0].spec();
		spec.setEvent(inputs.frames[inputs.frames.length - 1].event());
		if (inputs.hasFullState()) {
			spec.setRef(ZERO_UUID);
		} else {
			spec.setRef(DELTA_UUID);
		}
		for (var k = 0; k < inputs.frames.length; k++) {
			heap.putFrame(inputs.frames[k]);
		}
		Frame res = new Frame();
		res.appendStateHeader(spec);
		for (;!heap.eof();) {
			res.appendReduced(heap.current());
			heap.nextPrim();
		}

		return res;
	}

	public Reducer makeLWW() {
		return new Lww();
	}

//	func init() {
//		ron.RDTYPES[LWW_UUID] = MakeLWW
//	}

}
