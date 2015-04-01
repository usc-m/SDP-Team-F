package sdp.comms.packets;

import jssc.SerialPort;
import jssc.SerialPortException;
import sdp.util.CircularByteBuffer;

/**
 * Created by Matthew on 16/01/2015.
 */
public class KickerDecreasePacket extends Packet {
    public static final byte ID = 'X';
    public static final int Length = 1;

    // Empty constructor
    public KickerDecreasePacket(){}

    @Override
    public byte getID() {
        return ID;
    }

    @Override
    public void writePacket(SerialPort sendPort) throws SerialPortException {
        sendPort.writeByte(ID);
    }

    @Override
    public void readPacket(CircularByteBuffer stream) {        
    }
}
