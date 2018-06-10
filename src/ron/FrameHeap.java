package ron;

import java.util.*;

import static ron.UUID.COMMENT_UUID;

// FrameHeap is an iterator heap - gives the minimum available element
// at every step. Useful for merge sort like algorithms.
public class FrameHeap implements IHeap {

	// Most of the time, a heap has 2 elements, optimize for that.
	// Sometimes, it can get millions of elements, ensure that is O(NlogN)
	private PriorityQueue<Frame> iters;


//	public static IHeap makeFrameHeap(Comparator<Frame> primary, Comparator<Frame> secondary , int size) {
//		FrameHeap ret = new FrameHeap();
//		ret.iters = new PriorityQueue<>(comparator(primary, secondary));
//		return ret;
//	}

	public static Comparator<Frame> comparator(Comparator<Frame> primary, Comparator<Frame> secondary) {
		return (a, b) -> {
			var c = primary.compare(a, b);
			if (c == 0) {
				if (secondary != null) {
					return secondary.compare(a, b);
				} else {
//				c = int64(j) - int64(i);
//						c = j - i;
				}
			}
			//fmt.Printf("CMP %s %s GOT %d\n", heap.iters[i].OpString(), heap.iters[j].OpString(), c)
//				return c < 0;
			return 1;
		};

	}

	public void putAll(Batch b) {
		for (var i = 0; i < b.frames.length; i++) {
			this.put(b.frames[i]);
		}
	}

	private void put(Frame i) {
		i = i.clone();
		for (;;) {
			if (!i.eof() && (i.isHeader() || i.isQuery())) {
				i.next();
			} else {
				break;
			}
		}
		if (!i.eof() && !i.isHeader()) {
//			int at = this.iters.size();
			this.iters.add(i);
//			this.iters.add(i);
//			this.raise(at);
		}
	}

	public Frame current() {
//		if (this.iters.size() > 1) {
//			return this.iters[1];
//		} else {
//			return null;
//		}
		return iters.peek();
	}

//	public void remove(int i) {
////		this.iters[i] = this.iters[this.iters.length - 1];
////		this.iters = Frame.copyOfRange(this.iters, 0, this.iters.length -1 );
////		this.sink(i);
//		this.iters.re
//	}

//	public void next(int i) {
//		Objects.requireNonNull(this.iters[i], String.format("%s \nindex %d", Arrays.toString(this.iters), i));
//
//		else {
//			this.sink(i);
//		}
//	}

	public Frame next() {
		this.current().next();
		if (this.current().eof() || this.current().isHeader()) {
			this.iters.poll();
		}
		for (;this.iters.size() > 1 && this.current().type().equals(COMMENT_UUID);) { // skip comments
			this.current().next();
			if (this.current().eof() || this.current().isHeader()) {
				this.iters.poll();
			}
		}
		return this.current();
//		return next;
	}

	public Frame nextPrim() {
//		var eqs = new ArrayList<Integer>();
////		eqs = _eqs[0:0:16];
//		this.listEqs(1, eqs);
//		if (eqs.size() > 1) {
//			eqs.sort(Comparator.naturalOrder());
//		}
//		for (var i = eqs.size() - 1; i >= 0; i--) {
//			this.next(eqs.get(i));
//			this.sink(eqs.get(i));
//		}
//		System.out.println(Arrays.toString(this.iters));
//		System.out.println(eqs);
		return this.current();
	}

	public void putFrame(Frame frame) {
		this.put(frame);
	}

	public Frame frame() {
		Frame cur = Frame.makeFrame(128);
		while (!this.iters.isEmpty()) {
			cur.append(this.next());
		}
		return cur.close();
	}

	public void clear() {
		this.iters.clear();;
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

	public boolean eof() {
		return this.iters.isEmpty();
	}
}
