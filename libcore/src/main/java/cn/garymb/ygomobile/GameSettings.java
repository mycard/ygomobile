package cn.garymb.ygomobile;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

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
    public static final String CORE_SCRIPTS_PATH = "scripts";
    public static final String CORE_REPLAY_PATH = "replay";
    public static final String CORE_SCRIPTS_ZIP = "main.zip";
    private static GameSettings SETTINGS;
    protected Context context;
    private String ConfigVersion = "3.5";
    private float mScreenHeight, mScreenWidth, mDensity;

    public GameSettings(Context context) {
        this.context = context;
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

    public float getXScale() {
        return (mScreenHeight > mScreenWidth ? mScreenHeight : mScreenWidth) / 1024.0f;
    }

    public float getYScale() {
        return (mScreenHeight > mScreenWidth ? mScreenWidth : mScreenHeight) / 640.0f;
    }

    public static GameSettings get() {
        return SETTINGS;
    }

    public boolean isImmerSiveMode() {
        return false;
    }

    public int getGameScreenOritation() {
        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

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

    }

    public float getSmallerSize() {
        return mScreenHeight < mScreenWidth ? mScreenHeight : mScreenWidth;
    }

    public float getScreenWidth() {
        return Math.min(mScreenWidth, mScreenHeight);
    }

    public float getScreenHeight() {
        return Math.max(mScreenWidth, mScreenHeight);
    }

    /**
     * 游戏资源目录
     */
    public String getResourcePath() {
        try {
            return new File(Environment.getExternalStorageDirectory(), GAME_DIR).getAbsolutePath();
        } catch (Exception e) {
            return new File(context.getFilesDir(), GAME_DIR).getAbsolutePath();
        }
    }

    public String getCardImagePath() {
        return new File(getResourcePath(), "pics").getAbsolutePath();
    }

    public void setLastDeck(String name) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
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
        return new File(getResourcePath(), "fonts").getAbsolutePath();
    }

    public String getLastDeck() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_YDK, "new.ydk");
    }
}
