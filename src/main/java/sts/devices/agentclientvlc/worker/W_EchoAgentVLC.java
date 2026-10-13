package sts.devices.agentclientvlc.worker;

import sts.devices.agentclientvlc.helper.AgentLog;
import sts.devices.agentclientvlc.model.MRC.Vsmanager;
import sts.devices.agentclientvlc.readervideo.MultiReaderVideo;
import sts.drivers.worker.SS_EchoClient;
import sts.drivers.worker.helper.WorkerStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class W_EchoAgentVLC extends SS_EchoClient {

    private final String host;
    private final int port;
    private final String agentName;
    private long backoff = BACKOFF_MIN;
    private final MultiReaderVideo multiReaderVideo = new MultiReaderVideo();

    private WorkerStatus ws;
    private volatile boolean running = true;

    private static final int READER_SENDER_PRIO = 2;
    public static final int MAX_PRIO = 0;

    public static final int HELLO_TIMEOUT = 5000;
    public static final int NOP_GAP = 30000;
    public static final int NOP_TIMEOUT = 4*NOP_GAP;
    private static final int BACKOFF_MIN = 2_000, BACKOFF_MAX = 30_000;

    private volatile long nop = System.currentTimeMillis();

    private ConcurrentHashMap<String, Vsmanager> vss = new ConcurrentHashMap<>();

    public W_EchoAgentVLC(String agentId, String host, int port) {
        super();
        setName(agentId);
        this.agentName = agentId;
        this.host = host;
        this.port = port;
        this.ws = new WorkerStatus(new W_EchoAgentVLC_INIT(), this);
    }

    @Override
    public void run() {
        AgentLog.log("Agent"+agentName+" avviato");
        while (running) {
            try {
                getWs().getStatus().business(getWs());
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }catch (Exception e) {
                AgentLog.log("Errore nel ciclo stati: " + e);
            }
        }
        disconnect();
    }

    // CONNECTION
    public void openConnection() throws IOException {
        disconnect();
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(host, port), 5000);
            s.setTcpNoDelay(true);
            s.setKeepAlive(true);
            setSocket(s);
            setDis(new DataInputStream(s.getInputStream()));
            setDos(new DataOutputStream(s.getOutputStream()));
        } catch (IOException e) {
            try { s.close(); } catch (IOException ignored) {}
            throw e;
        }

        EchoAgentVLC_Reader reader = new EchoAgentVLC_Reader(getDis(), READER_SENDER_PRIO);
        EchoAgentVLC_Sender sender = new EchoAgentVLC_Sender(getDos(), READER_SENDER_PRIO);
        setFrameReader(reader);
        setFrameSender(sender);

        Thread tr = new Thread(reader, "VLC-Reader"); tr.setDaemon(true); tr.start();
        Thread ts = new Thread(sender, "VLC-Sender"); ts.setDaemon(true); ts.start();
        System.out.println("[CLIENT] connesso a " + host + ":" + port);
    }

    public synchronized void disconnect() {
        if (getFrameReader() != null) ((EchoAgentVLC_Reader) getFrameReader()).stop();
        if (getFrameSender() != null) ((EchoAgentVLC_Sender) getFrameSender()).stop();
        if (getSocket() != null) closeConnection();
    }

    public long nextDelay() {
        long jitter = ThreadLocalRandom.current().nextLong(0, backoff / 4 + 1);
        long d = backoff + jitter;
        backoff = Math.min(backoff * 2, BACKOFF_MAX);
        return d;
    }
    public void resetBackoff() { backoff = BACKOFF_MIN; }

    public void goToInit() {
        ws.setStatus(new W_EchoAgentVLC_INIT());
        ws.setEchoWorker(this);
    }

    public void goToUpload() {
        getWs().setStatus(new W_EchoAgentVLC_UPLOAD());
        getWs().setEchoWorker(this);
    }

    public void goToNormal() {
        resetBackoff();
        ws.setStatus(new W_EchoAgentVLC_NORMAL());
        ws.setEchoWorker(this);
    }

    public void shutdown() {
        running = false;
        disconnect();
        multiReaderVideo.release();
    }

    public WorkerStatus getWs() { return ws; }
    public void setWs(WorkerStatus ws) { this.ws = ws; }
    public String getAgentName() { return agentName; }

    public long getNop() {
        return nop;
    }

    public void setNop(long nop) {
        this.nop = nop;
    }

    public ConcurrentHashMap<String, Vsmanager> getVss() {
        return vss;
    }

    public void setVss(ConcurrentHashMap<String, Vsmanager> vss) {
        this.vss = vss;
    }

    public MultiReaderVideo getMultiReaderVideo() {
        return multiReaderVideo;
    }
}
