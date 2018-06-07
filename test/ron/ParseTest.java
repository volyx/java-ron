package ron;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static ron.FrameAppend.FORMAT_OP_LINES;
import static ron.Parser.RON_FULL_STOP;
import static ron.Parser.RON_start;
import static ron.UUID.ERROR_UUID;
import static ron.UUID.INT60LEN;

public class ParseTest {
	private static final Random rand = new Random();

	@Test
	public void TestParseUUID() {
		UUID uuidA = Parse.parseUUID("1".getBytes(StandardCharsets.UTF_8));
		if (uuidA.value() != (1L << 54) || uuidA.origin() != 0 || uuidA.scheme() != Const.UUID_NAME) {
			Assert.fail("Fail");
		}
		UUID uuidAB = Parse.parseUUID(uuidA, ")1".getBytes(StandardCharsets.UTF_8));
		if ((uuidAB.value() != ((1L << 54) | 1L)) || uuidAB.origin() != 0 || uuidAB.scheme() != Const.UUID_NAME) {
			Assert.fail("Fail " + uuidAB.value());
		}
		UUID hello = Parse.parseUUIDString("hello-111");
		UUID world = Parse.parseUUIDString(hello, "[world-111");
		UUID helloworld = Parse.parseUUIDString("helloworld-111");
		if (!world.equals(helloworld)) {
			Assert.fail("Fail " + world);
		}
		String err_str ="erro_error$~~~~~~~~~~";
		UUID error_uid = Parse.parseUUIDString(err_str);
		if (error_uid.isZero()) {
			Assert.fail("Fail " + error_uid);
		}
	}

	@Test
	public void TestParseFormatUUID() {
		String[][] tests = new String[][]{
			{"0", "1", "1"}, // 0
			{"1-x", ")1", "1000000001-x"},
			{"test-1", "-", "test-1"},
			{"hello-111", "[world", "helloworld-111"},
			{"helloworld-111", "[", "hello-111"},
			{"100001-orig", "[", "1-orig"}, // 5
			{"1+orig", "(2-", "10002-orig"},
			{"time+orig", "(1(2", "time1+orig2"},
			// TODO		{"name$user", "$scoped", "scoped$user"},
			{"any-thing", "hash%here", "hash%here"},
			{"[1s9L3-[Wj8oO", "-(2Biejq", "[1s9L3-(2Biejq"}, // 9
			{"0123456789-abcdefghij", ")~)~", "012345678~-abcdefghi~"},
			{"(2-[1jHH~", "-[00yAl", "(2-}yAl"},
			{"0123G-abcdb", "(4566(efF", "01234566-abcdefF"},
		};
		for (int i = 0; i < tests.length; i++) {

			String[] tri = tests[i];
			UUID context = Parse.parseUUIDString(tri[0]);
			UUID uuid = Parse.parseUUIDString(context, tri[1]);

			String str = uuid.stringValue();
			if (!str.equals(tri[2])) {
				Assert.fail(String.format("parse %d: %s must be %s", i, str, tri[2]));
			}
			String zip = uuid.zipString(context);
			if (!zip.equals(tri[1])) {
				Assert.fail(String.format("format %d: %s must be %s", i, zip, tri[1]));
			}
		}
	}



	@Test
	public void TestParseUUID2() {

		String[][] test32 = { // context: 0123456789-abcdefghi
			{"-", "0123456789-abcdefghi"},   // 00000
			{"B", "B"},                      // 00001
			{"(", "0123-abcdefghi"},         // 00010
			{"(B", "0123B-abcdefghi"},       // 00011
			{"+", "0123456789+abcdefghi"},   // 00100
			{"+B", "0123456789+B"},          // 00101
			{"+(", "0123456789+abcd"},       // 00110
			{"+(B", "0123456789+abcdB"},     // 00111
			{"A", "A"},                      // 01000 8
			{"AB", "AB"},                    // 01001
			{"A(", "A-abcd"},                // 01010
			{"A(B", "A-abcdB"},              // 01011
			{"A+", "A+abcdefghi"},           // 01100
			{"A+B", "A+B"},                  // 01101
			{"A+(", "A+abcd"},               // 01110
			{"A+(B", "A+abcdB"},             // 01111
			{")", "012345678-abcdefghi"},    // 10000 16
			{")B", "012345678B-abcdefghi"},  // 10001
			{")(", "012345678-abcd"},        // 10010
			{")(B", "012345678-abcdB"},      // 10011
			{")+", "012345678+abcdefghi"},   // 10100
			{")+B", "012345678+B"},          // 10101
			{")+(", "012345678+abcd"},       // 10110
			{")+(B", "012345678+abcdB"},     // 10111
			{")A", "012345678A-abcdefghi"},  // 11000
			{")AB", ""},                     // 11001 error - length
			{")A(", "012345678A-abcd"},      // 11010
			{")A(B", "012345678A-abcdB"},    // 11011
			{")A+", "012345678A+abcdefghi"}, // 11100
			{")A+B", "012345678A+B"},        // 11101
			{")A+(", "012345678A+abcd"},     // 11110
			{")A+(B", "012345678A+abcdB"},   // 11111
		};

		String defstr = "0123456789-abcdefghi";
		UUID def = Parse.parseUUIDString(defstr);
		for (int i = 0; i < test32.length; i++) {
			System.out.println(i);
			String zipped = test32[i][0];
			UUID unzipped;
			try {
				unzipped = Parse.parseUUIDString(test32[i][1]);
			} catch (RuntimeException e) {
				System.err.println(e.getMessage());
				unzipped = ERROR_UUID;
			}
			UUID next;
			try {
				next = Parse.parseUUIDString(def, zipped);
			} catch (RuntimeException e) {
				System.err.println(e.getMessage());
				next = ERROR_UUID;
			}
			if (test32[i][1].equals("")) {
				continue;
			}
			if (!next.equals(unzipped)) {
				Assert.fail(String.format("uuid parse fail at %d: '%s' should be '%s' context %s len %d", i, next.stringValue(), test32[i][1], defstr, zipped.length()));
			}
		}
	}


	public static long random_close_int(long base, long prefix) {
		if (prefix == 10) {
			return base;
		}
		if (prefix == 11) {
			return 0;
		}
		long shift = (10 - prefix) * 6;
		base >>= shift;
		base <<= shift;
		long rnd = rand.nextLong() & 63;
		base |= (rnd << (shift - 6));
		return base;
	}

	@Test
	public void TestParseFrame() {
// in java 9
//		ProcessHandle.current().pid();
//		pid := os.Getpid()
		long pid = rand.nextLong();
		System.out.printf("random seed %d", pid);

		rand.setSeed(pid);
		String defstr = "0123456789-abcdefghi";
		UUID def = Parse.parseUUIDString(defstr);
		int at;
		// 64 random uuids - 8 brackets
		int dim = INT60LEN + 2;
		UUID[] uuids = new UUID[dim * dim];

		for (int bv = 0; bv < dim; bv++) {
			for (int bo = 0; bo < dim; bo++) {
				long v = random_close_int(def.value(), (long) bv);
				long o = random_close_int(def.origin(), (long) bo);
				uuids[bv*dim+bo] = UUID.newEventUUID(v, o);
			}
		}
		// shuffle to 16 ops
		for (int i = 0; i < 1000; i++) {
			int f = Math.abs(rand.nextInt()) % uuids.length;
			int t = Math.abs(rand.nextInt()) % uuids.length;
			uuids[f] = uuids[t];
			uuids[t] = uuids[f];
		}
		// pack into a frame
		Frame frame = Frame.makeFrame(dim*dim*22 + dim*100);
		frame.Serializer.Format |= FORMAT_OP_LINES;
		int ops = 30;
		for (int j = 0; j < ops; j++) {
			at = j << 2;
			frame.appendStateHeader(Frame.newSpec(uuids[at], uuids[at+1], uuids[at+2], uuids[at+3]));
		}
		System.out.println(frame.string());
//		// recover, compare
		Frame iter = frame.rewind();
		for (int k = 0; k < ops; k++) {
			if (iter.eof()) {
				Assert.fail(String.format("Premature end: %d not %d, failed at %d\n", k, ops, iter.Parser.pos));
			}
			at = k << 2;
			for (int u = 0; u < 4; u++) {
				UUID uuid  = iter.UUID(u);
				if (!uuid.equals(uuids[at+u])) {
					Assert.fail(String.format("uuid %d decoding failed in op#%d, '%s' should be '%s'", u, k, iter.UUID(u).stringValue(), uuids[at+u].stringValue()));
				}
			}
			iter.next();
		}
		if (!iter.eof()) {
			Assert.fail("No end");
		}
	}

	@Test
	public void TestFrame_Next() {
		String[] ops = new String[] {"*a!", "*b=1", "*c=1!", "*d,", "*e,"};
		// "*a!*b=1*c=1!*d,*e,"
		String frameStr = String.join("", ops) + ".";
		System.out.println(frameStr);
		Frame frame = Parse.parseFrameString(frameStr);
		String names = "";
		int i = 0, l = 0;
		for (;!frame.eof();) {
			l += ops[i].length();
			if (i == (ops.length - 1)) {
				l++; //? ragel
			}
			if (frame.offset() != l) {
				Assert.fail(String.format("bad off: %d not %d '%s'", frame.offset(), l, frameStr));
			} else {
//				System.out.printf("OK %d %s", i, frame.type().stringValue());
			}
			i++;
			names += frame.type().stringValue();
			Parser.parseFrame(frame);
		}
		if (i != ops.length || !names.equals("abcde")) {
			Assert.fail(String.format("bad end: %d not %d, at %d, '%s' should be 'abcde'", i, ops.length, frame.offset(), names));

		}
	}


	@Test
	public void TestFrame_EOF2() {
		byte[] multi = "*a#A:1!:2=2:3=3.*b#B:1?:2=2.".getBytes(StandardCharsets.UTF_8);
		int[] states = new int[] {RON_start, RON_start, RON_FULL_STOP, RON_start, RON_FULL_STOP};
		int o = 0;
		Frame frame = Frame.makeStream(128);
		for (int i = 0; i < multi.length; i++) {
			frame.appendBytes(Arrays.copyOfRange(multi, i,  i+1));
			if (frame.next()) {
				if (states[o] != frame.Parser.state) {
					Assert.fail(String.format("state %d at pos %d op %d, expected %d", frame.Parser.state, frame.Parser.pos, o, states[o]));
//					break
				} else {
					//t.Logf("OK state %d at pos %d op %d", frame.Parser.state, frame.Parser.pos, o)
				}
				if (frame.Parser.state == RON_FULL_STOP) {
					frame = Frame.makeStream(1024);
				}
				o++;

			}
			System.out.println(i + " " + frame.Parser.state());
		}
		if (o != states.length) {
			Assert.fail(String.format("%d ops, needed %d", o, states.length));
		}
	}

}
