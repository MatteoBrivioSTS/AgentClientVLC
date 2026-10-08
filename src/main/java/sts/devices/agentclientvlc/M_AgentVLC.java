package sts.devices.agentclientvlc;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import sts.devices.agentclientvlc.model.AgentConfig;
import sts.devices.agentclientvlc.worker.W_EchoAgentVLC;

import java.io.File;
import java.io.IOException;

public class M_AgentVLC {
    public static void main(String[] args) {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            AgentConfig config = mapper.readValue(new File("config.json"),AgentConfig.class);
            Thread t = new Thread(new W_EchoAgentVLC(config.getAgentName(),config.getServerIp(),config.getServerPort()), "W_TCPClientVLC");
            System.out.println("NAME: "+config.getAgentName()+" IP: "+config.getServerIp()+" PORT: "+config.getServerPort());
            t.start();   // non daemon: la JVM resta viva
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}