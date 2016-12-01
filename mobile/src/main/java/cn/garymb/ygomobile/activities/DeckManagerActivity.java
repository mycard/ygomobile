package cn.garymb.ygomobile.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.File;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.CardListAdapater;
import cn.garymb.ygomobile.core.CardSearcher;
import cn.garymb.ygomobile.core.loader.ILoadCallBack;
import cn.garymb.ygomobile.deck.DeckAdapater;
import cn.garymb.ygomobile.deck.DeckInfo;
import cn.garymb.ygomobile.deck.DeckItemUtils;
import cn.garymb.ygomobile.deck.DeckLayoutManager;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.StringManager;

public class DeckManagerActivity extends BaseActivity implements ILoadCallBack {
    private DrawerLayout mDrawerlayout;
    private NavigationView mNavigationView;
    private SQLiteDatabase mCDB;
    //    private CardsLineView[] mMainCards;
//    private CardsLineView mExtraCards;
//    private CardsLineView mOtherCards;
    private StringManager mStringManager = StringManager.get();
    private LimitManager mLimitManager = LimitManager.get();
    private RecyclerView mRecyclerView;
    private DeckAdapater mDeckAdapater;
    private AppsSettings mSettings = AppsSettings.get();
    private ListView mListView;
    private CardSearcher mCardSelector;
    private CardListAdapater mCardListAdapater;
    private boolean isLoad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackHome();
        setContentView(R.layout.activity_deckmanager);
        mDrawerlayout = bind(R.id.drawer_layout);
        mNavigationView = bind(R.id.nav_view);
        mRecyclerView = bind(R.id.grid_deck);
        mRecyclerView.setAdapter((mDeckAdapater = new DeckAdapater(this, mRecyclerView)));
        mRecyclerView.setLayoutManager(new DeckLayoutManager(this, Constants.DECK_WIDTH_COUNT));

        View head = mNavigationView.getHeaderView(0);
        final View searchView = head.findViewById(R.id.layout_search);
        mListView = (ListView) head.findViewById(R.id.list_cards);
        head.findViewById(R.id.tab_search).setOnClickListener((v) -> {
            searchView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        });
        head.findViewById(R.id.tab_result).setOnClickListener((v) -> {
            searchView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        });
        mCardListAdapater = new CardListAdapater(this);
        mListView.setAdapter(mCardListAdapater);
        mListView.setOnItemClickListener(mCardListAdapater);
        mListView.setOnScrollListener(mCardListAdapater);
        mCardSelector = new CardSearcher(mDrawerlayout, head);
        mCardSelector.setDataLoader(mCardListAdapater);
        mCardListAdapater.setCallBack(this);
        mCardListAdapater.loadData();

        VUiKit.defer().when(() -> {
            if (!mStringManager.isLoad()) {
                mStringManager.load();
            }
            if (!mLimitManager.isLoad()) {
                mLimitManager.load();
            }
            if (openDb()) {
                File file = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH + "/2016-10-1forbidden list.ydk");
                LimitList limitList = mLimitManager.getCount() > 0 ? mLimitManager.getLimit(0) : null;
                return DeckItemUtils.readDeck(mCDB, file, limitList);
            } else {
                return null;
            }
        }).done((rs) -> {
            mDeckAdapater.setDeck(rs);
            mDeckAdapater.notifyDataSetChanged();
        });
//        mOtherCards = bind(R.id.deck_other);
//        mExtraCards = bind(R.id.deck_extras);
//        mOtherCards.setMaxSize(15);
//        mExtraCards.setMaxSize(15);
//        mMainCards = new CardsLineView[]{
//                bind(R.id.deck_main1),
//                bind(R.id.deck_main2),
//                bind(R.id.deck_main3),
//                bind(R.id.deck_main4),
//                bind(R.id.deck_main5),
//                bind(R.id.deck_main6),
//                };
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, R.string.search_open, R.string.search_close);
        toggle.setDrawerIndicatorEnabled(false);
        mDrawerlayout.setDrawerListener(toggle);
        toggle.syncState();
//        test();
    }

    @Override
    public void onLoad(boolean ok) {
        if (isLoad) {
            mCardSelector.onLoad(ok);
        } else {
            isLoad = ok;
            mCardSelector.initItems();
        }
    }

    private boolean openDb() {
        File cdb = new File(mSettings.getDataBasePath(), Constants.DATABASE_NAME);
        if (cdb.exists()) {
            try {
                mCDB = SQLiteDatabase.openOrCreateDatabase(cdb, null);
                return true;
            } catch (Exception e) {
                if (Constants.DEBUG)
                    Log.e("kk", "open db", e);
            }
        } else if (Constants.DEBUG) {
            Log.w("kk", "no find " + cdb);
        }
        return false;
    }

//    private void test()
//    {
//        mExtraCards.test();
//        mOtherCards.test();
//        int len = mMainCards.length;
//        for (int i = 0; i < len; i++) {
//            mMainCards[i].test();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deck_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                //弹条件对话框
                if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
                    mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
                } else if (isLoad) {
                    mDrawerlayout.openDrawer(Constants.CARD_SEARCH_GRAVITY);
                }
                break;
            case android.R.id.home:
                if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
                    mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
                    return true;
                }
                break;
            case R.id.action_deck_list:
                break;
            case R.id.action_new_deck:
                break;
            case R.id.action_rename:
                break;
            case R.id.action_delete:
                break;
            case R.id.action_save:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
