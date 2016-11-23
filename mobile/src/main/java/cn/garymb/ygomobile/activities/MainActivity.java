package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.content.Intent;
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

import cn.garymb.ygomobile.adapters.ServerListAdapater;
import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.bean.ServerList;
import cn.garymb.ygomobile.lite.BuildConfig;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.core.ResCheckTask;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.core.YGOStarter;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.XmlUtils;

import static cn.garymb.ygomobile.Constants.ASSET_SERVER_LIST;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private boolean enableStart;
    private ListView mListView;
    private ServerListAdapater mServerListAdapater;
    private ServerList mServerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setExitAnimEnable(false);
        setContentView(R.layout.activity_main);
        mServerListAdapater = new ServerListAdapater(this);
        mListView = (ListView) findViewById(R.id.list_server);
        mListView.setAdapter(mServerListAdapater);
        //资源复制
        checkResourceDownload((error) -> {
            if (error < 0) {
                enableStart = false;
            } else {
                enableStart = true;
            }
        });
        YGOStarter.onCreated(this);
        loadData();
    }

    private void loadData() {
        VUiKit.defer().when(() -> {
//            ServerList test=new ServerList();
//            List<ServerInfo> serverInfoList=new ArrayList<ServerInfo>();
//            ServerInfo serverInfo=new ServerInfo();
//            serverInfo.setName("test");
//            serverInfo.setServerAddr("127.0.0.1");
//            serverInfoList.add(serverInfo);
//            test.setServerInfoList(serverInfoList);
//            if (BuildConfig.DEBUG) {
//                Log.i("kk", "list=" + XmlUtils.get().toXml(test));
//            }
            InputStream in = getAssets().open(ASSET_SERVER_LIST);
            ServerList list = XmlUtils.get().getObject(ServerList.class, in);
            IOUtils.close(in);
            return list;
        }).fail((e) -> {

        }).done((list) -> {
            if (list != null) {
                if (BuildConfig.DEBUG) {
                    Log.i("kk", "list=" + list);
                }
                mServerList = list;
                mServerListAdapater.addAll(list.getServerInfoList());
                mServerListAdapater.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
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
