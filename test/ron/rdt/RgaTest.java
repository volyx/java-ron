package ron.rdt;

import org.junit.Assert;
import org.junit.Test;
import ron.Batch;
import ron.Parse;
import ron.TxtMapper;
import ron.TxtMapperImpl;

import static ron.RdtTest.runRONTest;

public class RgaTest {

	@Test
	public void testRGA_Primers() {
		runRONTest(
				Rga.makeRGAReducer(),
				"00-rga-basic.ron"
//				"test.ron"
				);
	}

	@Test
	public void TestRGA_Mapper() {
		var frame = "*rga#1UQ8p+bart@1UQ8yk+lisa!" +
				"@(s+bart'H'@[r'e'@(t'l'@[T'l'@[i'o'" +
				"@(w+lisa' '@(x'w'@(y'o'@[1'r'@{a'l'@[2'd'@[k'!'";
		var right = "Hello world!";
		TxtMapper txt = new TxtMapperImpl();
		String hello = txt.map(new Batch(Parse.parseFrameString(frame)));
		if (!hello.equals(right)) {
			Assert.fail(String.format("'%s' != '%s'", hello, right));
		}
	}

}
