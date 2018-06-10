package ron;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ron.rdt.Log;
import ron.rdt.Lww;

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
})
public class TestSuite {
}
