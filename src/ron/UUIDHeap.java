package ron;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import static ron.UUID.ZERO_UUID;

public class UUIDHeap {
	 private PriorityQueue<UUID> heap;
	 private boolean desc;

	public UUIDHeap() {
		this.heap = new PriorityQueue<>(UUID::compareTo);
	}


	public static UUIDHeap makeUHeap(boolean desc, int size) {
		UUIDHeap ret = new UUIDHeap();
		ret.desc = desc;
		return ret;
	}

	public void push(UUID item) {
		heap.add(item);
	}

	public UUID pop() {
		return heap.poll();
	}

	public void put(UUID u) {
		this.push(u);
	}

	public UUID take() {
		return this.pop();
	}

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

	public int len() {
		return this.heap.size();
	}
}
