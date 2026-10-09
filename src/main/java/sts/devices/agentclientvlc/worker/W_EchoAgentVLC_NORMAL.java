package sts.devices.agentclientvlc.worker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import sts.devices.agentclientvlc.model.MRC.Vsmanager;
import sts.devices.agentclientvlc.model.ReadPayload;
import sts.devices.agentclientvlc.model.VLC_MSG;
import sts.devices.agentclientvlc.model.MRC.VlcManager;
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
                switch (packet.getFirstValue()) {
                    case VLC_MSG.NOP -> {
                        echoWorker.setNop(now);
                    }
                    case VLC_MSG.READ ->
                    {

                    }
                    case VLC_MSG.VIDEO_LIVE ->
                    {
                            VLC_MSG msg = (VLC_MSG) packet.getSecondValue();
                            startLive(msg);
                    }
                    case VLC_MSG.VIDEO_PLBCK ->
                    {
                            VLC_MSG msg = (VLC_MSG) packet.getSecondValue();
                            startPlbck(msg);
                    }
                    case VLC_MSG.VIDEO_ALRM ->
                    {
                            VLC_MSG msg = (VLC_MSG) packet.getSecondValue();
                            startPopUpAlarm(msg);
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

    private void startLive(VLC_MSG msg) throws IOException {
//        VlcManager vlc = new ObjectMapper().readValue(msg.getPayload(), VlcManager.class);
        Vsmanager vs = new ObjectMapper().readValue(msg.getPayload(),Vsmanager.class);

        String url = "rtsp://" + URLEncoder.encode(vs.getUserName(), StandardCharsets.UTF_8)
                + ":" + URLEncoder.encode(vs.getPassword(), StandardCharsets.UTF_8)
                + "@" + vs.getIPaddress() + ":" + vs.getIPport()
                + "/cam/realmonitor?channel=1&subtype=0";

        echoWorker.getReaderVideo().startStream(vs.getDescription(), url);
        System.out.println("[CLIENT] avvio stream " + vs.getIPaddress());
    }

    private void startPlbck(VLC_MSG msg)
    {

    }

    private void startPopUpAlarm(VLC_MSG msg) throws IOException {
        Vsmanager vs = new ObjectMapper().readValue(msg.getPayload(),Vsmanager.class);

        String url = "rtsp://" + URLEncoder.encode(vs.getUserName(), StandardCharsets.UTF_8)
                + ":" + URLEncoder.encode(vs.getPassword(), StandardCharsets.UTF_8)
                + "@" + vs.getIPaddress() + ":" + vs.getIPport()
                + "/cam/realmonitor?channel="+vs.getId_videostream()+"&subtype=0";
        echoWorker.getReaderVideo().startStream(vs.getDescription(), url);
        System.out.println("[CLIENT] avvio stream " + vs.getIPaddress());
    }

    public WorkerStatus getWs() { return ws; }
    public void setWs(WorkerStatus ws) { this.ws = ws; }
    public W_EchoAgentVLC getEchoWorker() {return echoWorker;}
    public void setEchoWorker(W_EchoAgentVLC echoWorker) {this.echoWorker = echoWorker;}
}
//secondlife