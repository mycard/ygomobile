package cn.ygo.ocgcore;

import android.support.annotation.Keep;

public class OcgCoreApi {
    static {
        System.loadLibrary("ocgcore");
    }

    static final OcgCoreApi Core = new OcgCoreApi();

    //回调
    @Keep
    public static byte[] readFile(String name) {
        return Core.reader.read(name);
    }

    @Keep
    public static long getCard(long code) {
        CardData cardData = Core.cardManager.getCard(code);
        if (cardData != null) {
            return createCard(cardData);
        }
        return 0;
    }

    @Keep
    public static int messageHandler(int duel, long msg) {
        return Core.messager.handle(Duel.get(duel), msg);
    }

    private static long createCard(CardData cardData) {
        return createCard(
                cardData.Code,
                cardData.Alias,
                cardData.Setcode,
                cardData.Type,
                cardData.Level,
                cardData.Attribute,
                cardData.Race,
                cardData.Attack,
                cardData.Defense,
                cardData.LScale,
                cardData.RScale
        );
    }

    /***
     * 初始化
     *
     * @param reader      脚本读取
     * @param cardManager 卡片管理
     * @param messager    消息
     */
    public static void init(IFileReader reader, ICardManager cardManager, IMessager messager) {
        Core.reader = reader;
        Core.cardManager = cardManager;
        Core.messager = messager;
        initCore();
    }

    private static native long createCard(long code, long alias, long setcode, long type, long level,
                                         long attribute,
                                         long race, int attack, int defense, int lscale, int rscale);

    private static native void initCore();

    static native long createDuel(long seed);

    static native void startDuel(long pduel, long options);

    static native void endDuel(long pduel);

    static native void set_player_info(long pduel, int playerid, int lp, int startcount, int drawcount);

    static native byte[] get_log_message(long pduel);

    static native byte[] get_message(long pduel);

    static native int process(long pduel);

    static native void new_card(long pduel, long code, int owner, int playerid, int location, int sequence, int position);

    static native void new_tag_card(long pduel, long code, int owner, int location);

    static native byte[] query_card(long pduel, int playerid, int location, int sequence, int query_flag, int use_cache);

    static native int query_field_count(long pduel, int playerid, int location);

    static native byte[] query_field_card(long pduel, int playerid, int location, int query_flag, int use_cache);

    static native byte[] query_field_info(long pduel);

    static native void set_responsei(long pduel, int value);

    static native void set_responseb(long pduel, byte[] buf);

    static native int preload_script(long pduel, String script);

    IFileReader reader;
    ICardManager cardManager;
    IMessager messager;

    public interface IFileReader {
        byte[] read(String name);
    }

    public interface ICardManager {
        CardData getCard(long code);
    }

    public interface IMessager {
        int handle(Duel duel, long msgid);
    }
}
