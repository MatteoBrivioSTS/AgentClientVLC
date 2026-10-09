package sts.devices.agentclientvlc.model.MRC;

public class Vsmanager {
    private String pdn;
    private String type;
    private String IPaddress;
    private String IPport;
    private String userName;
    private String password;
    private String parameters;
    private String description;

    private String id_videostream;

    //Constructor
    public Vsmanager() {}

    public Vsmanager(String pdn, String type, String IPaddress, String IPport,
                     String userName, String password, String parameters, String description) {
        this.pdn = pdn;
        this.type = type;
        this.IPaddress = IPaddress;
        this.IPport = IPport;
        this.userName = userName;
        this.password = password;
        this.parameters = parameters;
        this.description = description;
    }

    //Constructor to sent to client with the videostream_id to get
    public Vsmanager(String pdn, String type, String IPaddress, String IPport, String userName,
                     String password, String parameters, String description, String id_videostream) {
        this.pdn = pdn;
        this.type = type;
        this.IPaddress = IPaddress;
        this.IPport = IPport;
        this.userName = userName;
        this.password = password;
        this.parameters = parameters;
        this.description = description;
        this.id_videostream = id_videostream;
    }

    //Getter and Setter
    public String getPdn() {
        return pdn;
    }

    public void setPdn(String pdn) {
        this.pdn = pdn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIPaddress() {
        return IPaddress;
    }

    public void setIPaddress(String IPaddress) {
        this.IPaddress = IPaddress;
    }

    public String getIPport() {
        return IPport;
    }

    public void setIPport(String IPport) {
        this.IPport = IPport;
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

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId_videostream() {
        return id_videostream;
    }

    public void setId_videostream(String id_videostream) {
        this.id_videostream = id_videostream;
    }

    //toString()

    @Override
    public String toString() {
        return "Vsmanager{" +
                "pdn='" + pdn + '\'' +
                ", type='" + type + '\'' +
                ", IPaddress='" + IPaddress + '\'' +
                ", IPport='" + IPport + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", parameters='" + parameters + '\'' +
                ", description='" + description + '\'' +
                ", id_videostream='" + id_videostream + '\'' +
                '}';
    }
}
