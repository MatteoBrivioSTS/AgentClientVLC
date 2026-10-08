package sts.devices.agentclientvlc.worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sts.devices.agentclientvlc.model.VLC_MSG;
import sts.devices.agentclientvlc.model.VlcManager;
import sts.drivers.worker.helper.ConcreteStatus;
import sts.drivers.worker.helper.Tuple;
import sts.drivers.worker.helper.WorkerStatus;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class W_EchoAgentVLC_NORMAL implements ConcreteStatus {

    private WorkerStatus ws;
    private W_EchoAgentVLC echoWorker;

    private long lastNopSent = 0;

    @Override
    public void business(WorkerStatus workerStatus) {
        if (ws == null)
        {
            System.out.println("normal");
            setWs(workerStatus);
        }
        if (echoWorker == null)
        {
            setEchoWorker((W_EchoAgentVLC) getWs().getEchoWorker());
        }
        try
        {
            long now = System.currentTimeMillis();
            EchoAgentVLC_Reader reader = (EchoAgentVLC_Reader) getEchoWorker().getFrameReader();
            Tuple<Integer,Object> packet = reader.getPriorityTank().getPacket();

            if (packet!=null)
            {
                switch (packet.getFirstValue())
                {
                    case VLC_MSG.NOP ->
                    {
                        echoWorker.setNop(now);
                    }
                    case VLC_MSG.READ ->
                    {
                        VLC_MSG msg = (VLC_MSG) packet.getSecondValue();
                        Map<String, VlcManager> vlcs = new ObjectMapper()
                                .readValue(msg.getPayload(), new TypeReference<Map<String, VlcManager>>() {});
                        if (vlcs!=null){echoWorker.getVlcs().putAll(vlcs);}
                    }
                    case VLC_MSG.VIDEO_REQUEST_KEY ->
                    {
                        VLC_MSG msg = (VLC_MSG) packet.getSecondValue();
                        startVideo(msg);
                    }
                }
            }
            if (now-lastNopSent> W_EchoAgentVLC.HELLO_GAP)
            {
                getEchoWorker().getFrameSender().getPriorityDelayedTank().getPriorityPacketTank()
                        .get(W_EchoAgentVLC.MAX_PRIO).add(new Tuple<>(VLC_MSG.NOP,new Object()));
                lastNopSent = now;
            }
            if (now - echoWorker.getNop() > W_EchoAgentVLC.HELLO_TIMEOUT)
            {
                System.err.println("[NORMAL] nessuna risposta dal server, torno in INIT");
                echoWorker.disconnect();
                echoWorker.getWs().setStatus(new W_EchoAgentVLC_INIT());
                echoWorker.getWs().setEchoWorker(echoWorker);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startVideo(VLC_MSG msg) throws IOException {
        VlcManager vlc = new ObjectMapper().readValue(msg.getPayload(), VlcManager.class);

        String url = "rtsp://" + URLEncoder.encode(vlc.getUserName(), StandardCharsets.UTF_8)
                + ":" + URLEncoder.encode(vlc.getPassword(), StandardCharsets.UTF_8)
                + "@" + vlc.getIp() + ":" + vlc.getPort()
                + "/cam/realmonitor?channel=1&subtype=0";

        echoWorker.getReaderVideo().startStream(vlc.getDescription(), url);
        System.out.println("[CLIENT] avvio stream " + vlc.getIp());   // non stampare l'url: ha la password
    }


    public WorkerStatus getWs() { return ws; }
    public void setWs(WorkerStatus ws) { this.ws = ws; }
    public W_EchoAgentVLC getEchoWorker() {return echoWorker;}
    public void setEchoWorker(W_EchoAgentVLC echoWorker) {this.echoWorker = echoWorker;}
}
//secondlife