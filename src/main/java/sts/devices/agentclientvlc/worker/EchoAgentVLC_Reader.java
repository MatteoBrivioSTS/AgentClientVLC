package sts.devices.agentclientvlc.worker;

import sts.devices.agentclientvlc.helper.AgentLog;
import sts.devices.agentclientvlc.model.VLC_MSG;
import sts.drivers.worker.helper.FrameReader;
import sts.drivers.worker.helper.Tuple;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class EchoAgentVLC_Reader extends FrameReader {
    private static final int MAX_PAYLOAD = 1_000_000;

    private volatile boolean closed      = false;
    private volatile boolean running     = true;

    public EchoAgentVLC_Reader(DataInputStream in, int numOfPriority) {
        super(in, numOfPriority);
    }

    public void stop() { running = false; }
    public boolean isClosed() { return closed; }

    @Override
    public void run() {
        try {
            while (running)
            {
                if (getInChannel()!=null)
                {
                    VLC_MSG vlcMsg = readMessage(getInChannel());
                    switch (vlcMsg.getCommandCode()) {
                        case VLC_MSG.HELLO -> {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.HELLO, new Object()));
                            System.out.println("HELLO REQ ACK");
                        }
                        case VLC_MSG.NOP -> {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.NOP, new Object()));
                            System.out.println("NOP REQ ACK");
                        }
                        case VLC_MSG.READ -> {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.READ, vlcMsg));
                        }
                        case VLC_MSG.VIDEO_LIVE -> {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.VIDEO_LIVE, vlcMsg));
                            System.out.println(vlcMsg);
                        }
                        case VLC_MSG.VIDEO_PLBCK -> {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.VIDEO_PLBCK, vlcMsg));
                            System.out.println(vlcMsg);
                        }
                        case VLC_MSG.VIDEO_ALRM -> {
                            getPriorityTank().getPriorityPacketTank().get(W_EchoAgentVLC.MAX_PRIO).
                                    add(new Tuple<>(VLC_MSG.VIDEO_ALRM, vlcMsg));
                            System.out.println(vlcMsg);
                        }
                        default -> AgentLog.log("Reader: comando sconosciuto " + vlcMsg.getCommandCode());
                    }
                }
            }
        } catch (IOException e) {
            if (running) AgentLog.log("Reader: connessione chiusa (" + e.getClass().getSimpleName() + ")");
            running = false;
        }finally {
            closed = true;
        }
    }

    private VLC_MSG readMessage(DataInputStream in) throws IOException {
        byte[] header = new byte[12];
        in.readFully(header);
        ByteBuffer bb = ByteBuffer.wrap(header);
        int code = bb.getInt();
        int type = bb.getInt();
        int len  = bb.getInt();

        if (len < 0 || len > MAX_PAYLOAD) throw new IOException("Lunghezza non valida: " + len);

        byte[] payload = new byte[len];
        if (len > 0) in.readFully(payload);
        return new VLC_MSG(code, type, len, payload);   // codice e type reali, non forzati
    }

}
