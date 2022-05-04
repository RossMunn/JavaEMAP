package data;


import interfaces.ReaderInterface;
import interfaces.WriterInterface;
import java.io.IOException;

public class VarLengthNumbers {

    /**
     * Represents 0b1000_0000. In Java bytes are signed, so we can't just use
     * that value in a byte object.
     */

    private static final byte CONTINUE_BIT   = -128;
    private static final byte SEGMENT_BITS   = ~CONTINUE_BIT;

    public static void writeVarInt(WriterInterface writer, int value) throws IOException {
        do {

            writer.writeByte((byte) (

                    (value & SEGMENT_BITS) |

                            ((value & ~SEGMENT_BITS) != 0 ? CONTINUE_BIT : 0)
            ));

            value >>>= 7;
        } while (value != 0);
    }

    public static void writeVarLong(WriterInterface writer, long value) throws IOException {
        do {

            writer.writeByte((byte) (

                    (value & SEGMENT_BITS) |

                            ((value & ~SEGMENT_BITS) != 0 ? CONTINUE_BIT : 0)
            ));

            value >>>= 7;
        } while (value != 0);
    }

    public static int readVarInt(ReaderInterface reader) throws IOException {

        byte currentByteIndex = 0;
        int currentByte;
        int value = 0;

        do {

            currentByte = reader.readByte();

            value |= (currentByte & SEGMENT_BITS) << (currentByteIndex * 7);
            currentByteIndex++;

            if (currentByteIndex == 5 && (currentByte & 0b1111_0000) != 0)
                throw new RuntimeException("Invalid VarInt");

        } while ((currentByte & CONTINUE_BIT) != 0);

        return value;
    }

    public static long readVarLong(ReaderInterface reader) throws IOException {

        byte currentByteIndex = 0;
        int currentByte;
        long value = 0;

        do {

            currentByte = reader.readByte();

            value |= (long) (currentByte & SEGMENT_BITS) << (currentByteIndex * 7);
            currentByteIndex++;

            if (currentByteIndex == 10 && (currentByte & 0b1111_1110) != 0)
                throw new RuntimeException("Invalid VarLong");

        } while ((currentByte & CONTINUE_BIT) != 0);

        return value;
    }
}
