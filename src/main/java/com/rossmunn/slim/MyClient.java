package com.rossmunn.slim;

import com.rossmunn.slim.network.PacketReceiver;
import com.rossmunn.slim.packets.DefaultPacket;
import com.rossmunn.slim.packets.Packet;
import com.rossmunn.slim.network.PacketSender;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class MyClient {

    public static final String SERVER_HOSTNAME = "system.bigbrainstuffs.com";

    DatagramSocket clientSocket;

    public static void main(String[] args) throws IOException {
        MyClient client = new MyClient();
        client.connect();
    }

    public void connect() throws IOException {

        Scanner scanner = new Scanner(System.in);

        // Initialize the client socket.
        try {
            clientSocket = new DatagramSocket();

        } catch(SocketException ex) {
            System.err.println(
                    "Failed to initialize the client socket. " + "Is the port right?"
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

        var listenerThread = new Thread(() -> {
            while (!clientSocket.isClosed()) {
                try {
                    Packet packet;
                    if ((packet = PacketReceiver.receiveChunk(clientSocket)) != null) {
                        // Packet received.
                        System.out.println(packet);
                    }

                    System.out.println();
                } catch (IOException ex) {
                    System.err.println("Error receiving packet.");
                    ex.printStackTrace();
                }
            }

            System.out.println();
            System.out.println("Connection closed.");
        });

        listenerThread.start();

        Packet packet = new DefaultPacket("Samderer Mold", "I mean test ahaha you saw nothing");
        PacketSender.sendPacket(packet, clientSocket, serverAddress, SLIM.PORT);

    }

}

