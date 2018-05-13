package ron;

import java.nio.charset.StandardCharsets;

import static ron.UUID.ZERO_UUID;

public class Parse {

	public static UUID parseUUIDString(String uuid) {
		return Parser.parseUUID(ZERO_UUID, uuid.getBytes(StandardCharsets.UTF_8));
	}

	public static UUID parseUUID(byte[] data) {
		return Parser.parseUUID(ZERO_UUID, data);
	}

	public static Frame parseFrame(byte[] data) { // TODO swap with OpenFrame
		return Frame.openFrame(data);
	}

	public static Frame parseFrameString(String frame) {
		return parseFrame(frame.getBytes(StandardCharsets.UTF_8));
	}

	public static Batch parseStringBatch(String[] strFrames) {
		Batch ret = new Batch();
		ret.frames = new Frame[strFrames.length];
		for (int i = 0; i < strFrames.length; i++) {
			ret.frames[i] = parseFrameString(strFrames[i]);
		}
		return ret;
	}
}
