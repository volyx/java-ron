package ron;

import org.junit.Assert;
import org.junit.Test;

import static ron.UUID.NEVER_UUID;
import static ron.UUID.ZERO_UUID;

public class UUIDMultiMapTest {

	@Test
	public void TestUUID2Map_List() {
		UUIDMultiMap um = UUIDMultiMap.makeUUID2Map();
		um.add(NEVER_UUID, ZERO_UUID);
		um.add(NEVER_UUID, NEVER_UUID);
		for (long i = 0; i < 100; i++) {
			um.add(ZERO_UUID, UUID.newNameUUID(i, 0));
		}
		UUID[] nv = um.list(NEVER_UUID);
		if (nv.length != 2 || !nv[0].equals(ZERO_UUID) || !nv[1].equals(NEVER_UUID)) {
			Assert.fail();
		}
		UUID[] hu = um.list(ZERO_UUID);
		if (hu.length != 100) {
			Assert.fail(String.format("Current %d", hu.length));
		}
		for (int i = 0; i < 100; i++) {
			if (hu[i].value() != (long) i) {
				Assert.fail();
			}
		}
		for (int i = 0; i < 100; i += 2) {
			um.remove(ZERO_UUID, UUID.newNameUUID((long) i, 0));
		}
		UUID[] odd = um.list(ZERO_UUID);
		for (int i = 1; i < 100; i += 2) {
			if (odd[i].value() != (long) i) {
				Assert.fail();
			}
		}
	}
}
