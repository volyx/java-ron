package ron;

public class ParserState {
    // position in the atom array, in the atom, in the half-atom
    public int atm, hlf, dgt;
    // ragel parser state
    public int state;
    // byte offset of the current op
    public int off;
    // parsing byte offset
    public int pos;
    // parser mode: streaming (might get more bytes) / block (complete frame)
    public boolean streaming;
    // which spec uuids are omitted/defaults in the current op
    // uint8
    public int omitted;

    public ParserState() {}

    public ParserState(ParserState ps) {
        this.atm = ps.atm;
        this.hlf = ps.hlf;
        this.dgt = ps.dgt;
        this.state = ps.state;
        this.off = ps.off;
        this.pos = ps.pos;
        this.streaming = ps.streaming;
        this.omitted = ps.omitted;
    }

    public int state() {
        return this.state;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ParserState{");
        sb.append("atm=").append(atm);
        sb.append(", hlf=").append(hlf);
        sb.append(", dgt=").append(dgt);
        sb.append(", state=").append(state);
        sb.append(", off=").append(off);
        sb.append(", pos=").append(pos);
        sb.append(", streaming=").append(streaming);
        sb.append(", omitted=").append(omitted);
        sb.append('}');
        return sb.toString();
    }
}