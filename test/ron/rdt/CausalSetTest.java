package ron.rdt;

import org.junit.Assert;
import org.junit.Test;
import ron.Parse;

import java.util.Arrays;

public class CausalSetTest {

	@Test
	public void TestCausalSet_Reduce() {
		var tests = new String[][]{
			{
				"*cas#test1@1=1",
						"*cas#test1@2=2",
						"*cas#test1@2:d!:0=2@1=1",
			},
			{
				"*cas#test1@1=1",
						"*cas#test1@2:1;",
						"*cas#test1@2:d!:1,",
			},
			{
				"*cas#test1@3:1;",
						"*cas#test1@4:2;",
						"*cas#test1@4:d!:2,@3:1,",
			},
			{
				"*cas#test1@2!@=2@1=1",
						"*cas#test1@5!@=5@3:2,@4:1,",
						"*cas#test1@5!@=5",
			},
			{
				"*cas#test1@2!@=2@1=1",
						"*cas#test1@3!@:2,@4:1,",
						"*cas#test1@5!@=5",
						"*cas#test1@5!@=5",
			},
			{
				"*cas#1VBC8+A@one!,",
						"*cas#1VBC8+A@two;",
						"*cas#1VBC8+A@~:one;",
						"*cas#1VBC8+A@~!@two,",
			},
			{
				"*cas#repeat!",
						"*cas#repeat@1=1;",
						"*cas#repeat:1;",
						"*cas#repeat@1=2;",
						"*cas#repeat@1!@=2",
			},
		};
		var cs = CausalSet.makeCausalSetReducer();
		for (int i = 0; i < tests.length; i++) {
			var test = tests[i];
			var inputs = Arrays.copyOfRange(test, 0, test.length - 1);
			var batch = Parse.parseStringBatch(inputs);
			var result = cs.reduce(batch);
			if (!result.string().equals(test[test.length - 1])) {
				Assert.fail(String.format("%d cset reduce fail, got\n'%s' want\n'%s'\n", i, result.string(), test[test.length - 1]));
			}
		}
	}
}
