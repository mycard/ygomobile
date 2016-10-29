package cn.garymb.ygomobile.test;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

import cn.garymb.ygomobile.NativeInitOptions;

public class GameSettings {
    public static String GAME_DIR = "ygocore";
    protected static final String PREF_LAST_YDK = "__last_ydk";
    public static final String DEFAULT_FONT_NAME = "ygo.ttf";
    public static final String DATABASE_NAME = "cards.cdb";
    public static final String FONT_DIRECTORY = "fonts";
    public static final String CORE_CONFIG_PATH = "core";
    public static final String CORE_SKIN_PATH = "textures";
    public static final String CORE_DECK_PATH = "deck";
    public static final String CORE_SINGLE_PATH = "single";
    public static final String CORE_IMAGE_PATH = "pics";
    public static final String CORE_SCRIPTS_PATH = "scripts";
    public static final String CORE_REPLAY_PATH = "replay";
    public static final String CORE_SCRIPTS_ZIP = "main.zip";
    public static final String CORE_SKIN_COVER = "bg.jpg";
    public static final String CORE_SKIN_CARD_BACK = "cover.jpg";
    public static final int[] CORE_SKIN_COVER_SIZE = new int[]{1024, 640};
    public static final int[] CORE_SKIN_CARD_BACK_SIZE = new int[]{177, 254};
    private static GameSettings SETTINGS;
    protected Context context;
    private String ConfigVersion = "3.5";
    private float mScreenHeight, mScreenWidth, mDensity;
    protected SharedPreferences sharedPreferences;
    private final static String NAME = "ygocore.settings";
    private final static String PREF_GAME_PATH = "game.path";

    public GameSettings(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        mDensity = context.getResources().getDisplayMetrics().density;
        mScreenHeight = context.getResources().getDisplayMetrics().heightPixels;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
    }

    static GameSettings init(GameSettings settings) {
        if (SETTINGS == null) {
            SETTINGS = settings;
        }
        return settings;
    }

    public static GameSettings get() {
        return SETTINGS;
    }

    public boolean isImmerSiveMode() {
        return false;
    }

//    public int getGameScreenOritation() {
//        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//    }

    public int getCardQuality() {
        return 1;
    }

    public String getCoreSkinPath() {
        return new File(getResourcePath(), CORE_SKIN_PATH).getAbsolutePath();
    }

    public NativeInitOptions getNativeInitOptions() {
        NativeInitOptions options = new NativeInitOptions();
        options.mOpenglVersion = 1;
        options.mIsSoundEffectEnabled = false;
        options.mCacheDir = getResourcePath();
        options.mDBDir = getDataBasePath();
        options.mCoreConfigVersion = getCoreConfigVersion();
        options.mResourcePath = getResourcePath();
        options.mExternalFilePath = getResourcePath();
        options.mCardQuality = getCardQuality();
        options.mIsFontAntiAliasEnabled = true;
        options.mIsPendulumScaleEnabled = false;
        return options;
    }

    public String getDataBasePath() {
        return context.getDatabasePath("test.db").getParent();
    }

    public String getCoreConfigVersion() {
        return ConfigVersion;
    }

    public void setCoreConfigVersion(String configVersion) {
        ConfigVersion = configVersion;
    }

    public void setResourcePath(String path) {
        sharedPreferences.edit().putString(PREF_GAME_PATH, path).commit();
    }

    public float getSmallerSize() {
        return mScreenHeight < mScreenWidth ? mScreenHeight : mScreenWidth;
    }

    public float getScreenWidth() {
        return mScreenWidth;//Math.min(mScreenWidth, mScreenHeight);
    }

    public float getXScale() {
        return (float) Math.max(mScreenWidth, mScreenHeight) / (float) CORE_SKIN_COVER_SIZE[0];
    }

    public float getYScale() {
        return (float) Math.min(mScreenWidth, mScreenHeight) / (float) CORE_SKIN_COVER_SIZE[1];
    }

    public float getScreenHeight() {
        return mScreenHeight;//Math.max(mScreenWidth, mScreenHeight);
    }

    /**
     * 游戏资源目录
     */
    public String getResourcePath() {
        String defPath;
        try {
            defPath = new File(Environment.getExternalStorageDirectory(), GAME_DIR).getAbsolutePath();
        } catch (Exception e) {
            defPath = new File(context.getFilesDir(), GAME_DIR).getAbsolutePath();
        }
        return sharedPreferences.getString(PREF_GAME_PATH, defPath);
    }

    public String getCardImagePath() {
        return new File(getResourcePath(), CORE_IMAGE_PATH).getAbsolutePath();
    }

    public float getDensity() {
        return mDensity;
    }

    public void setLastDeck(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_LAST_YDK, name);
        editor.commit();
    }

    public String getFontPath() {
        return getFontDefault();
    }

    public String getFontDefault() {
        return new File(getFontDirPath(), DEFAULT_FONT_NAME).getAbsolutePath();
    }

    public String getFontDirPath() {
        return new File(getResourcePath(), FONT_DIRECTORY).getAbsolutePath();
    }

    public String getLastDeck() {
        return sharedPreferences.getString(PREF_LAST_YDK, "new.ydk");
    }
}
