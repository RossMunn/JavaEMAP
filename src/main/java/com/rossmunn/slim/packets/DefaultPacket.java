package com.rossmunn.slim.packets;

import com.rossmunn.slim.network.PacketInputStream;
import com.rossmunn.slim.network.PacketOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DefaultPacket extends Packet {

    public String name;
    public String message;

    public DefaultPacket() {
        super (0x0);
    }

    public DefaultPacket(String name, String message) {
        super(0x0);
        this.name = name;
        this.message = message;
    }

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
        return super.toString() + String.format("Name: %s%n", name) + String.format("Message: %s%n", message);
    }
}
