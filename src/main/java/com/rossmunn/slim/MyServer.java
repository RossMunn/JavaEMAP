package com.rossmunn.slim;

import com.rossmunn.slim.network.PacketReceiver;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MyServer {
    DatagramSocket serverSocket;

    public static void main(String[] args) {
        MyServer server = new MyServer();
        server.start();
    }

    public void start() {
        if (serverSocket != null) return;

        try {
            //cant host multiple
            serverSocket = new DatagramSocket(SLIM.PORT);
            System.out.println("Now listening on port " + SLIM.PORT);

            System.out.println("Packet received:" + PacketReceiver.receivePacket(serverSocket));

        } catch (SocketException ex) {
            System.err.println(
                    "Failed to start the server. " +
                            "Is the port already taken?"
            );
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

