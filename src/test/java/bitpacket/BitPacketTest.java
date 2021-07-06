package bitpacket;

import bitpacket.huffman.HuffmanTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BitPacketTest {

    private HuffmanTree createRandomHuffmanTree(){
        float[] occurrences = new float[256];
        for(int i = 0;i < 256;i++) occurrences[i] = (float)Math.random()*500f;

        return new HuffmanTree(occurrences);
    }

    private HuffmanTree createFixedHuffmanTree(){
        float[] occurrences = new float[256];
        for(int i = 0;i < 256;i++) occurrences[i] = 1f;

        return new HuffmanTree(occurrences);
    }

    @Test
    void writeIntTest(){
        int x = 500;
        int bits = 10;

        BitPacket packet = new BitPacket();
        packet.writeInt(x, bits);

        packet.begin();
        assertEquals(x, packet.readInt(bits));
    }

    @Test
    void writeFloatTest() {
        float x = 1.666594f;

        BitPacket packet = new BitPacket();
        packet.writeFloat(x);

        packet.begin();
        assertEquals(x, packet.readFloat());
    }

    @Test
    void writeBitTest(){
        int b = 1;

        BitPacket packet = new BitPacket();
        packet.writeBit(b);

        packet.begin();
        assertEquals(b, packet.readBit());
    }

    @Test
    void writeCharTest(){
        char c = 'k';

        BitPacket packet = new BitPacket();
        packet.writeChar(c);

        packet.begin();
        assertEquals(c, packet.readChar());
    }

    @Test
    void writeStringTest(){
        String str = "Lorem ipsum dolor sit amet";

        BitPacket packet = new BitPacket();
        packet.writeString(str);

        packet.begin();
        assertEquals(str, packet.readString(str.length()));
    }

    @Test
    void testRandomizedHuffmanTree(){

        String stringValue = "the brown fox jumped over the lazy dog";

        BitPacket packet = new BitPacket();
        packet.writeString(stringValue);

        HuffmanTree randomizedHuffmanTree = createRandomHuffmanTree();

        packet.compress(randomizedHuffmanTree);
        packet.uncompress(randomizedHuffmanTree);

        assertEquals(stringValue, packet.readString(stringValue.length()));
    }

    @Test
    void testFixedHuffmanTree(){

        String stringValue = "the brown fox jumped over the lazy dog";

        BitPacket packet = new BitPacket();
        packet.writeString(stringValue);

        HuffmanTree fixedHuffmanTree = createFixedHuffmanTree();

        packet.compress(fixedHuffmanTree);
        packet.uncompress(fixedHuffmanTree);

        assertEquals(stringValue, packet.readString(stringValue.length()));
    }

    @Test
    void compressionTest(){
        String stringValue = "the brown fox jumped over the lazy dog";
        int bitValue = 1;
        char charValue = '|';
        float floatValue = 1e30f;
        int intValue = 1023;
        int intBits = 10;

        BitPacket packet = new BitPacket();
        packet.writeString(stringValue);
        packet.writeBit(bitValue);
        packet.writeChar(charValue);
        packet.writeFloat(floatValue);
        packet.writeInt(intValue, intBits);

        HuffmanTree huffmanTree = createRandomHuffmanTree();
        packet.compress(huffmanTree);

        // Checking if the compression messed up the data
        assertNotEquals(stringValue, packet.readString(stringValue.length()));

        packet.uncompress(huffmanTree);

        assertEquals(stringValue, packet.readString(stringValue.length()));
        assertEquals(bitValue, packet.readBit());
        assertEquals(charValue, packet.readChar());
        assertEquals(floatValue, packet.readFloat());
        assertEquals(intValue, packet.readInt(intBits));
    }
}
