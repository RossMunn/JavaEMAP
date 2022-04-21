import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MyClient {

    public static final String SERVER_HOSTNAME = "localhost";

    DatagramSocket clientSocket;

    public static void main(String[] args) {
        var client = new MyClient();
        client.start();
    }

    public void start() {

        var scanner = new Scanner(System.in);

        // Initialize the client socket.
        try {
            clientSocket = new DatagramSocket();

        } catch(SocketException ex) {
            System.err.println(
                    "Failed to initialize the client socket. " +
                            "Is there a free port?"
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

        System.out.print("> ");

        byte[] buffer = new byte[256];

        while (!clientSocket.isClosed()) {

            try {
                if (System.in.available() > 0) {
                    String message = scanner.nextLine();

                    if (message.equalsIgnoreCase("exit")) {
                        var exitBuffer = message.getBytes(StandardCharsets.UTF_8);
                        clientSocket.send(new DatagramPacket(
                            exitBuffer,
                            exitBuffer.length,
                            serverAddress,
                            EMAP.PORT
                        ));
                        clientSocket.close();
                        break;
                    }

                    // Otherwise, send the message.
                    var messageBuffer = message.getBytes(StandardCharsets.UTF_8);
                    clientSocket.send(new DatagramPacket(
                        messageBuffer,
                        messageBuffer.length,
                        serverAddress,
                        EMAP.PORT
                    ));

                    var incomingPacket = new DatagramPacket(
                        buffer,
                        buffer.length,
                        serverAddress,
                        EMAP.PORT
                    );
                    clientSocket.receive(incomingPacket);

                    // Convert the raw bytes into a String.
                    // See the server for more details on this.
                    var messageResponse = new String(
                        incomingPacket.getData(), 0, incomingPacket.getLength(),
                        StandardCharsets.UTF_8
                    );

                    System.out.println("Server: " + messageResponse);

                    System.out.print("> ");

                }

            } catch (IOException ex) {
                System.err.println(
                        "A communication error occurred with the server."
                );
                ex.printStackTrace();
                break;
            }

        }

    }

}
