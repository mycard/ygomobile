package cn.garymb.ygomobile;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import cn.garymb.ygomobile.BaseApplication;
import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.model.AppsSettings;

public class App extends BaseApplication {
    public PackageInfo packageInfo;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected GameSettings getSettings() {
        return new AppsSettings(this);
    }
}
