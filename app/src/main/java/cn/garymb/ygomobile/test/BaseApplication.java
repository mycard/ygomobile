package cn.garymb.ygomobile.test;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.garymb.ygomobile.core.IrrlichtBridge;


public class BaseApplication extends Application implements IrrlichtBridge.IrrlichtApplication {
    protected GameSettings settings;
    private SoundPool mSoundEffectPool;
    private Map<String, Integer> mSoundIdMap;

    @Override
    public void onCreate() {
        super.onCreate();
        initSoundEffectPool();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        settings = GameSettings.init(getSettings());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mSoundEffectPool.release();
    }

    protected GameSettings getSettings() {
        if (settings == null) {
            settings = new GameSettings(this);
        }
        return settings;
    }

    @SuppressWarnings("deprecation")
    protected void initSoundEffectPool() {
        mSoundEffectPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        AssetManager am = getAssets();
        String[] sounds;
        mSoundIdMap = new HashMap<String, Integer>();
        try {
            sounds = am.list("sound");
            for (String sound : sounds) {
                String path = "sound" + File.separator + sound;
                mSoundIdMap
                        .put(path, mSoundEffectPool.load(am.openFd(path), 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCardImagePath() {
        return settings.getCardImagePath();
    }

    @Override
    public void setLastDeck(String name) {
        settings.setLastDeck(name);
    }

    @Override
    public String getFontPath() {
        return settings.getFontPath();
    }

    @Override
    public String getLastDeck() {
        return settings.getLastDeck();
    }

    @Override
    public float getScreenWidth() {
        return settings.getScreenWidth();
    }

    @Override
    public float getScreenHeight() {
        return settings.getScreenHeight();
    }

    @Override
    public void playSoundEffect(String path) {
        Integer id = mSoundIdMap.get(path);
        if (id != null) {
            mSoundEffectPool.play(id, 0.5f, 0.5f, 2, 0, 1.0f);
        }
    }
}
