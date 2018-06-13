package ron;

public interface IHeap {

	void putFrame(Frame frame);

	Frame frame();

	void putAll(Batch batch);

	Frame current();

	Frame next();

	boolean eof();

	Frame nextPrim();

	void clear();
}
