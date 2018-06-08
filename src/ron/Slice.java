package ron;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


/**
 * bool operator==(const Slice& b) const {
 * return offset == b.offset && buf == b.buf && size == b.size;
 * }
 * <p>
 * char operator[](size_t off) const {
 * return buf[offset + off];
 * }
 * <p>
 * char operator*() const { return buf[offset]; }
 * <p>
 * Slice operator++() {
 * offset++;
 * return *this;
 * }
 * <p>
 * Slice operator++(int) {
 * Slice ret = *this;
 * offset++;
 * return ret;
 * }
 * <p>
 * char* begin() const { return buf; }
 * <p>
 * char* at() const { return buf + offset; }
 * <p>
 * void to(size_t new_offset) { offset = new_offset; }
 * void to(char* new_pos) { offset = new_pos - buf; }
 * <p>
 * char* end() const { return buf + size; }
 * <p>
 * operator std::string() const { return std::string(buf, buf + offset); }
 * <p>
 * bool used() const { return offset == size; }
 * <p>
 * size_t available() const { return size - offset; }
 * <p>
 * bool empty() const { return buf == NULL; }
 * <p>
 * void free() {
 * ::free((void*)buf);
 * buf = NULL;
 * size = offset = 0;
 * }
 * <p>
 * void append(char c) { buf[offset++] = c; }
 * <p>
 * void append(const char* c, size_t len) {
 * memcpy(buf + offset, c, len);
 * offset += len;
 * }
 * <p>
 * void append(Slice data) { append(data.buf + data.offset, data.available()); }
 * <p>
 * void append(std::string str) { append(str.c_str(), str.size()); }
 * <p>
 * bool has(size_t bytes) const { return available() >= bytes; }
 * <p>
 * char operator[](size_t idx) { return buf[offset + idx]; }
 * <p>
 * void copyTo(char* dest) const { memcpy(dest, buf + offset, size - offset); }
 * <p>
 * Slice write2read() const { return Slice(buf, 0, offset); }
 * <p>
 * Slice till(size_t pos) const { return Slice(buf, offset, pos); }
 * Slice till(const char* pos) const { return Slice(buf, offset, pos - buf); }
 */

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
		System.arraycopy(this.buf, 0, c, 0, this.offset);
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
