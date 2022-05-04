package packets;

import network.PacketInputStream;
import network.PacketOutputStream;

import java.io.IOException;

public abstract class Packet {

    private final int id;
    private byte[] snowflake;

    public Packet(int id) {
        this.id = id;
    }

    public int getPacketId() {
        return id;
    }

    public byte[] getSnowflake() {
        return snowflake;
    }

    public void setSnowflake(byte[] snowflake) {
        this.snowflake = snowflake;
    }

    public abstract void writeData(PacketOutputStream out) throws IOException;

    public abstract void readData(PacketInputStream in) throws IOException;

}

