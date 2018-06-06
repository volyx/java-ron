package ron;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class ParseTest {

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

}
