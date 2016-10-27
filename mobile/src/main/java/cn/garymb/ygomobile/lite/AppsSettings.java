package cn.garymb.ygomobile.lite;

import android.content.Context;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.NativeInitOptions;


public class AppsSettings extends GameSettings {
    public AppsSettings(Context context) {
        super(context);
    }

    @Override
    public NativeInitOptions getNativeInitOptions() {
        NativeInitOptions options = super.getNativeInitOptions();
        options.mIsSoundEffectEnabled = true;
        return options;
    }
}
