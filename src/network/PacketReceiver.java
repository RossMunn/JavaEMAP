package network;

import Packets.Packet;
import Packets.Packets;
import data.VarLengthNumbers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PacketReceiver {

    public static Packet receivePacket(DatagramSocket socket) throws IOException {

        byte[] buf = new byte[1024];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        InetAddress address = packet.getAddress();
        int port = packet.getPort();

        Chunk firstChunk = Chunk.fromBytes(packet.getData());


        ByteBuffer buffer = ByteBuffer.wrap(firstChunk.data);

        int packetSize = VarLengthNumbers.readVarInt(buffer::get);

        ByteBuffer packetBuffer = ByteBuffer.allocate(packetSize+4);
        packetBuffer.put(firstChunk.data);

        int bytesReceived = firstChunk.data.length - buffer.position();

        byte[] sendAckBuffer = new byte[28];
        sendAcknowledgement(socket, address, port, sendAckBuffer, firstChunk.snowflake, firstChunk.hash, firstChunk.index);

        Chunk lastChunk = firstChunk;
        while(bytesReceived < packetSize) {

            Chunk chunk = receiveChunk(socket, buf);
            if(chunk.index == lastChunk.index + 1) {
                packetBuffer.put(chunk.data);
                bytesReceived += chunk.data.length;
                sendAcknowledgement(socket, address, port, sendAckBuffer, chunk.snowflake, chunk.hash, chunk.index);
                lastChunk = chunk;
            }
        }

        packetBuffer.flip();
        VarLengthNumbers.readVarInt(packetBuffer::get);

        byte[] snowflake = new byte[16];
        packetBuffer.get(snowflake);

        int packetId = VarLengthNumbers.readVarInt(packetBuffer::get);

        Packet p = Packets.getPacket(packetId);


        if(p == null) return null;

        byte[] data = new byte[packetBuffer.remaining()];
        packetBuffer.get(data);

        p.setSnowflake(snowflake);
        p.readData(new PacketInputStream(new ByteArrayInputStream(data)));
        return p;
    }

    private static Chunk receiveChunk(DatagramSocket socket, byte[] buffer) throws IOException {

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        return Chunk.fromBytes(packet.getData());
    }

    private static void sendAcknowledgement(DatagramSocket socket, InetAddress address, int port, byte[] sendDataBuffer, byte[] snowflake, byte[] hash, int index) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(sendDataBuffer);
        buffer.put(snowflake);
        buffer.put(hash);
        buffer.putInt(index);
        socket.send(new DatagramPacket(buffer.array(), buffer.position(), address, port));
    }


}

class Chunk {

    static Chunk fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        short length = buffer.getShort();

        byte[] snowflake = new byte[16];
        buffer.get(snowflake);

        byte[] hash = new byte[8];
        buffer.get(hash);

        int index = buffer.getInt();

        byte[] data = new byte[length];
        buffer.get(data);

        return new Chunk(length, snowflake, hash, index, data);
    }

    short length;
    byte[] snowflake;
    byte[] hash;
    int index;

    byte[] data;

    public Chunk() {

    }

    public Chunk(short length, byte[] snowflake, byte[] hash, int index, byte[] data) {
        this.length = length;
        this.snowflake = snowflake;
        this.hash = hash;
        this.index = index;
        this.data = data;
    }


}
