package cn.ygo.ocgcore;


import java.util.HashMap;
import java.util.Map;

import static cn.ygo.ocgcore.OcgCoreApi.*;

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
            if (old == null) {
                old = new Duel(intptr);
                sDuel.put(Long.valueOf(intptr), old);
            }
        }
        return old;
    }

    public static Duel create(long seed) {
        return get(createDuel(seed));
    }

    public void start(long options) {
        startDuel(mPtr, options);
    }

    public void end() {
        endDuel(mPtr);
        synchronized (Duel.class) {
            sDuel.remove(Long.valueOf(mPtr));
        }
    }

    public void setPlayerInfo(int playerid, int lp, int startcount, int drawcount) {
        set_player_info(mPtr, playerid, lp, startcount, drawcount);
    }

    public byte[] getLogMessage() {
        return get_log_message(mPtr);
    }

    public byte[] getMessage() {
        return get_message(mPtr);
    }

    public int process() {
        return OcgCoreApi.process(mPtr);
    }

    public void newCard(long code, int owner, int playerid, int location, int sequence, int position) {
        new_card(mPtr, code, owner, playerid, location, sequence, position);
    }

    public void newTagCard(long code, int owner, int location) {
        new_tag_card(mPtr, code, owner, location);
    }

    public byte[] queryCard(int playerid, int location, int sequence, int query_flag, int use_cache) {
        return query_card(mPtr, playerid, location, sequence, query_flag, use_cache);
    }

    public int queryFieldCount(int playerid, int location) {
        return query_field_count(mPtr, playerid, location);
    }

    public byte[] queryFieldCard(int playerid, int location, int query_flag, int use_cache) {
        return query_field_card(mPtr, playerid, location, query_flag, use_cache);
    }

    public byte[] queryFieldInfo() {
        return query_field_info(mPtr);
    }

    public void setResponsei(int value) {
        set_responsei(mPtr, value);
    }

    public void setResponseb(byte[] buf) {
        set_responseb(mPtr, buf);
    }

    public int preloadScript(String script) {
        return preload_script(mPtr, script);
    }
}
