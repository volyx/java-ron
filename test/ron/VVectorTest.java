package ron;

import org.junit.Assert;
import org.junit.Test;

public class VVectorTest {

	@Test
	public void TestVVector_Add() {
		VVector vec = new VVector();
		vec.addString("1+A");
		vec.addString("2+B");
		vec.addString("3-B");
		vec.addString("4+A");
		UUID A = Parse.parseUUIDString("+A");
		if (!vec.getUUID(A).string().equals("4+A")) {
			Assert.fail();
		}
		UUID pB = Parse.parseUUIDString("+B");
		if (!vec.getUUID(pB).string().equals("2+B")) {
			Assert.fail();
		}
		UUID mB = Parse.parseUUIDString("-B");
		if (!vec.getUUID(mB).string().equals("3-B")) {
			Assert.fail();
		}
	}

}
