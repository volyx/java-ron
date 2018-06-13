package ron.rdt;

import org.junit.Test;

import static ron.RdtTest.runRONTest;

public class RgaTest {

//	@Test
	public void testRGA_Primers() {
		runRONTest(
				Rga.makeRGAReducer(),
//				"00-rga-basic.ron"
				"test.ron"
				);
	}

}
