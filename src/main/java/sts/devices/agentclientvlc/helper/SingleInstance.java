package sts.devices.agentclientvlc.helper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public final class SingleInstance {
    private static ServerSocket lock;   // tenuto vivo per tutta la vita del processo

    /** true se siamo la prima istanza. */
    public static boolean acquire(int port) {
        try {
            lock = new ServerSocket(port, 1, InetAddress.getLoopbackAddress());
            return true;
        } catch (IOException e) {
            return false;               // porta occupata: esiste già un agent
        }
    }
}