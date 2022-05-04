import Packets.ArrayPacket;
import Packets.DefaultPacket;
import Packets.Packet;
import network.PacketSender;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class MyClient {

    public static final String SERVER_HOSTNAME = "localhost";

    DatagramSocket clientSocket;

    public static void main(String[] args) throws IOException {
        MyClient client = new MyClient();
        client.start();
    }

    public void start() throws IOException {

        Scanner scanner = new Scanner(System.in);

        // Initialize the client socket.
        try {
            clientSocket = new DatagramSocket();

        } catch(SocketException ex) {
            System.err.println(
                    "Failed to initialize the client socket. " +
                            "Is the port right?"
            );
            ex.printStackTrace();
        }

        final InetAddress serverAddress;
        try {
            serverAddress = InetAddress.getByName(SERVER_HOSTNAME);
        } catch (UnknownHostException ex) {
            System.err.println("Unknown host: " + SERVER_HOSTNAME);
            ex.printStackTrace();
            return;
        }

        Packet packet = new ArrayPacket(1, 2, 3, 4, 5);//new DefaultPacket("George", "says hi!");
        clientSocket.setSoTimeout(1000);
        PacketSender.sendPacket(packet, clientSocket, serverAddress, EMAP.PORT);
    }



}

