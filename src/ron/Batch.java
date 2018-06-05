package ron;


public class Batch {
	public Frame[] frames = new Frame[0];

	public Batch append(Frame frame) {
		Frame[] frames = new Frame[this.frames.length + 1];
		System.arraycopy(this.frames, 0, frames, 0, this.frames.length);
		frames[this.frames.length] = frame;
		this.frames = frames;
		return this;
	}

//	func (batch Batch) Join() Frame {
//		size := 0
//		for _, f := range batch {
//			size += len(f.Body)
//		}
//		ret := MakeFrame(size)
//		for _, f := range batch {
//			ret.AppendFrame(f)
//		}
//
//		return ret.Rewind()
//	}

	public Frame join() {
		int size = 0;
		for (Frame f : frames) {
			size += f.Body.length();
		}
		Frame ret = Frame.makeFrame(size);
		for (Frame f : frames) {
			ret.appendFrame(f);
		}
		return ret.rewind();
	}
}
