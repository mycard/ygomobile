package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.core.ResCheckTask;
import cn.garymb.ygomobile.utils.VUiKit;
import cn.garymb.ygomobile.utils.YGOStarter;

public class MainActivity extends BaseActivity {
    private boolean enableStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitAnimEnable(false);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        //资源复制
        checkResourceDownload((error) -> {
            if (error < 0) {
                enableStart = false;
            } else {
                enableStart = true;
            }
        });
        YGOStarter.onCreated(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YGOStarter.onResumed(this);
    }

    @Override
    protected void onDestroy() {
        YGOStarter.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_game:
                if (enableStart) {
                    YGOStarter.startGame(this, null);
                } else {
                    VUiKit.show(this, R.string.dont_start_game);
                }
                break;
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_quit: {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle(R.string.quit_tip);
                builder.setNegativeButton(android.R.string.ok, (dlg,s)->{
                    dlg.dismiss();
                    finish();
                });
                builder.setNeutralButton(android.R.string.cancel, (dlg,s)->{
                    dlg.dismiss();
                });
                builder.show();
            }
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

    long exitLasttime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitLasttime <= 3000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.back_tip, Toast.LENGTH_SHORT).show();
            exitLasttime = System.currentTimeMillis();
        }
    }

}
