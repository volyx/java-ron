package ron;

import ron.Frame;
import ron.Parse;

import static ron.Const.TERM_HEADER;

public class FrameTest {

	public static void main(String[] args) {
//		TestFrame_Split();

		// TestBatchFrames

		String frame1 = "*lww#A@1!:a=1:b=2:c=3";
		String frame2 = "*lww#A@2!:d=4";
		Batch batch = new Batch();
		batch = batch.append(Parse.parseFrameString(frame1));
		batch = batch.append(Parse.parseFrameString(frame2));
		Frame frame12 = batch.join();
		String batchStr = "*lww#A@1!:a=1:b=2:c=3*#@2:0!:d=4";
		if (!frame12.string().equals(batchStr)) {
			System.err.printf("\n%s != \n%s\n", frame12.string(), batchStr);
			throw new RuntimeException("Fail");
		}
//		b2 := frame12.Split()
//		if len(b2) != 2 {
//			t.Fail()
//			t.Log("length", len(b2))
//			return
//		}
//		if b2[0].String() != frame1 {
//			t.Fail()
//			t.Logf("%s != %s\n", b2[0].String(), frame1)
//		}
//		if b2[1].String() != frame2 {
//			t.Fail()
//			t.Logf("%s != %s\n", b2[0].String(), frame1)
//		}
	}

	private static void TestFrame_Split() {
		Frame frame = Parse.parseFrameString("*lww#id1!:val=1*#id2:0!:val=2");
		System.out.println(frame.Parser);

		String h1 = "*lww#id1!:val=1";
		String h2 = "*lww#id2!:val=2";
		frame.next();
		frame.next();
		if (frame.term() != TERM_HEADER) {
			throw new RuntimeException("Fail");
		}
		final Pair<Frame, Frame> pair = frame.split2();
		Frame id1 = pair.getLeft();
		Frame id2 = pair.getRight();
		if (!id1.string().equals(h1)) {
			System.err.printf("\nneed: %s\nhave: %s\n", h1, id1.string());
			throw new RuntimeException("Fail left");
		}
		if (!id2.string().equals(h2)) {
			System.err.printf("\nneed: %s\nhave: %s\n", h2, id2.string());
			throw new RuntimeException("Fail right");
		}
		if (!id2.type().equals(UUID.newName("lww"))) {
			throw new RuntimeException("Fail");
		}
	}
}
