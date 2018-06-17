package ron.rdt;

import ron.Batch;
import ron.Frame;

public interface Reducer {
	//
	//	func (log Log) Features() int {
	//		return ron.ACID_FULL
	//	}
	//
	Frame reduce(Batch batch);
}
