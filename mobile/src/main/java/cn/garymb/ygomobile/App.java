package cn.garymb.ygomobile;


import cn.garymb.ygomobile.model.AppsSettings;

public class App extends BaseApplication {
    static  {
        System.loadLibrary("YGOMobile");
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected GameSettings getSettings() {
        return new AppsSettings(this);
    }
}
