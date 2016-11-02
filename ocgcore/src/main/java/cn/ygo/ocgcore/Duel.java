package cn.ygo.ocgcore;


import java.util.HashMap;
import java.util.Map;

public class Duel {
    private long mPtr;
    private static final Map<Long, Duel> sDuel = new HashMap<>();

    private Duel(long intptr) {
        this.mPtr = intptr;
    }

    public static Duel get(long intptr) {
        Duel old = null;
        synchronized (Duel.class) {
            old = sDuel.get(Long.valueOf(intptr));
        }
        return old;
    }

    public static Duel getOrCreate(long intptr) {
        Duel old = null;
        synchronized (Duel.class) {
            old = sDuel.get(Long.valueOf(intptr));
            if (old == null) {
                old = new Duel(intptr);
                sDuel.put(Long.valueOf(intptr), old);
            }
        }
        return old;
    }

    public static Duel create(long seed) {
        return getOrCreate(Api.createDuel(seed));
    }

    public void start(long options) {
        Api.startDuel(mPtr, options);
    }

    public void end() {
        Api.endDuel(mPtr);
        synchronized (Duel.class) {
            sDuel.remove(Long.valueOf(mPtr));
        }
    }

    public void setPlayerInfo(int playerid, int lp, int startcount, int drawcount) {
        Api.setPlayerInfo(mPtr, playerid, lp, startcount, drawcount);
    }

    public byte[] getLogMessage() {
        return Api.getLogMessage(mPtr);
    }

    public byte[] getMessage() {
        return Api.getMessage(mPtr);
    }

    public int process() {
        return Api.process(mPtr);
    }

    public void newCard(long code, int owner, int playerid, int location, int sequence, int position) {
        Api.newCard(mPtr, code, owner, playerid, location, sequence, position);
    }

    public void newTagCard(long code, int owner, int location) {
        Api.newTagCard(mPtr, code, owner, location);
    }

    public byte[] queryCard(int playerid, int location, int sequence, int query_flag, int use_cache) {
        return Api.queryCard(mPtr, playerid, location, sequence, query_flag, use_cache);
    }

    public int queryFieldCount(int playerid, int location) {
        return Api.queryFieldCount(mPtr, playerid, location);
    }

    public byte[] queryFieldCard(int playerid, int location, int query_flag, int use_cache) {
        return Api.queryFieldCard(mPtr, playerid, location, query_flag, use_cache);
    }

    public byte[] queryFieldInfo() {
        return Api.queryFieldInfo(mPtr);
    }

    public void setResponsei(int value) {
        Api.setResponseI(mPtr, value);
    }

    public void setResponseb(byte[] buf) {
        Api.setResponseB(mPtr, buf);
    }

    public int preloadScript(byte[] script) {
        return Api.preloadScript(mPtr, script);
    }

    public void OnMessage(long messageType) {

    }
}
