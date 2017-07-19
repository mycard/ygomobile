package cn.garymb.ygomobile.ui.online;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.activities.BaseActivity;
import cn.garymb.ygomobile.ui.plus.WebViewPlus;

public class MyCardActivity extends BaseActivity implements MyCard.MyCardListener {
    //大厅聊天
    //房间列表
    //个人页面
    //排行榜
    //新建房间
    private WebViewPlus mWebViewPlus;
    private MyCard mMyCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webbrowser);
        final Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        YGOStarter.onCreated(this);
        mMyCard = new MyCard(this);
        mWebViewPlus = $(R.id.webbrowser);
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
        mMyCard.attachWeb(mWebViewPlus);
        mMyCard.setMyCardListener(this);
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
        mWebViewPlus.loadUrl(mMyCard.getNewRoomUrl());
    }
}