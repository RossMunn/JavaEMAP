import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class MyServer {
    DatagramSocket serverSocket;

    public static void main(String[] args) {
        var server = new MyServer();
        server.start();
    }

    public void start() {
        if (serverSocket != null) return;

        try {

            serverSocket = new DatagramSocket(ScuffedProtocol.PORT);
            System.out.println(
                    "Now listening on port " + ScuffedProtocol.PORT + "!"
            );

            byte[] buffer = new byte[256];

            while (!serverSocket.isClosed()) {

                try {

                    var incomingPacket = new DatagramPacket(buffer, buffer.length);
                    serverSocket.receive(incomingPacket);
                    var clientAddress = incomingPacket.getAddress();
                    var clientPort = incomingPacket.getPort();
                    var message = new String(

                        incomingPacket.getData(),
                        0,
                        incomingPacket.getLength(),
                        StandardCharsets.UTF_8
                    );

                    if (message.equalsIgnoreCase("exit")) {
                        serverSocket.close();
                        serverSocket = null;
                        break;
                    }

                    System.out.println("Client: " + message);

                    var outgoingPacket = new DatagramPacket(
                        buffer, incomingPacket.getLength(),
                        clientAddress, clientPort
                    );

                    // Send the packet.
                    serverSocket.send(outgoingPacket);

                }  catch (IOException ex) {
                    System.err.println(
                            "Communication error. " +
                                    "Is there a problem with the client?"
                    );
                }

            }

        } catch (SocketException ex) {
            System.err.println(
                    "Failed to start the server. " +
                            "Is the port already taken?"
            );
            ex.printStackTrace();
        }

    }

}
