package ron;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ron.UUID.COMMENT_UUID;

public class FrameArrayHeap implements IHeap {
	private Frame[] iters;
	private Comparator<Frame> primary;
	private Comparator<Frame> secondary;

	public static IHeap makeFrameHeap(Comparator<Frame> primary, Comparator<Frame> secondary , int size) {
		FrameArrayHeap ret = new FrameArrayHeap();
		ret.iters = new Frame[1];
		ret.primary = primary;
		ret.secondary = secondary;
		return ret;
	}

	public boolean less(int i, int j) {
		var c = this.primary.compare(this.iters[i], this.iters[j]);
		if (c == 0) {
			if (this.secondary != null) {
				c = this.secondary.compare(this.iters[i], this.iters[j]);
			} else {
//				c = int64(j) - int64(i)
				c = j - i;
			}
		}
		//fmt.Printf("CMP %s %s GOT %d\n", this.iters[i].OpString(), this.iters[j].OpString(), c)
		return c < 0;
	}

	public void sink(int i) {
		var to = i;
		var j = i << 1;
		if (j < this.iters.length && this.less(j, i)) {
			to = j;
		}
		j++;
		if (j < this.iters.length && this.less(j, to)) {
			to = j;
		}
		if (to != i) {
			this.swap(i, to);
			this.sink(to);
		}
	}

	public void raise(int i) {
		var j = i >> 1;
		if (j > 0 && this.less(i, j)) {
			this.swap(i, j);
			if (j > 1) {
				this.raise(j);
			}
		}
	}

	public int len() {
		return this.iters.length - 1;
	}

	public void swap(int i, int j) {
		//fmt.Printf("SWAP %d %d\n", i, j)
		swap(this.iters, i, j);
	}

	public static final <T> void swap(T[] a, int i, int j) {
		T t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

	@Override
	public void putAll(Batch b) {
		for (var i = 0; i < b.frames.length; i++) {
			this.put(b.frames[i]);
		}
	}

	public void put(Frame i) {
		for (; ; ) {
			if (!i.eof() && (i.isHeader() || i.isQuery())) {
				i.next();
			} else {
				break;
			}
		}
		if (!i.eof() && !i.isHeader()) {
			var at = this.iters.length;
			this.iters = Frame.append(this.iters, i);
			this.raise(at);
		}
	}

	@Override
	public Frame current() {
		if (this.iters.length > 1) {
			return this.iters[1];
		} else {
			return null;
		}
	}

	public void remove(int i) {
		this.iters[i] = this.iters[this.iters.length - 1];
		this.iters = Frame.copyOfRange(this.iters, 0,this.iters.length - 1);
		this.sink(i);
	}

	public void next(int i) {
		this.iters[i].next();
		if (this.iters[i].eof() || this.iters[i].isHeader()) {
			this.remove(i);
		} else {
			this.sink(i);
		}
	}

	public void listEqs(int at, List<Integer> eqs) {
//	*eqs = append(*eqs, at)
		eqs.add(at);
		var l = at << 1;
		if (l < this.iters.length) {
			if (0 == this.primary.compare(this.iters[1], this.iters[l])) {
				this.listEqs(l, eqs);
			}
			var r = l | 1;
			if (r < this.iters.length) {
				if (0 == this.primary.compare(this.iters[1], this.iters[r])) {
					this.listEqs(r, eqs);
				}
			}
		}
	}

	@Override
	public void putFrame(Frame frame) {
		this.put(frame);
	}

	@Override
	public Frame frame() {
		var cur = Frame.makeFrame(128);
		for (;!this.eof();) {
			cur.append(this.current());
			this.next();
		}
		return cur.close();
	}


	@Override
	public Frame next() {
//		fmt.Printf("%v\n", this.iters)
		this.next(1);
		for (; this.iters.length > 1 && this.iters[1].type().equals(COMMENT_UUID); ) { // skip comments
			this.next(1);
		}
		return this.current();
	}

	@Override
	public boolean eof() {
		return this.iters.length == 1;
	}

	@Override
	public Frame nextPrim() {
//		var _eqs [16]int
		var eqs = new ArrayList<Integer>();
		this.listEqs(1, eqs);
		if (eqs.size() > 1) {
			eqs.sort(Comparator.naturalOrder());
		}
		for (var i = eqs.size() - 1; i >= 0; i--) {
			this.next(eqs.get(i));
			this.sink(eqs.get(i));
		}
		//fmt.Printf("%v\n", this.iters)
		//fmt.Printf("%v\n", eqs)
		return this.current();
	}

	public static Comparator<Frame> eventComparator() {
		return Comparator.comparing(Frame::event);
	}

	public static Comparator<Frame> eventComparatorDesc() {
		return eventComparator().reversed();
	}

	public static Comparator<Frame> refComparator() {
		return Comparator.comparing(Frame::ref);
	}

	public static Comparator<Frame> refComparatorDesc() {
		return refComparator().reversed();
	}
}
