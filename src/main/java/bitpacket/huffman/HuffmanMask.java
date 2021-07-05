package bitpacket.huffman;

public class HuffmanMask {
    private final int mask;
    private final int length;

    public HuffmanMask(int mask, int length){
        this.mask = mask;
        this.length = length;
    }

    public int getMask(){
        return mask;
    }

    public int getLength(){
        return length;
    }
}
