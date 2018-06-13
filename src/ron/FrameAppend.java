package ron;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static ron.Const.BASE_PUNCT;
import static ron.Const.IS_BASE;
import static ron.Const.PREFIX_PUNCT;
import static ron.UUID.INT60_FULL;
import static ron.UUID.UUID_NAME_UPPER_BITS;


public class FrameAppend {

	public static int FORMAT_UNZIP = 		1;
	public static int FORMAT_GRID =  		1 << 1;
	public static int FORMAT_SPACE = 		1 << 2;
	public static int FORMAT_HEADER_SPACE = 1 << 3;
	public static int FORMAT_NOSKIP = 		1 << 4;
	public static int FORMAT_REDEFAULT = 	1 << 5;
	public static int FORMAT_OP_LINES = 	1 << 6;
	public static int FORMAT_FRAME_LINES =  1 << 7;
	public static int FORMAT_INDENT = 		1 << 8;

	public static int  FRAME_FORMAT_CARPET = FORMAT_GRID | FORMAT_SPACE | FORMAT_OP_LINES | FORMAT_NOSKIP | FORMAT_UNZIP;
	public static int  FRAME_FORMAT_TABLE = FORMAT_GRID | FORMAT_SPACE | FORMAT_OP_LINES;
	public static int  FRAME_FORMAT_LIST = FORMAT_OP_LINES | FORMAT_INDENT;
	public static int  FRAME_FORMAT_LINE = FORMAT_FRAME_LINES | FORMAT_HEADER_SPACE;

//FORMAT_CONDENSED = 1 << iota
//FORMAT_OP_LINES
//FORMAT_FRAMES
//FORMAT_TABLE
	public static String  SPACES22 = "                      ";
	public static String  SPACES88 = SPACES22 + SPACES22 + SPACES22 + SPACES22;
	public static String ZEROS10 = "0000000000";

	// FormatInt outputs a 60-bit "Base64x64" int into the output slice
//	func FormatInt(output []byte, value uint64) []byte {
//		tail := bits.TrailingZeros64(value)
//		if tail > 54 {
//			tail = 54
//		}
//		tail -= tail % 6
//		for i := 54; i >= tail; i -= 6 {
//			output = append(output, BASE64[(value>>>uint(i))&63])
//		}
//		return output
//	}

	// FormatInt outputs a 60-bit "Base64x64" int into the output slice
	public static Slice formatInt(Slice output, long value) {
		int tail = Long.numberOfTrailingZeros(value);
		if (tail > 54) {
			tail = 54;
		}
		tail -= tail % 6;
		for (int i = 54; i >= tail; i -= 6) {
			output = output.append(Const.BASE64.getBytes(StandardCharsets.UTF_8)[(int) ((value >>> i) & 63)]);
		}
		return output;
	}

	public static Slice formatZipInt(Slice output, long value, long context) {
		int prefix = int60Prefix(value, context);
		if (prefix == 60) {
			return output;
		}
		if (prefix >= 4 * 6) {
			prefix -= prefix % 6;
			value = (value << (prefix)) & INT60_FULL;
			Slice.checkElementIndex(prefix/6 - 4, PREFIX_PUNCT.length);
			byte pchar = PREFIX_PUNCT[prefix/6 - 4];
			output = output.append(pchar);
			if (value != 0) {
				output = formatInt(output, value);
			}
		} else {
			output = formatInt(output, value);
		}
		return output;
	}

	public static int int60Prefix(long a, long b) {
		return Long.numberOfLeadingZeros((a^b) & INT60_FULL) - 4;
	}


	public static int sharedPrefix(UUID uuid, UUID context) {
		uuid = uuid.clone(); context = context.clone();
		int vp = Long.numberOfLeadingZeros(uuid.value() ^ context.value());
		vp -= vp % 6;
		int op = Long.numberOfLeadingZeros((uuid.origin() ^ context.origin()) & INT60_FULL);
		op -= op % 6;
		int ret = vp + op;
		if (uuid.scheme() != context.scheme()) {
			ret--;
		}
		return ret;
	}

	public static Slice formatUUID(Slice buf, UUID uuid)  {
		uuid = uuid.clone();
		int variety = uuid.variety();
		if (variety != 0) {
			buf = buf.append(BASE_PUNCT[variety]).append('/');
		}
		buf = formatInt(buf, uuid.value());
		if (uuid.origin() != UUID_NAME_UPPER_BITS) {
			buf = buf.append(uuid.sign());
			buf = formatInt(buf, uuid.replica());
		}
		return buf;
	}

	public static Slice formatZipUUID(Slice buf, UUID uuid, UUID context) {
		uuid = uuid.clone(); context = context.clone();
		if (uuid.variety() != context.variety()) {
			// don't want to optimize this; a rare case anyway
			return formatUUID(buf, uuid);
		}
		int start = buf.length();
		buf = formatZipInt(buf, uuid.value(), context.value());
		if (uuid.isTranscendentName()) {
			return buf;
		}
		buf = buf.append(uuid.sign());
		int at = buf.length();
		buf = formatZipInt(buf, uuid.origin(), context.origin());
		// sometimes, we may skip UUID separator (+-%$)
		if (uuid.scheme() == context.scheme() && at > start + 1) {
			if (buf.length() > at && !isBase64(buf.get(at))) {
//				copy(buf[at-1:], buf[at:])
//				buf = buf[:len(buf)-1]
				final Slice dst = new Slice(Arrays.copyOfRange(buf.array(), at - 1, buf.length()));
				final Slice src = new Slice(Arrays.copyOfRange(buf.array(), at, buf.length()));
				// we need get minimum slice like in Go
				if (dst.length() > src.length()) {
					buf = buf.copy(src,  at - 1);
				} else {
					buf = buf.copy(dst,  at - 1);
				}
				buf = new Slice(Arrays.copyOfRange(buf.array(), 0 , buf.array().length - 1));
			} else if (buf.length() == at && !isBase64(buf.get(start))) {
				buf = new Slice(Arrays.copyOfRange(buf.array(), 0, buf.length() - 1));
			}
		}
		return buf;
	}

	public static boolean isBase64(byte b) {
		int bi = (int) b;
		return ((IS_BASE[bi >>> 6] >>> (bi & 63)) & 1) != 0;
	}

}
