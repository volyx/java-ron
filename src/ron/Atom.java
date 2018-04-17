package ron;


import static ron.Frame.DIGIT_OFFSETS;
import static ron.Frame.PREFIX_MASKS;

public class Atom extends UUID {
//	public static final long INT60_FLAGS = Long.parseUnsignedLong(Long.toString(public static final long INT60_FLAGS = ))  << 60;
	public static final long INT60_FLAGS = 15 << 60;

	public Atom(long value, long origin) {
		super(value, origin);
	}

	public Atom(UUID uuid) {
		super(uuid);
	}

	public static Atom NewAtom() {
		return new Atom(0L, 0L);
	}

	public void set6(int half, int dgt, long value) {
		uuid[half] |= value << DIGIT_OFFSETS[dgt]; // FIXME reverse numbering
	}

	public void set2(int origin, int i, int atomUuid) {
		throw new UnsupportedOperationException();
	}

	public void trim6(int half, int dgt) {
		uuid[half] &= INT60_FLAGS | PREFIX_MASKS[dgt];
	}

	public long get6(int half, int dgt) {
		return (uuid[half] >> DIGIT_OFFSETS[dgt]) & 63L;
	}

	public void init64(int half, long flags) {
		uuid[half] = flags << 60;
	}

//	func (a *Atom) reset4(half Half, idx uint, value uint8) {
//		a[half] &^= 15 << (idx << 2)
//		a[half] |= uint64(value) << (idx << 2)
//	}

	public void reset4(int half, int idx, long value) {
//		uuid[half] &^= 15 << (idx << 2);
		uuid[1] = value << 60;
		uuid[half] |= value << (idx << 2);
	}

	public void arab64(int value, int i) {
		throw new UnsupportedOperationException();
	}

	public void set1(int half, int idx) {
		uuid[half] |= 1 << idx;
	}

	public void inc16(int origin, int i) {
		throw new UnsupportedOperationException();
	}

	public void arab16(int origin, int i) {
		throw new UnsupportedOperationException();
	}

	public void set32(int half, int idx, long value) {
		uuid[half] |= value << (idx << 5);
	}

	public void setOrigin(long origin) {
		this.uuid[1] = origin;
	}

	public int Type() {
		throw new UnsupportedOperationException();
	}

}