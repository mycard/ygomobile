package cn.garymb.ygomobile.ui.events;

public class ServerInfoEvent {
    public int position;
    public boolean delete;

    public ServerInfoEvent() {
    }

    public ServerInfoEvent(int position, boolean delete) {
        this.position = position;
        this.delete = delete;
    }
}
