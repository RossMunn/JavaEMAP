package packets;

public class Packets {

    public static Packet getPacket(int id) {
        switch (id) {
            case 0: return new DefaultPacket();
        }

        return null;
    }

}
