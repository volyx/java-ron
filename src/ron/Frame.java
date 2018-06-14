package ron;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static ron.Atom.BIT60;
import static ron.Atom.ZERO_UUID_ATOM;
import static ron.Const.*;
import static ron.FrameAppend.*;
import static ron.Parser.*;
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
		this.Body = new Slice(new byte[0]);
    }
    public Frame(Atom[] atoms, int term) {
		this();
        this.atoms = atoms;
        this.term = term;
	}

    public static Frame openFrame(Slice data) {
        Frame frame = new Frame();
        frame.Body = data;
        return ron.Parser.parseFrame(frame);
    }

	public static Frame makeFormattedFrame(long format, int prealloc_bytes) {
		Frame ret = new Frame();
		ret.Body = new Slice(new byte[prealloc_bytes], 0);
		ret.Serializer =  new SerializeState(format);
		return ret;
	}

	public static Frame makeFrame(int prealloc_bytes) {
		Frame frame = new Frame();
		frame.Body = new Slice(new byte[prealloc_bytes], 0);
		return frame;
	}

	public static Frame makeStream(int prealloc_bytes) {
		Frame ret = new Frame();
		ret.Body = new Slice(new byte[prealloc_bytes], 0);
		ret.Parser.streaming = true;
		//ret.Parser.state = RON_start
		return ret;
	}

	public Spec spec() {
//		var ret [4]Atom
		return new Spec(copy(slice(this.atoms, 0, 4)));
//		return ret[:]
	}

//	func MakeFrame(prealloc_bytes int) (ret Frame) {
//		ret.Body = make([]byte, 0, prealloc_bytes)
//		return
//	}

	public static Frame parseStream(byte[] buf)  {
		Frame ret = Frame.makeFrame(1000 + buf.length);
		ret.appendBytes(buf);
		ret.Parser.streaming = true;
		ret.next();
		return ret;
	}

	public String opString() {
		Frame cur = makeFrame(128);
		cur.append(this);
		return new String(cur.Body.array(), StandardCharsets.UTF_8);
	}

	public UUID UUID(int idx) {
		return new UUID(this.atoms[idx]);
	}

	public Atom Atom(int i) {
		return this.atoms[i+4];
	}

	public long integer(int i) {
		return this.atoms[i+4].integer();
	}


	public UUID type() {
		return this.UUID(SPEC_TYPE);
	}


	public static Frame newBufferFrame(Slice data) {
        return ron.Parse.parseFrame(data);
    }

	public static Frame newFrame() {
		return newBufferFrame(new Slice(new byte[1024], 0));
	}


	public byte[] rest()  {
    	return Arrays.copyOfRange(this.Body.array(), this.Parser.pos, this.Body.length());
 	}

    // Split returns two frames: one from the start to the current pos (exclusive),
    // another from the current pos (incl) to the end. The right one is "stripped".
    public Pair<Frame, Frame> split2() {
    	Frame frame = this.clone();
        // TODO text vs binary
        Frame left = new Frame();
		left.Body = new Slice(Arrays.copyOfRange(frame.Body.array(), 0, frame.Parser.off));
		left = ron.Parser.parseFrame(left);
		Frame right = newBufferFrame(new Slice(new byte[128 + frame.Body.length() - frame.Parser.pos], 0));
        right.append(frame);
        right.appendBytes(frame.rest());
        return Pair.create(left, right);
    }

	public static Spec newSpec(UUID t, UUID o, UUID e, UUID l) {
		return new Spec(new Atom[] {new Atom(t), new Atom(o), new Atom(e), new Atom(l)});
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

	public Frame close() {
		return parseFrame(this);
	}

	public void append(Frame other) {
    	other = other.clone();
		long flags = this.Serializer.Format;
		int start = this.Body.length();
		if (this.Body.length() > 0 && ((0 != (flags & FORMAT_OP_LINES)) || (0 != (flags & FORMAT_FRAME_LINES) && !other.isFramed()))) {
			this.Body = this.Body.append('\n');
			if (0 != (flags & FORMAT_INDENT) && !other.isHeader()) {
				this.Body = this.Body.append("    ");
			}
		} else if (0 != (flags & FORMAT_HEADER_SPACE) && this.isHeader()) {
			this.Body = this.Body.append(' ');
		}

		if (this.atoms.length == 0) {
			this.atoms = slice(this._atoms, 0, 4);
		}
		this.appendSpec(other);

		if (0 != (flags & FORMAT_GRID)) {
			int rest = 4 * 22 - (this.Body.length() - start);
			this.Body = this.Body.append(Arrays.copyOfRange(Slice.toBytes(SPACES88), 0, rest));
		}

		this.appendAtoms(other);

		int defaultTerm = TERM_REDUCED;
		if (this.term == TERM_RAW) {
			defaultTerm = TERM_RAW;
		}

		if (other.term != defaultTerm || other.count() == 0) {
			this.Body = this.Body.append(TERM_PUNCT[other.term]);
		}

		if (other.atoms.length > this.atoms.length) {
			this.atoms = copy(slice(other.atoms, 0, this.atoms.length));
			this.atoms = append(this.atoms, other.atoms[this.atoms.length]);
		} else {
			this.atoms = copy(other.atoms);
			this.atoms = slice(this.atoms, 0, other.atoms.length);
		}
		this.term = other.term;
		this.position++;

	}

	public int count() {
		return this.atoms.length - 4;
	}

	public String string() {
		return new String(Arrays.copyOfRange(this.Body.array(), 0, this.Body.length()), StandardCharsets.UTF_8);
	}

	public int term() {
		return this.term;
	}

	public UUID object() {
		return this.UUID(SPEC_OBJECT);
	}
	public UUID event() {
		return this.UUID(SPEC_EVENT);
	}


	public UUID ref() {
		return this.UUID(SPEC_REF);
	}

	public boolean isHeader() {
		return this.term() == TERM_HEADER;
	}

	public boolean isQuery() {
		return this.term() == TERM_QUERY;
	}

	public boolean isComment() {
		return this.type().equals(COMMENT_UUID);
	}

	public boolean isEmpty() {
		return this.Body.length() == 0;
	}

	public boolean isFramed() {
		return this.term() == TERM_REDUCED;
	}

	public boolean isRaw() {
		return this.term() == TERM_RAW;
	}

	public boolean isFullState() {
		return this.isHeader() && this.ref().isZero();
	}

	public void appendBytes(byte[] data) {
		this.Body = this.Body.append(data);
	}

	public void appendSpec(Frame other) {
    	other = other.clone();
		int start = this.Body.length();
		long flags = this.Serializer.Format;
		int skips = 0;
//		Atom[] spec = other.atoms[:4];
		Atom[] spec = slice(other.atoms, 0 ,4);
		Atom[] context = slice(this.atoms, 0, 4);

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
				int rest = t*22 - (this.Body.length() - start);
				this.Body = this.Body.append(SPACES88.substring(0, rest));
			} else if (do_space && t > 0) {
				this.Body = this.Body.append(' ');
			}
			if (!do_noskip && spec[t].equals(context[t]) && (other.term == TERM_REDUCED || spec[t].equals(ZERO_UUID_ATOM))) {
				skips++;
				continue;
			}
			this.Body = this.Body.append(SPEC_PUNCT[t]);
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
					this.Body = this.Body.append(REDEF_PUNCT[ctxAt]);
				}
				this.appendUUID(new UUID(spec[t]), ctxUUID);
			} else {
				this.appendUUID(new UUID(spec[t]), new UUID(context[t]));
			}
		}
		if (skips == 4) {
			this.Body = this.Body.append('@');
		}
	}

	public void appendAtoms(Frame other) {
    	other = other.clone();
		for (int i = 4; i < other.atoms.length; i++) {
			Atom a = other.atoms[i];
			Objects.requireNonNull(a);
			switch (a.type()) {
				case ATOM_INT:
				{
					this.Body = this.Body.append(ATOM_INT_SEP);
					this.Body = this.Body.append(a.integer());
					break;
				}
				case ATOM_STRING:
				{
					this.Body = this.Body.append(ATOM_STRING_SEP);
					this.Body = this.Body.append(other.escString(i-4));
					this.Body = this.Body.append(ATOM_STRING_SEP);
					break;
				}
				case ATOM_FLOAT:
				{
					this.Body = this.Body.append(ATOM_FLOAT_SEP);
					this.appendFloat(a);
					break;
				}
				case ATOM_UUID:
				{
					this.Body = this.Body.append(ATOM_UUID_SEP);
					this.appendUUID(new UUID(a), ZERO_UUID); // TODO defaults
					break;
				}
				default: throw new RuntimeException(a.type() + "");
			}
		}
	}


	public void appendFloat(Atom a) {
    	a = a.clone();
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
    	uuid = uuid.clone(); context = context.clone();
		if (0 != (this.Serializer.Format & FORMAT_UNZIP)) {
			this.Body = formatUUID(this.Body, uuid);
		} else if (!uuid.equals(context)) {
			this.Body = formatZipUUID(this.Body, uuid, context);
		}
	}

	public byte[] escString(int idx) {
		return this.atoms[idx+4].escString(this.Body);
	}

	public String rawString(int idx) {
		Atom atom = this.atoms[idx+4];
		if (atom.Type() != ATOM_STRING) {
			return "";
		}
		return atom.rawString(this.Body.array());
	}

//	public static <T> T[] append(T[] a, T[] b) {
//		T[] c = (T[]) new Object[a.length + b.length];
//		System.arraycopy(a, 0, c, 0, a.length);
//		System.arraycopy(b, 0, c, a.length, b.length);
//		return (T[]) c;
//	}

	public void appendSpecValuesTerm(Spec spec, Atom[] values, int term) {
    	spec = spec.clone();
		Atom[] atoms = new Atom[values.length + 4 + 1];
		for (int i = 0; i < spec.spec.length; i++) {
			atoms[i] = spec.spec[i];
		}
		int j = 0;
		for (int i = spec.spec.length; i < spec.spec.length + values.length; i++) {
			atoms[i] = spec.spec[j++];
		}
//		atoms = append(atoms, spec...)
//		atoms = append(atoms, values...)
		Frame tmp = new Frame(atoms, term);
		this.append(tmp);
	}

	public void appendEmpty(Spec spec, int term) {
		spec = spec.clone();
//		Atom[] atoms = new Atom[6];
//		Atom[] atoms = new Atom[6];
		Atom[] atoms = copy(slice(spec.spec, 0, 4));
//		atoms = copy(spec);
//		spec.spec =
//		System.arraycopy(spec.spec, 0, atoms, 0, 4);
		Frame tmp = new Frame(atoms, term);
		this.append(tmp);
	}

	public void appendReduced(Frame other) {
		other = other.clone();
		var tmpTerm = other.term;
		other.term = TERM_REDUCED;
		this.append(other);
		other.term = tmpTerm;
	}

	public void appendEmptyReducedOp(Spec spec) {
    	spec = spec.clone();
		this.appendEmpty(spec, TERM_REDUCED);
	}

	public void appendReducedRef(UUID ref, Frame other) {
    	ref = ref.clone();
    	other = other.clone();
		Atom tmpRef = other.atoms[SPEC_REF];
		int tmpTerm = other.term;
		other.atoms[SPEC_REF] = new Atom(ref);
		other.term = TERM_REDUCED;
		this.append(other);
		other.atoms[SPEC_REF] = tmpRef;
		other.term =  tmpTerm;
	}

//	func (frame *Frame) AppendReducedOpInt(spec Spec, value int64) {
//		frame.AppendSpecValT(spec, NewIntegerAtom(value), TERM_REDUCED)
//	}
//
//	func (frame *Frame) AppendReducedOpUUID(spec Spec, value UUID) {
//		frame.AppendSpecValT(spec, NewUUIDAtom(value), TERM_REDUCED)
//	}
//
//	func (frame *Frame) AppendStateHeader(spec Spec) {
//		frame.AppendEmpty(spec, TERM_HEADER)
//	}

	public void appendStateHeader(Spec spec) {
		spec = spec.clone();
		appendEmpty(spec, TERM_HEADER);
	}

	public void appendStateHeaderValues(UUID rdt, UUID obj, UUID ev, UUID ref, Atom[] values) {
		Spec spec = newSpec(rdt, obj, ev, ref);
		this.appendSpecValuesTerm(spec, values, TERM_HEADER);
	}

	public static Atom[] append(Atom[] a, Atom b) {
    	Objects.requireNonNull(a);
    	Objects.requireNonNull(b);
//		System.out.println("append");
		return append(a, new Atom[]{b});
	}

	public static UUID[] append(UUID[] a, UUID b) {
    	Objects.requireNonNull(a);
    	Objects.requireNonNull(b);
		return append(a, new UUID[]{b});
	}


	public static UUID[] append(UUID[] a, UUID[] b) {
		UUID[] c = new UUID[a.length + b.length];
		for (int i = 0;i <= a.length - 1; i++) {
			Objects.requireNonNull(a[i]);
			c[i] = a[i];
		}
		for (int j = 0;j <= b.length - 1; j++) {
			Objects.requireNonNull(b[j]);
			c[a.length + j] = b[j];
		}
		return c;
	}


	public static Frame[] append(Frame[] a, Frame b) {
		Objects.requireNonNull(a);
		Objects.requireNonNull(b);
		return append(a, new Frame[]{b});
	}

	public static Frame[] append(Frame[] a, Frame[] b) {
		Frame[] c = new Frame[a.length + b.length];
		for (int i = 0;i <= a.length - 1; i++) {
			Objects.requireNonNull(a[i]);
			c[i] = a[i];
		}
		for (int j = 0;j <= b.length - 1; j++) {
			Objects.requireNonNull(b[j]);
			c[a.length + j] = b[j];
		}
		return c;
	}

	public static Atom[] append(Atom[] a, Atom[] b) {
//		System.out.println("input " + Arrays.toString(a) + " " + Arrays.toString(b));
		Atom[] c = new Atom[a.length + b.length];
		for (int i = 0;i <= a.length - 1; i++) {
			Objects.requireNonNull(a[i]);
//			c[i] = new Atom(a[i]);
			c[i] = a[i];
		}
		for (int j = 0;j <= b.length - 1; j++) {
			Objects.requireNonNull(b[j]);
//			c[a.length + j] = new Atom(b[j]);
			c[a.length + j] = b[j];
		}

//		System.out.println("output " + Arrays.toString(c));
		return c;
	}

	public static Atom[] copy(Atom[] b) {
		Atom[] a = new Atom[b.length];
		for (int i = 0;i <= b.length - 1; i++) {
			Objects.requireNonNull(b[i]);
			a[i] = b[i].clone();
		}
		return a;
	}

	public static Frame[] copy(Frame[] b) {
		Frame[] a = new Frame[b.length];
		for (int i = 0;i <= b.length - 1; i++) {
			Objects.requireNonNull(b[i]);
			a[i] = b[i].clone();
		}
		return a;
	}

	public static Atom[] slice(Atom[] original, int from, int to) {
		if (from > to) {
			throw new IllegalStateException();
		}
		Atom[] c = new Atom[to - from];
		int j = 0;
		while (from < to) {
			Objects.requireNonNull(original[from]);
//			c[j] = new Atom(original[from]);
			c[j] = original[from];
			from++;
			j++;
		}
		return c;
	}

	public static Frame[] slice(Frame[] original, int from, int to) {
		if (from > to) {
			throw new IllegalStateException();
		}
		Frame[] c = new Frame[to - from];
		int j = 0;
		while (from < to) {
//			Objects.requireNonNull(original[from]);
			c[j] = original[from];
			from++;
			j++;
		}
		return c;
	}

	public boolean next() {
        ron.Parser.parseFrame(this);
        if (this.Parser.state == RON_error) {
            return false;
        }
        if (this.Parser.streaming) {
            return IsComplete(this);
        }
        return true;
    }

	public Frame rewind() {
    	Frame frame = this.clone();
		return Parse.parseFrame(frame.Body);
	}

	public Frame reformat(long format) {
		Frame ret = Frame.makeFrame(this.len());
		ret.Serializer.Format = format;
		Frame clone = this.clone();
		for (;!clone.eof();) {
			ret.append(clone);
			clone.next();
		}
		return ret.rewind();
	}

	public int len() {
		return this.Body.length();
	}

	// True if we are past the last op
	public boolean eof() {
		return this.Parser.state == RON_error;
	}

	public int offset() {
		return this.Parser.pos;
	}

	// IsEqual checks for single-op equality
	public boolean compare(Frame other) {
    	int at = 0; boolean eq = false;
		if (this.eof() || other.eof()) {
			return this.eof() && other.eof();// 0
		}
		if (this.term() != other.term()) {
			return false; //, -1
		}
		for (int i = 0; i < 4; i++) { // FIXME strings are difficult
			if (!this.atoms[i].equals(other.atoms[i])) {
				return false; //, i
			}
		}
		if (this.count() != other.count()) {
			return false;//, -2
		}
		return true;//, 0
	}

	// (eq bool, op, at int)
	public boolean compareAll(Frame other)  {
    	int op = 0; int at = 0;
		boolean eq = false;
		for (;!this.eof() && !other.eof();) {
			eq = this.compare(other);
			if (!eq) {
				return eq;
			}
			op++;
			this.next();
			other.next();
		}
		if (!this.eof() || !other.eof()) {
			eq = false;
			return eq;
		}
		return eq;
	}

	public String getString(int i) {
		return this.rawString(i);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Frame other = (Frame) o;
		return compareAll(other);

//		func (frame Frame) Equal(other Frame) bool {
//			eq, _, _ := frame.CompareAll(other)
//			return eq
//		}
	}

	// overall, serialized batches are used in rare cases
// (delivery fails, cross-key transactions)
// hence, we don't care about performance that much
// still, may consider explicit-length formats at some point
	public Batch split() {
    	Frame frame = this.clone();
		Batch ret = new Batch();
		int i = 0;
		for (;!frame.eof();) {
			Frame next = newFrame();
//			 System.out.println(i++ + " 1 " + next.string() + "\n" + Arrays.asList(frame._atoms));
			next.append(frame);
//			 System.out.println(i++ + " 2 " + next.string() + "\n" + Arrays.asList(frame._atoms));
			frame.next();
//			 System.out.println(i++ + " 3 " + next.string()+ "\n" + Arrays.asList(frame._atoms));
			for (;!frame.eof() && frame.term() == TERM_REDUCED;) {
//				 System.out.println(i++ + " 4 " + next.string() + "\n" + Arrays.asList(frame._atoms));
				next.append(frame);
//				System.out.println(i++ + " 5 " + next.string() + Arrays.asList(next.atoms)+ "\n" + Arrays.asList(this.atoms));
				frame.next();
//				System.out.println(i++ + " 6 " + next.string() + Arrays.asList(next.atoms) + "\n" + Arrays.asList(this.atoms));
			}
			ret = ret.append(next.rewind());
		}
		return ret;
	}

	public Frame clone() {
		Frame clone = new Frame();
		clone.Parser = new ParserState(this.Parser);
		clone.Serializer = new SerializeState(this.Serializer);
		clone.term = this.term;
		clone.binary = this.binary;
		clone._atoms = copy(this._atoms);
		clone.atoms = copy(this.atoms);
		clone.position = this.position;
		int l = this.Body.length();
		// prevent from appending to the same buffer
//		clone.Body = this.Body[0:l:l];
		clone.Body = new Slice(Arrays.copyOfRange(this.Body.array(), 0, this.Body.array().length), l);
		return clone;
	}

	public long origin() {
		return this.atoms[SPEC_EVENT].uuid[1];
	}

	public long time() {
    	return this.atoms[SPEC_EVENT].uuid[0];
	}

	public Atom[] atoms() {
		return slice(this.atoms, 0, this.atoms.length);
	}

	@Override
	public String toString() {return this.string();
	}
}