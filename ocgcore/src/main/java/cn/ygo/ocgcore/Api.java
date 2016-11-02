package cn.ygo.ocgcore;


import java.io.File;

public class Api {
    static {
        System.loadLibrary("ocgcore");
    }

    static final Api Core = new Api();

    private IFileReader reader;
    private ICardManager cardManager;
    private IMessager messager;

    public interface IFileReader {
        int read(String name, byte[] buf);
    }

    public interface ICardManager {
        CardData getCard(long code);
        void loadCards();
    }

    public interface IMessager {
        int handle(Duel duel, long msgid);
    }

    /***
     * cdb在游戏根目录，脚本在script
     * @param ygocore 游戏目录
     */
    public static void initCore(String ygocore) {
        initCore(new FileReader(new File(ygocore, "script").getAbsolutePath()),
                new CardManager(ygocore),
                new Messager());
    }

    /***
     *
     * @param scriptDir 脚本目录
     * @param dbDir 数据库目录/文件
     */
    public static void initCore(String scriptDir,String dbDir) {
        initCore(new FileReader(scriptDir),
                new CardManager(dbDir),
                new Messager());
    }

    /***
     * 初始化
     *
     * @param reader      脚本读取
     * @param cardManager 卡片管理
     * @param messager    消息
     */
    public static void initCore(IFileReader reader, ICardManager cardManager, IMessager messager) {
        if (reader == null) {
            throw new NullPointerException("IFileReader is no null.");
        }
        if (cardManager == null) {
            throw new NullPointerException("ICardManager is no null.");
        }
        if (messager == null) {
            throw new NullPointerException("IMessager is no null.");
        }
        Core.reader = reader;
        Core.cardManager = cardManager;
        Core.messager = messager;
        init();
        cardManager.loadCards();
    }

    //回调
    public static int readFile(String name, byte[] buf) {
        return Core.reader.read(name, buf);
    }
    //回调
    public static long getCard(long code) {
        CardData cardData = Core.cardManager.getCard(code);
        if (cardData != null) {
            return createCard(cardData);
        }
        return 0;
    }
    //回调
    public static int messageHandler(long duel, long msg) {
        Duel d = Duel.get(duel);
        if (d != null) {
            return Core.messager.handle(d, msg);
        }
        return -1;
    }
    //回调
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

    private static native long createCard(long code, long alias, long setcode, long type, int level,
                                          int attribute,
                                          long race, int attack, int defense, int lscale, int rscale);

    private static native void init();

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

    public static native int preloadScript(long pduel, byte[] script);
}
