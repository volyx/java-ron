package ron;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Slice {
	private byte[] buf;
	private int offset;

	public Slice(byte[] buf) {
		this.buf = buf;
		this.offset = 0;
	}

	public byte get(int i) {
		return buf[i];
	}


	public void set(int j, byte b) {
		this.buf[j] = b;
	}

	public int length() {
		return buf.length;
	}

	public byte[] array() {
		return buf;
	}

	public Slice append(byte[] b) {
		byte[] c = new byte[length() + b.length];
		System.arraycopy(this.buf, 0, c, 0, length());
		System.arraycopy(b, 0, c, length(), b.length);
		return new Slice(c);
	}

	public Slice append(byte b) {
		return this.append(new byte[]{b});
	}

	public static Slice append(byte a, char b) {
		Slice s = new Slice(new byte[] {a});
		return s.append(b);
	}

	public Slice append(char c) {
		return append(Character.valueOf(c).toString().getBytes(StandardCharsets.UTF_8));
	}

	public Slice append(String c) {
		return append(c.getBytes(StandardCharsets.UTF_8));
	}

	public Slice append(long v) {
		return append(longToBytes(v));
	}


	public static byte[] toBytes(String c) {
		return c.getBytes(StandardCharsets.UTF_8);
	}


	public Slice copy(Slice src) {

		System.arraycopy(src, 0, dst.array(), 0, src.length);
		return dst;
	}


	private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

	public static byte[] longToBytes(long x) {
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();//need flip
		return buffer.getLong();
	}

}
