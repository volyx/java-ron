package ron;

import java.nio.charset.StandardCharsets;

public class FrameAppend {
	// FormatInt outputs a 60-bit "Base64x64" int into the output slice
	public static byte[] formatInt(byte[] output, long value) {
		int tail = Long.numberOfTrailingZeros(value);
		if (tail > 54) {
			tail = 54;
		}
		tail -= tail % 6;
		int j = 0;
		for (int i = 54; i >= tail; i -= 6) {
			output[j] = Const.BASE64.getBytes(StandardCharsets.UTF_8)[(int) (value >> i & 63)];
			j++;
		}
		return output;
	}
}
