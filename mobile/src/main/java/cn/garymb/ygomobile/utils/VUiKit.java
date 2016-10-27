package cn.garymb.ygomobile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;

import org.jdeferred.android.AndroidDeferredManager;

/**
 * @author Lody
 *         <p>
 *         A set of tools for UI.
 */
public class VUiKit {
    private static final AndroidDeferredManager gDM = new AndroidDeferredManager();
    private static final Handler gUiHandler = new Handler(Looper.getMainLooper());

    public static AndroidDeferredManager defer() {
        return gDM;
    }
    public static int dpToPx(Context context,int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeiget() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static void post(Runnable r) {
        gUiHandler.post(r);
    }

    public static void postDelayed(long delay, Runnable r) {
        gUiHandler.postDelayed(r, delay);
    }

}
