package sts.devices.agentclientvlc.helper;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

public final class AgentLog {
    private static final Path FILE = Paths.get("agent.log");
    private static final long MAX_BYTES = 5_000_000;

    public static synchronized void log(String msg) {
        String line = LocalDateTime.now() + " [" + Thread.currentThread().getName() + "] " + msg + System.lineSeparator();
        try {
            if (Files.exists(FILE) && Files.size(FILE) > MAX_BYTES)
                Files.move(FILE, Paths.get("agent.old.log"), StandardCopyOption.REPLACE_EXISTING);
            Files.writeString(FILE, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ignored) {}
    }
}