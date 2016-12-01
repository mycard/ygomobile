package cn.garymb.ygomobile.activities;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
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

public class DeckManagerActivity extends BaseActivity implements ILoadCallBack, CardSearcher.Callback {
    private DrawerLayout mDrawerlayout;
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
//      NavigationView  mNavigationView = bind(R.id.nav_view);
        mRecyclerView = bind(R.id.grid_deck);
        mRecyclerView.setAdapter((mDeckAdapater = new DeckAdapater(this, mRecyclerView)));
        mRecyclerView.setLayoutManager(new DeckLayoutManager(this, Constants.DECK_WIDTH_COUNT));
        ItemTouchHelper touchHelper = new ItemTouchHelper(mCallback);
        touchHelper.attachToRecyclerView(mRecyclerView);
//        View head = mNavigationView.getHeaderView(0);
        mListView = (ListView) findViewById(R.id.list_cards);
        mCardListAdapater = new CardListAdapater(this);
        mListView.setAdapter(mCardListAdapater);
        mListView.setOnItemClickListener(mCardListAdapater);
        mListView.setOnScrollListener(mCardListAdapater);
        mCardSelector = new CardSearcher(bind(R.id.nav_view_list));
        mCardSelector.setDataLoader(mCardListAdapater);
        mCardSelector.setCallback(this);
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerlayout, R.string.search_open, R.string.search_close);
        toggle.setDrawerIndicatorEnabled(false);
        mDrawerlayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private int color(int id) {
        return getResources().getColor(id);
    }

    private ItemTouchHelper.Callback mCallback = new ItemTouchHelper.Callback() {
        Drawable bg = null;

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundDrawable(bg);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                bg = viewHolder.itemView.getBackground();
                viewHolder.itemView.setBackgroundColor(color(R.color.bg));
            }
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
            } else {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            }
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int left = viewHolder.getAdapterPosition();
            int right = target.getAdapterPosition();
            if (DeckItemUtils.isLabel(left) || DeckItemUtils.isLabel(right)) {
                return false;
            }
            if(DeckItemUtils.isExtra(left) && !DeckItemUtils.isExtra(right)){
                return false;
            }
            if(DeckItemUtils.isExtra(right)){
                return false;
            }
            // mDeckAdapater.notifyItemChanged(left, right);
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private void showSearch(boolean autoclose) {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
        if (autoclose && mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        } else if (isLoad) {
            mDrawerlayout.openDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
    }

    private void showResult(boolean autoclose) {
        if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
            mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
        }
        if (autoclose && mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        } else if (isLoad) {
            mDrawerlayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onSearch() {
        showResult(false);
    }

    @Override
    public void onReset() {

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
                showSearch(true);
                break;
            case R.id.action_card_list:
                showResult(true);
                break;
            case android.R.id.home:
                if (mDrawerlayout.isDrawerOpen(Constants.CARD_SEARCH_GRAVITY)) {
                    mDrawerlayout.closeDrawer(Constants.CARD_SEARCH_GRAVITY);
                    return true;
                }
                if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerlayout.closeDrawer(Gravity.LEFT);
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
