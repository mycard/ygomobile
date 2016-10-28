package cn.garymb.ygomobile.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.NativeInitOptions;

@SuppressLint("CommitPrefEdits")
public class AppsSettings extends GameSettings {

    public AppsSettings(Context context) {
        super(context);
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
}
