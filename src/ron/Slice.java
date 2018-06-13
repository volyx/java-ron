package ron;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class Slice {
	private byte[] buf;
	private int offset;

	public Slice(byte[] buf) {
		this.buf = buf;
		this.offset = buf.length;
	}

	public Slice(byte[] buf, int offset) {
		this.buf = buf;
		this.offset = offset;
	}

	public byte get(int off) {
//		final int index = offset + off;
		checkElementIndex(off, buf.length);
		return buf[off];
	}

	public int length() {
//		return buf.length;
		return offset;
	}

	public byte[] array() {
		return buf;
	}

	public Slice append(byte[] b) {
		byte[] c = new byte[length() + b.length];
		for (int i = 0; i < this.offset; i++) {
			c[i] = this.buf[i];
		}
//		System.arraycopy(this.buf, 0, c, 0, this.offset);
		System.arraycopy(b, 0, c, this.offset, b.length);
		this.buf = c;
		this.offset += b.length;
		return this;
	}

	public Slice append(byte b) {
		return this.append(new byte[]{b});
	}


	public Slice append(char c) {
		return append(Character.valueOf(c).toString().getBytes(StandardCharsets.UTF_8));
	}

	public Slice append(String c) {
		return append(c.getBytes(StandardCharsets.UTF_8));
	}

	public Slice append(long v) {
		return append(Long.toUnsignedString(v));
	}


	public static byte[] toBytes(String c) {
		return c.getBytes(StandardCharsets.UTF_8);
	}

	public String string() {
		return new String(Arrays.copyOfRange(this.buf, 0, offset), StandardCharsets.UTF_8);
	}

	public Slice copy(Slice src, int offset) {
		System.arraycopy(src.buf, 0, this.buf, offset, src.array().length);
		return this;
	}

	public static int checkElementIndex(int index, int size) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index " + index + " size " + size);
		}
		return index;
	}

	@Override
	public String toString() {
		return "Slice{" + "buf=" + new String(buf) +
				'}';
	}
}
