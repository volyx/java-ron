package ron;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.copyOfRange;
import static ron.Atom.BIT60;
import static ron.Atom.ZERO_UUID_ATOM;
import static ron.Const.*;
import static ron.FrameAppend.*;
import static ron.Parser.IsComplete;
import static ron.Parser.ORIGIN;
import static ron.Parser.RON_error;
import static ron.UUID.COMMENT_UUID;
import static ron.UUID.ZERO_UUID;

// RON Frame is a vector of immutable RON ops.
// A frame is always positioned on some op (initially, the first one).
// In a sense, Frame is its own iterator: frame.Next(), returns true is the
// frame is re-positioned to the next op, false on error (EOF is an error too).
// That is made to minimize boilerplate as Frames are forwarded based on the
// frame header (the first op).
// Frame is not thread-safe; the underlying buffer is append-only, thus thread-safe.
public class Frame {
	public static final int DEFAULT_ATOMS_ALLOC = 6;
	public static final byte[] zeros  = "0000000000".getBytes(StandardCharsets.UTF_8);
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
    public Slice Body;

    public Frame() {
		this.Serializer = new SerializeState();
		this.Parser = new ParserState();
		for (int i = 0; i < _atoms.length; i++) {
            _atoms[i] = new Atom(0L, 0L);
        }
//        for (int i = 0; i < atoms.length; i++) {
//            atoms[i] = new Atom(0L, 0L);
//        }
        atoms = new Atom[0];
    }

//	func OpenFrame(data []byte) Frame {
//		frame := Frame{}
//		frame.Body = data
//		frame.Parse()
//		return frame
//	}

    public static Frame openFrame(Slice data) {
        Frame frame = new Frame();
        frame.Body = data;
        return ron.Parser.parseFrame(frame);
    }

//	func MakeFormattedFrame(format uint, prealloc_bytes int) (ret Frame) {
//		ret.Body = make([]byte, 0, prealloc_bytes)
//		ret.Serializer.Format = format
//		return
//	}

	public static Frame makeFrame(int prealloc_bytes) {
		Frame frame = new Frame();
		frame.Body = new Slice(new byte[prealloc_bytes], 0);
		return frame;
	}

//	func MakeFrame(prealloc_bytes int) (ret Frame) {
//		ret.Body = make([]byte, 0, prealloc_bytes)
//		return
//	}

//	func ParseStream(buf []byte) Frame {
//		ret := MakeFrame(1000 + len(buf))
//		ret.AppendBytes(buf)
//		ret.Parser.streaming = true
//		ret.Next()
//		return ret
//	}

	public UUID UUID(int idx) {
		return new UUID(this.atoms[idx]);
	}


	public UUID type() {
		return this.UUID(SPEC_TYPE);
	}


	public static Frame newBufferFrame(Slice data) {
        return ron.Parse.parseFrame(data);
    }

	public byte[] rest()  {
    	return Arrays.copyOfRange(this.Body.array(), this.Parser.pos, this.Body.length());
 	}

    // Split returns two frames: one from the start to the current pos (exclusive),
    // another from the current pos (incl) to the end. The right one is "stripped".
    public Pair<Frame, Frame> split2() {
        // TODO text vs binary
        Frame frame = this;
        Frame left = new Frame();
		left.Body = new Slice(Arrays.copyOfRange(frame.Body.array(), 0, frame.Parser.off));
		left = ron.Parser.parseFrame(left);
		Frame right = newBufferFrame(new Slice(new byte[128 + frame.Body.length() - frame.Parser.pos], 0));
        right.append(frame);
        right.appendBytes(frame.rest());
        return Pair.create(left, right);
    }

	public void appendAll(Frame i) {
		if (i.isEmpty()) {
			return;
		}
		while (!i.eof()) {
			append(i);
			i.next();
		}
	}

	public void appendFrame(Frame second) {
    	this.appendAll(second);
	}

	public void append(Frame other) {
		Frame frame = this;
		long flags = this.Serializer.Format;
		int start = this.Body.length();
		if (frame.Body.length() > 0 && (0 != (flags & FORMAT_OP_LINES)) || (0 != (flags & FORMAT_FRAME_LINES) && !other.isFramed())) {
			frame.Body = frame.Body.append('\n');
			if (0 != (flags & FORMAT_INDENT) && !other.isHeader()) {
				frame.Body = frame.Body.append("    ");
			}
		} else if (0 != (flags & FORMAT_HEADER_SPACE) && frame.isHeader()) {
			frame.Body = frame.Body.append(' ');
		}

		if (frame.atoms.length == 0) {
			frame.atoms = copyOfRange(frame._atoms, 0, 4);
		}
		frame.appendSpec(other);

		if (0 != (flags & FORMAT_GRID)) {
			int rest = 4 * 22 - frame.Body.length() - start;
			frame.Body = frame.Body.append( copyOfRange(Slice.toBytes(SPACES88), 0, rest));
		}

		frame.appendAtoms(other);

		int defaultTerm = TERM_REDUCED;
		if (frame.term == TERM_RAW) {
			defaultTerm = TERM_RAW;
		}

		if (other.term != defaultTerm || other.count() == 0) {
			frame.Body = frame.Body.append(TERM_PUNCT[other.term]);
		}

		if (other.atoms.length > frame.atoms.length) {
			copy(frame.atoms, copyOfRange(other.atoms, 0, frame.atoms.length));
			frame.atoms = append(frame.atoms, other.atoms[frame.atoms.length]);
		} else {
			copy(frame.atoms, other.atoms);
			frame.atoms = copyOfRange(frame.atoms, 0, other.atoms.length);
		}
		frame.term = other.term;
		frame.position++;

	}

	public int count() {
		return this.atoms.length - 4;
	}

	public String string() {
		return new String(this.Body.array(), StandardCharsets.UTF_8);
	}

	public int term() {
		return this.term;
	}

	public static <T> void copy(T[] dest, T[] src) {
    	System.arraycopy(src, 0, dest, 0, src.length);
	}

	public boolean isHeader() {
		return this.term() == TERM_HEADER;
	}

	public boolean isEmpty() {
		return this.Body.length() == 0;
	}

	public boolean isFramed() {
		return this.term() == TERM_REDUCED;
	}

	public void appendBytes(byte[] data) {
		this.Body = this.Body.append(data);
	}

	public void appendSpec(Frame other) {
		Frame frame = this;
		int start = frame.Body.length();
		long flags = frame.Serializer.Format;
		int skips = 0;
//		Atom[] spec = other.atoms[:4];
		Atom[] spec = copyOfRange(other.atoms, 0 ,4);
		Atom[] context = copyOfRange(frame.atoms, 0, 4);

		boolean do_grid = (flags & FORMAT_GRID) != 0;
		boolean do_space = (flags & FORMAT_SPACE) != 0;
		boolean do_noskip = (flags & FORMAT_NOSKIP) != 0;
		boolean do_redef = (flags & FORMAT_REDEFAULT) != 0;

		int k = 4;
		if (spec[SPEC_TYPE] == new Atom(COMMENT_UUID)) {
			k = 1;
		}

		for (int t = 0; t < k; t++) {
			if (do_grid) {
				int rest = t*22 - (frame.Body.length() - start);
				frame.Body = frame.Body.append(SPACES88.substring(0, rest));
			} else if (do_space && t > 0) {
				frame.Body = frame.Body.append(' ');
			}
			if (!do_noskip && spec[t].equals(context[t]) && (other.term == TERM_REDUCED || spec[t].equals(ZERO_UUID_ATOM))) {
				skips++;
				continue;
			}
			frame.Body = frame.Body.append(SPEC_PUNCT[t]);
			if (t > 0 && do_redef) {
				int ctxAt = 0;
				UUID ctxUUID = new UUID(spec[t-1]);
				int ctxPL = sharedPrefix(new UUID(spec[t]), ctxUUID);
				for (int i = 1; i < 4; i++) {
					int pl = sharedPrefix(new UUID(spec[t]), new UUID(context[i]));
					if (pl > ctxPL) {
						ctxPL = pl;
						ctxUUID = new UUID(context[i]);
						ctxAt = i;
					}
				}
				if (ctxAt != t) {
					frame.Body = frame.Body.append(REDEF_PUNCT[ctxAt]);
				}
				frame.appendUUID(new UUID(spec[t]), ctxUUID);
			} else {
				frame.appendUUID(new UUID(spec[t]), new UUID(context[t]));
			}
		}
		if (skips == 4) {
			frame.Body = frame.Body.append('@');
		}
	}

	public void appendAtoms(Frame other) {
		Frame frame = this;
		for (int i = 4; i < other.atoms.length; i++) {
			Atom a = other.atoms[i];
			switch (a.type()) {
				case ATOM_INT:
				{
					frame.Body = frame.Body.append(ATOM_INT_SEP);
					frame.Body = frame.Body.append(a.integer());
					break;
				}
				case ATOM_STRING:
				{
					frame.Body = frame.Body.append(ATOM_STRING_SEP);
					frame.Body = frame.Body.append(other.escString(i-4));
					frame.Body = frame.Body.append(ATOM_STRING_SEP);
					break;
				}
				case ATOM_FLOAT:
				{
					frame.Body = frame.Body.append(ATOM_FLOAT_SEP);
					frame.appendFloat(a);
					break;
				}
				case ATOM_UUID:
				{
					frame.Body = frame.Body.append(ATOM_UUID_SEP);
					frame.appendUUID(new UUID(a), ZERO_UUID); // TODO defaults
					break;
				}
				default: throw new RuntimeException(a.type() + "");
			}
		}
	}


	public void appendFloat(Atom a) {
    	Frame frame = this;
		if (a.uuid[0] == 0) {
			frame.Body = frame.Body.append( "0.0");
			return;
		}
		String intStr = String.format("%d", a.uuid[0]);
		int e = a.pow();
		if ((a.uuid[ORIGIN] & BIT60) != 0) {
			frame.Body = frame.Body.append( '-');
		}
		if (e < 0) { // neg e
			e = -e;
			int ip = intStr.length() - e;
			if (ip > 0) { // integer part
				frame.Body = frame.Body.append( intStr.substring(0, ip));
				frame.Body = frame.Body.append( ".");
				StringBuilder tail = new StringBuilder(intStr.substring(ip));
				while (tail.length() > 1 && tail.charAt(tail.length() -1) == '0') {
					tail = new StringBuilder(tail.substring(0, tail.length() - 1));
				}
				frame.Body = frame.Body.append( tail.toString());
			} else if (ip == 0) {
				frame.Body = frame.Body.append( "0.");
				frame.Body = frame.Body.append( intStr);
			} else {
				int de = 1 - ip;
				frame.Body = frame.Body.append( intStr.substring(0, 1));
				frame.Body = frame.Body.append( ".");
				if (intStr.length() > 1) {
					frame.Body = frame.Body.append( intStr.substring(1));
				} else {
					frame.Body = frame.Body.append( "0");
				}
				frame.Body = frame.Body.append( "e-");
				String exp = String.format("%d", de);
				frame.Body = frame.Body.append( exp);
			}
		} else {
			if (e + intStr.length() <= 10) {
				frame.Body = frame.Body.append( intStr);
				frame.Body = frame.Body.append( Arrays.copyOfRange(zeros, 0, e));
				frame.Body = frame.Body.append( ".0");
			} else {
				String exp = String.format("%d", e + intStr.length() - 1);
				frame.Body = frame.Body.append(intStr.charAt(0));
				frame.Body = frame.Body.append( ".");
				frame.Body = frame.Body.append( intStr.substring(1));
				frame.Body = frame.Body.append( "e+");
				frame.Body = frame.Body.append( exp);
			}
		}
	}

	public void appendUUID(UUID uuid, UUID context) {

		if (0 != (this.Serializer.Format & FORMAT_UNZIP)) {
			this.Body = formatUUID(this.Body, uuid);
		} else if (uuid != context) {
			this.Body = formatZipUUID(this.Body, uuid, context);
		}
	}

	public byte[] escString(int idx) {
		return this.atoms[idx+4].escString(this.Body);
	}

//	public static <T> T[] append(T[] a, T[] b) {
//		T[] c = (T[]) new Object[a.length + b.length];
//		System.arraycopy(a, 0, c, 0, a.length);
//		System.arraycopy(b, 0, c, a.length, b.length);
//		return (T[]) c;
//	}

	public static Atom[] append(Atom[] a, Atom b) {
//		final List<T> list = new ArrayList<>(Arrays.asList(a));
//		list.add(b);
//		return (T[]) list.toArray(new Object[list.size()]);
		Atom[] c = new Atom[a.length + 1];
		System.arraycopy(a, 0, c, 0, a.length);
		c[a.length] = b;
		return c;
	}

//	func (frame *Frame) Next() bool {
//		frame.Parse()
//		if frame.Parser.state == RON_error {
//			return false
//		}
//		if frame.Parser.streaming {
//			return frame.IsComplete()
//		}
//		return true
//	}


	public boolean next() {
        Frame frame = ron.Parser.parseFrame(this);
        if (frame.Parser.state == RON_error) {
            return false;
        }
        if (frame.Parser.streaming) {
            return IsComplete(frame);
        }
        return true;
    }

	public Frame rewind() {
		return Parse.parseFrame(this.Body);
	}

	public int len() {
		return this.Body.length();
	}

	// True if we are past the last op
	public boolean eof() {
		return this.Parser.state == RON_error;
	}


//    Cursor Begin () {
//        return null;
//    }
//
//    void Append (Op op) {}
}