package cn.garymb.ygomobile;


import android.app.Activity;
import android.support.v7.app.AppCompatDelegate;

import cn.garymb.ygomobile.core.AppsSettings;

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
    public void attachGame(Activity activity) {
        super.attachGame(activity);
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
    public boolean canNdkCash() {
        return false;
    }

    @Override
    public boolean isImmerSiveMode() {
        return AppsSettings.get().isImmerSiveMode();
    }
    public boolean isSensorRefresh(){
        return AppsSettings.get().isSensorRefresh();
    }
    @Override
    public float getScreenHeight() {
        return AppsSettings.get().getScreenHeight();
    }
}
