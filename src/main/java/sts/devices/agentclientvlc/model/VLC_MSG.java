package sts.devices.agentclientvlc.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class VLC_MSG {
    //header
    private int commandCode;
    private int type;
    private int dataLenght;

    //payload
    private byte[] payload;

    public final static int HELLO = 1;
    public final static int NOP = 0;
    public final static int READ = 2;
    public static final int VIDEO_REQUEST_KEY = 5;

    public VLC_MSG() {}

    public VLC_MSG(int commandCode, int type, int dataLenght, byte[] payload) {
        this.commandCode = commandCode;
        this.type = type;
        this.dataLenght = dataLenght;
        this.payload = payload;
    }

    public VLC_MSG(int commandCode, int type, int dataLenght) {
        this.commandCode = commandCode;
        this.type = type;
        this.dataLenght = dataLenght;
    }

    public byte[] headerToByte(VLC_MSG vlc)
    {
        ByteBuffer bb = ByteBuffer.allocate(12);
        bb.putInt(commandCode);
        bb.putInt(type);
        bb.putInt(dataLenght);

        return bb.array();
    }

    public static VLC_MSG fromHeader(byte[] header) {
        ByteBuffer bb = ByteBuffer.wrap(header);

        int commandCode = bb.getInt();
        int type = bb.getInt();
        int dataLenght = bb.getInt();

        return new VLC_MSG(commandCode, type, dataLenght, null);
    }

    //Getter and setter

    public int getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDataLenght() {
        return dataLenght;
    }

    public void setDataLenght(int dataLenght) {
        this.dataLenght = dataLenght;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    //toString()
    @Override
    public String toString() {
        return "VLC_MSG{" +
                "commandCode=" + commandCode +
                ", type=" + type +
                ", dataLenght=" + dataLenght +
                ", payload=" + Arrays.toString(payload) +
                '}';
    }
}
