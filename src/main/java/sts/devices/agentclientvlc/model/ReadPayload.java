package sts.devices.agentclientvlc.model;

import sts.devices.agentclientvlc.model.MRC.VlcManager;
import sts.devices.agentclientvlc.model.MRC.Vsmanager;

import java.util.concurrent.ConcurrentHashMap;

public class ReadPayload {
    private ConcurrentHashMap<String, VlcManager> vls;
    private ConcurrentHashMap<String, Vsmanager> vss;

    //Constructor
    public ReadPayload() {
    }

    public ReadPayload(ConcurrentHashMap<String, VlcManager> vls, ConcurrentHashMap<String, Vsmanager> vss) {
        this.vls = vls;
        this.vss = vss;
    }

    //Getter and Setter
    public ConcurrentHashMap<String, VlcManager> getVls() {
        return vls;
    }

    public void setVls(ConcurrentHashMap<String, VlcManager> vls) {
        this.vls = vls;
    }

    public ConcurrentHashMap<String, Vsmanager> getVss() {
        return vss;
    }

    public void setVss(ConcurrentHashMap<String, Vsmanager> vss) {
        this.vss = vss;
    }

    //toString()
    @Override
    public String toString() {
        return "ReadPayload{" +
                "vls=" + vls +
                ", vss=" + vss +
                '}';
    }
}
