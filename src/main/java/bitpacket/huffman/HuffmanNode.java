package bitpacket.huffman;

public class HuffmanNode  implements Comparable<HuffmanNode>{

    private final byte value;
    private final float occurrences;
    private HuffmanNode left = null;   // bit 0
    private HuffmanNode right = null;  // bit 1

    public HuffmanNode(byte value, float occurrences){
        this.value = value;
        this.occurrences = occurrences;
    }

    public boolean isLeaf(){
        return left == null && right == null;
    }

    public byte getValue(){
        return value;
    }

    public float getOccurrences(){
        return occurrences;
    }

    public void setChildren(HuffmanNode left, HuffmanNode right){
        this.left = left;
        this.right = right;
    }

    public HuffmanNode getLeftChild(){
        return left;
    }

    public HuffmanNode getRightChild(){
        return right;
    }

    @Override
    public int compareTo(HuffmanNode node) {
        if (occurrences > node.occurrences) return 1;
        else if (occurrences < node.occurrences) return -1;
        return 0;
    }
}