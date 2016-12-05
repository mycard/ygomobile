package cn.garymb.ygomobile.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.CardListAdapater;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardLoader;
import cn.garymb.ygomobile.core.CardSearcher;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.StringManager;

abstract class BaseCardsAcitivity extends BaseActivity implements CardLoader.CallBack {
    protected DrawerLayout mDrawerlayout;
    private ListView mListView;
    protected CardSearcher mCardSelector;
    protected CardListAdapater mCardListAdapater;
    protected CardLoader mCardLoader;
    protected boolean isLoad = false;

    protected StringManager mStringManager = StringManager.get();
    protected LimitManager mLimitManager = LimitManager.get();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        Toolbar toolbar = bind(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        mDrawerlayout = bind(R.id.drawer_layout);
        ViewGroup group = bind(R.id.layout_main);
        group.addView(getMainView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mListView = (ListView) findViewById(R.id.list_cards);
        mCardListAdapater = new CardListAdapater(this);
        mListView.setAdapter(mCardListAdapater);
//
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, toolbar, R.string.search_open, R.string.search_close);
        toggle.setDrawerIndicatorEnabled(false);
        mDrawerlayout.addDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener((v)->{
            onBack();
        });
        toggle.syncState();
        mCardLoader = new CardLoader(this);
        mCardLoader.setCallBack(this);
        mCardSelector = new CardSearcher(bind(R.id.nav_view_list), mCardLoader);
        setListeners();
        ProgressDialog dlg = ProgressDialog.show(this, null, getString(R.string.loading));
        VUiKit.defer().when(() -> {
            if (!mStringManager.isLoad()) {
                mStringManager.load();//loadFile(stringfile.getAbsolutePath());
            }
            if (!mLimitManager.isLoad()) {
                mLimitManager.load();//loadFile(stringfile.getAbsolutePath());
            }
            if (mLimitManager.getCount() > 0) {
                mCardLoader.setLimitList(mLimitManager.getLimitFromIndex(0));
            }
            mCardLoader.openDb();
        }).done((rs) -> {
            dlg.dismiss();
            onInit();
        });
    }

    protected int getDimen(int id){
        return (int)getResources().getDimension(id);
    }

    protected void setListeners() {
        mListView.setOnItemClickListener((adapterView, view, pos, id) -> {
            CardInfo cardInfo = mCardListAdapater.getItemById(id);
            onCardClick(cardInfo, pos);
        });
        mListView.setOnItemLongClickListener((adapterView, view, pos, id) -> {
            CardInfo cardInfo = mCardListAdapater.getItemById(id);
            onCardLongClick(view, cardInfo, pos);
            return true;
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        Glide.with(BaseCardsAcitivity.this).resumeRequests();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Glide.with(BaseCardsAcitivity.this).pauseRequests();
                        break;
                    case SCROLL_STATE_FLING:
                        Glide.with(BaseCardsAcitivity.this).resumeRequests();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return onBack();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean onBack(){
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
            return true;
        }
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
            return true;
        }
        finish();
        return true;
    }

    protected void onInit() {
        mCardSelector.initItems();
        isLoad = true;
    }

    protected abstract View getMainView();

    @Override
    public void onSearchResult(List<CardInfo> cardInfos) {
        Log.i("kk", "find " + (cardInfos == null ? -1 : cardInfos.size()));
        mCardListAdapater.set(cardInfos);
        mCardListAdapater.notifyDataSetChanged();
    }

    @Override
    public void onResetSearch() {

    }

    protected void hideDrawers() {
        if (mDrawerlayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerlayout.closeDrawer(Gravity.RIGHT);
        }
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerlayout.closeDrawer(Gravity.RIGHT);
        } else if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    protected abstract void onCardClick(CardInfo cardInfo, int pos);

    protected abstract void onCardLongClick(View view, CardInfo cardInfo, int pos);

    protected void showSearch(boolean autoclose) {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
        if (autoclose && mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        } else if (isLoad) {
            mDrawerlayout.openDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
    }

    protected void showResult(boolean autoclose) {
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
        if (autoclose && mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        } else if (isLoad) {
            mDrawerlayout.openDrawer(Gravity.LEFT);
        }
    }
}
