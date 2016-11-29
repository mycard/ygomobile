package cn.garymb.ygomobile.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.CardListAdapater;
import cn.garymb.ygomobile.core.CardSelector;
import cn.garymb.ygomobile.lite.R;

public class CardSearchActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView mListView;
    private CardListAdapater mCardListAdapater;
    private DrawerLayout mDrawerlayout;
    private CardSelector mCardSelector;
    private boolean isLoad =false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackHome();
        setContentView(R.layout.activity_search);
        mListView = (ListView) findViewById(R.id.list_cards);
        mCardListAdapater = new CardListAdapater(this);
        mListView.setAdapter(mCardListAdapater);
        mListView.setOnItemClickListener(mCardListAdapater);
        mListView.setOnScrollListener(mCardListAdapater);
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, R.string.search_open, R.string.search_close);
        toggle.setDrawerIndicatorEnabled(false);
        mDrawerlayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mCardSelector = new CardSelector(mDrawerlayout,navigationView.getHeaderView(0), mCardListAdapater);
        mCardListAdapater.setILoadCallBack((ok)->{
            mCardSelector.onSearchOk();
        });
        mCardListAdapater.loadData((ok)->{
            isLoad = ok;
            mCardSelector.initItems();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_search, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //弹条件对话框
                if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
                    mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
                } else if(isLoad){
                    mDrawerlayout.openDrawer(Constants.CARD_SEARCH_GRAVITY);
                }
                break;
            case android.R.id.home:
                if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
                    mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
