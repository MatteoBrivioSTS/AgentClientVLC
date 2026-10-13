package sts.devices.agentclientvlc.model;

import sts.devices.agentclientvlc.model.MRC.Vsmanager;

import java.util.concurrent.ConcurrentHashMap;

public class ReadPayload {
    private ConcurrentHashMap<String, Vsmanager> vss;

    //Constructor
    public ReadPayload() {
    }

    public ReadPayload(ConcurrentHashMap<String, Vsmanager> vss) {
        this.vss = vss;
    }

    //Getter and Setter

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
                "vss=" + vss +
                '}';
    }
}
