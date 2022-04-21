package Packets;

import data.VarLengthNumbers;
import java.io.IOException;
import java.io.InputStream;

public class PacketInputStream{

    private InputStream stream;

    public PacketInputStream(InputStream stream){
        this.stream = stream;
    }

    public short readShort() throws IOException {
        if(stream.read() != 0x03) throw new RuntimeException("Incorrect Data Type: Expected Short-0x03");
        return readShort();
    }

    public int readVarInt() throws IOException {
        if(stream.read() != 0x08) throw new RuntimeException("Incorrect Data Type: Expected VarInt-0x08");
        return VarLengthNumbers.readVarInt(stream::read);
    }

    public long readVarLong() throws IOException {
        if(stream.read() != 0x09) throw new RuntimeException("Incorrect Data Type: Expected VarLong-0x09");
        return VarLengthNumbers.readVarLong(stream::read);
    }

}


