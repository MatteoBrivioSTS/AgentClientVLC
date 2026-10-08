package sts.devices.agentclientvlc.worker;

import sts.devices.agentclientvlc.model.VlcManager;
import sts.devices.agentclientvlc.readervideo.ReaderVideo;
import sts.drivers.worker.SS_EchoClient;
import sts.drivers.worker.helper.Tuple;
import sts.drivers.worker.helper.WorkerStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class W_EchoAgentVLC extends SS_EchoClient {

    private ReaderVideo readerVideo;
    private final String host;
    private final int port;
    private final String agentId;

    private WorkerStatus ws;
    private volatile boolean running = true;

    private static final int READ_SEND_PRIO = 2;
    public static final int MAX_PRIO = 0;

    public static final int HELLO_TIMEOUT = 300000;
    public static final int HELLO_GAP = 60000;

    private volatile long nop = System.currentTimeMillis();

    public ConcurrentHashMap<String, VlcManager> vlcs = new ConcurrentHashMap<>();

    public W_EchoAgentVLC(String agentId, String host, int port) {
        super();
        setName(agentId);
        this.agentId = agentId;
        this.host = host;
        this.port = port;
        this.ws = new WorkerStatus(new W_EchoAgentVLC_INIT(), this);
    }

    @Override
    public void run() {
        while (running) {
            try {
                getWs().getStatus().business(getWs());
                Thread.sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
        disconnect();
    }

    // CONNECTION
    public void openConnection() throws IOException {
        disconnect();   // ferma reader/sender vecchi e chiude il socket, se esistono
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(host, port), 5000);
            s.setTcpNoDelay(true);
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            setSocket(s);
            setDis(in);
            setDos(out);
        } catch (IOException e) {
            try { s.close(); } catch (IOException ignored) {}
            throw e;
        }

        EchoAgentVLC_Reader reader = new EchoAgentVLC_Reader(getDis(), READ_SEND_PRIO);
        EchoAgentVLC_Sender sender = new EchoAgentVLC_Sender(getDos(), READ_SEND_PRIO);
        setFrameReader(reader);
        setFrameSender(sender);

        Thread tr = new Thread(reader, "VLC-Reader"); tr.setDaemon(true); tr.start();
        Thread ts = new Thread(sender, "VLC-Sender"); ts.setDaemon(true); ts.start();
        System.out.println("[CLIENT] connesso a " + host + ":" + port);
    }

    public void disconnect() {
        new Exception("disconnect chiamato da qui").printStackTrace();
        if (getFrameReader() != null) ((EchoAgentVLC_Reader) getFrameReader()).stop();
        if (getFrameSender() != null) ((EchoAgentVLC_Sender) getFrameSender()).stop();
        if (getSocket() != null) closeConnection();
    }

    // HELPERS FOR STATES
    public Tuple<Integer, Object> poll() {
        return getFrameReader().getPriorityTank().getPacket();
    }

    // WORKER STATUS
    public void goToInit() {
        getWs().setStatus(new W_EchoAgentVLC_INIT());
        getWs().setEchoWorker(this);
    }

    public void goToNormal() {
        getWs().setStatus(new W_EchoAgentVLC_NORMAL());
        getWs().setEchoWorker(this);
    }

    public void goToUpload() {
        getWs().setStatus(new W_EchoAgentVLC_UPLOAD());
        getWs().setEchoWorker(this);
    }

    public void shutdown()
    {
        running = false;
        if (readerVideo!=null)readerVideo.release();
    }

    public WorkerStatus getWs() { return ws; }
    public void setWs(WorkerStatus ws) { this.ws = ws; }
    public String getAgentId() { return agentId; }

    public long getNop() {
        return nop;
    }

    public void setNop(long nop) {
        this.nop = nop;
    }

    public ConcurrentHashMap<String, VlcManager> getVlcs() {
        return vlcs;
    }

    public void setVlcs(ConcurrentHashMap<String, VlcManager> vlcs) {
        this.vlcs = vlcs;
    }

    public synchronized ReaderVideo getReaderVideo()
    {
        if (readerVideo==null)
            readerVideo = new ReaderVideo();
        return readerVideo;
    }

}
