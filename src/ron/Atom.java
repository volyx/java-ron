package ron;

import java.util.Arrays;

import static ron.Frame.DIGIT_OFFSETS;
import static ron.Frame.PREFIX_MASKS;
import static ron.Parser.ORIGIN;
import static ron.Parser.VALUE;

public class Atom extends UUID {
	public static final boolean debug = false;
	//	public static final long INT60_FLAGS = Long.parseUnsignedLong(Long.toString(public static final long INT60_FLAGS = ))  << 60;
	public static final long INT32_FULL = (1L << 32) - 1;
	//	public static final long INT60_FLAGS = 15L << 60;
	public static final long INT60_FLAGS = Long.parseUnsignedLong("17293822569102704640");
	public static final long BIT60 = 1L << 60;
	public static final long BIT61 = 1L << 61;
	public static final long INT16_FULL = (1L << 16) - 1;
	public static UUID ZERO_UUID_ATOM = new Atom(ZERO_UUID);

	public Atom(long value, long origin) {
		super(value, origin);
	}

	public Atom(UUID uuid) {
		super(uuid);
	}

	public Atom clone() {
		return new Atom(this);
	}

	public static Atom NewAtom() {
		return new Atom(0L, 0L);
	}

	public UUID UUID() {
		return new UUID(this);
	}

	// a[half] |= uint64(value) << DIGIT_OFFSETS[dgt] /
	public void set6(int half, int dgt, long value) {
		if (debug) {
			System.out.println("set6 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] = uuid[half] | (value << DIGIT_OFFSETS[dgt]); // FIXME reverse numbering
		if (debug) {
			System.out.println("set6 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}

	}

	public void set2(int half, int idx, long value) {
		if (debug) {
			System.out.println("set2 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] |= value << (idx << 1);
		if (debug) {
			System.out.println("set2 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
	}

	public void trim6(int half, int dgt) {
		if (debug) {
			System.out.println("trim6 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] &= INT60_FLAGS | PREFIX_MASKS[dgt];
		if (debug) {
			System.out.println("trim6 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
	}

	public long get6(int half, int dgt) {
		return (uuid[half] >>> DIGIT_OFFSETS[dgt]) & 63L;
	}

	public void init64(int half, long flags) {
		if (debug) {
			System.out.println("init64 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] = flags << 60;
		if (debug) {
			System.out.println("init64 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
	}


	public void reset4(int half, long idx, long value) {
		if (debug) {
			System.out.println("reset4 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] &= uuid[half] ^ (15L << (idx << 2));
//		uuid[half] = value << 60;
		uuid[half] |= value << (idx << 2);
		if (debug) {
			System.out.println("reset4 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
	}

	public void arab64(int half, int value) {
		if (debug) {
			System.out.println("arab64 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] *= 10;
		uuid[half] += value;
		if (debug) {
			System.out.println("arab64 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
	}

	public void set1(int half, int idx) {
		if (debug) {
			System.out.println("set1 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] = uuid[half] | (1L << idx);
		if (debug) {
			System.out.println("set1 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}

	}

	public void inc16(int half, long idx) {
		if (debug) {
			System.out.println("inc16 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		long shift = idx << 4;
		long i = uuid[half] >>> shift;
		i++;
		uuid[half] &= uuid[half] ^ (INT16_FULL << shift);
		uuid[half] |= (i & INT16_FULL) << shift;
		if (debug) {
			System.out.println("inc16 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}

	}

	public void arab16(int half, long value) {
		if (debug) {
			System.out.println("arab16 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		long i = uuid[half] & INT16_FULL;
		i *= 10;
		i += value;
		uuid[half] &= uuid[half] ^ INT16_FULL;
		uuid[half] |= i & INT16_FULL;
		if (debug) {
			System.out.println("arab16 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
	}

	public void set32(int half, int idx, long value) {
		if (debug) {
			System.out.println("set32 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
		uuid[half] |= value << (idx << 5);
		if (debug) {
			System.out.println("set32 " + Long.toUnsignedString(uuid[half]) + " half " + half);
		}
	}

	public void setOrigin(long origin) {
		this.uuid[1] = origin;
	}

	public int type() {
		return (int) (uuid[1] >>> 62);
	}

	public long integer() {
		int neg = (int) (uuid[1] & (1L << 60));
		long ret = uuid[0];
		if (neg == 0) {
			return ret;
		} else {
			return -ret;
		}
	}

	public int Type() {
		return type();
	}

	public double Float() {
		int pow = this.pow();
		double ret = (double) (uuid[VALUE]) * Math.pow(10, pow);
		if ((uuid[ORIGIN] & BIT60) != 0) {
			ret = -ret;
		}
		return ret;
	}


	public int pow() {
		Atom a = this;
		int pow = (int) (a.uuid[ORIGIN] & INT16_FULL);
		if ((a.uuid[ORIGIN] & BIT61) != 0) {
			pow = -pow;
		}
		pow -= (int) ((a.uuid[ORIGIN] >>> 16) & INT16_FULL);
		return pow;
	}

	public byte[] escString(Slice body) {
		int from = (int) (uuid[0] >>> 32);
		int till = (int) (uuid[0] & INT32_FULL);
		// FIXME check if binary;
		if (body.array().length <= till) {
			throw new IllegalStateException("array length " + body.array().length + " till " + till);
		}
		if (body.array().length <= from) {
			throw new IllegalStateException("array length " + body.array().length + " from " + till);
		}
		return Arrays.copyOfRange(body.array(), from, till);
	}

	public String rawString(byte[] body) {
		long from = uuid[0] >>> 32;
		long till = uuid[0] & INT32_FULL;
		// FIXME check if binary
		return new String(unesc(Arrays.copyOfRange(body, (int) from, (int) till)));
	}

	// add JSON escapes
	public byte[] esc(byte[] str) {
		return str;
	}

	// remove JSON escapes
	public byte[] unesc(byte[] str) {
		// TODO
		return str;
	}

}