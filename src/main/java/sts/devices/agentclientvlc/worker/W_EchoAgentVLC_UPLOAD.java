package sts.devices.agentclientvlc.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import sts.devices.agentclientvlc.model.ReadPayload;
import sts.devices.agentclientvlc.model.VLC_MSG;
import sts.drivers.worker.helper.ConcreteStatus;
import sts.drivers.worker.helper.Tuple;
import sts.drivers.worker.helper.WorkerStatus;

import java.io.IOException;

public class W_EchoAgentVLC_UPLOAD implements ConcreteStatus {
    private WorkerStatus ws;
    private W_EchoAgentVLC echoWorker;

    private int steps;

    private long lastNopSent = 0;

    @Override
    public void business(WorkerStatus workerStatus) {
        if (ws == null)
        {
            System.out.println("upload");
            setWs(workerStatus);
        }
        if (echoWorker == null)
        {
            setEchoWorker((W_EchoAgentVLC) getWs().getEchoWorker());
        }
        try
        {
            long now = System.currentTimeMillis();
            switch (steps)
            {
                case 0 ->
                {

                    getEchoWorker().getFrameSender().getPriorityDelayedTank().getPriorityPacketTank()
                                .get(W_EchoAgentVLC.MAX_PRIO).add(new Tuple<>(VLC_MSG.READ,new Object()));
                        echoWorker.setNop(now);
                        steps++;
                }
                case 1 ->
                {
                    EchoAgentVLC_Reader reader = (EchoAgentVLC_Reader) getEchoWorker().getFrameReader();
                    Tuple<Integer,Object> packet = reader.getPriorityTank().getPacket();
                    if (packet!=null)
                    {
                        switch (packet.getFirstValue())
                        {
                            case VLC_MSG.NOP ->
                            {
                                getEchoWorker().setNop(now);
                            }
                            case VLC_MSG.READ_PL ->
                            {
                                handleReadPayload((VLC_MSG) packet.getSecondValue());
                                getEchoWorker().setNop(now);
                                getEchoWorker().goToNormal();
                            }
//                            default ->
//                            {
//                                getEchoWorker().getFrameReader().getPriorityTank().
//                                        putPacket(W_EchoAgentVLC.MAX_PRIO,new Tuple<>(packet.getFirstValue(),packet.getSecondValue()));
//                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void handleReadPayload(VLC_MSG msg) throws IOException {

        ReadPayload readPayload = new ObjectMapper().readValue(msg.getPayload(), ReadPayload.class);
        if (readPayload.getVls() != null) {echoWorker.getVlcs().putAll(readPayload.getVls());}
        if (readPayload.getVss() != null) {echoWorker.getVss().putAll(readPayload.getVss());}

    }

    public WorkerStatus getWs() {
        return ws;
    }

    public void setWs(WorkerStatus ws) {
        this.ws = ws;
    }

    public W_EchoAgentVLC getEchoWorker() {
        return echoWorker;
    }

    public void setEchoWorker(W_EchoAgentVLC echoWorker) {
        this.echoWorker = echoWorker;
    }

    public long getLastNopSent() {
        return lastNopSent;
    }

    public void setLastNopSent(long lastNopSent) {
        this.lastNopSent = lastNopSent;
    }
}
