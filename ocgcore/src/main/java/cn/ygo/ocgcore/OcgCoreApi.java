package cn.ygo.ocgcore;

public class OcgCoreApi {

    public static native void init();

    static native int create_duel(long seed);

    static native void start_duel(int pduel, int options);

    static native void end_duel(int pduel);

    static native void set_player_info(int pduel, int playerid, int lp, int startcount, int drawcount);

    static native byte[] get_log_message(int pduel);

    static native byte[] get_message(int pduel);

    static native int process(int pduel);

    static native void new_card(int pduel, long code, int owner, int playerid, int location, int sequence, int position);

    static native void new_tag_card(int pduel, long code, int owner, int location);

    static native byte[] query_card(int pduel, int playerid, int location, int sequence, int query_flag, int use_cache);

    static native int query_field_count(int pduel, int playerid, int location);

    static native byte[] query_field_card(int pduel, int playerid, int location, int query_flag, int use_cache);

    static native byte[] query_field_info(int pduel);

    static native void set_responsei(int pduel, int value);

    static native void set_responseb(int pduel, byte[] buf);

    static native int preload_script(int pduel, String script);

    public static byte[] script_reader(String name, int[] ids) {
        return null;
    }

    public static long card_reader(long id, CardData card) {
        return 0;
    }

    public static long message_handler(int ptr, long id) {
        return 0;
    }

    public static native void set_script_reader(ScriptReader f);

    public static native void set_card_reader(CardReader f);

    public static native void set_message_handler(MessageHandler f);
}
