package sts.devices.agentclientvlc.model;

public class AgentConfig {
    private String agentName;
    private String serverIp;
    private int serverPort;

    //Constructor

    public AgentConfig() {}

    public AgentConfig(String agentName, String serverIp, int serverPort) {
        this.agentName = agentName;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    //toString()

    @Override
    public String toString() {
        return "AgentConfig{" +
                "agentName='" + agentName + '\'' +
                ", serverIp='" + serverIp + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }
}
