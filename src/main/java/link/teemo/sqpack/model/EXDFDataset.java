package link.teemo.sqpack.model;

public class EXDFDataset {
    public final short type;
    public final short offset;
    public EXDFDataset(short type, short offset) {
        this.type = type;
        this.offset = offset;
    }
}