package ron;

import org.junit.Assert;
import org.junit.Test;
import ron.rdt.Lww;

public class LwwTest {


	// 3-part tables: first all inserts, then all deletes
	String[][] lww_3_tests = new String[][] {
		{ // 0+o
			"*lww#test!",
					"*lww#test@time:a'A'",
					"*lww#test@time!:a'A'",
		},
		{ // s+o
			"*lww#test@1!:a'A'",
					"*lww#test@2:b'B'",
					"*lww#test@2!@1:a'A'@2:b'B'",
		},
		{ // o+o
			"*lww#test@1:a'A1'",
					"*lww#test@2:a'A2'",
					"*lww#test@2:d!:a'A2'",
		},
		{ // p+p
			"*lww#test@1:d! :a'A1':b'B1':c'C1'",
					"*lww#test@2:d! :a'A2':b'B2'",
					"*lww#test@2:d!:a'A2':b'B2'@1:c'C1'",
		},
		{
			"*lww#test@0ld!@new:key'new_value'",
					"*lww#test@new:key'new_value'",
					"*lww#test@new!:key'new_value'",
		},
		{
			// lww array 2x2
			//     0   1
			//   +--------+
			// 0 | 0  '1' |
			// 1 | 1   2  |
			//   +--------+
			"*lww#array@1! :0%0 = 0,  :)1%0 = -1",
					"*lww#array@2! :0%)1 '1',  :)1%0 = 1,  :)1%)1 = 2",
					"*lww#array@2!@1:%=0@2:%)1'1':)1)=1:%)1=2",
		},
	};

	@Test
	public void TestLWW_Reduce() {
		for (var i = 0; i < lww_3_tests.length; i++) {
			String[] test = lww_3_tests[i];
			String C = test[2];
			Frame[] frames = new Frame[] {
				Parse.parseFrameString(test[0]),
					Parse.parseFrameString(test[1]),
			};
			Lww lww = new Lww();
			Frame frameC = lww.reduce(new Batch(frames));
			//fmt.Println(frameA.String(), frameB.String(), frameC.String())
			if (!frameC.string().equals(C)) {
				Assert.fail(String.format("\n-------------------------\nwrong result at %d: \nhave [ %s ]\nneed [ %s ]\n\n", i, frameC.string(), C));
			}
		}
	}

}
