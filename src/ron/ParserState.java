package ron;

public class ParserState {
    // position in the atom array, in the atom, in the half-atom
    public int atm, hlf, dgt;
    // ragel parser state
    public int state;
    // byte offset of the current op
    public int off;
    // parsing byte offset
    public int position;
    // parser mode: streaming (might get more bytes) / block (complete frame)
    public boolean streaming;
    // which spec uuids are omitted/defaults in the current op
    // uint8
    public int omitted;
}