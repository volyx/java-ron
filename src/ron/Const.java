package ron;

/// tweak abc2java
/// re /(\w+)\s+(\S+)\s+(.*)/_PUNCT $1 $2\n_ENUM $1 $3\n_SEPS $1 $2 $3\n/
/// fn /_PUNCT (\w+) (.*)/ (s,enm,seps) => { return "\tstatic final byte[] "+enm+"_PUNCT = \""+seps.replace(/\\/,"\\\\")+'".getBytes(StandardCharsets.UTF_8);' }
/// fn /_ENUM (\w+) (.*)/ (s,enm,vals)=>{ var i = 0; return vals.split(/\s+/).map(name=>'\tpublic static final int '+enm+"_"+name+" = " + i++ ).join(';\n') + ";\n" }
/// fn /_SEPS (\w+) (\S+) (.*)/ (s,enm,sepstr,names) => { seps=sepstr.match(/./g).reverse(); return names.split(/\s+/g).map(name=>"\tstatic final char "+enm+"_"+name+"_SEP = \'"+seps.pop().replace(/([\\'])/,"\\$1")+"';").join('\n') }
/// end


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Const {
/// paste ABC [0755330a]
/// use abc2java [6740edb7]
	static final byte[] SPEC_PUNCT = "*#@:".getBytes(StandardCharsets.UTF_8);
	public static final int SPEC_TYPE = 0;
	public static final int SPEC_OBJECT = 1;
	public static final int SPEC_EVENT = 2;
	public static final int SPEC_REF = 3;

	static final char SPEC_TYPE_SEP = '*';
	static final char SPEC_OBJECT_SEP = '#';
	static final char SPEC_EVENT_SEP = '@';
	static final char SPEC_REF_SEP = ':';

	static final byte[] UUID_PUNCT = "$%+-".getBytes(StandardCharsets.UTF_8);
	public static final int UUID_NAME = 0;
	public static final int UUID_HASH = 1;
	public static final int UUID_EVENT = 2;
	public static final int UUID_DERIVED = 3;

	static final char UUID_NAME_SEP = '$';
	static final char UUID_HASH_SEP = '%';
	static final char UUID_EVENT_SEP = '+';
	static final char UUID_DERIVED_SEP = '-';

	static final byte[] ATOM_PUNCT = ">='^".getBytes(StandardCharsets.UTF_8);
	public static final int ATOM_UUID = 0;
	public static final int ATOM_INT = 1;
	public static final int ATOM_STRING = 2;
	public static final int ATOM_FLOAT = 3;

	static final char ATOM_UUID_SEP = '>';
	static final char ATOM_INT_SEP = '=';
	static final char ATOM_STRING_SEP = '\'';
	static final char ATOM_FLOAT_SEP = '^';

	static final byte[] TERM_PUNCT = ";,!?".getBytes(StandardCharsets.UTF_8);
	public static final int TERM_RAW = 0;
	public static final int TERM_REDUCED = 1;
	public static final int TERM_HEADER = 2;
	public static final int TERM_QUERY = 3;

	static final char TERM_RAW_SEP = ';';
	static final char TERM_REDUCED_SEP = ',';
	static final char TERM_HEADER_SEP = '!';
	static final char TERM_QUERY_SEP = '?';

	static final byte[] REDEF_PUNCT = "`\\|/".getBytes(StandardCharsets.UTF_8);
	public static final int REDEF_PREV = 0;
	public static final int REDEF_OBJECT = 1;
	public static final int REDEF_EVENT = 2;
	public static final int REDEF_REF = 3;

	static final char REDEF_PREV_SEP = '`';
	static final char REDEF_OBJECT_SEP = '\\';
	static final char REDEF_EVENT_SEP = '|';
	static final char REDEF_REF_SEP = '/';

	static final byte[] PREFIX_PUNCT = "([{}])".getBytes(StandardCharsets.UTF_8);
	public static final int PREFIX_PRE4 = 0;
	public static final int PREFIX_PRE5 = 1;
	public static final int PREFIX_PRE6 = 2;
	public static final int PREFIX_PRE7 = 3;
	public static final int PREFIX_PRE8 = 4;
	public static final int PREFIX_PRE9 = 5;

	static final char PREFIX_PRE4_SEP = '(';
	static final char PREFIX_PRE5_SEP = '[';
	static final char PREFIX_PRE6_SEP = '{';
	static final char PREFIX_PRE7_SEP = '}';
	static final char PREFIX_PRE8_SEP = ']';
	static final char PREFIX_PRE9_SEP = ')';

	static final byte[] BASE_PUNCT = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~".getBytes(StandardCharsets.UTF_8);
	public static final int BASE_0 = 0;
	public static final int BASE_1 = 1;
	public static final int BASE_2 = 2;
	public static final int BASE_3 = 3;
	public static final int BASE_4 = 4;
	public static final int BASE_5 = 5;
	public static final int BASE_6 = 6;
	public static final int BASE_7 = 7;
	public static final int BASE_8 = 8;
	public static final int BASE_9 = 9;
	public static final int BASE_10 = 10;
	public static final int BASE_11 = 11;
	public static final int BASE_12 = 12;
	public static final int BASE_13 = 13;
	public static final int BASE_14 = 14;
	public static final int BASE_15 = 15;
	public static final int BASE_16 = 16;
	public static final int BASE_17 = 17;
	public static final int BASE_18 = 18;
	public static final int BASE_19 = 19;
	public static final int BASE_20 = 20;
	public static final int BASE_21 = 21;
	public static final int BASE_22 = 22;
	public static final int BASE_23 = 23;
	public static final int BASE_24 = 24;
	public static final int BASE_25 = 25;
	public static final int BASE_26 = 26;
	public static final int BASE_27 = 27;
	public static final int BASE_28 = 28;
	public static final int BASE_29 = 29;
	public static final int BASE_30 = 30;
	public static final int BASE_31 = 31;
	public static final int BASE_32 = 32;
	public static final int BASE_33 = 33;
	public static final int BASE_34 = 34;
	public static final int BASE_35 = 35;
	public static final int BASE_36 = 36;
	public static final int BASE_37 = 37;
	public static final int BASE_38 = 38;
	public static final int BASE_39 = 39;
	public static final int BASE_40 = 40;
	public static final int BASE_41 = 41;
	public static final int BASE_42 = 42;
	public static final int BASE_43 = 43;
	public static final int BASE_44 = 44;
	public static final int BASE_45 = 45;
	public static final int BASE_46 = 46;
	public static final int BASE_47 = 47;
	public static final int BASE_48 = 48;
	public static final int BASE_49 = 49;
	public static final int BASE_50 = 50;
	public static final int BASE_51 = 51;
	public static final int BASE_52 = 52;
	public static final int BASE_53 = 53;
	public static final int BASE_54 = 54;
	public static final int BASE_55 = 55;
	public static final int BASE_56 = 56;
	public static final int BASE_57 = 57;
	public static final int BASE_58 = 58;
	public static final int BASE_59 = 59;
	public static final int BASE_60 = 60;
	public static final int BASE_61 = 61;
	public static final int BASE_62 = 62;
	public static final int BASE_63 = 63;

	static final char BASE_0_SEP = '0';
	static final char BASE_1_SEP = '1';
	static final char BASE_2_SEP = '2';
	static final char BASE_3_SEP = '3';
	static final char BASE_4_SEP = '4';
	static final char BASE_5_SEP = '5';
	static final char BASE_6_SEP = '6';
	static final char BASE_7_SEP = '7';
	static final char BASE_8_SEP = '8';
	static final char BASE_9_SEP = '9';
	static final char BASE_10_SEP = 'A';
	static final char BASE_11_SEP = 'B';
	static final char BASE_12_SEP = 'C';
	static final char BASE_13_SEP = 'D';
	static final char BASE_14_SEP = 'E';
	static final char BASE_15_SEP = 'F';
	static final char BASE_16_SEP = 'G';
	static final char BASE_17_SEP = 'H';
	static final char BASE_18_SEP = 'I';
	static final char BASE_19_SEP = 'J';
	static final char BASE_20_SEP = 'K';
	static final char BASE_21_SEP = 'L';
	static final char BASE_22_SEP = 'M';
	static final char BASE_23_SEP = 'N';
	static final char BASE_24_SEP = 'O';
	static final char BASE_25_SEP = 'P';
	static final char BASE_26_SEP = 'Q';
	static final char BASE_27_SEP = 'R';
	static final char BASE_28_SEP = 'S';
	static final char BASE_29_SEP = 'T';
	static final char BASE_30_SEP = 'U';
	static final char BASE_31_SEP = 'V';
	static final char BASE_32_SEP = 'W';
	static final char BASE_33_SEP = 'X';
	static final char BASE_34_SEP = 'Y';
	static final char BASE_35_SEP = 'Z';
	static final char BASE_36_SEP = '_';
	static final char BASE_37_SEP = 'a';
	static final char BASE_38_SEP = 'b';
	static final char BASE_39_SEP = 'c';
	static final char BASE_40_SEP = 'd';
	static final char BASE_41_SEP = 'e';
	static final char BASE_42_SEP = 'f';
	static final char BASE_43_SEP = 'g';
	static final char BASE_44_SEP = 'h';
	static final char BASE_45_SEP = 'i';
	static final char BASE_46_SEP = 'j';
	static final char BASE_47_SEP = 'k';
	static final char BASE_48_SEP = 'l';
	static final char BASE_49_SEP = 'm';
	static final char BASE_50_SEP = 'n';
	static final char BASE_51_SEP = 'o';
	static final char BASE_52_SEP = 'p';
	static final char BASE_53_SEP = 'q';
	static final char BASE_54_SEP = 'r';
	static final char BASE_55_SEP = 's';
	static final char BASE_56_SEP = 't';
	static final char BASE_57_SEP = 'u';
	static final char BASE_58_SEP = 'v';
	static final char BASE_59_SEP = 'w';
	static final char BASE_60_SEP = 'x';
	static final char BASE_61_SEP = 'y';
	static final char BASE_62_SEP = 'z';
	static final char BASE_63_SEP = '~';

	static final byte[] FRAME_PUNCT = ".".getBytes(StandardCharsets.UTF_8);
	public static final int FRAME_TERM = 0;

	static final char FRAME_TERM_SEP = '.';


/// end

	public static final int UUID_NAME_TRANSCENDENT = 0;
	public static final int UUID_NAME_ISBN = 1;

	public static final String BASE64 = new String(BASE_PUNCT, StandardCharsets.UTF_8);

	public static final int ABC[] = new int[128];
	public static final long[] IS_BASE = new long[4];

	public static void initABC(byte[] punct) {
		for (int i = 0; i < punct.length; i++) {
			int l = punct[i];
			ABC[l] = i;
		}
	}

	static {
		for (int i = 0; i < 128; i++) {
			ABC[i] = 255;
		}

		for (int i = 0; i < BASE_PUNCT.length; i++) {
			int li = BASE_PUNCT[i];
			ABC[li] = i;
			IS_BASE[li >>> 6] |= 1L << (li & 63);
		}

		initABC(PREFIX_PUNCT);
		initABC(TERM_PUNCT);
		initABC(UUID_PUNCT);
		initABC(SPEC_PUNCT);
	}
}

