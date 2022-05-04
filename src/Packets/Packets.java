package Packets;

public class Packets {

    public static Packet getPacket(int packetID) {
        switch (packetID) {
            case 0:
                return new DefaultPacket();
            case 1:
                return new ArrayPacket();
        }
        return null;
    }

}

