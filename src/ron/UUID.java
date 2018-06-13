
package ron;

import java.util.Arrays;
import java.util.Objects;

import static ron.Const.*;
import static ron.FrameAppend.formatZipUUID;

public class UUID implements Comparable<UUID> {

	public static final long UUID_NAME_FLAG = (long) UUID_NAME << 60;

	public static int INT60LEN = 10;
	public static long UUID_NAME_UPPER_BITS = (long) UUID_NAME << 60;

	public static long INT60_FULL = (1L << 60) - 1;
	public static long INT60_ERROR = INT60_FULL;
	public static long INT60_INFINITY = 63L << (6 * 9);
	public static long INT60_FLAGS = Long.parseUnsignedLong(Long.toString(15)) << 60;
	public static UUID ZERO_UUID = new UUID(0, 0);

	public static UUID NEVER_UUID = newNameUUID(INT60_INFINITY, 0);

	public static UUID COMMENT_UUID = NEVER_UUID;

	public static UUID ERROR_UUID = newNameUUID(INT60_ERROR, 0);

	public final long[] uuid = new long[2];

	public UUID(long value, long origin) {
		this.uuid[0] = value;
		this.uuid[1] = origin;
	}

	public UUID(UUID a) {
		Objects.requireNonNull(a);
		this.uuid[0] = a.uuid[0];
		this.uuid[1] = a.uuid[1];
	}

	public UUID clone() {
		return new UUID(this);
	}

	public long value() {
		return uuid[0] & INT60_FULL;
	}

	public long origin() {
		return uuid[1] & INT60_FULL;
	}

	@Override
	public int compareTo(ron.UUID o) {
		long diff = this.value() - o.value();
		if (diff == 0) {
			diff = this.origin() - o.origin();
		}
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
		UUID uuid1 = (UUID) o;
		return Arrays.equals(uuid, uuid1.uuid);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(uuid);
	}

	public boolean isTranscendentName() {
		return uuid[1] == UUID_NAME_UPPER_BITS;
	}

	public boolean isName() {
		return this.scheme() == UUID_NAME;
	}

	public boolean laterThan(UUID b) {
		if (this.value() == b.value()) {
			return this.origin() > b.origin();
		} else {
			return this.value() > b.value();
		}
	}

	public boolean earlierThan(UUID b) {
		// FIXME define through Compare
		if (this.value() == b.value()) {
			return this.origin() < b.origin();
		} else {
			return this.value() < b.value();
		}
	}

	public int scheme() {
		return (int) (this.uuid[1] >>> 60) & 3;
	}


	public int variety() {
		// order is important cause cast has higher priority than  >>>
		return (int) (this.uuid[0] >>> 60);
	}

	public byte sign() {
		return UUID_PUNCT[this.scheme()];
	}


	public long replica() {
		return this.uuid[1] & INT60_FULL;
	}

	public boolean sameAs(UUID b) {
		if (this.value() != b.value()) {
			return false;
		} else if (this.origin() == b.origin()) {
			return true;
		} else if ((this.origin() ^ b.origin() & INT60_FULL) != 0) {
			return false;
		} else {
			return (this.origin() & INT60_FULL) == (b.origin() & INT60_FULL);
		}
	}

	public UUID derived() {
		if (this.scheme() == UUID_EVENT) {
			return newUUID(UUID_DERIVED, this.value(), this.origin());
		} else {
			return this;
		}
	}

	public static UUID newRonUUID(long scheme, long variety, long value, long origin) {
		return new UUID(value | (variety & 15) << 60, origin | (scheme & 15) << 60);
	}

	public static UUID newUUID(long scheme, long value, long origin) {
		return newRonUUID(scheme, 0, value, origin);
	}

	public static UUID newEventUUID(long time, long origin) {
		return newUUID(UUID_EVENT, time, origin);
	}

	public static UUID newNameUUID(long time, long origin) {
		return newUUID(UUID_NAME, time, origin);
	}

	public UUID newHashUUID(long time, long origin) {
		return newUUID(UUID_HASH, time, origin);
	}

	// use for static strings only - panics on error
	public static UUID newName(String name) {
		return Parse.parseUUIDString(name);
	}

	public UUID newError(String name) {
		UUID nam = Parse.parseUUIDString(name);
		return newNameUUID(nam.value(), INT60_ERROR);
	}

	public boolean isTemplate() {
		return this.sign() == UUID_NAME && this.value() == 0 && this.origin() != 0;
	}

	public UUID toScheme(long scheme) {
		return newUUID(scheme, this.value(), this.origin());
	}

	public boolean isZero() {
		return this.value() == 0;
	}

	public boolean isError() {
		return this.origin() == INT60_ERROR;
	}

//	func (uuid UUID) ZipString(context UUID) string {
//		var arr [INT60LEN*2 + 2]byte
//		return string(FormatZipUUID(arr[:0], uuid, context))
//	}


	public String zipString(UUID context) {
		byte[] arr = new byte [INT60LEN * 2 + 2];
		return new String(formatZipUUID(new Slice(arr, 0), this, context).array());
	}

	public String string() {
		String ret = this.zipString(ZERO_UUID);
		if (ret.length() == 0) {
			ret = "0";
		}
		return ret;
	}

	public String error() {
		if (this.isError()) {
			return this.string();
		} else {
			return "";
		}
	}

	public static UUID[] append(UUID[] a, UUID b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);
		return append(a, new UUID[]{b});
	}

	public static UUID[] append(UUID[] a, UUID[] b) {
		UUID[] c = new UUID[a.length + b.length];
		for (int i = 0;i <= a.length - 1; i++) {
			Objects.requireNonNull(a[i]);
			c[i] = new UUID(a[i]);
		}
		for (int j = 0;j <= b.length - 1; j++) {
			Objects.requireNonNull(b[j]);
			c[a.length + j] = new UUID(b[j]);
		}
		return c;
	}


	@Override
	public String toString() {
		return Arrays.toString(uuid);
	}
}