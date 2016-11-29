package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.ServerListAdapater;
import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.bean.ServerList;
import cn.garymb.ygomobile.core.CardSelector;
import cn.garymb.ygomobile.lite.BuildConfig;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.core.ResCheckTask;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.core.YGOStarter;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.XmlUtils;

import static cn.garymb.ygomobile.Constants.ASSET_SERVER_LIST;

public class MainActivity extends BaseActivity {
    private boolean enableStart;
    private ListView mListView;
    private ServerListAdapater mServerListAdapater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setExitAnimEnable(false);
        mListView = (ListView) findViewById(R.id.list_server);
        mServerListAdapater = new ServerListAdapater(this);
        mListView.setAdapter(mServerListAdapater);
        mListView.setOnItemClickListener(mServerListAdapater);
        mListView.setOnItemLongClickListener(mServerListAdapater);
        //资源复制
        checkResourceDownload((error) -> {
            if (error < 0) {
                enableStart = false;
            } else {
                enableStart = true;
            }
        });
        YGOStarter.onCreated(this);
        //加载服务器列表
        mServerListAdapater.loadData();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.quit_tip);
                builder.setNegativeButton(android.R.string.ok, (dlg, s) -> {
                    dlg.dismiss();
                    finish();
                });
                builder.setNeutralButton(android.R.string.cancel, (dlg, s) -> {
                    dlg.dismiss();
                });
                builder.show();
            }
            break;
            case R.id.action_add_server:
                mServerListAdapater.addServer();
                break;
            case R.id.action_card_search:
                startActivity(new Intent(this, CardSearchActivity.class));
                break;
            case R.id.action_deck_manager:
                startActivity(new Intent(this, DeckManagerActivity.class));
                break;
            case R.id.action_help: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_HELP));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, R.string.no_webbrowser, Toast.LENGTH_SHORT).show();
                }
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
