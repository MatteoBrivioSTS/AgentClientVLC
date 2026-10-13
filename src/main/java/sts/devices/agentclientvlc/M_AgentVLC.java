package sts.devices.agentclientvlc;

import com.fasterxml.jackson.databind.ObjectMapper;
import sts.devices.agentclientvlc.helper.AgentLog;
import sts.devices.agentclientvlc.helper.SingleInstance;
import sts.devices.agentclientvlc.model.AgentConfig;
import sts.devices.agentclientvlc.worker.W_EchoAgentVLC;

import java.io.File;
import java.io.IOException;

public class M_AgentVLC {
    public static void main(String[] args) {

            if (!SingleInstance.acquire(47001))
            {
                System.out.println("Agent già in esecuzione");
                return;
            }

            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                AgentLog.log("ECCEZIONE NON GESTITA in " + t.getName() + ": " + e);
                for (StackTraceElement el : e.getStackTrace()) AgentLog.log("    at " + el);
            });
            AgentConfig config;
            try {
                ObjectMapper mapper = new ObjectMapper();
                config = mapper.readValue(new File("config.json"), AgentConfig.class);
            } catch (IOException e) {
                AgentLog.log("config.json non leggibile: " + e);
                System.err.println("config.json non leggibile: " + e.getMessage());
                return;
            }

        if (config.getAgentName() == null || config.getServerIp() == null) {
            AgentLog.log("config.json incompleto (agentName/serverIp)");
            return;
        }
        W_EchoAgentVLC worker = new W_EchoAgentVLC(
                config.getAgentName(), config.getServerIp(), config.getServerPort());
        Runtime.getRuntime().addShutdownHook(new Thread(worker::shutdown));

        AgentLog.log("NAME: " + config.getAgentName() + " IP: " + config.getServerIp()
                + " PORT: " + config.getServerPort());
        new Thread(worker, "W_EchoAgentVLC").start();   // non daemon: la JVM resta viva
    }

}