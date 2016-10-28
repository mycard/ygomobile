package cn.garymb.ygomobile.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.task.ResCheckTask;
import cn.garymb.ygomobile.utils.VUiKit;


public class MainActivity extends AppCompatActivity {
    private boolean enableStart;
    private View mRoot;
    private boolean hasbar;
    private View mContentView;
    private Drawable rootOld;
    private boolean isFirst = true;
    private int oldRequestedOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        mRoot = getWindow().getDecorView();
        mContentView = mRoot.findViewById(android.R.id.content);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
//            actionBar.hide();
            hasbar = actionBar.isShowing();
        }
        //资源复制
        checkResourceDownload((error) -> {
            if (error < 0) {
                enableStart = false;
            } else {
                enableStart = true;
            }
        });
    }

    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    private void quitFullScreen() {
        ActionBar actionBar=getSupportActionBar();
        if(hasbar && actionBar!=null){
            actionBar.show();
        }
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    private void showLoadingBg() {
        oldRequestedOrientation =getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        rootOld = mRoot.getBackground();
        mContentView.setVisibility(View.INVISIBLE);
        mRoot.setBackgroundResource(R.drawable.bg);
        setFullScreen();
    }

    private void hideLoadingBg() {
        setRequestedOrientation(oldRequestedOrientation);
        mContentView.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 16) {
            mRoot.setBackground(rootOld);
        } else {
            mRoot.setBackgroundDrawable(rootOld);
        }
        quitFullScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirst) {
            hideLoadingBg();
        }
        isFirst = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:

                break;
            case R.id.action_game:
                if (enableStart) {
                    showLoadingBg();
                    YGOStarter.startGameWithOptions(this, null);
                } else {
                    VUiKit.show(this, R.string.dont_start_game);
                }
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkResourceDownload(ResCheckTask.ResCheckListener listener) {
        ResCheckTask task = new ResCheckTask(this, listener);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
