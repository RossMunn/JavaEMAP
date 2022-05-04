package Packets;

import network.PacketInputStream;
import network.PacketOutputStream;

import java.io.IOException;

public abstract class Packet {

    private byte[] snowflake;

    public byte[] getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(byte[] snowflake) {
        this.snowflake = snowflake;
    }

    public abstract int getPacketId();

    public abstract void writeData(PacketOutputStream out) throws IOException;

    public abstract void readData(PacketInputStream in) throws IOException;
}

