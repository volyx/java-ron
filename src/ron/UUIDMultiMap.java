package ron;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
// min RAM use per object: 16b key, 16b slice, 16*2b on the slab = 64
public class UUIDMultiMap {
	private static final int U2M_DEFAULT_SLICE_SIZE = 2;
	private static final int U2M_SLAB_SIZE = 32; // 32*16=512 bytes
	private Map<UUID, UUID[]> subs;
	private UUID[] slab;


	public static UUIDMultiMap makeUUID2Map() {
		UUIDMultiMap um = new UUIDMultiMap();
		um.subs = new HashMap<>();
		um.slab = new UUID[U2M_SLAB_SIZE];
		for (int i = 0; i < um.slab.length; i++) {
			um.slab[i] = new UUID(0L, 0L);
		}
		return um;
	}

	public void add(UUID key, UUID value) {
		UUID[] values = this.subs.get(key);

		if (values == null) {
			if (this.slab.length < U2M_DEFAULT_SLICE_SIZE) {
				this.slab = new UUID[U2M_SLAB_SIZE];
				for (int i = 0; i < this.slab.length; i++) {
					this.slab[i] = new UUID(0L, 0L);
				}
			}
//			values = Arrays.copyOfRange(this.slab, 0, 1);
			values = new UUID[0];
			this.slab = Arrays.copyOfRange(this.slab, U2M_DEFAULT_SLICE_SIZE, this.slab.length);
		}
		values = UUID.append(values, value);
		this.put(key, values);
	}

	public int len() {
		return this.subs.size();
	}

	public UUID[] keys() {
		UUID[] ret = new UUID[this.len()];
		for (UUID key : this.subs.keySet()) {
			ret = UUID.append(ret, key);
		}
		return ret;
	}

	public UUID[] list(UUID key) {
		return this.subs.get(key);
	}

	public UUID[] take(UUID key) {
		UUID[] ret = this.list(key);
		this.subs.remove(key);
		return ret;
	}

	public void put(UUID key, UUID[] values) {
		if (values.length > 0) {
			this.subs.put(key, values);
		} else {
			this.subs.remove(key);
		}
	}

	public void remove(UUID key, UUID value) {
		UUID[] values = this.subs.get(key);
		boolean ok = values != null;
		if (!ok) {
			return;
		}
		for (int i = 0; i < values.length; i++) {
			if (values[i] == value) {
				int l1 = values.length - 1;
				values[i] = values[l1];
				values = Arrays.copyOfRange(values, 0, l1);
				i--;
			}
		}
		// TODO perf N^2
		// [ ] um.rm - list of removals for len(values)>100?
		// [ ] on every iteration, check against um.rm
		// [ ] um.rm over limit => iterate/merge
	}

}
