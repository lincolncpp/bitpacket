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

    @Test
    void skipBeginEndTest(){
        int intValue = 123456;
        int intBits = 32;
        int bitsToSkip = 10;

        BitPacket packet = new BitPacket();
        packet.writeInt(5151, bitsToSkip);

        packet.begin();
        packet.end();

        packet.writeInt(intValue, intBits);
        packet.begin();
        packet.skip(bitsToSkip);

        assertEquals(intValue, packet.readInt(intBits));
    }

    @Test
    void sizeTest(){
        int intMaxValue = 12345;
        int intBits = 15;


        BitPacket packet = new BitPacket();

        for(int i = 0;i <= intMaxValue;i++){
            packet.writeInt(i, intBits);
        }

        // Checking the last insertion
        packet.skip(-intBits);
        assertEquals(intMaxValue, packet.readInt(intBits));

        int bitsCount = (intMaxValue+1)*intBits;
        int bytesCount = (bitsCount+7)/8*8;
        assertEquals(bitsCount, packet.bitCount());
        assertEquals(bytesCount, packet.packetSize());
    }

    @Test
    void argsConstructorTest(){
        int elementsCount = 1000;

        BitPacket packetOne = new BitPacket();
        for(int i = 0;i < elementsCount;i++){
            packetOne.writeFloat((float)Math.random()*1e30f);
        }
        packetOne.begin();

        BitPacket packetTwo = new BitPacket(packetOne.toArray());
        for(int i = 0;i < elementsCount;i++){
            assertEquals(packetOne.readFloat(), packetTwo.readFloat());
        }
    }
}
