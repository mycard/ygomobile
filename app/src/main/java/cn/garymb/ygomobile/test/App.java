package cn.garymb.ygomobile.test;


import android.util.Log;

import cn.garymb.ygomobile.NativeInitOptions;

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected GameSettings getSettings() {
        return new GameSettings(this){
            @Override
            public NativeInitOptions getNativeInitOptions() {
                NativeInitOptions options= super.getNativeInitOptions();
//                options.mCacheDir = context.getCacheDir().getAbsolutePath();
                Log.i("Irrlicht",""+options.toString());
                return options;
            }
        };
    }
}
