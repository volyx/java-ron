package ron;


import java.util.Arrays;
import java.util.Objects;

public class Batch {
	public Frame[] frames = new Frame[0];
	public Batch(){}
	public Batch(Frame frame) {
		this.frames = new Frame[] {frame};
	}
	public Batch(Frame[] frames) {
		this.frames = frames;
	}

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

	public String string() {
		String ret = "";
		for (Frame frame : frames) {
			ret += frame.string() + "\n";
		}
		return ret;
	}

	// Equal checks two batches for op-by-op equality (irrespectively of frame borders)
	public boolean compare(Batch other) {
		boolean eq = false; int op = 0, at;
		int bi = 0, oi = 0;
		Frame bf = new Frame();
		Frame of = new Frame();
		for (;(!bf.eof() || bi < this.frames.length) && (!of.eof() || oi < other.frames.length);) {
			for (;bf.eof() && bi < this.frames.length;) {
				bf = this.frames[bi];
				bi++;
			}
			for (;of.eof() && oi < other.frames.length;) {
				of = other.frames[oi];
				oi++;
			}
			eq = bf.compare(of);
			if (!eq) {
				return eq;
			}
			op++;
			bf.next();
			of.next();
		}
		if (bi != this.frames.length || oi != other.frames.length || !bf.eof() || !of.eof()) {
			eq = false;
		}
		return eq;
	}


	public boolean hasFullState() {
		for (Frame f : this.frames) {
			if (f.isFullState()) {
				return true;
			}
		}
		return false;

	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Batch other = (Batch) o;
		return this.compare(other);
	}
}
