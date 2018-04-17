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

//	func ParseFrame(data []byte) Frame { // TODO swap with OpenFrame
//		return OpenFrame(data)
//	}
//
//	func ParseFrameString(frame string) Frame {
//		return ParseFrame([]byte(frame))
//	}
//
//	func ParseStringBatch(strFrames []string) Batch {
//		ret := Batch{}
//		for _, s := range strFrames {
//			ret = append(ret, ParseFrameString(s))
//		}
//		return ret
//	}
}
