package cn.garymb.ygomobile.bean;

import net.kk.xml.annotations.XmlElement;

@XmlElement("server")
public class ServerInfo {
    @XmlElement("name")
    private String name;
    @XmlElement("ip")
    private String serverAddr;
    @XmlElement("port")
    private int port;

    @XmlElement("player-name")
    private String playerName;

    public ServerInfo() {

    }

    public ServerInfo(String name, String serverAddr, int port) {
        this.name = name;
        this.serverAddr = serverAddr;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerInfo that = (ServerInfo) o;

        if (port != that.port) return false;
        return serverAddr != null ? serverAddr.equals(that.serverAddr) : that.serverAddr == null;

    }

    @Override
    public int hashCode() {
        int result = serverAddr != null ? serverAddr.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "name='" + name + '\'' +
                ", serverAddr='" + serverAddr + '\'' +
                ", port=" + port +
                ", playerName='" + playerName + '\'' +
                '}';
    }
}
