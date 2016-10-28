package cn.garymb.ygomobile.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.NativeInitOptions;

@SuppressLint("CommitPrefEdits")
public class AppsSettings extends GameSettings {
    private SharedPreferences sharedPreferences;
    private final static String NAME = "ygocore.settings";
    private final static String PREF_GAME_PATH = "game.path";
    public AppsSettings(Context context) {
        super(context);
        sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    @Override
    public NativeInitOptions getNativeInitOptions() {
        NativeInitOptions options = super.getNativeInitOptions();
//        options.mCacheDir = context.getCacheDir().getAbsolutePath();
//        options.mOpenglVersion = 2;
        options.mIsSoundEffectEnabled = true;
        Log.i("Irrlicht", "" + options.toString());
        return options;
    }

    @Override
    public void setResourcePath(String path) {
        sharedPreferences.edit().putString(PREF_GAME_PATH, path).commit();
    }

    @Override
    public String getResourcePath() {
        return sharedPreferences.getString(PREF_GAME_PATH, super.getResourcePath());
    }
}
