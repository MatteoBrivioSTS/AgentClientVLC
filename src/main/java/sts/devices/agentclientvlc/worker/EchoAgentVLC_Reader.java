package sts.devices.agentclientvlc.worker;

import sts.devices.agentclientvlc.model.VLC_MSG;
import sts.drivers.worker.helper.FrameReader;
import sts.drivers.worker.helper.Tuple;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EchoAgentVLC_Reader extends FrameReader {
    private volatile boolean running = true;

    public EchoAgentVLC_Reader(DataInputStream in, int numOfPriority) {
        super(in, numOfPriority);
    }

    public void stop() { running = false; }

    @Override
    public void run() {
        try {
            while (running)
            {
                if (getInChannel()!=null)
                {
                    VLC_MSG vlcHeader = byteHeaderSerializer(getInChannel());
                    switch (vlcHeader.getCommandCode())
                    {
                        case VLC_MSG.HELLO ->
                        {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.HELLO,new Object()));
                            System.out.println("HELLO REQ ACK");
                        }
                        case VLC_MSG.NOP ->
                        {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.NOP,new Object()));
                            System.out.println("NOP REQ ACK");
                        }
                        case VLC_MSG.READ ->
                        {
                            VLC_MSG vlcMsg = getPayload(vlcHeader);
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.READ,vlcMsg));
                        }
                        case VLC_MSG.VIDEO_REQUEST_KEY ->
                        {
                            VLC_MSG vlcMsg = getPayload(vlcHeader);
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.VIDEO_REQUEST_KEY,vlcMsg));
                            System.out.println(vlcMsg);
                        }
                    }
                }
            }
        } catch (IOException e) {
            if (running) System.err.println("[Sender] scrittura fallita: " + e.getMessage());
            running = false;   // il sender si ferma, la connessione è comunque finita
        }
    }

    private VLC_MSG getPayload(VLC_MSG header) throws IOException {
        int dataLenght = header.getDataLenght();
        byte[] payload = new byte[dataLenght];
        getInChannel().readFully(payload);
        VLC_MSG vlcMsg = new VLC_MSG(VLC_MSG.READ,1,dataLenght,payload);
        return vlcMsg;
    }


    private VLC_MSG byteHeaderSerializer(DataInputStream dataInputStream) throws IOException {
        DataInputStream in = new DataInputStream(dataInputStream);
        byte[] header = new byte[12];
        in.readFully(header);
        ByteBuffer bb = ByteBuffer.wrap(header);
        VLC_MSG vlcHeader = new VLC_MSG(bb.getInt(),bb.getInt(),bb.getInt());
        return  vlcHeader;
    }
}
