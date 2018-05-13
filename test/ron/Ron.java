package ron;

import java.util.Arrays;

public class Ron {

	public static void main(String[] args) {
		if (args.length == 0) {
			return;
		}
		String cmd = args[0];

		String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);
		switch (cmd) {
			case "convert":
				Convert.convert(cmdArgs);
				break;
			default:
				throw new RuntimeException("Unknown cmd " + cmd);
		}
	}
}
