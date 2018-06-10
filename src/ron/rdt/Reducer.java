package ron.rdt;

import ron.Batch;

public interface Reducer {
	//
	//	func (log Log) Features() int {
	//		return ron.ACID_FULL
	//	}
	//
	ron.Frame reduce(Batch batch);
}
