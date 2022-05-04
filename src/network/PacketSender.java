package network;

import packets.DataTypeId;
import packets.Magic;
import packets.Packet;
import data.UUIDUtil;
import data.VarLengthNumbers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public class PacketSender {

    public static void sendPacket(Packet packet, DatagramSocket socket, InetAddress address, int port) throws IOException {
        if(packet.getSnowflake()==null) packet.setSnowflake(UUIDUtil.asBytes(UUID.randomUUID()));
        byte[] packetBytes = getPacketBytes(packet);

        int sendBufferSize = 1024;
        int maxLength = 980; //max length of body data in chunk
        ByteBuffer sendBuffer = ByteBuffer.allocate(sendBufferSize);

        byte[] hash = new byte[8];

        int index = 0;
        int byteIndex = 0;

        int count = (int) Math.ceil(packetBytes.length / (float) maxLength);

        while(byteIndex != packetBytes.length) {
            sendBuffer.rewind(); //reset buffer

            // Compute the size of the body
            int payloadLength = Math.min(packetBytes.length - byteIndex, maxLength);

            // Magic
            sendBuffer.put(DataTypeId.MAGIC);
            sendBuffer.putInt(Magic.CHUNK);

            // Length
            sendBuffer.put(DataTypeId.SHORT);
            sendBuffer.putShort((short) payloadLength);

            // Snowflake
            sendBuffer.put(DataTypeId.FIXED_BYTES);
            sendBuffer.put(packet.getSnowflake());

            // Hash
            sendBuffer.put(DataTypeId.FIXED_BYTES);
            sendBuffer.put(hash);

            // Index
            sendBuffer.put(DataTypeId.INT);
            sendBuffer.putInt(index);

            // Count
            sendBuffer.put(DataTypeId.INT);
            sendBuffer.putInt(count);

            // Payload (Body)
            sendBuffer.put(packetBytes, byteIndex, payloadLength);
            byteIndex += payloadLength;

            int errorCount = 0;
            while (true) {

                //send packet
                sendBuffer.flip();
                DatagramPacket packetToSend = new DatagramPacket(
                        sendBuffer.array(),
                        0,
                        sendBuffer.remaining(),
                        address,
                        port
                );

                socket.send(packetToSend);

                byte[] receiveAckData = new byte[44];
                if(receiveAck(socket, receiveAckData, packet.getSnowflake(), hash, index)){
                    index++;
                    break;
                }
                errorCount++;
                if(errorCount >= 5){
                    return;
                }

            }
        }
    }

    private static byte[] getPacketBytes(Packet packet) throws IOException {

        //write Header and Body of packet

        ByteArrayOutputStream packetData = new ByteArrayOutputStream();
        DataOutputStream packetDataOutput = new DataOutputStream(packetData);

        packetDataOutput.write(packet.getSnowflake());
        VarLengthNumbers.writeVarInt(packetDataOutput::writeByte, packet.getPacketId());

        PacketOutputStream packetStream = new PacketOutputStream(packetDataOutput);
        packet.writeData(packetStream);

        int size = packetData.size(); //get length of packet

        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream(size+10);
        DataOutputStream output = new DataOutputStream(outputBytes);

        VarLengthNumbers.writeVarInt(output::writeByte, size);
        output.write(packetData.toByteArray());

        return Arrays.copyOf(outputBytes.toByteArray(), output.size());
    }

    private static boolean receiveAck(DatagramSocket socket, byte[] receiveBufferData, byte[] snowflake, byte[] hash, int index) throws IOException {

        DatagramPacket incomingPacket = new DatagramPacket(receiveBufferData, receiveBufferData.length);
        try {
            socket.receive(incomingPacket);
        }catch(SocketTimeoutException e){
            return false;
        }
        byte[] data = incomingPacket.getData();


        //check snowflake
        for(int i = 0; i < 16; i++){
            if(data[i]!=snowflake[i]) return false;
        }

        //check hash
        for(int i = 0; i < 8; i++){
            if(data[i+16]!=hash[i]) return false;
        }

        //check index
        int indexToCheck = ByteBuffer.wrap(receiveBufferData, 24, 4).getInt();
        if(indexToCheck != index) return false;

        //all good
        return true;
    }

}
