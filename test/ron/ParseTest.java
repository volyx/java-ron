package ron;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static ron.Const.ATOM_FLOAT;
import static ron.Const.ATOM_PUNCT;
import static ron.Const.TERM_REDUCED;
import static ron.FrameAppend.FORMAT_OP_LINES;
import static ron.Parser.RON_FULL_STOP;
import static ron.Parser.RON_error;
import static ron.Parser.RON_start;
import static ron.UUID.ERROR_UUID;
import static ron.UUID.INT60LEN;

public class ParseTest {
	private static final Random rand = new Random();

	@Test
	public void TestParseUUID() {
		UUID uuidA = Parse.parseUUIDString("1");
		if (uuidA.value() != (1L << 54) || uuidA.origin() != 0 || !uuidA.isName()) {
			Assert.fail("Fail");
		}
		UUID uuidAB = Parse.parseUUIDString(uuidA, ")1");
		if ((uuidAB.value() != ((1L << 54) | 1L)) || uuidAB.origin() != 0 || !uuidAB.isName()) {
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

			String str = uuid.string();
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
			// System.out.println(i);
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
				Assert.fail(String.format("uuid parse fail at %d: '%s' should be '%s' context %s len %d", i, next.string(), test32[i][1], defstr, zipped.length()));
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
		base >>>= shift;
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
		// System.out.printf("random seed %d", pid);

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
					Assert.fail(String.format("uuid %d decoding failed in op#%d, '%s' should be '%s'", u, k, iter.UUID(u).string(), uuids[at+u].string()));
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
//				System.out.printf("OK %d %s", i, frame.type().string());
			}
			i++;
			names += frame.type().string();
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

	@Test
	public void TestFrame_EOF() {
		String[] streams = new String[] {
			"#id . #one! #two! #three!. ",
					"...",
					"#first#incomplete",
		};
		int[][] states = new int[][]{
			{RON_FULL_STOP, RON_start, RON_start, RON_FULL_STOP},
			{RON_FULL_STOP, RON_FULL_STOP, RON_FULL_STOP},
			{RON_start},
		};
		for (int k = 0; k < streams.length; k++) {
			String stream = streams[k];
			Frame frame = Frame.parseStream(new byte[]{});
			// feed by 1 char
			// EOF -> Rest()
			int s = 0;
			for (int i = 0; i < stream.length(); i++) {
				frame.appendBytes(Arrays.copyOfRange(stream.getBytes(StandardCharsets.UTF_8), i, i+1));
				frame.next();
				//t.Log(k, i, stream[i:i+1], frame.pos, frame.Parser.State(), frame.IsComplete())
				if (Parser.IsComplete(frame)) {
					if (s > states[k].length) {
						Assert.fail(String.format("stream %d off %d got %d need nothing", k, i, frame.Parser.state()));
					}
					if (frame.Parser.state() != states[k][s]) {
						Assert.fail(String.format("stream %d off %d got %d need %d", k, i, frame.Parser.state(), states[k][s]));

					}
					s++;
					if (frame.Parser.state() == RON_FULL_STOP) {
						frame = Frame.parseStream(frame.rest());
					}
				}
			}
			if (s != states[k].length) {
				Assert.fail(String.format("need %d complete states, got %d", states[k].length, s));
			}
		}
	}


	// A RON-text file must start with '*'
	@Test
	public void TestXParseOpWhitespace() {
		String str = "*lww \t #test ;\n#next?";
		Frame frame = Parse.parseFrameString(str);
		if (str.charAt(frame.offset() - 1) != '\n') {
			Assert.fail();
		}
		frame.next();
		if (frame.offset() != str.length()) {
			Assert.fail();
		}
	}

	@Test
	public void TestXParseMalformedOp() {
		String[] tests = new String[] {
			"novalue",
					"# broken - uuid?",
					"#too-many@values!!??=5=6^7.0^8.0'extra'",
					"#invalid-float ^31.41.5",
					"",
					"'unescaped ' quote'",
					">badreference",
					"#no_uuid-sep@$$",
					"#trailing garbage",
					"#reordered .uuids =1",
					"#repeat #uuids =1",
		};
		for (int i = 0; i < tests.length; i++) {
			String str = tests[i];
			Frame frame = Parse.parseFrameString(str);
			if (!frame.eof() && frame.offset() >= str.length()) {
				Assert.fail(String.format("parsed %d but invalid: '%s' (%d)", i, str, frame.offset()));
			}
		}
	}

	@Test(timeout = 5_000L)
	public void TestParseComment() {
		String[][] tests = new String[][]{
			{
				"*lww#object@time!:field'value' *~'comment'! *rga!.",
						"*rga!",
			},
			{
				"*lww#object@time! :field'value', *~'comment', *lww:another'value'.",
						"*lww #object @time :another'value',",
			},
		};
		for (int k = 0; k < tests.length; k++) {
			String[] test = tests[k];
			Frame frame = Parse.parseFrameString(test[0]);
			Frame correct = Parse.parseFrameString(test[1]);
			for (;frame.Parser.state() != RON_FULL_STOP;){
				frame.next();
			}
			boolean eq = frame.equals(correct);
			if (!eq) {
				Assert.fail(String.format("%d need \n'%s'\n got \n'%s'\n", k, correct.opString(), frame.opString()));
			}
		}
	}

	@Test
	public void TestParseTermDuplet() {
		String frameStr = "*lww#object@time+orig?! :keyA 'А' :keyB 'Б'";
		Frame frame = Parse.parseFrameString(frameStr);
		if (!frame.isQuery()) {
			Assert.fail("no query parsed");
		}
		UUID obj = frame.object();
		frame.next();
		if (frame.eof() || !frame.isHeader() || !frame.object().equals(obj)) {
			Assert.fail("state header not parsed");
		}
		frame.next();
		if (frame.eof() || frame.term() != TERM_REDUCED || !frame.object().equals(obj)) {
			Assert.fail("inner op not parsed");
		}
	}

	@Test
	public void TestOp_ParseAtoms() {
		String[][] tests = new String[][]{
			{"*a>0>1>2>3", ">>>>"},
			{"*a>0>0,#next>0>0", ">>"},
			{"*a,", ""},
			{"*a=1^2.0", "=^"},
			{"*a'str''quoted \\'mid\\' str'", "''"},
		};
		for (int i = 0; i < tests.length; i++) {
			String str = tests[i][0];
			Frame frame = Parse.parseFrameString(str);
			if (frame.eof()) {
				Assert.fail(String.format("not parsed %d: '%s' (%d)", i, str, frame.offset()));
			}
			String types = "";
			for (int a = 0; a < frame.count(); a++) {
				types += new String(new byte[] {ATOM_PUNCT[frame.Atom(a).type()]});
			}
			if (!types.equals(tests[i][1])) {
				Assert.fail(String.format("misparsed %d: '%s' (%s)", i, types, tests[i][1]));
			}
		}
	}

	@Test
	public void TestOp_ParseFloat() {
		String[][] frames = new String[][]{
//			{"*lww#id^3.141592", "*lww#id^3.141592"},
			{"*lww#id^-0.25", "*lww#id^-0.25"},
			{"*lww#id^0.000001", "*lww#id^1.0e-6"},
			{"*lww#id^-0.00000e+02", "*lww#id^0.0"},
			{"*lww#id^-1.00000e+09", "*lww#id^-1000000000.0"},
			{"*lww#id^1000000000.0e-1", "*lww#id^100000000.0"},
			{"*lww#id^12345.6789e+16", "*lww#id^1.23456789e+20"},
		};
		double[] vals = new double[]{
//						3.141592,
					 	-0.25,
					 	0.000001,
						0,
					 	-1e+9,
					 	1e+8,
					 	1.23456789e+20,
		};
		for (int i = 0; i < frames.length; i++) {
			Frame frame = Parse.parseFrameString(frames[i][0]);
			if (frame.count() != 1 || frame.Atom(0).type() != ATOM_FLOAT) {
				Assert.fail("misparsed a float " + i);
			}
			Atom atom = frame.Atom(0);
			double val = atom.Float();
			if (Math.abs(val - vals[i]) > 0.001) {
				Assert.fail(String.format("%d float value unparsed %e!=%e", i, val, vals[i]));
			}
			Frame back = Frame.newFrame();
			back.append(frame);
			if (!back.string().equals(frames[i][1])) {
				Assert.fail(String.format("float serialize fail (got/want):\n%s\n%s\n", back.string(), frames[i][1]));
			}
		}
	}

	@Test
	public void TestParse_SpecOnly() {
		String str = "#test:)1#test:)2#test:)3";
		Frame frame = Parse.parseFrameString(str);
		long c = 0;
		for (;!frame.eof();) {
			c++;
			if (frame.ref().value() != c) {
				Assert.fail();
			}
			frame.next();
		}
		if (c != 3) {
			Assert.fail();
		}
	}

	@Test
	public void TestParse_Errors() {
		String[] frames = new String[]{
			"#test>linkмусор",
					"#string'unfinishe",
					"#id<",
					"#bad@term=?",
					"#no-term?-",
					"#notfloat^a",
					"#notesc'\\'",
					"*type=1NT",
					"*ty&",
		};
		for (int k = 0; k < frames.length; k++) {
			String f = frames[k];
			byte[] buf = f.getBytes(StandardCharsets.UTF_8);
			Frame frame = Parse.parseFrame(new Slice(buf));
			if (frame.Parser.state() != RON_error) {
				Assert.fail(String.format("mistakenly parsed %d [ %s ] %d\n", k, f, frame.offset()));
			}
		}
	}

	@Test
	public void TestFrame_ParseStream() {
		String str = "*op1=123*op2!*op3!.";
		Frame frame = Frame.makeStream(1024);
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			frame.Body = frame.Body.append(str.charAt(i));
			Parser.parseFrame(frame);
			//fmt.Println(frame.Parser.state, "AT", frame.Offset(), string(frame.Body[:frame.Offset()]))
			if (Parser.IsComplete(frame)) {
				//fmt.Println("TADAAM", frame.OpString(), frame.Count(), "\n")
				count++;
			}
		}
		if (count != 3) {
			Assert.fail(String.format("count %d!=3", count));
		}
	}

	@Test
	public void TestAtom_UUID() {
		String str = "*lww#1TUAQ+gritzko@`:bar=1 #(R@`:foo > (Q";
		Frame frame = Parse.parseFrameString(str);
		UUID uuid1 = frame.object();
		UUID uuid2 = frame.event();
		if (!uuid1.equals(uuid2)) {
			Assert.fail();
		}
		frame.next();
		UUID uuid3 = frame.Atom(0).UUID();
		if (!uuid1.equals(uuid3)) {
			Assert.fail(String.format("%s !=\n%s", uuid1, uuid3));
		}
	}

	@Test
	public void TestParserState_Omitted() {
		Frame frame = Parse.parseFrameString("*type#id!@ev@ev:");
		if (frame.Parser.omitted != (4|8)) {
			Assert.fail(String.format("omitted is %d for %s", frame.Parser.omitted, frame.opString()));
		}
		frame.next();
		if (frame.Parser.omitted != (1|2|8)) {
			Assert.fail();
		}
		frame.next();
		if (frame.Parser.omitted != (1|2)) {
			Assert.fail();
		}
	}


}
