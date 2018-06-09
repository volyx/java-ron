package ron;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		FormatTest.class,
		FrameTest.class,
		ParseTest.class,
		UUIDTest.class,
		VVectorTest.class,
})
public class TestSuite {
}
