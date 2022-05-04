package Packets;

import network.PacketInputStream;
import network.PacketOutputStream;

import java.io.IOException;
import java.util.Arrays;

public class ArrayPacket extends Packet {

    public int[] data;

    public ArrayPacket() {
    }

    public ArrayPacket(int... data) {
        this.data = data;
    }

    @Override
    public int getPacketId() {
        return 1;
    }

    @Override
    public void writeData(PacketOutputStream out) throws IOException {
        out.writeArray(data);
    }

    @Override
    public void readData(PacketInputStream in) throws IOException {
        data = in.readIntArray();
    }

    @Override
    public String toString() {
        return "ArrayPacket{" + Arrays.toString(data) + '}';
    }
}


