package ron;

import java.util.HashMap;
import java.util.Map;

public class VVector {
	private Map<Long, Long> vec = new HashMap<>();
	public void add(UUID uuid) {
		Long val = vec.get(uuid.uuid[1]);
		boolean ok = val != null;
		if (!ok || val < uuid.uuid[0]) {
			vec.put(uuid.uuid[1], uuid.uuid[0]);
		}
	}

	 public void addString(String uuidString) {
		UUID uuid = Parse.parseUUIDString(uuidString);
//		if err == nil {
//			vec.Add(uuid)
//		}
//		return err
		add(uuid);
	}

	public Long get(UUID uuid) {
		return vec.get(uuid.uuid[1]);
	}

	public UUID getUUID(UUID uuid) {
		return new UUID(vec.get(uuid.uuid[1]), uuid.uuid[1]);
	}

}
