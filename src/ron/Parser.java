
// line 1 "ragel/java-parser.rl"
package ron;


import java.util.Arrays;
import static ron.Const.*;
import static ron.Frame.append;
import static ron.UUID.UUID_NAME_FLAG;
import static ron.UUID.ZERO_UUID;


// Parse consumes one op from data[], unless the buffer ends earlier.
// Fills atoms[]
public class Parser {

// The parser reached end-of-input (in block mode) or
// the closing dot (in streaming mode) successfully.
// The rest of the input is frame.Rest()
public static final int RON_FULL_STOP = -1;

// half
public static final int VALUE = 0;
public static final int	ORIGIN = 1;


// line 25 "ragel/java-parser.rl"

// line 30 "java/src/ron/Parser.java"
private static byte[] init__RON_actions_0()
{
	return new byte [] {
	    0,    1,    2,    1,    6,    1,    7,    1,    8,    1,   12,    1,
	   15,    1,   16,    1,   19,    1,   22,    1,   24,    1,   25,    1,
	   26,    1,   28,    1,   29,    1,   36,    1,   38,    2,    0,    9,
	    2,    5,    1,    2,    6,    9,    2,    6,   10,    2,   14,   13,
	    2,   16,   15,    2,   17,   18,    2,   17,   19,    2,   20,   16,
	    2,   21,   22,    2,   21,   23,    2,   27,   16,    2,   28,   29,
	    2,   31,   16,    2,   33,   34,    2,   33,   36,    2,   35,   34,
	    2,   36,   38,    3,    0,    4,    1,    3,    5,    3,    2,    3,
	    6,    5,    1,    3,    6,   14,   13,    3,    6,   31,   16,    3,
	    7,   14,   13,    3,    7,   31,   16,    3,   14,   37,   34,    3,
	   14,   37,   36,    3,   16,   33,   34,    3,   16,   33,   36,    3,
	   20,   16,   15,    3,   27,   16,   15,    3,   30,    0,    9,    3,
	   31,   16,   15,    3,   33,   36,   38,    3,   35,   11,   13,    3,
	   35,   32,   15,    3,   36,   35,   34,    4,    0,    4,    3,    2,
	    4,    6,   10,   14,   13,    4,    6,   10,   31,   16,    4,    6,
	   14,   37,   34,    4,    6,   14,   37,   36,    4,    6,   31,   16,
	   15,    4,    7,   14,   37,   34,    4,    7,   14,   37,   36,    4,
	    7,   31,   16,   15,    4,   14,   37,   32,   15,    4,   14,   37,
	   36,   38,    4,   16,   33,   36,   38,    4,   20,   16,   33,   34,
	    4,   20,   16,   33,   36,    4,   27,   16,   33,   34,    4,   27,
	   16,   33,   36,    4,   30,    0,    4,    1,    4,   31,   16,   33,
	   34,    4,   31,   16,   33,   36,    4,   36,   35,   11,   13,    4,
	   36,   35,   32,   15,    5,    6,   10,   14,   37,   34,    5,    6,
	   10,   14,   37,   36,    5,    6,   10,   31,   16,   15,    5,    6,
	   14,   37,   32,   15,    5,    6,   14,   37,   36,   38,    5,    6,
	   31,   16,   33,   34,    5,    6,   31,   16,   33,   36,    5,    7,
	   14,   37,   32,   15,    5,    7,   14,   37,   36,   38,    5,    7,
	   31,   16,   33,   34,    5,    7,   31,   16,   33,   36,    5,   20,
	   16,   33,   36,   38,    5,   27,   16,   33,   36,   38,    5,   30,
	    0,    4,    3,    2,    5,   31,   16,   33,   36,   38,    5,   33,
	   36,   35,   11,   13,    6,    6,   10,   14,   37,   32,   15,    6,
	    6,   10,   14,   37,   36,   38,    6,    6,   10,   31,   16,   33,
	   34,    6,    6,   10,   31,   16,   33,   36,    6,    6,   31,   16,
	   33,   36,   38,    6,    7,   31,   16,   33,   36,   38,    6,   16,
	   33,   36,   35,   11,   13,    7,    6,   10,   31,   16,   33,   36,
	   38,    7,   20,   16,   33,   36,   35,   11,   13,    7,   27,   16,
	   33,   36,   35,   11,   13,    7,   31,   16,   33,   36,   35,   11,
	   13,    8,    6,   31,   16,   33,   36,   35,   11,   13,    8,    7,
	   31,   16,   33,   36,   35,   11,   13,    9,    6,   10,   31,   16,
	   33,   36,   35,   11,   13
	};
}

private static final byte _RON_actions[] = init__RON_actions_0();


private static short[] init__RON_key_offsets_0()
{
	return new short [] {
	    0,    0,   15,   20,   25,   32,   34,   55,   62,   64,   67,   69,
	   73,   75,   77,   93,  109,  143,  173,  189,  205,  221,  221,  239,
	  269,  293,  313,  331,  356,  390,  425,  459,  483,  508,  542,  577,
	  611
	};
}

private static final short _RON_key_offsets[] = init__RON_key_offsets_0();


private static char[] init__RON_trans_keys_0()
{
	return new char [] {
	   32,   33,   35,   39,   42,   44,   58,   59,   61,   62,   63,   64,
	   94,    9,   13,   10,   13,   34,   39,   92,   10,   13,   34,   39,
	   92,   32,   43,   45,    9,   13,   48,   57,   48,   57,   32,   43,
	   45,   91,   93,   95,  123,  125,  126,    9,   13,   36,   37,   40,
	   41,   48,   57,   65,   90,   97,  122,   32,   43,   45,    9,   13,
	   48,   57,   48,   57,   46,   48,   57,   48,   57,   43,   45,   48,
	   57,   48,   57,   10,   13,   32,   33,   35,   39,   42,   44,   46,
	   58,   59,   61,   62,   63,   64,   94,    9,   13,   32,   33,   35,
	   39,   42,   44,   46,   58,   59,   61,   62,   63,   64,   94,    9,
	   13,   32,   33,   35,   39,   42,   44,   46,   58,   59,   61,   62,
	   63,   64,   91,   93,   94,   96,  123,  125,  126,    9,   13,   36,
	   37,   40,   41,   43,   45,   48,   57,   65,   90,   95,  122,   32,
	   33,   35,   39,   42,   44,   46,   58,   59,   61,   62,   63,   64,
	   91,   93,   94,   95,  123,  125,  126,    9,   13,   40,   41,   48,
	   57,   65,   90,   97,  122,   32,   33,   35,   39,   42,   44,   46,
	   58,   59,   61,   62,   63,   64,   94,    9,   13,   32,   33,   35,
	   39,   42,   44,   46,   58,   59,   61,   62,   63,   64,   94,    9,
	   13,   32,   33,   35,   39,   42,   44,   46,   58,   59,   61,   62,
	   63,   64,   94,    9,   13,   32,   33,   35,   39,   42,   44,   46,
	   58,   59,   61,   62,   63,   64,   94,    9,   13,   48,   57,   32,
	   33,   35,   39,   42,   44,   46,   58,   59,   61,   62,   63,   64,
	   91,   93,   94,   95,  123,  125,  126,    9,   13,   40,   41,   48,
	   57,   65,   90,   97,  122,   32,   33,   35,   39,   42,   44,   46,
	   58,   59,   61,   62,   63,   64,   94,   95,  126,    9,   13,   48,
	   57,   65,   90,   97,  122,   32,   33,   35,   39,   42,   44,   46,
	   58,   59,   61,   62,   63,   64,   69,   94,  101,    9,   13,   48,
	   57,   32,   33,   35,   39,   42,   44,   46,   58,   59,   61,   62,
	   63,   64,   94,    9,   13,   48,   57,   32,   33,   35,   39,   42,
	   44,   46,   47,   58,   59,   61,   62,   63,   64,   94,   95,  126,
	    9,   13,   48,   57,   65,   90,   97,  122,   32,   33,   35,   39,
	   42,   44,   46,   58,   59,   61,   62,   63,   64,   91,   93,   94,
	   95,  123,  125,  126,    9,   13,   36,   37,   40,   41,   43,   45,
	   48,   57,   65,   90,   97,  122,   32,   33,   35,   39,   42,   44,
	   46,   47,   58,   59,   61,   62,   63,   64,   91,   93,   94,   95,
	  123,  125,  126,    9,   13,   36,   37,   40,   41,   43,   45,   48,
	   57,   65,   90,   97,  122,   32,   33,   35,   39,   42,   44,   46,
	   58,   59,   61,   62,   63,   64,   91,   93,   94,   95,  123,  125,
	  126,    9,   13,   36,   37,   40,   41,   43,   45,   48,   57,   65,
	   90,   97,  122,   32,   33,   35,   39,   42,   44,   46,   58,   59,
	   61,   62,   63,   64,   94,   95,  126,    9,   13,   48,   57,   65,
	   90,   97,  122,   32,   33,   35,   39,   42,   44,   46,   47,   58,
	   59,   61,   62,   63,   64,   94,   95,  126,    9,   13,   48,   57,
	   65,   90,   97,  122,   32,   33,   35,   39,   42,   44,   46,   58,
	   59,   61,   62,   63,   64,   91,   93,   94,   95,  123,  125,  126,
	    9,   13,   36,   37,   40,   41,   43,   45,   48,   57,   65,   90,
	   97,  122,   32,   33,   35,   39,   42,   44,   46,   47,   58,   59,
	   61,   62,   63,   64,   91,   93,   94,   95,  123,  125,  126,    9,
	   13,   36,   37,   40,   41,   43,   45,   48,   57,   65,   90,   97,
	  122,   32,   33,   35,   39,   42,   44,   46,   58,   59,   61,   62,
	   63,   64,   91,   93,   94,   95,  123,  125,  126,    9,   13,   36,
	   37,   40,   41,   43,   45,   48,   57,   65,   90,   97,  122,   32,
	   33,   35,   39,   42,   44,   46,   58,   59,   61,   62,   63,   64,
	   91,   93,   94,   95,  123,  125,  126,    9,   13,   36,   37,   40,
	   41,   43,   45,   48,   57,   65,   90,   97,  122,    0
	};
}

private static final char _RON_trans_keys[] = init__RON_trans_keys_0();


private static byte[] init__RON_single_lengths_0()
{
	return new byte [] {
	    0,   13,    5,    5,    3,    0,    9,    3,    0,    1,    0,    2,
	    0,    2,   14,   14,   20,   20,   14,   14,   14,    0,   14,   20,
	   16,   16,   14,   17,   20,   21,   20,   16,   17,   20,   21,   20,
	   20
	};
}

private static final byte _RON_single_lengths[] = init__RON_single_lengths_0();


private static byte[] init__RON_range_lengths_0()
{
	return new byte [] {
	    0,    1,    0,    0,    2,    1,    6,    2,    1,    1,    1,    1,
	    1,    0,    1,    1,    7,    5,    1,    1,    1,    0,    2,    5,
	    4,    2,    2,    4,    7,    7,    7,    4,    4,    7,    7,    7,
	    7
	};
}

private static final byte _RON_range_lengths[] = init__RON_range_lengths_0();


private static short[] init__RON_index_offsets_0()
{
	return new short [] {
	    0,    0,   15,   21,   27,   33,   35,   51,   57,   59,   62,   64,
	   68,   70,   73,   89,  105,  133,  159,  175,  191,  207,  208,  225,
	  251,  272,  291,  308,  330,  358,  387,  415,  436,  458,  486,  515,
	  543
	};
}

private static final short _RON_index_offsets[] = init__RON_index_offsets_0();


private static short[] init__RON_indicies_0()
{
	return new short [] {
	    0,    2,    3,    4,    3,    2,    3,    2,    5,    6,    2,    3,
	    7,    0,    1,    1,    1,    1,    9,   10,    8,    1,    1,    1,
	   12,   13,   11,   14,   15,   15,   14,   16,    1,   17,    1,   18,
	   19,   19,   20,   20,   21,   20,   20,   21,   18,   19,   20,   21,
	   21,   21,    1,   22,   23,   23,   22,   24,    1,   25,    1,   26,
	   25,    1,   27,    1,   28,   28,   29,    1,   29,    1,    1,    1,
	   11,    0,    2,    3,    4,    3,    2,   30,    3,    2,    5,    6,
	    2,    3,    7,    0,    1,   31,   32,   33,   34,   33,   32,   35,
	   33,   32,   36,   37,   32,   33,   38,   31,    1,   39,   40,   41,
	   43,   41,   40,   45,   41,   40,   47,   48,   40,   41,   44,   44,
	   49,   50,   44,   44,   46,   39,   42,   44,   42,   46,   46,   46,
	    1,   51,   40,   41,   43,   41,   40,   45,   41,   40,   47,   48,
	   40,   41,   52,   52,   49,   53,   52,   52,   53,   51,   52,   53,
	   53,   53,    1,   51,   40,   41,   43,   41,   40,   45,   41,   40,
	   47,   48,   40,   41,   49,   51,    1,   54,   55,   56,   57,   56,
	   55,   58,   56,   55,   59,   60,   55,   56,   61,   54,    1,   62,
	   63,   64,   65,   64,   63,   66,   64,   63,   67,   68,   63,   64,
	   69,   62,    1,    1,   70,   71,   72,   73,   72,   71,   74,   72,
	   71,   75,   76,   71,   72,   77,   70,   17,    1,   78,   79,   80,
	   81,   80,   79,   83,   80,   79,   85,   86,   79,   80,   82,   82,
	   87,   84,   82,   82,   84,   78,   82,   84,   84,   84,    1,   88,
	   89,   90,   91,   90,   89,   92,   90,   89,   94,   95,   89,   90,
	   96,   93,   93,   88,   93,   93,   93,    1,   97,   98,   99,  100,
	   99,   98,  101,   99,   98,  102,  103,   98,   99,  104,  105,  104,
	   97,   27,    1,   97,   98,   99,  100,   99,   98,  101,   99,   98,
	  102,  103,   98,   99,  105,   97,   29,    1,   88,   89,   90,   91,
	   90,   89,   92,  106,   90,   89,   94,   95,   89,   90,   96,   93,
	   93,   88,   93,   93,   93,    1,  107,  108,  109,  111,  109,  108,
	  113,  109,  108,  115,  116,  108,  109,  112,  112,  117,  114,  112,
	  112,  114,  107,  110,  112,  110,  114,  114,  114,    1,  118,  119,
	  120,  121,  120,  119,  122,  123,  120,  119,  125,  126,  119,  120,
	  112,  112,  127,  124,  112,  112,  124,  118,  110,  112,  110,  124,
	  124,  124,    1,  118,  119,  120,  121,  120,  119,  122,  120,  119,
	  125,  126,  119,  120,  112,  112,  127,  124,  112,  112,  124,  118,
	  110,  112,  110,  124,  124,  124,    1,  128,  129,  130,  131,  130,
	  129,  132,  130,  129,  134,  135,  129,  130,  136,  133,  133,  128,
	  133,  133,  133,    1,  128,  129,  130,  131,  130,  129,  132,  137,
	  130,  129,  134,  135,  129,  130,  136,  133,  133,  128,  133,  133,
	  133,    1,  138,  139,  140,  142,  140,  139,  144,  140,  139,  146,
	  147,  139,  140,  143,  143,  148,  145,  143,  143,  145,  138,  141,
	  143,  141,  145,  145,  145,    1,  149,  150,  151,  152,  151,  150,
	  153,  154,  151,  150,  156,  157,  150,  151,  143,  143,  158,  155,
	  143,  143,  155,  149,  141,  143,  141,  155,  155,  155,    1,  149,
	  150,  151,  152,  151,  150,  153,  151,  150,  156,  157,  150,  151,
	  143,  143,  158,  155,  143,  143,  155,  149,  141,  143,  141,  155,
	  155,  155,    1,  159,   40,   41,   43,   41,   40,   45,   41,   40,
	   47,   48,   40,   41,   44,   44,   49,   46,   44,   44,   46,  159,
	   42,   44,   42,   46,   46,   46,    1,    0
	};
}

private static final short _RON_indicies[] = init__RON_indicies_0();


private static byte[] init__RON_trans_targs_0()
{
	return new byte [] {
	    1,    0,   15,   16,    2,    4,    6,    7,    3,   19,   13,    3,
	   19,   13,    4,    5,   22,   22,    6,   23,   28,   29,    7,    8,
	    9,    9,   10,   25,   12,   26,   21,   15,   15,   16,    2,   21,
	    4,    6,    7,   16,   15,   16,   17,    2,   33,   21,   34,    4,
	    6,    7,   36,   18,   31,   32,   20,   15,   16,    2,   21,    4,
	    6,    7,   20,   15,   16,    2,   21,    4,    6,    7,   20,   15,
	   16,    2,   21,    4,    6,    7,   20,   15,   16,    2,   24,   21,
	   27,    4,    6,    7,   20,   15,   16,    2,   21,   24,    4,    6,
	    7,   20,   15,   16,    2,   21,    4,    6,   11,    7,   24,   20,
	   15,   16,   23,    2,   24,   21,   28,    4,    6,    7,   20,   15,
	   16,    2,   21,   30,   30,    4,    6,    7,   18,   15,   16,    2,
	   21,   31,    4,    6,    7,   31,   18,   15,   16,   17,    2,   31,
	   21,   33,    4,    6,    7,   18,   15,   16,    2,   21,   35,   35,
	    4,    6,    7,    1
	};
}

private static final byte _RON_trans_targs[] = init__RON_trans_targs_0();


private static short[] init__RON_trans_actions_0()
{
	return new short [] {
	    0,    0,   81,  151,  155,  155,  155,  155,   25,   69,   25,    0,
	   27,    0,    0,   51,   54,   15,    0,  139,  243,  346,    0,   63,
	   60,   17,    0,   19,   21,   23,   31,    0,  159,  258,  263,   84,
	  263,  263,  263,    0,  115,   45,   33,  208,   87,  213,  163,  208,
	  208,  208,    9,    0,   36,   91,   13,  123,  406,   48,  218,   48,
	   48,   48,    0,   75,  358,   11,  147,   11,   11,   11,   57,  223,
	  421,  131,  334,  131,  131,  131,   72,  248,  437,  143,   36,  352,
	   91,  143,  143,  143,  111,  322,  454,  203,  399,    1,  203,  203,
	  203,   66,  233,  429,  135,  340,  135,  135,    0,  135,    7,  103,
	  298,  445,   39,  188,   95,  392,    1,  188,  188,  188,  173,  378,
	  463,  280,  413,    7,    1,  280,  280,  280,    5,  193,  107,  310,
	  316,    1,  310,  310,  310,    7,    3,  178,   99,   39,  286,   95,
	  292,    1,  286,  286,  286,   42,  268,  168,  364,  371,    7,    1,
	  364,  364,  364,  119
	};
}

private static final short _RON_trans_actions[] = init__RON_trans_actions_0();


private static short[] init__RON_eof_actions_0()
{
	return new short [] {
	    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	    0,    0,    0,   29,  119,  119,  119,  127,   78,    0,  228,  253,
	  328,  238,  238,  328,  304,  385,  385,  198,  198,  183,  274,  274,
	  119
	};
}

private static final short _RON_eof_actions[] = init__RON_eof_actions_0();


static final int RON_start = 14;
static final int RON_first_final = 14;
static final int RON_error = 0;

static final int RON_en_main = 14;


// line 26 "ragel/java-parser.rl"

// line 27 "ragel/java-parser.rl"

// line 28 "ragel/java-parser.rl"

// line 29 "ragel/java-parser.rl"


 public static Frame parseFrame(Frame frame) {

         // frame.Parser = new ParserState();
         ParserState ps = frame.Parser;

         switch (ps.state) {
                 case RON_error:
                     if (ps.pos!=0) {
                         return frame;
                     }
                    
// line 338 "java/src/ron/Parser.java"
	{
	( ps.state) = RON_start;
	}

// line 42 "ragel/java-parser.rl"
                     frame.position = -1;
                     // frame.atoms = frame._atoms[:4]
                     frame.atoms = Frame.slice(frame.atoms, 0, 4);
                     break;
                 case RON_FULL_STOP:
                     ps.state = RON_error;
                     return frame;

                 case RON_start:
                     ps.off = ps.pos;
                     // frame.atoms = frame._atoms[:4];
                     frame.atoms = Frame.slice(frame.atoms, 0, 4);
                     ps.atm = 0; ps.hlf = 0; ps.dgt = 0;
                     break;
             }

             if (ps.pos >= frame.Body.length()) {
                 if (!ps.streaming) {
                     ps.state = RON_error;
                 }
                 return frame;
             }

             int pe = frame.Body.length(); int eof = frame.Body.length();
             int n = 0;
             // int _ = eof;
             // int _ = pe ;// FIXME kill

             if (ps.streaming) {
                 eof = -1;
             }

             int atm = ps.atm; int hlf = ps.hlf; int dgt = ps.dgt;
             Atom[] atoms = frame.atoms;
             int e_sgn = 0, e_val = 0, e_frac = 0;
             int p = ps.pos;

            
// line 382 "java/src/ron/Parser.java"
	{
	int _klen;
	int _trans = 0;
	int _acts;
	int _nacts;
	int _keys;
	int _goto_targ = 0;

	_goto: while (true) {
	switch ( _goto_targ ) {
	case 0:
	if ( p == pe ) {
		_goto_targ = 4;
		continue _goto;
	}
	if ( ( ps.state) == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
case 1:
	_match: do {
	_keys = _RON_key_offsets[( ps.state)];
	_trans = _RON_index_offsets[( ps.state)];
	_klen = _RON_single_lengths[( ps.state)];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + _klen - 1;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + ((_upper-_lower) >> 1);
			if ( ( frame.Body).get(p) < _RON_trans_keys[_mid] )
				_upper = _mid - 1;
			else if ( ( frame.Body).get(p) > _RON_trans_keys[_mid] )
				_lower = _mid + 1;
			else {
				_trans += (_mid - _keys);
				break _match;
			}
		}
		_keys += _klen;
		_trans += _klen;
	}

	_klen = _RON_range_lengths[( ps.state)];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + (_klen<<1) - 2;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + (((_upper-_lower) >> 1) & ~1);
			if ( ( frame.Body).get(p) < _RON_trans_keys[_mid] )
				_upper = _mid - 2;
			else if ( ( frame.Body).get(p) > _RON_trans_keys[_mid+1] )
				_lower = _mid + 2;
			else {
				_trans += ((_mid - _keys)>>1);
				break _match;
			}
		}
		_trans += _klen;
	}
	} while (false);

	_trans = _RON_indicies[_trans];
	( ps.state) = _RON_trans_targs[_trans];

	if ( _RON_trans_actions[_trans] != 0 ) {
		_acts = _RON_trans_actions[_trans];
		_nacts = (int) _RON_actions[_acts++];
		while ( _nacts-- > 0 )
	{
			switch ( _RON_actions[_acts++] )
			{
	case 0:
// line 5 "ragel/../ragel/./uuid-grammar.rl"
	{
    }
	break;
	case 1:
// line 9 "ragel/../ragel/./uuid-grammar.rl"
	{
        dgt = ABC[( frame.Body).get(p)] + 4;
        atoms[atm].trim6(hlf, dgt);
    }
	break;
	case 2:
// line 14 "ragel/../ragel/./uuid-grammar.rl"
	{
        atoms[atm].set6(hlf, dgt, ABC[( frame.Body).get(p)]);
        dgt++;
        if (dgt>10) {
            { p += 1; _goto_targ = 5; if (true)  continue _goto;}
        }
    }
	break;
	case 3:
// line 22 "ragel/../ragel/./uuid-grammar.rl"
	{
        atoms[atm].trim6(hlf, 0);
    }
	break;
	case 4:
// line 26 "ragel/../ragel/./uuid-grammar.rl"
	{
    }
	break;
	case 5:
// line 29 "ragel/../ragel/./uuid-grammar.rl"
	{
        dgt = 0;
        hlf = ORIGIN;
    }
	break;
	case 6:
// line 34 "ragel/../ragel/./uuid-grammar.rl"
	{
    }
	break;
	case 7:
// line 37 "ragel/../ragel/./uuid-grammar.rl"
	{
    }
	break;
	case 8:
// line 40 "ragel/../ragel/./uuid-grammar.rl"
	{
	atoms[atm].init64(VALUE, atoms[atm].get6(hlf, 0));
        dgt--;
    }
	break;
	case 9:
// line 45 "ragel/../ragel/./uuid-grammar.rl"
	{
        hlf = ORIGIN;
	atoms[atm].reset4(ORIGIN, 15, ABC[( frame.Body).get(p)]);
    }
	break;
	case 10:
// line 50 "ragel/../ragel/./uuid-grammar.rl"
	{
        atoms[atm].setOrigin(UUID_NAME_FLAG);
    }
	break;
	case 11:
// line 6 "ragel/../ragel/base-grammar.rl"
	{
        ps.omitted = 15;
    }
	break;
	case 12:
// line 10 "ragel/../ragel/base-grammar.rl"
	{
        if (atm>0) {
            atoms[atm] = atoms[atm-1].clone();
        }
    }
	break;
	case 13:
// line 16 "ragel/../ragel/base-grammar.rl"
	{
        n = (ABC[( frame.Body).get(p)]);
        hlf = 0;
        dgt = 0;
        if (n < atm) { 
            // parse #op1#op2#op3 without Ragel state explosion
            ( ps.state) = (RON_start);
            frame.position++;
            p--;
            { p += 1; _goto_targ = 5; if (true)  continue _goto;}
        } else { 
            // next UUID
            atm = n;
            ps.omitted -= 1 << n;
        }
    }
	break;
	case 14:
// line 33 "ragel/../ragel/base-grammar.rl"
	{
        atm++;
    }
	break;
	case 15:
// line 37 "ragel/../ragel/base-grammar.rl"
	{
        hlf = 0;
        dgt = 0;
        atoms = append(atoms, Atom.NewAtom());
    }
	break;
	case 16:
// line 42 "ragel/../ragel/base-grammar.rl"
	{
        atm++;
    }
	break;
	case 17:
// line 46 "ragel/../ragel/base-grammar.rl"
	{
    }
	break;
	case 18:
// line 48 "ragel/../ragel/base-grammar.rl"
	{
        if (( frame.Body).get(p)=='-') {
		atoms[atm].set1(ORIGIN, 60);
        }
    }
	break;
	case 19:
// line 53 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].arab64( VALUE, ( frame.Body).get(p) - '0' );
        // TODO max size for int/float/string
    }
	break;
	case 20:
// line 57 "ragel/../ragel/base-grammar.rl"
	{
        atoms[atm].set2(ORIGIN, 31, ATOM_INT);
    }
	break;
	case 21:
// line 61 "ragel/../ragel/base-grammar.rl"
	{
    }
	break;
	case 22:
// line 63 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].arab64( VALUE, ( frame.Body).get(p) - '0' );
        // TODO max size for int/float/string
    }
	break;
	case 23:
// line 67 "ragel/../ragel/base-grammar.rl"
	{
        if (( frame.Body).get(p)=='-') {
            atoms[atm].set1(ORIGIN, 60);
        }
    }
	break;
	case 24:
// line 72 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].arab64( VALUE, ( frame.Body).get(p) - '0' );
        atoms[atm].inc16( ORIGIN, 1 );
        // TODO max size for int/float/string
    }
	break;
	case 25:
// line 77 "ragel/../ragel/base-grammar.rl"
	{
        if (( frame.Body).get(p)=='-') {
		atoms[atm].set1(ORIGIN, 61);
        }
    }
	break;
	case 26:
// line 82 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].arab16(ORIGIN, ( frame.Body).get(p)-'0');
        // TODO max size for int/float/string
    }
	break;
	case 27:
// line 86 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].set2(ORIGIN, 31, ATOM_FLOAT);
    }
	break;
	case 28:
// line 90 "ragel/../ragel/base-grammar.rl"
	{
        atoms[atm].set32(VALUE, 1, p);
    }
	break;
	case 29:
// line 93 "ragel/../ragel/base-grammar.rl"
	{
        atoms[atm].set32(VALUE, 0, p);
	atoms[atm].set2(ORIGIN, 31, ATOM_STRING);
    }
	break;
	case 30:
// line 98 "ragel/../ragel/base-grammar.rl"
	{
        if (atm==4) {
            atoms[atm] = atoms[SPEC_OBJECT].clone();
        } else if (atoms[atm-1].Type()==ATOM_UUID) {
            atoms[atm] = atoms[atm-1].clone();
        }
    }
	break;
	case 31:
// line 105 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].set2(ORIGIN, 31, ATOM_UUID);
    }
	break;
	case 32:
// line 110 "ragel/../ragel/base-grammar.rl"
	{
        atm = 4;
        hlf = 0;
        dgt = 0;
    }
	break;
	case 33:
// line 115 "ragel/../ragel/base-grammar.rl"
	{
    }
	break;
	case 34:
// line 118 "ragel/../ragel/base-grammar.rl"
	{
        frame.term = ABC[( frame.Body).get(p)];
    }
	break;
	case 35:
// line 122 "ragel/../ragel/base-grammar.rl"
	{
        hlf = 0;
        if (p>ps.off && frame.position!=-1) {
            // one op is done, so stop parsing for now
            // make sure the parser restarts with the next op
            p--;
            ( ps.state) = (RON_start);
            { p += 1; _goto_targ = 5; if (true)  continue _goto;}
        } else {
            //op_idx++;
            if (frame.term!=TERM_RAW) {
                frame.term = TERM_REDUCED;
            }
        }
    }
	break;
	case 36:
// line 138 "ragel/../ragel/base-grammar.rl"
	{
        frame.position++;
    }
	break;
	case 37:
// line 142 "ragel/../ragel/base-grammar.rl"
	{
    }
	break;
	case 38:
// line 145 "ragel/../ragel/base-grammar.rl"
	{
        ( ps.state) = (RON_FULL_STOP);
        { p += 1; _goto_targ = 5; if (true)  continue _goto;}
    }
	break;
// line 744 "java/src/ron/Parser.java"
			}
		}
	}

case 2:
	if ( ( ps.state) == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
	if ( ++p != pe ) {
		_goto_targ = 1;
		continue _goto;
	}
case 4:
	if ( p == eof )
	{
	int __acts = _RON_eof_actions[( ps.state)];
	int __nacts = (int) _RON_actions[__acts++];
	while ( __nacts-- > 0 ) {
		switch ( _RON_actions[__acts++] ) {
	case 6:
// line 34 "ragel/../ragel/./uuid-grammar.rl"
	{
    }
	break;
	case 7:
// line 37 "ragel/../ragel/./uuid-grammar.rl"
	{
    }
	break;
	case 10:
// line 50 "ragel/../ragel/./uuid-grammar.rl"
	{
        atoms[atm].setOrigin(UUID_NAME_FLAG);
    }
	break;
	case 14:
// line 33 "ragel/../ragel/base-grammar.rl"
	{
        atm++;
    }
	break;
	case 16:
// line 42 "ragel/../ragel/base-grammar.rl"
	{
        atm++;
    }
	break;
	case 20:
// line 57 "ragel/../ragel/base-grammar.rl"
	{
        atoms[atm].set2(ORIGIN, 31, ATOM_INT);
    }
	break;
	case 27:
// line 86 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].set2(ORIGIN, 31, ATOM_FLOAT);
    }
	break;
	case 31:
// line 105 "ragel/../ragel/base-grammar.rl"
	{
	atoms[atm].set2(ORIGIN, 31, ATOM_UUID);
    }
	break;
	case 33:
// line 115 "ragel/../ragel/base-grammar.rl"
	{
    }
	break;
	case 36:
// line 138 "ragel/../ragel/base-grammar.rl"
	{
        frame.position++;
    }
	break;
	case 37:
// line 142 "ragel/../ragel/base-grammar.rl"
	{
    }
	break;
// line 827 "java/src/ron/Parser.java"
		}
	}
	}

case 5:
	}
	break; }
	}

// line 85 "ragel/java-parser.rl"


             ps.atm = atm; ps.hlf = hlf; ps.dgt = dgt;
             ps.pos = p;
             frame.atoms = atoms;

             switch (ps.state) {
                 case RON_error:
                     frame.position = -1;
                     break;
                 case RON_FULL_STOP:
                 case RON_start:
                    break;
                 default:
                     if (ps.state>=RON_first_final) { // one of end states
                          if (!ps.streaming && p>=eof) {
                              // in the block mode, the final dot is optional/implied
                              ps.state = RON_FULL_STOP;
                          }
                       break;
                     }

                     if (!ps.streaming) {
                         ps.state = RON_error;
                         frame.position = -1;
                     }
             }

             // System.out.println("omits "+ frame.IsComplete() + " " + frame.term!=TERM_REDUCED +  ps.omitted + frame.Parser.state)
             if (IsComplete(frame) && frame.term != TERM_REDUCED && ps.omitted!=0) {
                 for (int u = 0; u < 4; u++) {
                     if ((ps.omitted & (1<<u)) != 0) {
                         frame.atoms[u] = new Atom(ZERO_UUID);
                     }
                 }
             }

             return frame;
    }

    // Whether op parsing is complete (not always the case for the streaming mode)
    public static boolean IsComplete(Frame frame) {
        return (frame.Parser.state == RON_start && frame.position >= 0) || frame.Parser.state == RON_FULL_STOP;
    }

      
// line 131 "ragel/java-parser.rl"
      
// line 886 "java/src/ron/Parser.java"
private static byte[] init__UUID_actions_0()
{
	return new byte [] {
	    0,    1,    2,    1,    6,    1,    7,    1,    8,    2,    0,    9,
	    2,    5,    1,    2,    6,    9,    2,    6,   10,    3,    0,    4,
	    1,    3,    5,    3,    2,    3,    6,    5,    1,    4,    0,    4,
	    3,    2
	};
}

private static final byte _UUID_actions[] = init__UUID_actions_0();


private static byte[] init__UUID_key_offsets_0()
{
	return new byte [] {
	    0,    0,   18,   32,   40,   49,   67,   86
	};
}

private static final byte _UUID_key_offsets[] = init__UUID_key_offsets_0();


private static char[] init__UUID_trans_keys_0()
{
	return new char [] {
	   43,   45,   91,   93,   95,  123,  125,  126,   36,   37,   40,   41,
	   48,   57,   65,   90,   97,  122,   91,   93,   95,  123,  125,  126,
	   40,   41,   48,   57,   65,   90,   97,  122,   95,  126,   48,   57,
	   65,   90,   97,  122,   47,   95,  126,   48,   57,   65,   90,   97,
	  122,   43,   45,   91,   93,   95,  123,  125,  126,   36,   37,   40,
	   41,   48,   57,   65,   90,   97,  122,   43,   45,   47,   91,   93,
	   95,  123,  125,  126,   36,   37,   40,   41,   48,   57,   65,   90,
	   97,  122,   43,   45,   91,   93,   95,  123,  125,  126,   36,   37,
	   40,   41,   48,   57,   65,   90,   97,  122,    0
	};
}

private static final char _UUID_trans_keys[] = init__UUID_trans_keys_0();


private static byte[] init__UUID_single_lengths_0()
{
	return new byte [] {
	    0,    8,    6,    2,    3,    8,    9,    8
	};
}

private static final byte _UUID_single_lengths[] = init__UUID_single_lengths_0();


private static byte[] init__UUID_range_lengths_0()
{
	return new byte [] {
	    0,    5,    4,    3,    3,    5,    5,    5
	};
}

private static final byte _UUID_range_lengths[] = init__UUID_range_lengths_0();


private static byte[] init__UUID_index_offsets_0()
{
	return new byte [] {
	    0,    0,   14,   25,   31,   38,   52,   67
	};
}

private static final byte _UUID_index_offsets[] = init__UUID_index_offsets_0();


private static byte[] init__UUID_indicies_0()
{
	return new byte [] {
	    0,    0,    2,    2,    3,    2,    2,    3,    0,    2,    3,    3,
	    3,    1,    4,    4,    5,    4,    4,    5,    4,    5,    5,    5,
	    1,    6,    6,    6,    6,    6,    1,    7,    6,    6,    6,    6,
	    6,    1,    8,    8,    9,    9,   10,    9,    9,   10,    8,    9,
	   10,   10,   10,    1,    8,    8,   11,    9,    9,   12,    9,    9,
	   12,    8,    9,   12,   12,   12,    1,    8,    8,    9,    9,   12,
	    9,    9,   12,    8,    9,   12,   12,   12,    1,    0
	};
}

private static final byte _UUID_indicies[] = init__UUID_indicies_0();


private static byte[] init__UUID_trans_targs_0()
{
	return new byte [] {
	    2,    0,    5,    6,    3,    4,    3,    3,    2,    3,    5,    7,
	    7
	};
}

private static final byte _UUID_trans_targs[] = init__UUID_trans_targs_0();


private static byte[] init__UUID_trans_actions_0()
{
	return new byte [] {
	    9,    0,   21,   33,   12,   25,    1,    7,   15,   29,    1,    7,
	    1
	};
}

private static final byte _UUID_trans_actions[] = init__UUID_trans_actions_0();


private static byte[] init__UUID_eof_actions_0()
{
	return new byte [] {
	    0,    0,    0,    5,    5,    3,   18,   18
	};
}

private static final byte _UUID_eof_actions[] = init__UUID_eof_actions_0();


static final int UUID_start = 1;
static final int UUID_first_final = 2;
static final int UUID_error = 0;

static final int UUID_en_main = 1;


// line 132 "ragel/java-parser.rl"


    // func ()
    // (UUID, error)
    public static UUID parseUUID (UUID ctx_uuid, byte[] data) {


    	int cs = 0; int p = 0; int pe = data.length; int eof = data.length;
        // _ = eof

        int atm = 0; int hlf = VALUE; int dgt = 0;

        // atoms := [1]Atom{Atom(ctx_uuid)}
        Atom[] atoms = new Atom[] {new Atom(ctx_uuid)};

    	
// line 1030 "java/src/ron/Parser.java"
	{
	cs = UUID_start;
	}

// line 1035 "java/src/ron/Parser.java"
	{
	int _klen;
	int _trans = 0;
	int _acts;
	int _nacts;
	int _keys;
	int _goto_targ = 0;

	_goto: while (true) {
	switch ( _goto_targ ) {
	case 0:
	if ( p == pe ) {
		_goto_targ = 4;
		continue _goto;
	}
	if ( cs == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
case 1:
	_match: do {
	_keys = _UUID_key_offsets[cs];
	_trans = _UUID_index_offsets[cs];
	_klen = _UUID_single_lengths[cs];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + _klen - 1;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + ((_upper-_lower) >> 1);
			if ( data[p] < _UUID_trans_keys[_mid] )
				_upper = _mid - 1;
			else if ( data[p] > _UUID_trans_keys[_mid] )
				_lower = _mid + 1;
			else {
				_trans += (_mid - _keys);
				break _match;
			}
		}
		_keys += _klen;
		_trans += _klen;
	}

	_klen = _UUID_range_lengths[cs];
	if ( _klen > 0 ) {
		int _lower = _keys;
		int _mid;
		int _upper = _keys + (_klen<<1) - 2;
		while (true) {
			if ( _upper < _lower )
				break;

			_mid = _lower + (((_upper-_lower) >> 1) & ~1);
			if ( data[p] < _UUID_trans_keys[_mid] )
				_upper = _mid - 2;
			else if ( data[p] > _UUID_trans_keys[_mid+1] )
				_lower = _mid + 2;
			else {
				_trans += ((_mid - _keys)>>1);
				break _match;
			}
		}
		_trans += _klen;
	}
	} while (false);

	_trans = _UUID_indicies[_trans];
	cs = _UUID_trans_targs[_trans];

	if ( _UUID_trans_actions[_trans] != 0 ) {
		_acts = _UUID_trans_actions[_trans];
		_nacts = (int) _UUID_actions[_acts++];
		while ( _nacts-- > 0 )
	{
			switch ( _UUID_actions[_acts++] )
			{
	case 0:
// line 5 "ragel/../ragel/uuid-grammar.rl"
	{
    }
	break;
	case 1:
// line 9 "ragel/../ragel/uuid-grammar.rl"
	{
        dgt = ABC[data[p]] + 4;
        atoms[atm].trim6(hlf, dgt);
    }
	break;
	case 2:
// line 14 "ragel/../ragel/uuid-grammar.rl"
	{
        atoms[atm].set6(hlf, dgt, ABC[data[p]]);
        dgt++;
        if (dgt>10) {
            { p += 1; _goto_targ = 5; if (true)  continue _goto;}
        }
    }
	break;
	case 3:
// line 22 "ragel/../ragel/uuid-grammar.rl"
	{
        atoms[atm].trim6(hlf, 0);
    }
	break;
	case 4:
// line 26 "ragel/../ragel/uuid-grammar.rl"
	{
    }
	break;
	case 5:
// line 29 "ragel/../ragel/uuid-grammar.rl"
	{
        dgt = 0;
        hlf = ORIGIN;
    }
	break;
	case 6:
// line 34 "ragel/../ragel/uuid-grammar.rl"
	{
    }
	break;
	case 8:
// line 40 "ragel/../ragel/uuid-grammar.rl"
	{
	atoms[atm].init64(VALUE, atoms[atm].get6(hlf, 0));
        dgt--;
    }
	break;
	case 9:
// line 45 "ragel/../ragel/uuid-grammar.rl"
	{
        hlf = ORIGIN;
	atoms[atm].reset4(ORIGIN, 15, ABC[data[p]]);
    }
	break;
// line 1174 "java/src/ron/Parser.java"
			}
		}
	}

case 2:
	if ( cs == 0 ) {
		_goto_targ = 5;
		continue _goto;
	}
	if ( ++p != pe ) {
		_goto_targ = 1;
		continue _goto;
	}
case 4:
	if ( p == eof )
	{
	int __acts = _UUID_eof_actions[cs];
	int __nacts = (int) _UUID_actions[__acts++];
	while ( __nacts-- > 0 ) {
		switch ( _UUID_actions[__acts++] ) {
	case 6:
// line 34 "ragel/../ragel/uuid-grammar.rl"
	{
    }
	break;
	case 7:
// line 37 "ragel/../ragel/uuid-grammar.rl"
	{
    }
	break;
	case 10:
// line 50 "ragel/../ragel/uuid-grammar.rl"
	{
        atoms[atm].setOrigin(UUID_NAME_FLAG);
    }
	break;
// line 1211 "java/src/ron/Parser.java"
		}
	}
	}

case 5:
	}
	break; }
	}

// line 154 "ragel/java-parser.rl"


        if (cs < UUID_first_final || dgt > 10) {
            throw new RuntimeException(String.format("parse error at pos %d", p));
            // return ERROR_UUID, errors.New(fmt.Sprintf("parse error at pos %d", p))
        } else {
            return new UUID(atoms[0]);
        }
    }
}

