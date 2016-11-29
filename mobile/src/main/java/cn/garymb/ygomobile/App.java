package cn.garymb.ygomobile;


import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

import cn.garymb.ygomobile.lite.BuildConfig;
import cn.garymb.ygomobile.settings.AppsSettings;

public class App extends GameApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        AppsSettings.init(this);
    }

    @Override
    public NativeInitOptions getNativeInitOptions() {
        NativeInitOptions options= AppsSettings.get().getNativeInitOptions();
        return options;
    }

    @Override
    public float getSmallerSize() {
        return AppsSettings.get().getSmallerSize();
    }

    @Override
    public float getXScale() {
        return AppsSettings.get().getXScale();
    }

    @Override
    public float getYScale() {
        return AppsSettings.get().getYScale();
    }

    @Override
    public String getCardImagePath() {
        return AppsSettings.get().getCardImagePath();
    }

    @Override
    public void setLastDeck(String name) {
        AppsSettings.get().setLastDeck(name);
    }

    @Override
    public String getFontPath() {
        return AppsSettings.get().getFontPath();
    }

    @Override
    public String getLastDeck() {
        return AppsSettings.get().getLastDeck();
    }

    @Override
    public float getScreenWidth() {
        return AppsSettings.get().getScreenWidth();
    }

    @Override
    public boolean isLockSreenOrientation() {
        return AppsSettings.get().isLockSreenOrientation();
    }

    @Override
    public boolean isImmerSiveMode() {
        return AppsSettings.get().isImmerSiveMode();
    }

    @Override
    public float getScreenHeight() {
        return AppsSettings.get().getScreenHeight();
    }
}
