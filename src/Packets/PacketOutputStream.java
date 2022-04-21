package Packets;

import data.VarLengthNumbers;

import java.io.DataOutputStream;
import java.io.IOException;

public class PacketOutputStream {

        private DataOutputStream stream;

        public PacketOutputStream(DataOutputStream stream){
            this.stream = stream;
        }

        public void writeShort(short value) throws IOException {

        }

        public void writeVarInt(int value) throws IOException {
            VarLengthNumbers.writeVarInt(stream::write, value);
        }

        public void writeVarLong(long value) throws IOException {
            VarLengthNumbers.writeVarLong(stream::write, value);
    }
}
