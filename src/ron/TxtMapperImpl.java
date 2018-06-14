package ron;

import static ron.rdt.Rga.RGA_UUID;

public class TxtMapperImpl implements TxtMapper {

	@Override
	public String map(Batch batch) {
		if (batch.frames.length == 0) {
			return "";
		}
		Frame rga = batch.frames[0];
		if (!rga.type().equals(RGA_UUID) || !rga.isHeader()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (rga.next(); !rga.eof() && !rga.isHeader(); rga.next()) {
			if (rga.ref().isZero()) {
				sb.append(rga.rawString(0));
			}
		}
		return sb.toString();
	}
}
