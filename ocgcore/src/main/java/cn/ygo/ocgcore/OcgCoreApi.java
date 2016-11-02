package cn.ygo.ocgcore;

import android.support.annotation.Keep;

public class OcgCoreApi {
    static {
        System.loadLibrary("ocgcore");
    }

    static final OcgCoreApi Core = new OcgCoreApi();

    //回调
    @Keep
    public static int readFile(String name,byte[] buf) {
        return Core.reader.read(name, buf);
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
    public static int messageHandler(long duel, long msg) {
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
        if(reader == null){
            throw new NullPointerException("IFileReader is no null.");
        }
        if(cardManager == null){
            throw new NullPointerException("ICardManager is no null.");
        }
        if(messager == null){
            throw new NullPointerException("IMessager is no null.");
        }
        Core.reader = reader;
        Core.cardManager = cardManager;
        Core.messager = messager;
        initCore();
    }

    private static native long createCard(long code, long alias, long setcode, long type, long level,
                                         long attribute,
                                         long race, int attack, int defense, int lscale, int rscale);

    private static native void initCore();

    public static native long createDuel(long seed);

    public static native void startDuel(long pduel, long options);

    public static native void endDuel(long pduel);

    public static native long getCardCode(long card);

    public static native void setPlayerInfo(long pduel, int playerid, int lp, int startcount, int drawcount);

    public static native byte[] getLogMessage(long pduel);

    public static native byte[] getMessage(long pduel);

    public static native int process(long pduel);

    public static native void newCard(long pduel, long code, int owner, int playerid, int location, int sequence, int position);

    public static native void newTagCard(long pduel, long code, int owner, int location);

    public static native byte[] queryCard(long pduel, int playerid, int location, int sequence, int query_flag, int use_cache);

    public static native int queryFieldCount(long pduel, int playerid, int location);

    public static native byte[] queryFieldCard(long pduel, int playerid, int location, int query_flag, int use_cache);

    public static native byte[] queryFieldInfo(long pduel);

    public static native void setResponseI(long pduel, int value);

    public static native void setResponseB(long pduel, byte[] buf);

    public static native int preloadScript(long pduel, char[] script);

    private IFileReader reader;
    private ICardManager cardManager;
    private IMessager messager;

    public interface IFileReader {
        int read(String name,byte[] buf);
    }

    public interface ICardManager {
        CardData getCard(long code);
    }

    public interface IMessager {
        int handle(Duel duel, long msgid);
    }
}
