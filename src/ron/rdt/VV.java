package ron.rdt;

import ron.*;

import java.util.Comparator;

public class VV implements Reducer {
	private final IHeap heap;

	public static final UUID VV_UUID = UUID.newName("vv");

	public VV(IHeap heap) {
		this.heap = heap;
	}


	public static Comparator<Frame> vvComparator() {
		return new Comparator<Frame>() {
			@Override
			public int compare(Frame a, Frame b) {
				return (int) (a.origin() - b.origin());
			}
		};
	}


	public static Reducer makeVVReducer() {
		return new VV(FrameArrayHeap.makeFrameHeap(vvComparator(), FrameArrayHeap.eventComparatorDesc(), 2));
	}

	@Override
	public Frame reduce(Batch batch) {
				Spec spec = Frame.newSpec(
				VV_UUID,
				batch.frames[0].object(),
				batch.frames[batch.frames.length - 1].event(),
				UUID.ZERO_UUID
				);
		this.heap.putAll(batch);
		var re = Frame.newFrame();
		re.appendStateHeader(spec);
		for (;!this.heap.eof();) {
			spec.spec[Const.SPEC_EVENT] = this.heap.current().atoms()[Const.SPEC_EVENT];
			re.appendEmpty(spec, Const.TERM_REDUCED);
			this.heap.nextPrim();
		}
		return re;
	}

//	func (vv VV) Features() int {
//		return ron.ACID_FULL
//	}

//	func init() {
//		ron.RDTYPES[VV_UUID] = MakeVVReducer
//	}

}
