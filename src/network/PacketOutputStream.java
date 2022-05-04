package network;

import data.VarLengthNumbers;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static packets.DataTypeId.*;

public class PacketOutputStream {

    private final DataOutputStream stream;

    public PacketOutputStream(OutputStream stream) {
        this(new DataOutputStream(stream));
    }

    public PacketOutputStream(DataOutputStream stream){
        this.stream = stream;
    }

    public void writeNull() throws IOException {
        stream.writeByte(NULL);
    }

    public void writeBoolean(boolean b) throws IOException {
        stream.writeByte(BOOLEAN);
        stream.writeByte(b ? 1 : 0);
    }

    public void writeByte(byte value, boolean signed) throws IOException {
        if(!signed){
            stream.writeByte(BYTE);
            stream.writeByte(value);
        }else{
            stream.writeByte(SIGNED_BYTE);
            // write the two's complement of the value to the stream
            stream.writeByte(~value); //TO-DO: check if this is correct

        }
    }

    public void writeShort(short value, boolean signed) throws IOException {
        if(!signed){
            stream.writeByte(SHORT);
            stream.writeShort(value);
        }else{
            stream.writeByte(SIGNED_SHORT);
            // write the two's complement of the value to the stream
            stream.writeShort(~value); //TO-DO: check if this is correct

        }
    }

    public void writeInt(int value, boolean signed) throws IOException {
        if(!signed){
            stream.writeByte(INT);
            stream.writeInt(value);
        }else{
            stream.writeByte(SIGNED_INT);
            // write the two's complement of the value to the stream
            stream.writeInt(~value); //TO-DO: check if this is correct

        }
    }

    public void writeLong(long value, boolean signed) throws IOException {
        if(!signed){
            stream.writeByte(LONG);
            stream.writeLong(value);
        }else{
            stream.writeByte(SIGNED_LONG);
            // write the two's complement of the value to the stream
            stream.writeLong(~value); //TO-DO: check if this is correct

        }
    }

    public void writeFloat(float value) throws IOException {
        stream.writeByte(FLOAT);
        stream.writeFloat(value);
    }

    public void writeDouble(double value) throws IOException {
        stream.writeByte(DOUBLE);
        stream.writeDouble(value);
    }

    public void writeVarInt(int value) throws IOException {
        stream.writeByte(VAR_INT);
        VarLengthNumbers.writeVarInt(stream::write, value);
    }

    public void writeVarLong(long value) throws IOException {
        stream.writeByte(VAR_LONG);
        VarLengthNumbers.writeVarLong(stream::writeByte, value);
    }

    public void writeString(String value) throws IOException {
        stream.writeByte(STRING);
        VarLengthNumbers.writeVarInt(stream::writeByte, value.length());
        byte[] bytes = value.getBytes();
        stream.write(bytes);
    }

    public void writeBytes(byte[] value) throws IOException {
        stream.writeByte(BYTES);
        VarLengthNumbers.writeVarInt(stream::writeByte, value.length);
        stream.write(value);
    }

    public void writeArray(short[] value) throws IOException {
        stream.writeByte(ARRAY);
        stream.writeByte(SHORT);
        VarLengthNumbers.writeVarInt(stream::writeByte, value.length);
        for(short s : value){
            stream.writeShort(s);
        }
    }

    public void writeArray(int[] value) throws IOException {
        stream.writeByte(ARRAY);
        stream.writeByte(INT);
        VarLengthNumbers.writeVarInt(stream::writeByte, value.length);
        for(int i : value){
            stream.writeInt(i);
        }
    }

    public void writeArray(long[] value) throws IOException {
        stream.writeByte(ARRAY);
        stream.writeByte(LONG);
        VarLengthNumbers.writeVarInt(stream::writeByte, value.length);
        for(long l : value){
            stream.writeLong(l);
        }
    }

    public void writeArray(float[] value) throws IOException {
        stream.writeByte(ARRAY);
        stream.writeByte(FLOAT);
        VarLengthNumbers.writeVarInt(stream::writeByte, value.length);
        for(float f : value){
            stream.writeFloat(f);
        }
    }

    public void writeArray(double[] value) throws IOException {
        stream.writeByte(ARRAY);
        stream.writeByte(DOUBLE);
        VarLengthNumbers.writeVarInt(stream::writeByte, value.length);
        for(double d : value){
            stream.writeDouble(d);
        }
    }


}
