package sts.devices.agentclientvlc.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import sts.devices.agentclientvlc.helper.AgentLog;
import sts.devices.agentclientvlc.model.MRC.Vsmanager;
import sts.devices.agentclientvlc.model.VLC_MSG;
import sts.drivers.worker.helper.ConcreteStatus;
import sts.drivers.worker.helper.Tuple;
import sts.drivers.worker.helper.WorkerStatus;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        long now = System.currentTimeMillis();
        try
        {
            EchoAgentVLC_Reader reader = (EchoAgentVLC_Reader) getEchoWorker().getFrameReader();
            if(reader.isClosed()||now-echoWorker.getNop()>W_EchoAgentVLC.NOP_TIMEOUT)
            {
                System.out.println("NORMAL CONNESIONE PERSA");
                echoWorker.disconnect();
                echoWorker.goToInit();
                return;
            }

            if (now-lastNopSent> W_EchoAgentVLC.NOP_GAP)
            {
                getEchoWorker().getFrameSender().getPriorityDelayedTank().getPriorityPacketTank()
                        .get(W_EchoAgentVLC.MAX_PRIO).add(new Tuple<>(VLC_MSG.NOP,new Object()));
                lastNopSent = now;
            }

            Tuple<Integer,Object> packet = reader.getPriorityTank().getPacket();

            if (packet!=null)
            {
                echoWorker.setNop(now);
                switch (packet.getFirstValue()) {
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
                    default -> AgentLog.log("Normal comando ignorato: "+packet.getFirstValue());
                }
            }
        } catch (Exception e) {
            AgentLog.log("NORMAL errore: " + e);
        }
    }

    private void startLive(VLC_MSG msg) throws IOException {
        Vsmanager vs = new ObjectMapper().readValue(msg.getPayload(),Vsmanager.class);
        if (vs!=null)
        {
            String url = "rtsp://" + URLEncoder.encode(vs.getUserName(), StandardCharsets.UTF_8)
                    + ":" + URLEncoder.encode(vs.getPassword(), StandardCharsets.UTF_8)
                    + "@" + vs.getIPaddress() + ":" + vs.getIPport()
                    + "/cam/realmonitor?channel="+vs.getId_videostream()+"&subtype=0";

            System.out.println("[CLIENT] avvio stream " + vs.getIPaddress());
            echoWorker.getMultiReaderVideo().startStream(vs.getPdn(),vs.getDescription(),url,Integer.parseInt(vs.getPopUpTiming()));
        }
    }

    private void startPlbck(VLC_MSG msg) throws IOException {
        Vsmanager vs = new ObjectMapper().readValue(msg.getPayload(),Vsmanager.class);
        if (vs!=null)
        {
            LocalDateTime start = LocalDateTime.parse(vs.getDate()).minusSeconds(Long.parseLong(vs.getPlayback()));
            LocalDateTime end = LocalDateTime.parse(vs.getDate()).plusSeconds(Long.parseLong(vs.getPlayback()));
            DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
            String startTime = start.format(dtf);
            String endTime = end.format(dtf);

            String url = "rtsp://" + vs.getUserName() + ":" + vs.getPassword() + "@" + vs.getIPaddress() + ":" + vs.getIPport()
                    + "/cam/playback?channel="+vs.getId_videostream()+"&subtype=0"+"&starttime="+startTime+"&endtime="+endTime;

            System.out.println("[CLIENT] avvio stream " + vs.getIPaddress()+" url: "+url);
            echoWorker.getMultiReaderVideo().startStream(vs.getPdn(),vs.getDescription(),url,Integer.parseInt(vs.getPopUpTiming()));
        }
    }

    private void startPopUpAlarm(VLC_MSG msg) throws IOException {
        Vsmanager vs = new ObjectMapper().readValue(msg.getPayload(),Vsmanager.class);
        if (vs!=null)
        {
            String url = "rtsp://" + URLEncoder.encode(vs.getUserName(), StandardCharsets.UTF_8)
                    + ":" + URLEncoder.encode(vs.getPassword(), StandardCharsets.UTF_8)
                    + "@" + vs.getIPaddress() + ":" + vs.getIPport()
                    + "/cam/realmonitor?channel="+vs.getId_videostream()+"&subtype=0";

            System.out.println("[CLIENT] avvio stream " + vs.getIPaddress());
            echoWorker.getMultiReaderVideo().startStream(vs.getPdn(),vs.getDescription(),url,Integer.parseInt(vs.getPopUpTiming()));
        }
    }

    public WorkerStatus getWs() { return ws; }
    public void setWs(WorkerStatus ws) { this.ws = ws; }
    public W_EchoAgentVLC getEchoWorker() {return echoWorker;}
    public void setEchoWorker(W_EchoAgentVLC echoWorker) {this.echoWorker = echoWorker;}
}
