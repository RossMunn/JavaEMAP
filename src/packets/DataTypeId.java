package packets;

public class DataTypeId {

    public static final byte
            NULL = (byte) 0x00,
            BOOLEAN = (byte) 0x01,
            BYTE = (byte) 0x02,
            SIGNED_BYTE = (byte) 0xA2,
            SHORT = (byte) 0x03,
            SIGNED_SHORT = (byte) 0xA3,
            INT = (byte) 0x04,
            SIGNED_INT = (byte) 0xA4,
            LONG = (byte) 0x05,
            SIGNED_LONG = (byte) 0xA5,
            FLOAT = (byte) 0x06,
            DOUBLE = (byte) 0x07,
            VAR_INT = (byte) 0x08,
            VAR_LONG = (byte) 0x09,
            STRING = (byte) 0x20,
            BYTES = (byte) 0x21,
            ARRAY = (byte) 0x22,
            FIXED_BYTES = (byte) 0xFE,

            MAGIC = (byte) 0xFF;
}

