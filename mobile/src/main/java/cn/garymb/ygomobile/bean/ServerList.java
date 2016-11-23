package cn.garymb.ygomobile.bean;

import net.kk.xml.annotations.XmlElement;

import java.util.List;

@XmlElement("servers")
public class ServerList {
    @XmlElement("server")
    private List<ServerInfo> mServerInfoList;

    public ServerList() {

    }

    public List<ServerInfo> getServerInfoList() {
        return mServerInfoList;
    }

    public void setServerInfoList(List<ServerInfo> serverInfoList) {
        mServerInfoList = serverInfoList;
    }

    @Override
    public String toString() {
        return "ServerList{" +
                "mServerInfoList=" + mServerInfoList +
                '}';
    }
}
