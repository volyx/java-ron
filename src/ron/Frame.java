package ron;

import static ron.Const.FRAME_TERM_SEP;

// RON Frame is a vector of immutable RON ops.
// A frame is always positioned on some op (initially, the first one).
// In a sense, Frame is its own iterator: frame.Next(), returns true is the
// frame is re-positioned to the next op, false on error (EOF is an error too).
// That is made to minimize boilerplate as Frames are forwarded based on the
// frame header (the first op).
// Frame is not thread-safe; the underlying buffer is append-only, thus thread-safe.
public class Frame {
	public static final int DEFAULT_ATOMS_ALLOC = 6;
    public static final byte[] FRAME_TERM_ARR = new byte [] {FRAME_TERM_SEP, '\n'};
    public static final long[] DIGIT_OFFSETS = new long[] {54, 48, 42, 36, 30, 24, 18, 12, 6, 0, 255};
    public static final long[] PREFIX_MASKS = new long[] {0, 1134907106097364992L, 1152640029630136320L, 1152917106560335872L, 1152921435887370240L, 1152921503533105152L, 1152921504590069760L, 1152921504606584832L, 1152921504606842880L, 1152921504606846912L, 1152921504606846975L};

    public ParserState Parser;
    public SerializeState Serializer;
    // RON coding: binary/text
    public boolean binary;
    // The current position in the frame (op idx).
    public int position;
    // ints hosts the current op: 4 pairs for spec uuid entries, the rest is values (also pairs).
    // General convention: hte first int is hte value, the second is flags and other stuff.
    public Atom[] _atoms = new Atom[DEFAULT_ATOMS_ALLOC];
    public Atom[] atoms = new Atom[DEFAULT_ATOMS_ALLOC];
    // Op terminator (see OP_TERM)
    public int term;
    // Frame body, raw bytes.
    public byte[] Body;

    public Frame() {
        for (int i = 0; i < _atoms.length; i++) {
            _atoms[i] = new Atom(0L, 0L);
        }
    }

    public boolean Next() {
    	throw new UnsupportedOperationException();
//        Frame frame = FrameParser.parseFrame(this);
//        if (frame.Parser.state == RON_error) {
//            return false;
//        }
//        if (frame.Parser.streaming) {
//            return IsComplete(frame);
//        }
//        return true;
    }


//    Cursor Begin () {
//        return null;
//    }
//
//    void Append (Op op) {}
}