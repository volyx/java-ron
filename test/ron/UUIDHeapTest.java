package ron;

import org.junit.Assert;
import org.junit.Test;

import static ron.UUID.NEVER_UUID;
import static ron.UUID.ZERO_UUID;

public class UUIDHeapTest {

	@Test
	public void TestUHeap_TakeUUID() {
		UUIDHeap h = new UUIDHeapImpl();
		h.put(ZERO_UUID);
		h.put(ZERO_UUID);
		h.put(NEVER_UUID);
		h.put(NEVER_UUID);
		h.put(NEVER_UUID);
		if (h.len() != 5) {
			Assert.fail(String.format("len() %d", h.len()));
		}
		final UUID zero = h.take();
		if (!zero.equals(ZERO_UUID)) {
			Assert.fail(String.format("need:%s\nhave:%s", ZERO_UUID, zero));
		}
		if (!h.popUnique().equals(ZERO_UUID)) {
			Assert.fail();
		}
		if (h.len() != 3) {
			Assert.fail();
		}
		if (!h.popUnique().equals(NEVER_UUID)) {
			Assert.fail();
		}
		if (h.len() != 0) {
			Assert.fail();
		}
	}
}
