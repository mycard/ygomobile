package cn.garymb.ygomobile.utils;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

import java.lang.reflect.Method;

public class SystemUtils {

    public static DisplayMetrics getHasVirtualDisplayMetrics(Activity context) {
        int dpi = 0;
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        Class<?> c;
        try {
            c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dm;
    }
}
