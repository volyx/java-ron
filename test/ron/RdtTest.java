package ron;

import org.junit.Assert;
import ron.rdt.Reducer;
import ron.rdt.RgaTest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class RdtTest {

	public static void runRONTest(Reducer reducer, String scriptFile) {
		final byte[] scriptBuf;
		final URL resource = RgaTest.class.getClassLoader().getResource(scriptFile);
		Objects.requireNonNull(resource);
		try {
			scriptBuf = Files.readAllBytes(Paths.get(resource.toURI().getPath()));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		final Frame scriptFrame = Parse.parseFrame(new Slice(scriptBuf));
		Batch script = scriptFrame.split();
		if (script.frames.length == 0) {
			Assert.fail("script not parsed");
		} // todo parse err

		for (;script.frames.length != 0;) {
			Frame q = script.frames[0];
			script.frames = Frame.slice(script.frames, 1, script.frames.length);
			if (!q.isComment() || !q.isQuery()) {
				Assert.fail("no test header");
			}

			System.out.println(q.getString(0) + "?..");

			var l = 0;
			for (;script.frames.length > l && !script.frames[l].isComment();) {
				l++;
			}
			Batch inputs = new Batch(Frame.slice(script.frames, 0, l));
			script = new Batch(Frame.slice(script.frames, l, script.frames.length));

			Frame output = reducer.reduce(inputs);
			Batch outputs = output.split();

			if (script.frames.length == 0 || !script.frames[0].isHeader()) {
				Assert.fail("no output specified");
			}
			Frame a = script.frames[0];
			script = new Batch(Frame.slice(script.frames, 1, script.frames.length));

			l = 0;
			for (;script.frames.length > l && !script.frames[l].isComment();) {
				l++;
			}

			Batch correct = new Batch(Frame.slice(script.frames, 0, l));
			script = new Batch(Frame.slice(script.frames, l, script.frames.length));

			boolean ok = correct.compare(outputs);
			if (!ok) {
				System.err.printf("FAILS to produce %s:\n%s\nshould be\n%s\n", a.getString(0), outputs.string(), correct.string());
				System.err.printf("exact input, output:\n%s\n---\n%s\n\n", inputs.string(), output.string());
//				System.out.printf("mismatch at %d %d\n", op, at);
				Assert.fail();


//				Assert.fail(String.format(
//						"FAILS to produce %s:\n" +
//								"%s\n" +
//								"should be\n" +
//								"%s\n" +
//						"exact input, output:\n" +
//								"%s\n" +
//								"---\n" +
//								"%s\n" +
//								"\n"
//						/*"mismatch at %d %d\n"*/, a.getString(0), outputs.string(), correct.string(), inputs.string(), output.string()/*, op, at*/));
			} else {
				System.out.println("produces " + a.getString(0) + "!\n");
			}

		}
	}

}
