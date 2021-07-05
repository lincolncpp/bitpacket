package bitpacket;


import bitpacket.huffman.HuffmanMask;
import bitpacket.huffman.HuffmanNode;
import bitpacket.huffman.HuffmanTree;

import java.util.ArrayList;

public class BitPacket {

    private ArrayList<Byte> bytes;
    private int pointer = 0;
    private int last = 0;

    public BitPacket(){
        bytes = new ArrayList<>();
    }

    public BitPacket(byte[] array){
        bytes = new ArrayList<>();
        last = array.length*8;

        for (byte b:array) {
            bytes.add(b);
        }
    }

    public void clear(){
        bytes = new ArrayList<>();
        pointer = 0;
        last = 0;
    }

    public byte[] toArray(){
        byte[] array = new byte[bytes.size()];

        for(int i = 0;i < bytes.size();i++){
            array[i] = bytes.get(i);
        }

        return array;
    }

    public int bitCount(){
        return last;
    }

    public int packetSize(){
        return bytes.size()*8;
    }

    public void begin(){
        pointer = 0;
    }

    public void end(){
        pointer = last;
    }

    private void advance(int bits){
        pointer += bits;
        last = Math.max(last, pointer);
    }

    public void skip(int bits){
        advance(bits);
    }

    public void writeInt(int value, int bits){
        for(int i = 0;i < bits;i++){
            int current = pointer/8;
            int j = pointer%8;

            if (current == bytes.size()) bytes.add((byte) 0);
            Byte b = bytes.get(current);

            if (((value&(1<<i)) == 0) != ((b&(1<<j)) == 0)) b = (byte)((int)b^(1<<j));
            bytes.set(current, b);

            advance(1);
        }
    }

    public int readInt(int bits){
        int result = 0;

        for(int i = 0;i < bits;i++){
            Byte b = bytes.get(pointer/8);

            if ((b&(1<<(pointer%8))) != 0) result ^= 1<<i;

            advance(1);
        }

        return result;
    }

    public void writeBit(int value){ writeInt(value, 1); }

    public int readBit(){ return readInt(1); }

    public void writeFloat(float value){
        writeInt(Float.floatToIntBits(value), 32);
    }

    public float readFloat(){
        return Float.intBitsToFloat(readInt(32));
    }

    public void writeChar(char value){
        writeInt(value, 8);
    }

    public char readChar(){
        return (char)readInt(8);
    }

    public void writeString(String value){
        for(int i = 0;i < value.length();i++){
            writeChar(value.charAt(i));
        }
    }

    public String readString(int length){
        String result = "";
        for(int i = 0;i < length;i++){
            result += readChar();
        }
        return result;
    }

    public void compress(HuffmanTree tree){
        ArrayList<Byte> currentBytes = bytes;
        clear();

        for(Byte b:currentBytes){
            HuffmanMask mask = tree.getMask(b);
            writeInt(mask.getMask(), mask.getLength());
        }
    }

    public void uncompress(HuffmanTree tree){
        ArrayList<Byte> newBytes = new ArrayList<>();
        HuffmanNode node = tree.getRoot();

        begin();
        while(pointer < last){
            int bit = readBit();

            if (bit == 0) node = node.getLeftChild();
            else node = node.getRightChild();

            if (node.isLeaf()){
                newBytes.add(node.getValue());
                node = tree.getRoot();
            }
        }

        clear();
        bytes = newBytes;
        last = bytes.size()*8;
    }

}
