package cn.ygo.ocgcore;


import static cn.ygo.ocgcore.OcgCoreApi.*;

public class Duel {
    private int mPtr;

    public Duel(int intptr) {
        this.mPtr = intptr;
    }

    public static Duel create(long seed) {
        return new Duel(create_duel(seed));
    }

    public void start(int options) {
        start_duel(mPtr, options);
    }

    public void end() {
        end_duel(mPtr);
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
