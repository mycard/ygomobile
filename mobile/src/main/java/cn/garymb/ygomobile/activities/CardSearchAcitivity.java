package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.CardListAdapater;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardDetail;
import cn.garymb.ygomobile.core.CardLoader;
import cn.garymb.ygomobile.core.CardSearcher;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.StringManager;

public class CardSearchAcitivity extends BaseActivity implements CardLoader.CallBack {
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
        setContentView(R.layout.activity_search);
        Toolbar toolbar = bind(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        mDrawerlayout = bind(R.id.drawer_layout);

        mListView = (ListView) findViewById(R.id.list_cards);
        mCardListAdapater = new CardListAdapater(this);
        mCardListAdapater.setShowCode(true);
        mListView.setAdapter(mCardListAdapater);
//
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, toolbar, R.string.search_open, R.string.search_close);
        toggle.setDrawerIndicatorEnabled(false);
        mDrawerlayout.addDrawerListener(toggle);
        toggle.setToolbarNavigationClickListener((v) -> {
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

    protected int getDimen(int id) {
        return (int) getResources().getDimension(id);
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
                        Glide.with(CardSearchAcitivity.this).resumeRequests();
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        Glide.with(CardSearchAcitivity.this).pauseRequests();
                        break;
                    case SCROLL_STATE_FLING:
                        Glide.with(CardSearchAcitivity.this).resumeRequests();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    private boolean onBack() {
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
        mCardLoader.loadData();
    }

    @Override
    public void onSearchResult(List<CardInfo> cardInfos) {
//        Log.d("kk", "find " + (cardInfos == null ? -1 : cardInfos.size()));
        mCardListAdapater.set(cardInfos);
        mCardListAdapater.notifyDataSetChanged();
    }

    @Override
    public void onResetSearch() {

    }

    @Override
    public void onSearchStart(LimitList limitList) {
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_search2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //弹条件对话框
                showSearch(true);
                break;
            case android.R.id.home:
                return onBack();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        } else {
            super.onBackPressed();
        }
    }

    protected void onCardClick(CardInfo cardInfo, int pos) {
        showCard(cardInfo);
    }

    protected void onCardLongClick(View view, CardInfo cardInfo, int pos) {

    }

    protected void showCard(CardInfo cardInfo) {
        if (cardInfo != null) {
            CardDetail cardDetail = new CardDetail(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(cardDetail.getView());
            final Dialog dialog = builder.show();
            cardDetail.bind(cardInfo, mStringManager, new CardDetail.OnClickListener() {
                @Override
                public void onOpenUrl(CardInfo cardInfo) {
                    String uri = Constants.WIKI_SEARCH_URL + String.format("%08d", cardInfo.Code);
                    WebActivity.open(getContext(), cardInfo.Name, uri);
                }

                @Override
                public void onClose() {
                    dialog.dismiss();
                }

                @Override
                public void onAddSideCard(CardInfo cardInfo) {

                }

                @Override
                public void onAddMainCard(CardInfo cardInfo) {

                }
            });
        }
    }

    protected void showSearch(boolean autoclose) {
        if (autoclose && mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        } else if (isLoad) {
            mDrawerlayout.openDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
    }
}
