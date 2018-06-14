package ron;

public interface UUIDHeap {
	void push(UUID item);

	UUID pop();

	void put(UUID u);

	UUID take();

	UUID popUnique();

	int len();
}
