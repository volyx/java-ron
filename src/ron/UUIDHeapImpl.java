package ron;

import java.util.PriorityQueue;

import static ron.UUID.ZERO_UUID;

public class UUIDHeapImpl implements UUIDHeap {
	 private PriorityQueue<UUID> heap;
	 private boolean desc;

	UUIDHeapImpl() {
		this.heap = new PriorityQueue<>(UUID::compareTo);
	}


	public static UUIDHeap makeUHeap(boolean desc, int size) {
		UUIDHeapImpl ret = new UUIDHeapImpl();
		ret.desc = desc;
		return ret;
	}

	@Override
	public void push(UUID item) {
		heap.add(item);
	}

	@Override
	public UUID pop() {
		return heap.poll();
	}

	@Override
	public void put(UUID u) {
		this.push(u);
	}

	@Override
	public UUID take() {
		return this.pop();
	}

	@Override
	public UUID popUnique() {
		if (this.heap.isEmpty()) {
			return ZERO_UUID;
		}
		UUID ret = heap.poll();
		for (;!this.heap.isEmpty() && this.heap.peek().equals(ret);) {
			heap.poll();
		}
		return ret;
	}

	@Override
	public int len() {
		return this.heap.size();
	}
}
