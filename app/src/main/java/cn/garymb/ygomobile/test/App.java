package cn.garymb.ygomobile.test;


import android.content.Context;
import android.util.Log;

import cn.garymb.ygomobile.NativeInitOptions;
import cn.garymb.ygomobile.StaticApplication;

public class App extends BaseApplication {
    private StaticApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    protected GameSettings getSettings() {
        return new GameSettings(this) {
            @Override
            public NativeInitOptions getNativeInitOptions() {
                NativeInitOptions options = super.getNativeInitOptions();
//                options.mCacheDir = context.getCacheDir().getAbsolutePath();
                Log.i("Irrlicht", "" + options.toString());
                return options;
            }
        };
    }
}
