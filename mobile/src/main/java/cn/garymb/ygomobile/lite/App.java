package cn.garymb.ygomobile.lite;


import com.github.nativehandler.NativeCrashHandler;

import cn.garymb.ygomobile.BaseApplication;
import cn.garymb.ygomobile.GameSettings;

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected GameSettings getSettings() {
        return new AppsSettings(this);
    }
}
