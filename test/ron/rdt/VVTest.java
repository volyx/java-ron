package ron.rdt;

import org.junit.Assert;
import org.junit.Test;
import ron.Batch;
import ron.Frame;
import ron.Parse;

public class VVTest {
	@Test
	public void TestVV_Reduce() {
		var vvs = new String[]{
			"*vv#vec@1+a!@,",
					"*vv#vec@3+b!@2+a,@3+b,@1+c,",
					"*vv#vec@4+c!@3+b,@4+c,",
		};
		var batch = new Batch();
		for (String s : vvs) {
			batch.frames = Frame.append(batch.frames, Parse.parseFrameString(s));
		}
		var vv = VV.makeVVReducer();
		Frame res = vv.reduce(batch);
		String correct = "*vv#vec@4+c!@2+a,@3+b,@4+c,";
		if (!res.string().equals(correct)) {
			Assert.fail(String.format("got \n%s != \n%s\n", res.string(), correct));
		}
	}
}
