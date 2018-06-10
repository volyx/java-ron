package ron;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static ron.Const.*;

public class UUIDTest {

	@Test
	public void TestUUID_Parse() {
		Map<String, UUID> samples = new HashMap<>();
		samples.put("1", UUID.newRonUUID(UUID_NAME, UUID_NAME_TRANSCENDENT, 1L << 54, 0));
		samples.put("1/978$1400075997", UUID.newRonUUID(UUID_NAME, UUID_NAME_ISBN, 164135095794401280L, 19140298535113287L));
		for (String uuidStr : samples.keySet()) {
			final UUID uuid = samples.get(uuidStr);
			UUID parsed = Parse.parseUUIDString(uuidStr);
//			if err != nil {
//				t.Fail()
//				t.Log("parse fail", err)
//				break
//			}
			if (!uuid.equals(parsed)) {
				Assert.fail(String.format("got %s expected %s", parsed.string(), uuid.string()));
			}
			if (!parsed.string().equals(uuidStr)) {
				Assert.fail(String.format("serialized as %s expected %s", parsed.string(), uuidStr));
			}
		}


	}

}
