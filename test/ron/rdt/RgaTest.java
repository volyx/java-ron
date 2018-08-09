package ron.rdt;

import org.junit.Assert;
import org.junit.Test;
import ron.*;

import static ron.RdtTest.runRONTest;

public class RgaTest {

	@Test
	public void testRGA_Primers() {
		runRONTest(
				Rga.makeRGAReducer(),
				"00-rga-basic.ron"
				);
	}

	@Test
	public void TestRGA_Mapper() {
		var frame = "*rga#1UQ8p+bart@1UQ8yk+lisa!" +
				"@(s+bart'H'@[r'e'@(t'l'@[T'l'@[i'o'" +
				"@(w+lisa' '@(x'w'@(y'o'@[1'r'@{a'l'@[2'd'@[k'!'";
		var right = "Hello world!";
		TxtMapper txt = new TxtMapperImpl();
		String hello = txt.map(new Batch(Parse.parseFrameString(frame)));
		if (!hello.equals(right)) {
			Assert.fail(String.format("'%s' != '%s'", hello, right));
		}
	}

	@Test
	public void TestRGA() {
		var frameStr = "*rga#1UQ8p+bart@1UQ8yk+lisa!" +
				"@(s+bart'H'@[r'e'@(t'l'@[T'l'@[i'o'" +
				"@(w+lisa' '@(x'w'@(y'o'@[1'r'@{a'l'@[2'd'@[k'!'";
		final Frame frame = Parse.parseFrameString(frameStr);

		Rga.makeRGAReducer().reduce(new Batch(frame));
	}

	@Test
	public void TestHelloWorld() {

		var src = new String[]{
				"*rga#1UQ8p+bart!",
				"*rga#1UQ8p+bart@1UQ8s+bart:0'H'",
				"*rga#1UQ8p+bart@1UQ8sr+bart:1UQ8s+bart'e'",
				"*rga#1UQ8p+bart@1UQ8t+bart:1UQ8sr+bart'l'",
				"*rga#1UQ8p+bart@1UQ8tT+bart:1UQ8t+bart'l'",
				"*rga#1UQ8p+bart@1UQ8ti+bart:1UQ8tT+bart'o'",
				"*rga#1UQ8p+bart@1UQ8w+lisa:1UQ8ti+bart' '",
				"*rga#1UQ8p+bart@1UQ8x+lisa:1UQ8w+lisa'w'",
				"*rga#1UQ8p+bart@1UQ8y+lisa:1UQ8x+lisa'o'",
				"*rga#1UQ8p+bart@1UQ8y1+lisa:1UQ8y+lisa'r'",
				"*rga#1UQ8p+bart@1UQ8y1a+lisa:1UQ8y1+lisa'l'",
				"*rga#1UQ8p+bart@1UQ8y2+lisa:1UQ8y1a+lisa'd'",
				"*rga#1UQ8p+bart@1UQ8yk+lisa:1UQ8y2+lisa'!'",
		};

//		var count = 1000;
		var count = 1;

		for (var i = 0; i < count; i++) {

//			seed := time.Now().UnixNano()
			//fmt.Printf("ACI test seed %d\n", seed)
			var seed = 1512325615325939065L;
//			r := rand.New(rand.NewSource(seed))

//			data := make([]string, 13)
//			perm := r.Perm(len(src))
//			for i, v := range perm { // this way we test COMMUTATIVITY
//				data[v] = src[i]
//			}
			var frames = new Frame[src.length];
			for (var j = 0; j < src.length; j++) {
				frames[j] = Parse.parseFrameString(src[j]);
			}

			var rga = Rga.makeRGAReducer();

			var frame = rga.reduce(new Batch(frames));

//			for (; frames.length > 1;) {
//				from := int(r.Uint32()) % len(frames)
//				till := int(r.Uint32()) % (len(frames) - from)
//				till += from + 1
//				//fmt.Printf("\nReduce %d..%d of %d\n", from, till, len(frames))
//				for _, f := range frames[from:till] {
//					fmt.Printf("+ %s\n", f.String())
//				}
//				// this way we test ASSOCIATIVITYz
//				frameC := rga.Reduce(frames[from:till]).Reformat(ron.FRAME_FORMAT_LIST);
//				fmt.Printf("---\n%s\n\n", frameC.String())
//				f := make(ron.Batch, 0, len(frames))
//				f = append(f, frames[:from]...)
//				f = append(f, frameC)
//				f = append(f, frames[till:]...)
//				frames = f
//			}

			var right = "Hello world!";
			var txt = new TxtMapperImpl();
//			hello := txt.Map(ron.Batch{frames[0]})
			var hello = txt.map(new Batch(frame));
			if (!hello.equals(right)) {
				Assert.fail(String.format("'%s' != '%s', seed %d", hello, right, seed));
			} else {
				System.out.println(String.format("%d %d %s\n", i, seed, hello));
			}

		}
	}
}
