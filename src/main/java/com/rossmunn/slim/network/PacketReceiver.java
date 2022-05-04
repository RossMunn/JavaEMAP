package com.rossmunn.slim.network;

import com.rossmunn.slim.data.UUIDUtil;
import com.rossmunn.slim.packets.DataTypeId;
import com.rossmunn.slim.packets.Magic;
import com.rossmunn.slim.packets.Packet;
import com.rossmunn.slim.packets.Packets;
import com.rossmunn.slim.data.VarLengthNumbers;
import net.openhft.hashing.LongHashFunction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PacketReceiver {

    private static final Map<String, Chunk[]> chunks = new HashMap<>();

    private static boolean hasAllChunks(String snowflake) {
        return Arrays.stream(chunks.get(snowflake)).noneMatch(Objects::isNull);
    }

    public static Packet receiveChunk(DatagramSocket socket) throws IOException {

        byte[] datagramBuffer = new byte[1024];

        // Read the datagram.
        DatagramPacket datagram = new DatagramPacket(datagramBuffer, datagramBuffer.length);
        socket.receive(datagram);

        // Determine the sender of the datagram.
        InetAddress address = datagram.getAddress();
        int port = datagram.getPort();

        Chunk chunk = Chunk.fromBytes(datagram.getData());

        if (!chunks.containsKey(chunk.snowflake.toString())) {
            chunks.put(chunk.snowflake.toString(), new Chunk[chunk.count]);
        }

        System.out.println(Arrays.stream(chunks.get(chunk.snowflake.toString())).filter(Objects::nonNull).count());

        boolean didWriteChunk = false;

        // If this index has already been received, ignore it.
        if (Arrays.stream(chunks.get(chunk.snowflake.toString())).anyMatch(packetChunk -> packetChunk != null && packetChunk.index == chunk.index))
            return null;

        if (chunk.count != chunks.get(chunk.snowflake.toString()).length)
            throw new AssertionError("Chunk length mismatch.");

        // Otherwise, find an empty slot in the chunk array for it (the chunk array will be sorted later).
        for (int i = 0; i < chunks.get(chunk.snowflake.toString()).length; i++) {
            // Otherwise, find an empty slot in the chunk array for it (the chunk array will be sorted later).
            if (chunks.get(chunk.snowflake.toString())[i] == null) {
                chunks.get(chunk.snowflake.toString())[i] = chunk;
                didWriteChunk = true;
                break;
            }
        }

        // If there were no empty slots, the count was wrong.
        if (!didWriteChunk)
            throw new AssertionError("Received more chunks than chunk count or chunk count inconsistency.");

        // If we haven't gotten all the chunks yet, return.
        if (!hasAllChunks(chunk.snowflake.toString())) return null;

        // Now order all the chunks.
        var packet = Arrays.stream(chunks.get(chunk.snowflake.toString()))
                // Sort the chunks by index.
                .sorted(Comparator.comparingInt((Chunk value) -> value.index))
                // Combine the streamed chunks into a list.
                .collect(Collectors.toList());

        // and remove them from the map
        chunks.remove(chunk.snowflake.toString());

        // Compute the total length of the packet by summing the lengths of the
        // chunk payloads.
        AtomicInteger totalLength = new AtomicInteger();
        packet.forEach((value) -> totalLength.addAndGet(value.length));

        ByteBuffer packetBuffer = ByteBuffer.allocate(totalLength.get());
        packet.forEach(packetChunk -> packetBuffer.put(packetChunk.data));
        packetBuffer.flip(); // 'Seal' the packet buffer.

//        int bytesReceived = firstChunk.data.length - buffer.position();
//
//
//        byte[] sendAckBuffer = new byte[28];
//        sendAcknowledgement(socket, address, port, sendAckBuffer, firstChunk.snowflake, firstChunk.hash, firstChunk.index);
//
//        Chunk lastChunk = firstChunk;
//        while(bytesReceived < packetSize) {
//
//            Chunk chunk = receiveChunk(socket, buf);
//            if(chunk.index == lastChunk.index + 1) {
//                packetBuffer.put(chunk.data);
//                bytesReceived += chunk.data.length;
//                sendAcknowledgement(socket, address, port, sendAckBuffer, chunk.snowflake, chunk.hash, chunk.index);
//                lastChunk = chunk;
//            }
//        }

        // Read packet header.
        if (packetBuffer.get() != DataTypeId.MAGIC || packetBuffer.getInt() != Magic.PACKET)
            throw new AssertionError("Packet must start with magic value.");

        if (packetBuffer.get() != DataTypeId.VAR_INT)
            throw new AssertionError("Packet length is in invalid format.");
        VarLengthNumbers.readVarInt(packetBuffer::get);

        if (packetBuffer.get() != DataTypeId.FIXED_BYTES)
            throw new AssertionError("Packet snowflake is in invalid format.");
        byte[] snowflake = new byte[16];
        packetBuffer.get(snowflake);

        if (packetBuffer.get() != DataTypeId.VAR_INT)
            throw new AssertionError("Packet ID is in invalid format.");
        int packetId = VarLengthNumbers.readVarInt(packetBuffer::get);

        // Create packet object.
        Packet p = Packets.getPacket(packetId);
        if(p == null) return null;

        // Read packet body.
        byte[] data = new byte[packetBuffer.remaining()];
        packetBuffer.get(data);

        // Set packet object values and return packet.
        p.setSnowflake(snowflake);
        p.readData(new PacketInputStream(new ByteArrayInputStream(data)));
        return p;
    }

//    private static void sendAcknowledgement(DatagramSocket socket, InetAddress address, int port, byte[] sendDataBuffer, byte[] snowflake, byte[] hash, int index) throws IOException {
//        ByteBuffer buffer = ByteBuffer.wrap(sendDataBuffer);
//        buffer.put(snowflake);
//        buffer.put(hash);
//        buffer.putInt(index);
//        socket.send(new DatagramPacket(buffer.array(), buffer.position(), address, port));
//    }


}

class Chunk {

    private static final LongHashFunction hashinator = LongHashFunction.xx3();

    static Chunk fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        if (buffer.get() != DataTypeId.MAGIC || buffer.getInt() != Magic.CHUNK)
            throw new AssertionError("Chunk must start with magic value.");

        if (buffer.get() != DataTypeId.SHORT)
            throw new AssertionError("Chunk length is in invalid format.");
        short length = buffer.getShort();

        if (buffer.get() != DataTypeId.FIXED_BYTES)
            throw new AssertionError("Chunk snowflake is in invalid format.");
        byte[] snowflake = new byte[16];
        buffer.get(snowflake);

        if (buffer.get() != DataTypeId.FIXED_BYTES)
            throw new AssertionError("Chunk hash is in invalid format.");
        long hash = buffer.getLong();

        if (buffer.get() != DataTypeId.INT)
            throw new AssertionError("Chunk index is in invalid format.");
        int index = buffer.getInt();

        if (buffer.get() != DataTypeId.INT)
            throw new AssertionError("Chunk count is in invalid format.");
        int count = buffer.getInt();

        byte[] data = new byte[length];
        buffer.get(data);

        // Verify payload hash
        if (hashinator.hashBytes(data) != hash)
            throw new AssertionError("Chunk hash mismatch.");

        return new Chunk(length, snowflake, hash, index, count, data);
    }

    short length;
    UUID snowflake;
    long hash;
    int index;
    int count;
    byte[] data;

    public Chunk(short length, byte[] snowflake, long hash, int index, int count, byte[] data){
        this.length = length;
        this.snowflake = UUIDUtil.asUuid(snowflake);
        this.hash = hash;
        this.index = index;
        this.count = count;
        this.data = data;
    }


}
