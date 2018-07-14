package ron;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static ron.Const.ATOM_INT;
import static ron.Const.BASE_PUNCT;
import static ron.FrameAppend.*;

public class FormatTest {

	public static final Random random = new Random();

	@Test
	public void TestUUID_String() {
		String[][] tests = new String[][]{
			{"}DcR-L8w", "}IYI-", "}IYI-0"},
			{"0$author", "name$author2", "name{2"},
			{"0", "1", "1"},
			{"0", "123-0", "123-"},
			{"0", "0000000001-orig", ")1-orig"},
			{"1time01-src", "1time02+src", "{2+"},
			{"hash%here", "hash%there", "%there"},
			{"1", ")1", "0000000001"}, //7
			{"0", "name$0", "name"},
			{"time+orig", "time1+orig2", "(1(2"},
			{"time-orig", "time1+orig2", "(1+(2"},
			{"[1s9L3-[Wj8oO", "[1s9L3-(2Biejq", "-(2Biejq"},
			{"}DcR-}L8w", "}IYI-", "}IYI}"}, //12
			{"A$B", "A-B", "-"},
		};
		for (int i = 0; i < tests.length; i++) {
			String[] tri = tests[i];
			UUID context = Parse.parseUUIDString(tri[0]);
			UUID uuid = Parse.parseUUIDString(tri[1]);

			String zip = uuid.zipString(context);
			if (!zip.equals(tri[2])) {
				Assert.fail(String.format("case %d: %s must be %s (%s, %s)", i, zip, tri[2], uuid.string(), context.string()));
			}
		}
	}

	@Test
	public void Test_IsBase() {
		for (byte b : BASE_PUNCT) {
			if (!isBase64(b)) {
				Assert.fail();
			}
		}

		if (isBase64((byte) '.')) {
			Assert.fail();
		}
	}

	@Test
	public void TestOp_String() {
		// FIXME EMPTY_OP.String() is ".0#0..." !!!
		String str = "*lww#object@time-origin:loc=1,";
		Frame op = Parse.parseFrameString(str);
		if (op.count() != 1 || op.Atom(0).Type() != ATOM_INT) {
			Assert.fail(String.format("misparsed %s", str));
		}
		Frame context = op.clone();
		op.atoms[2].uuid[0]++;
		op.atoms[3].uuid[0]++;
		Frame cur = Frame.makeFrame(1024);
		cur.append(context);
		long le = cur.len();
		cur.append(op);
		String opstr = new String(Arrays.copyOfRange(cur.Body.array(), (int) le, cur.Body.length()));
		String correct = "@)1:)1=1";
		if (!opstr.equals(correct)) {
			Assert.fail(String.format("incorrect: '%s' != '%s'", opstr, correct));
		}
	}

	public static class Sample {
		long flags;
		String correct;

		public Sample(long flags, String correct) {
			this.flags = flags;
			this.correct = correct;
		}
	}

	@Test
	public void  TestFormatOptions() {
		String framestr = "*lww#test!@1:key'value'@2:number=1*rga#text@3'T'!*rga#text@6:3,@4'e'@5'x'@6't'*lww#more:a=1;";
		Sample[] formats = new Sample[] {
			new Sample(FORMAT_FRAME_LINES | FORMAT_HEADER_SPACE,
						"*lww#test! @1:key'value'@2:number=1\n*rga#text@3:0'T'! @6:3,@4'e'@5'x'@6't'\n*lww#more@0:a=1;") // TODO drop :0 @0

		};
		for (int k = 0; k < formats.length; k++) {
			final Sample f = formats[k];
			final Frame frame = Frame.openFrame(new Slice(framestr.getBytes(StandardCharsets.UTF_8)));
			final Frame formatted = Frame.makeFormattedFrame(f.flags, 1024);
			for (;!frame.eof();) {
				for (Atom a : frame.atoms) {
					System.out.print(Long.toUnsignedString(a.uuid[0]) + "," + Long.toUnsignedString(a.uuid[1]) + " ");
				}
				System.out.println();
				formatted.append(frame);
				frame.next();
			}
			if (!formatted.string().equals(f.correct)) {
				Assert.fail(String.format("incorrect format at %d\n---\n%s\n---should be---\n%s\n\nparsed as %s\n", k, formatted.string(), f.correct, formatted.rewind().reformat(FRAME_FORMAT_CARPET).string()));
			}
		}
	}
}
