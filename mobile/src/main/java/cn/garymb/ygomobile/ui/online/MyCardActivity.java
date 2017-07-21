package cn.garymb.ygomobile.ui.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.activities.BaseActivity;
import cn.garymb.ygomobile.ui.cards.DeckManagerActivity;
import cn.garymb.ygomobile.ui.plus.WebViewPlus;

public class MyCardActivity extends BaseActivity implements MyCard.MyCardListener, NavigationView.OnNavigationItemSelectedListener {
    //大厅聊天
    //房间列表
    //个人页面
    //排行榜
    //新建房间
    private WebViewPlus mWebViewPlus;
    private MyCard mMyCard;
    protected DrawerLayout mDrawerlayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_mycard);
        final Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        YGOStarter.onCreated(this);
        mMyCard = new MyCard(this);
        mWebViewPlus = $(R.id.webbrowser);
        mDrawerlayout = $(R.id.drawer_layout);
        NavigationView navigationView = $(R.id.nav_main);
        navigationView.setNavigationItemSelectedListener(this);
        View navHead = navigationView.getHeaderView(0);

        mWebViewPlus.enableHtml5();
        mWebViewPlus.setWebChromeClient(new WebViewPlus.DefWebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (toolbar != null) {
                    toolbar.setSubtitle(title);
                } else {
                    setTitle(title);
                }
            }
        });
        mMyCard.attachWeb(mWebViewPlus, this);
        try {
            mWebViewPlus.loadUrl(mMyCard.getLoginUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onBackHome() {
        if (mWebViewPlus.canGoBack()) {
            mWebViewPlus.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebViewPlus.canGoBack()) {
            mWebViewPlus.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        mWebViewPlus.onResume();
        mWebViewPlus.resumeTimers();
        YGOStarter.onResumed(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebViewPlus.onPause();
        mWebViewPlus.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebViewPlus.stopLoading();
        mWebViewPlus.setWebChromeClient(null);
        mWebViewPlus.setWebViewClient(null);
        mWebViewPlus.destroy();
        YGOStarter.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (doMenu(item.getItemId())) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean doMenu(int id) {
        switch (id) {
            case android.R.id.home:
                onBackHome();
                break;
            case R.id.action_deck_manager:
                startActivity(new Intent(this, DeckManagerActivity.class));
                closeDrawer();
                break;
            case R.id.action_new_room:
                if(TextUtils.isEmpty(mMyCard.getNewRoomUrl())){
                    try {
                        mWebViewPlus.loadUrl(mMyCard.getLoginUrl());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    mWebViewPlus.loadUrl(mMyCard.getNewRoomUrl());
                }
                closeDrawer();
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onLogin(MyCard.User user) {

    }

    @Override
    public void onHome() {
        mWebViewPlus.loadUrl(mMyCard.getHomeUrl());
    }

    @Override
    public void watchReplay() {

    }

    @Override
    public void puzzleMode() {

    }

    @Override
    public void openDrawer() {
        if (!mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void backHome() {
        finish();
    }

    @Override
    public void closeDrawer() {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void share(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "请选择"));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(doMenu(item.getItemId())){
            return true;
        }
        return false;
    }
}