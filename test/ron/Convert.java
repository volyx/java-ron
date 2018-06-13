package ron;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Convert {

	public static void convert(String[] args) {
		if (args.length == 0) {
			throw new RuntimeException("nothing to convert");
		}
		String noun = args[0];
		String[] nounArgs = Arrays.copyOfRange(args,1, args.length);
		switch (noun) {
			case "int":
				convert_int(nounArgs);
				break;
			case "int4":
				convert_int4(nounArgs);
				break;
			case "uuid":
				convert_uuid(nounArgs);
				break;
			default:
				throw new RuntimeException("convert what? '" + noun + "'");
		}
	}

	private static void convert_int(String[] args) {
		if (args.length == 0) {
			throw new RuntimeException("nothing to convert");
		}
		if (args[0].equals("-")) {
			Scanner scanner = new Scanner(System.in);
			while (scanner.hasNext()) {
				final long i = scanner.nextLong();
				byte[] out = new byte[12];
				Slice bi = FrameAppend.formatInt(new Slice(out), i);
				// System.out.println(new String(bi.array(), StandardCharsets.UTF_8));
			}
		}
	}

	private static void convert_uuid(String[] args) {
		if (args.length == 0) {
			throw new RuntimeException("nothing to convert");
		}

		String[] uuids = Arrays.copyOfRange(args, 0 , 1);
		args = Arrays.copyOfRange(args, 1 , args.length);
		Scanner scanner = new Scanner(System.in);
		List<String> readUUID = new ArrayList<>();
		if (uuids[0].equals("-")) {
			int j = 0;
			while (scanner.hasNext()) {
				readUUID.add(scanner.next());
			}
		}
		uuids = readUUID.toArray(new String[0]);
		String to = "int";
		if ((args.length > 1)  && (args[0].equals("to"))) {
			to = args[1];
			args = Arrays.copyOfRange(args, 2, args.length);
		}
		for (String a : uuids) {
			UUID uuid = Parse.parseUUIDString(a);

			switch (to) {
				case "int":
					// System.out.println(String.format("%s %s", Long.toUnsignedString(uuid.uuid[0]), Long.toUnsignedString(uuid.uuid[1])));
					break;
				case "int4":
					// System.out.println(String.format("%d %d %d %d",  uuid.variety(), uuid.value(), uuid.scheme(), uuid.origin()));
					break;
				default:
					throw new RuntimeException("convert to what?");
			}
		}
	}

	private static void convert_int4(String[] args) {

	}


}
