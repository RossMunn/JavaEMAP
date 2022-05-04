package com.rossmunn.slim.network;

import com.rossmunn.slim.data.VarLengthNumbers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.rossmunn.slim.packets.DataTypeId.*;

public class PacketInputStream{

    private DataInputStream stream;

    public PacketInputStream(InputStream in){
        this(new DataInputStream(in));
    }

    public PacketInputStream(DataInputStream stream){
        this.stream = stream;
    }

    public boolean readBoolean() throws IOException {
        if(stream.read() != BOOLEAN) exception("Boolean", BOOLEAN);
        return stream.readByte()==1;
    }

    public int readByte() throws IOException {
        if(stream.read() != BYTE) exception("Byte", BYTE);
        return stream.readUnsignedByte();
    }

    public byte readSignedByte() throws IOException {
        if(stream.read() != SIGNED_BYTE) exception("SignedByte", SIGNED_BYTE);
        return stream.readByte();
    }

    public int readShort() throws IOException {
        if(stream.read() != SHORT) exception("Short", SHORT);
        return stream.readUnsignedShort();
    }

    public short readSignedShort() throws IOException {
        if(stream.read() != SIGNED_SHORT) exception("Signed Short", SIGNED_SHORT);
        return stream.readShort();
    }

    public int readInt() throws IOException {
        if(stream.read() != INT) exception("Int", INT);
        return stream.readInt();
    }

    public int readSignedInt() throws IOException {
        if(stream.read() != SIGNED_INT) exception("Signed Int", SIGNED_INT);
        return ~stream.readInt();
    }

    public long readLong() throws IOException {
        if(stream.read() != LONG) exception("Long", LONG);
        return stream.readLong();
    }

    public long readSignedLong() throws IOException {
        if(stream.read() != SIGNED_LONG) exception("Signed Long", SIGNED_LONG);
        return ~stream.readLong();
    }

    public float readFloat() throws IOException {
        if(stream.read() != FLOAT) exception("Float", FLOAT);
        return stream.readFloat();
    }

    public double readDouble() throws IOException {
        if(stream.read() != DOUBLE) exception("Double", DOUBLE);
        return stream.readDouble();
    }


    public int readVarInt() throws IOException {
        if(stream.read() != VAR_INT) throw new RuntimeException("Incorrect Data Type: Expected VarInt-0x08");
        return VarLengthNumbers.readVarInt(stream::readByte);
    }

    public long readVarLong() throws IOException {
        if(stream.read() != VAR_LONG) throw new RuntimeException("Incorrect Data Type: Expected VarLong-0x09");
        return VarLengthNumbers.readVarLong(stream::readByte);
    }

    public String readString() throws IOException {
        if(stream.read() != STRING){
            exception("String", STRING);
        }
        int length = VarLengthNumbers.readVarInt(stream::readByte);

        byte[] data = new byte[length];
        int read = stream.read(data);

        if(length != 0 && read != length) throw new IOException("Incorrect String Size");
        return new String(data, StandardCharsets.UTF_8);
    }

    public byte[] readBytes() throws IOException {
        if(stream.read() != BYTES) exception("Bytes", BYTES);
        int length = VarLengthNumbers.readVarInt(stream::readByte);
        byte[] data = new byte[length];
        int read = stream.read(data);
        if(length !=0 && read != length) throw new IOException("Incorrect Packet Size");
        return data;
    }

    public short[] readShortArray() throws IOException {
        if(stream.read() != ARRAY) exception("Array", ARRAY);
        if(stream.read() != SHORT) exception("Short_Array", SHORT);
        int length = VarLengthNumbers.readVarInt(stream::readByte);
        short[] data = new short[length];
        for(int i = 0; i < length; i++){
            data[i] = stream.readShort();
        }
        return data;
    }

    public int[] readIntArray() throws IOException {
        if(stream.read() != ARRAY) exception("Array", ARRAY);
        if(stream.read() != INT) exception("Int_Array", INT);
        int length = VarLengthNumbers.readVarInt(stream::readByte);
        int[] data = new int[length];
        for(int i = 0; i < length; i++) {
            data[i] = stream.readInt();
        }
        return data;
    }

    public float[] readFloatArray() throws IOException {
        if(stream.read() != ARRAY) exception("Array", ARRAY);
        if(stream.read() != FLOAT) exception("Float_Array", FLOAT);
        int length = VarLengthNumbers.readVarInt(stream::readByte);
        float[] data = new float[length];
        for(int i = 0; i < length; i++) {
            data[i] = stream.readFloat();
        }
        return data;
    }

    public double[] readDoubleArray() throws IOException {
        if(stream.read() != ARRAY) exception("Array", ARRAY);
        if(stream.read() != DOUBLE) exception("Double_Array", DOUBLE);
        int length = VarLengthNumbers.readVarInt(stream::readByte);
        double[] data = new double[length];
        for(int i = 0; i < length; i++) {
            data[i] = stream.readDouble();
        }
        return data;
    }

    public long[] readLongArray() throws IOException {
        if(stream.read() != ARRAY) exception("Array", ARRAY);
        if(stream.read() != LONG) exception("Long_Array", LONG);
        int length = VarLengthNumbers.readVarInt(stream::readByte);
        long[] data = new long[length];
        for(int i = 0; i < length; i++) {
            data[i] = stream.readLong();
        }
        return data;
    }





    private void exception(String name, int id) throws IOException {
        throw new RuntimeException("Incorrect Data Type: Expected " + name + "-" + id);
    }

}

