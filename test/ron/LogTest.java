package ron;

import org.junit.Assert;
import org.junit.Test;
import ron.rdt.Log;
import ron.rdt.Reducer;

public class LogTest {

	@Test
	public void TestLog_Reduce() {
		String[] vvs = new String[] {
			"*lww#id!@2+B:b=2@1+A:a=1",
					//"*lww#id!",
					"*lww#id@3+C:c=3@1+A:a=1",
		};

		// FIXME  *log  vs  *lww
		// ack:   time-orig
		// monopatch

		Batch batch = Parse.parseStringBatch(vvs);
		Reducer log = Log.makeLogReducer();
		Frame res = log.reduce(batch);
		var correct = "*lww#id@3+C!:c=3@2+B:b=2@1+A:a=1";
		if (!res.string().equals(correct)) {
			Assert.fail(String.format("got \n%s != \n%s\n", res.string(), correct));
		}
	}

}
