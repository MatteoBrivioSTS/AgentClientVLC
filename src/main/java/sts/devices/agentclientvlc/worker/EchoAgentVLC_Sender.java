package sts.devices.agentclientvlc.worker;

import sts.devices.agentclientvlc.model.VLC_MSG;
import sts.drivers.worker.helper.FrameSender;
import sts.drivers.worker.helper.Tuple;

import java.io.DataOutputStream;
import java.io.IOException;

public class EchoAgentVLC_Sender extends FrameSender {
    private volatile boolean running = true;

    public EchoAgentVLC_Sender(DataOutputStream out, int numOfPriority) {
        super(out, numOfPriority);
    }

    public void stop() { running = false; }


    @Override
    public void run() {
        try {
            while (running) {
                Tuple<Integer, Object> packet = getPriorityDelayedTank().getPacket();
                if (packet!=null)
                {
                    switch (packet.getFirstValue())
                    {
                        case VLC_MSG.HELLO ->
                        {
                            VLC_MSG vlcHelloReq = new VLC_MSG(VLC_MSG.HELLO,0,0);
                            send(vlcHelloReq.headerToByte(vlcHelloReq));
                        }
                        case VLC_MSG.NOP ->
                        {
                            VLC_MSG vlcNopReq = new VLC_MSG(VLC_MSG.NOP,0,0);
                            send(vlcNopReq.headerToByte(vlcNopReq));
                        }
                        case VLC_MSG.READ ->
                        {
                            VLC_MSG vlcNopReq = new VLC_MSG(VLC_MSG.READ,0,0);
                            send(vlcNopReq.headerToByte(vlcNopReq));
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            if (running) System.err.println("[Sender] scrittura fallita: " + e.getMessage());
            running = false;   // il sender si ferma, la connessione è comunque finita
            throw new RuntimeException(e);
        }
    }

    @Override
    public void send(byte[] outpack) {
        try {
            getOutChannel().write(outpack);
            getOutChannel().flush();
        } catch (IOException e) {
            if (running) System.err.println("[Sender] scrittura fallita: " + e.getMessage());
            running = false;
        }
    }
}