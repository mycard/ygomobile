package cn.garymb.ygomobile.ui.home;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import java.net.URLEncoder;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.GameUriManager;
import cn.garymb.ygomobile.YGOMobileActivity;
import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.core.IrrlichtBridge;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.plus.DialogPlus;
import cn.garymb.ygomobile.ui.plus.VUiKit;
import cn.garymb.ygomobile.utils.AlipayPayUtils;
import cn.garymb.ygomobile.utils.ComponentUtils;
import cn.garymb.ygomobile.utils.NetUtils;

import static cn.garymb.ygomobile.Constants.ACTION_RELOAD;
import static cn.garymb.ygomobile.Constants.ALIPAY_URL;
import static cn.garymb.ygomobile.Constants.NETWORK_IMAGE;

public class MainActivity extends HomeActivity {
    private GameUriManager mGameUriManager;
    private ImageUpdater mImageUpdater;
    private boolean enableStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YGOStarter.onCreated(this);
        mImageUpdater = new ImageUpdater(this);
        //资源复制
        checkResourceDownload((error, isNew) -> {
            if (error < 0) {
                enableStart = false;
            } else {
                enableStart = true;
            }
            if (isNew) {
                new DialogPlus(this)
                        .setTitleText(getString(R.string.settings_about_change_log))
                        .loadUrl("file:///android_asset/changelog.html", Color.TRANSPARENT)
                        .hideButton()
                        .setOnCloseLinster((dlg) -> {
                            dlg.dismiss();
                            //mImageUpdater
                            if (NETWORK_IMAGE && NetUtils.isConnected(getContext())) {
                                if (!mImageUpdater.isRunning()) {
                                    mImageUpdater.start();
                                }
                            }
                        })
                        .show();
            } else {
                getGameUriManager().doIntent(getIntent());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        YGOStarter.onResumed(this);
        //如果游戏Activity已经不存在了，则
        if (!ComponentUtils.isActivityRunning(this, new ComponentName(this, YGOMobileActivity.class))) {
            sendBroadcast(new Intent(IrrlichtBridge.ACTION_STOP).setPackage(getPackageName()));
        }
    }

    @Override
    protected void onDestroy() {
        YGOStarter.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ACTION_RELOAD.equals(intent.getAction())) {
            checkResourceDownload((error, isNew) -> {
                if (error < 0) {
                    enableStart = false;
                } else {
                    enableStart = true;
                }
                getGameUriManager().doIntent(getIntent());
            });
        } else {
            getGameUriManager().doIntent(intent);
        }
    }

    private GameUriManager getGameUriManager() {
        if (mGameUriManager == null) {
            mGameUriManager = new GameUriManager(this);
        }
        return mGameUriManager;
    }

    private void checkResourceDownload(ResCheckTask.ResCheckListener listener) {
        ResCheckTask task = new ResCheckTask(this, listener);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    @Override
    protected void openGame() {
        if (enableStart) {
            YGOStarter.startGame(this, null);
        } else {
            VUiKit.show(this, R.string.dont_start_game);
        }
    }

    @Override
    public void updateImages() {
        if(!NETWORK_IMAGE){
            DialogPlus dialog  = new DialogPlus(this);
            dialog.setTitle("公告");
            dialog.setMessage("由于版权关系，github不提供卡图下载。\n" +
                    "如果需要下载卡图，则需要钱搭服务，作者没啥钱，收大家钱搞会被骂。\n" +
                    "所以以后不提供卡图下载，大家将就用集成卡图。\n" +
                    "如果集成卡图也被举报，那么以后不内置卡图。");
            dialog.show();
            return;
        }
        if(NetUtils.isConnected(this)) {
            if (!mImageUpdater.isRunning()) {
                mImageUpdater.start();
            } else {
                showToast(R.string.downloading_images, Toast.LENGTH_SHORT);
            }
        }else{
            showToast(getString(R.string.tip_no_netwrok));
        }
    }
}
