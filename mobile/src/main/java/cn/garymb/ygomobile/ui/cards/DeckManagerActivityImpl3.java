package cn.garymb.ygomobile.ui.cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewItemListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;
import java.util.Locale;

import cn.garymb.ygomobile.AppsSettings;
import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.bean.events.CardInfoEvent;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.loader.CardLoader;
import cn.garymb.ygomobile.loader.DeckLoader;
import cn.garymb.ygomobile.loader.ImageLoader;
import cn.garymb.ygomobile.ui.activities.BaseActivity;
import cn.garymb.ygomobile.ui.cards.deck.DeckAdapater2;
import cn.garymb.ygomobile.ui.plus.DialogPlus;
import cn.garymb.ygomobile.ui.plus.VUiKit;
import ocgcore.LimitManager;
import ocgcore.StringManager;
import ocgcore.data.Card;
import ocgcore.data.LimitList;

class DeckManagerActivityImpl3 extends BaseActivity implements RecyclerViewItemListener.OnItemListener, CardLoader.CallBack {
    private RecyclerView mRecyclerView;
    private ImageLoader mImageLoader;
    private DeckAdapater2 mDeckAdapater;
    protected StringManager mStringManager = StringManager.get();
    protected LimitManager mLimitManager = LimitManager.get();
    protected CardLoader mCardLoader;
    private AppsSettings mSettings = AppsSettings.get();
    private String mPreLoad;
    private File mYdkFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_cards3);
        mImageLoader = ImageLoader.get(this);
        mRecyclerView = $(R.id.grid_cards);

        int w = getResources().getDisplayMetrics().widthPixels / 10;
        int h = Math.round((255.0f / 177.0f) * w);

        mRecyclerView.setAdapter((mDeckAdapater = new DeckAdapater2(this, w, h)));
        mRecyclerView.setLayoutManager(new DeckLayoutManager(this, Constants.DECK_WIDTH_MAX_COUNT, w, h));
        mRecyclerView.addItemDecoration(new DeckItemDecoration());
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemListener(mRecyclerView, this));
        EventBus.getDefault().register(this);

        mCardLoader = new CardLoader(this);
        mCardLoader.setCallBack(this);
        doIntent(getIntent());
        loadData(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        doIntent(intent);
    }

    private void doIntent(Intent intent) {
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            String path = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (!TextUtils.isEmpty(path)) {
                mPreLoad = path;
            }
        }
    }

    @Override
    protected void onDestroy() {
        ImageLoader.onDestory(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCardInfoEvent(CardInfoEvent event) {
//        int pos = event.position;
//        Card cardInfo = mCardListAdapater.getItem(pos);
//        if (cardInfo == null) {
//            mCardListAdapater.hideMenu(null);
//        } else if (event.toMain) {
//            if (!addMainCard(cardInfo)) {// || !checkLimit(cardInfo, false)) {
//                mCardListAdapater.hideMenu(null);
//            }
//        } else {
//            if (!addSideCard(cardInfo)) {// || !checkLimit(cardInfo, false)) {
//                mCardListAdapater.hideMenu(null);
//            }
//        }
    }

    @Override
    public void onItemClick(View view, int pos) {

    }

    @Override
    public void onItemLongClick(View view, int pos) {

    }

    @Override
    public void onItemDoubleClick(View view, int pos) {

    }

    private void loadData(boolean init) {
        DialogPlus dlg = DialogPlus.show(this, null, getString(R.string.loading));
        VUiKit.defer().when(() -> {
            if (init) {
                StringManager.get().load();//loadFile(stringfile.getAbsolutePath());
                LimitManager.get().load();//loadFile(stringfile.getAbsolutePath());
                if (mLimitManager.getCount() > 1) {
                    mCardLoader.setLimitList(mLimitManager.getLimit(1));
                }
                mCardLoader.openDb();
            }
            File file = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH + "/" + mSettings.getLastDeck() + Constants.YDK_FILE_EX);
            if (!TextUtils.isEmpty(mPreLoad)) {
                file = new File(mPreLoad);
                mPreLoad = null;
            }
            if (!file.exists()) {
                //当默认卡组不存在的时候
                File[] files = getYdkFiles();
                if (files != null && files.length > 0) {
                    file = files[0];
                }
            }
            //EXTRA_DECK
            if (file == null) {
                return new DeckInfo();
            }
            mYdkFile = file;
            if (mCardLoader.isOpen() && file.exists()) {
                return DeckLoader.readDeck(mCardLoader, file, mCardLoader.getLimitList());
            } else {
                return new DeckInfo();
            }
        }).done((rs) -> {
            dlg.dismiss();
//            mCardSelector.initItems();
            setCurYdkFile(mYdkFile);
//            initLimitListSpinners(mLimitSpinner);
//            initDecksListSpinners(mDeckSpinner);
            mDeckAdapater.setLimitList(mCardLoader.getLimitList());
            mDeckAdapater.updateDeck(rs);
            mDeckAdapater.notifyDataSetChanged();
        });
    }

    @Override
    public void onSearchStart() {

    }

    @Override
    public void onLimitListChanged(LimitList limitList) {

    }

    @Override
    public void onSearchResult(List<Card> Cards) {

    }

    @Override
    public void onResetSearch() {

    }

    private void setCurYdkFile(File file) {
        mYdkFile = file;
//        if (file != null && file.exists()) {
//            String name = IOUtils.tirmName(file.getName(), Constants.YDK_FILE_EX);
//            setActionBarSubTitle(name);
//            if (!noSaveLast) {
//                mSettings.setLastDeck(name);
//            }
//        } else {
//            setActionBarSubTitle(getString(R.string.noname));
//        }
    }

    private File[] getYdkFiles() {
        File dir = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH);
        File[] files = dir.listFiles((file, s) -> {
            return s.toLowerCase(Locale.US).endsWith(Constants.YDK_FILE_EX);
        });
        return files;
    }

    class DeckItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }

    class DeckLayoutManager extends GridLayoutManager {
        private int cardWidth, cardHeight;

        public DeckLayoutManager(Context context, final int span, int width, int height) {
            super(context, span);
            cardWidth = width;
            cardHeight = height;
            setSpanSizeLookup(new SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mDeckAdapater.isLabel(position)) {
                        return getSpanCount();
                    }
                    return 1;
                }
            });
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (Exception e) {

            }
        }

        @Override
        public void measureChild(View child, int widthUsed, int heightUsed) {
            int pos = mRecyclerView.getChildAdapterPosition(child);
            try {
                if (!mDeckAdapater.isLabel(pos)) {
                    setMeasuredDimension(cardWidth, cardHeight);
                } else {
                    super.measureChild(child, widthUsed, heightUsed);
                }
            } catch (Exception e) {
                super.measureChild(child, widthUsed, heightUsed);
            }
        }
    }
}
