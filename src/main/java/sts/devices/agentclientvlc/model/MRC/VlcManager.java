package sts.devices.agentclientvlc.model.MRC;

public class VlcManager {
    private String pdn;
    private String brand;
    private String type;
    private String ip;
    private String userName;
    private String password;
    private String port;
    private String description;

    //Constructor
    public VlcManager() {}

    public VlcManager(String pdn, String brand, String type, String ip, String userName,
                      String password, String port, String description) {
        this.pdn = pdn;
        this.brand = brand;
        this.type = type;
        this.ip = ip;
        this.userName = userName;
        this.password = password;
        this.port = port;
        this.description = description;
    }

    //Getter and Setter
    public String getPdn() {
        return pdn;
    }

    public void setPdn(String pdn) {
        this.pdn = pdn;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //toString()
    @Override
    public String toString() {
        return "VlcManager{" +
                "pdn='" + pdn + '\'' +
                ", brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", ip='" + ip + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", port='" + port + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
