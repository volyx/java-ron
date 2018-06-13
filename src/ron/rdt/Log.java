package ron.rdt;

import ron.*;

import static ron.Const.UUID_EVENT;
import static ron.UUID.ZERO_UUID;

public class Log implements Reducer {
	public static UUID LOG_UUID = UUID.newName("log");
	private IHeap heap;

	public static Reducer makeLogReducer() {
		final Log log = new Log();
		log.heap = FrameArrayHeap.makeFrameHeap(FrameArrayHeap.eventComparatorDesc(), null, 2);
		return log;
	}
//
//	func (log Log) Features() int {
//		return ron.ACID_FULL
//	}
//
	@Override
	public ron.Frame reduce(Batch batch)  {
		this.heap.putAll(batch);
		Spec spec = Frame.newSpec(
				batch.frames[0].type(),
				batch.frames[0].object(),
				batch.frames[batch.frames.length - 1].event(),
				ZERO_UUID
				);
		Frame re = Frame.newFrame();
		re.appendStateHeader(spec);
		for (;!this.heap.eof() && this.heap.current().event().scheme() == UUID_EVENT;) {
			re.appendReduced(this.heap.current());
			this.heap.nextPrim();
		}
		this.heap.clear();
		return re;
	}

//	func init() {
//		ron.RDTYPES[LOG_UUID] = MakeLogReducer
//	}

}
