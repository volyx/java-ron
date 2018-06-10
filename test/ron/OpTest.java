package ron;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class OpTest {

	@Test
	public void TestParseOp() {
		var frame = "*lww#test-author@(time-origin:loc=1''>test";
		Frame iter = Parse.parseFrameString(frame);
		if (!iter.type().string().equals("lww")) {
			Assert.fail(String.format("'%s' %s != '%s'\n", iter.type().string(), Arrays.toString(iter.type().string().getBytes(StandardCharsets.UTF_8)), "lww"));
		}
		if (!iter.object().string().equals("test-author")) {
			Assert.fail(String.format("'%s' %s != '%s'\n", iter.type().string(), Arrays.toString(iter.object().string().getBytes(StandardCharsets.UTF_8)), "test-author"));
		}
		System.out.println(iter.opString());
		long i = iter.integer(0);
		if (i != 1) {
			Assert.fail(String.format("int parse fails: %d", i));
		}
	}
}
