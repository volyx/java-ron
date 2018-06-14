package ron;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ron.rdt.*;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		FormatTest.class,
		FrameTest.class,
		ParseTest.class,
		UUIDTest.class,
		OpTest.class,
		VVectorTest.class,
		UUIDMultiMapTest.class,
		UUIDHeapTest.class,
		FrameHeapTest.class,
		LogTest.class,
		LwwTest.class,
		RgaTest.class,
		CausalSetTest.class,
		SetTest.class,
		VVTest.class,
})
public class TestSuite {
}
