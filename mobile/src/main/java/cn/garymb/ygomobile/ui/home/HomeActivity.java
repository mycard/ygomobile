package cn.garymb.ygomobile.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tubb.smrv.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.activities.AboutActivity;
import cn.garymb.ygomobile.ui.activities.BaseActivity;
import cn.garymb.ygomobile.ui.activities.WebActivity;
import cn.garymb.ygomobile.ui.adapters.ServerListAdapter;
import cn.garymb.ygomobile.ui.cards.CardSearchAcitivity;
import cn.garymb.ygomobile.ui.cards.DeckManagerActivity;
import cn.garymb.ygomobile.ui.events.ServerInfoEvent;
import cn.garymb.ygomobile.ui.plus.DialogPlus;
import cn.garymb.ygomobile.ui.preference.SettingsActivity;

import static cn.garymb.ygomobile.Constants.ALIPAY_URL;

abstract class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout mDrawerlayout;
    protected SwipeMenuRecyclerView mServerList;
    private ServerListAdapter mServerListAdapter;
    private ServerListManager mServerListManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupActionBar();
        setExitAnimEnable(false);
        mServerList = $(R.id.list_server);
        mDrawerlayout = $(R.id.drawer_layout);
        mServerListAdapter = new ServerListAdapter(this);
        //server list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mServerList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mServerList.addItemDecoration(dividerItemDecoration);
        mServerList.setAdapter(mServerListAdapter);
        mServerListManager = new ServerListManager(this, mServerListAdapter);
        mServerListManager.bind(mServerList);
        mServerListManager.syncLoadData();

        //nav
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, $(R.id.toolbar), R.string.search_open, R.string.search_close);
        mDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = $(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(0)
                .findViewById(R.id.nav_donation)
                .setOnClickListener((v) -> {
                    openAliPay2Pay(ALIPAY_URL);
                });
        //event
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerInfoEvent(ServerInfoEvent messageEvent) {
        if (messageEvent.delete) {
            DialogPlus dialogPlus = new DialogPlus(getContext());
            dialogPlus.setTitle(R.string.question);
            dialogPlus.setMessage(R.string.delete_server_info);
            dialogPlus.setMessageGravity(Gravity.CENTER_HORIZONTAL);
            dialogPlus.setLeftButtonListener((dialog, which) -> {
                mServerListManager.delete(messageEvent.position);
                dialog.dismiss();
            });
            dialogPlus.setCancelable(false);
            dialogPlus.setCloseLinster(null);
            dialogPlus.show();
        } else {
            mServerListManager.showEditDialog(messageEvent.position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (doMenu(item.getItemId())) {
            closeDrawer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (doMenu(item.getItemId())) {
            closeDrawer();
            return true;
        }
        return false;
    }

    private boolean doMenu(int id) {
        switch (id) {
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_game:
                openGame();
                break;
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.action_quit: {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.quit_tip);
                builder.setMessageGravity(Gravity.CENTER_HORIZONTAL);
                builder.setLeftButtonListener((dlg, s) -> {
                    dlg.dismiss();
                    finish();
                });
                builder.show();
            }
            break;
            case R.id.action_add_server:
                mServerListManager.addServer();
                break;
            case R.id.action_card_search:
                startActivity(new Intent(this, CardSearchAcitivity.class));
                break;
            case R.id.action_deck_manager:
                startActivity(new Intent(this, DeckManagerActivity.class));
                break;
            case R.id.action_help: {
                WebActivity.open(this, getString(R.string.help), Constants.URL_HELP);
            }
            break;
            case R.id.action_update_images:
                updateImages();
                break;
            default:
                return false;
        }
        return true;
    }

    long exitLasttime = 0;

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if (System.currentTimeMillis() - exitLasttime <= 3000) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.back_tip, Toast.LENGTH_SHORT).show();
            exitLasttime = System.currentTimeMillis();
        }
    }

    private void closeDrawer() {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
    }


    protected abstract void openGame();

    protected abstract void updateImages();

    protected abstract void openAliPay2Pay(String qrCode);
}
