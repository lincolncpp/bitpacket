package bitpacket.huffman;

import java.util.PriorityQueue;

public class HuffmanTree {

    private HuffmanNode root;
    private final HuffmanMask[] byteMask;

    public HuffmanTree(float[] occurrences){
        byteMask = new HuffmanMask[256];

        buildTree(occurrences);
        buildMask(root, 0, 0);
    }

    private void buildMask(HuffmanNode node, int mask, int length){
        if (node.isLeaf()){
            int value = node.getValue();
            value += 128;

            byteMask[value] = new HuffmanMask(mask, length);
        }
        else{
            buildMask(node.getLeftChild(), mask, length+1);
            buildMask(node.getRightChild(), mask ^ (1<<length), length+1);
        }
    }

    private void buildTree(float[] occurrences){
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();

        for(int value = -128;value <= 127;value++){
            queue.add(new HuffmanNode((byte)value, occurrences[value+128]));
        }

        while(queue.size() > 1){
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();

            HuffmanNode parent = new HuffmanNode((byte)0, left.getOccurrences() + right.getOccurrences());
            parent.setChildren(left, right);

            queue.add(parent);
        }
        root = queue.poll();
    }

    public HuffmanNode getRoot(){
        return root;
    }

    public HuffmanMask getMask(byte value){
        return byteMask[(int)value+128];
    }
}
