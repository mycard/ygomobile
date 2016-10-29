package cn.garymb.ygomobile.utils;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.HashMap;

import cn.garymb.ygodata.YGOGameOptions;
import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.YGOMobileActivity;
import cn.garymb.ygomobile.lite.R;

import static cn.garymb.ygomobile.GameSettings.CORE_SKIN_COVER_SIZE;

public class YGOStarter {

    private static void setFullScreen(Activity activity, ActivityShowInfo activityShowInfo) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    private static void quitFullScreen(Activity activity, ActivityShowInfo activityShowInfo) {
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (activityShowInfo.hasSupperbar && actionBar != null) {
                actionBar.show();
            }
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (activityShowInfo.hasBar && actionBar != null) {
                actionBar.show();
            }
        }
        final WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(attrs);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private static void showLoadingBg(Activity activity) {
        ActivityShowInfo activityShowInfo = Infos.get(activity);
        if (activityShowInfo == null) {
            return;
        }
//        Log.i("checker", "show:" + activity);
        activityShowInfo.oldRequestedOrientation = activity.getRequestedOrientation();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        activityShowInfo.rootOld = activityShowInfo.mRoot.getBackground();
        activityShowInfo.mContentView.setVisibility(View.INVISIBLE);
        //读取当前的背景图，如果卡的话，可以考虑缓存bitmap
        String bgfile = GameSettings.get().getCoreSkinPath()+"/"+ GameSettings.CORE_SKIN_COVER;
        Bitmap bmp = BitmapUtils.createNewBitmapAndCompressByFile(bgfile, CORE_SKIN_COVER_SIZE ,false);
        if(bmp ==null){
            activityShowInfo.mRoot.setBackgroundResource(R.drawable.bg);
        }else {
            Drawable bg = new BitmapDrawable(activity.getResources(), bmp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                activityShowInfo.mRoot.setBackground(bg);
            } else {
                activityShowInfo.mRoot.setBackgroundDrawable(bg);
            }
        }
        setFullScreen(activity, activityShowInfo);
    }

    private static void hideLoadingBg(Activity activity, ActivityShowInfo activityShowInfo) {
        activity.setRequestedOrientation(activityShowInfo.oldRequestedOrientation);
        activityShowInfo.mContentView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 16) {
            activityShowInfo.mRoot.setBackground(activityShowInfo.rootOld);
        } else {
            activityShowInfo.mRoot.setBackgroundDrawable(activityShowInfo.rootOld);
        }
        quitFullScreen(activity, activityShowInfo);
    }

    public static ActivityShowInfo onCreated(Activity activity) {
        ActivityShowInfo activityShowInfo = Infos.get(activity);
        if (activityShowInfo == null) {
            activityShowInfo = new ActivityShowInfo();
            Infos.put(activity, activityShowInfo);
//            Log.i("checker", "init:" + activity);
        }
        activityShowInfo.oldRequestedOrientation = activity.getRequestedOrientation();
        activityShowInfo.mRoot = activity.getWindow().getDecorView();
        activityShowInfo.mContentView = activityShowInfo.mRoot.findViewById(android.R.id.content);
        activityShowInfo.rootOld = activityShowInfo.mRoot.getBackground();
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                activityShowInfo.hasSupperbar = actionBar.isShowing();
            }
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                activityShowInfo.hasBar = actionBar.isShowing();
            }
        }
        return activityShowInfo;
    }
    public static void onDestroy(Activity activity) {
        Infos.remove(activity);
    }
    public static void onResumed(Activity activity) {
        ActivityShowInfo activityShowInfo = Infos.get(activity);
//        Log.i("checker", "resume:" + activity);
        if (activityShowInfo == null) {
            return;
        }
        if (!activityShowInfo.isFirst) {
            hideLoadingBg(activity, activityShowInfo);
        }
        activityShowInfo.isFirst = false;
    }

    public static void startGame(Activity activity, YGOGameOptions options) {
        showLoadingBg(activity);
        Intent intent = new Intent(activity, YGOMobileActivity.class);
        if (options != null) {
            intent.putExtra(YGOGameOptions.YGO_GAME_OPTIONS_BUNDLE_KEY, options);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private static HashMap<Activity, ActivityShowInfo> Infos = new HashMap<>();

    private static class ActivityShowInfo {
        View mRoot;
        boolean hasSupperbar;
        boolean hasBar;
        View mContentView;
        Drawable rootOld;
        boolean isFirst = true;
        int oldRequestedOrientation;
    }
}
