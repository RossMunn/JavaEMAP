package Packets;

import network.PacketInputStream;
import network.PacketOutputStream;

import java.io.IOException;

public class DefaultPacket extends Packet {

    public String name;
    public String message;

    public DefaultPacket(){}

    public DefaultPacket(String name, String message) {
        this.name = name;
        this.message = message;
    }

    @Override
    public int getPacketId() {
        return 0;
    }

    @Override
    public void writeData(PacketOutputStream out) throws IOException {
        out.writeString(name);
        out.writeString(message);
    }

    @Override
    public void readData(PacketInputStream in) throws IOException {
        name = in.readString();
        message = in.readString();
    }

    @Override
    public String toString() {
        return name + message;
    }
}
