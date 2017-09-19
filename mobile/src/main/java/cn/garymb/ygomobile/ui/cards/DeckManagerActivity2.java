package cn.garymb.ygomobile.ui.cards;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.util.List;
import java.util.Locale;

import cn.garymb.ygomobile.AppsSettings;
import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.loader.CardLoader;
import cn.garymb.ygomobile.loader.DeckLoader;
import cn.garymb.ygomobile.loader.ImageLoader;
import cn.garymb.ygomobile.ui.activities.BaseActivity;
import cn.garymb.ygomobile.ui.plus.DialogPlus;
import cn.garymb.ygomobile.ui.plus.VUiKit;
import cn.garymb.ygomobile.ui.widget.DeckView;
import cn.garymb.ygomobile.utils.IOUtils;
import ocgcore.LimitManager;
import ocgcore.StringManager;
import ocgcore.data.Card;
import ocgcore.data.LimitList;

public class DeckManagerActivity2 extends BaseActivity implements CardLoader.CallBack {
    private DeckView mDeckView;
    protected CardSearcher mCardSelector;
    protected StringManager mStringManager = StringManager.get();
    protected LimitManager mLimitManager = LimitManager.get();
    protected CardLoader mCardLoader;
    protected boolean isLoad = false;
    private String mPreLoad;
    private LimitList mLimitList;
    private File mYdkFile;
    private ImageLoader mImageLoader;
    private AppsSettings mSettings = AppsSettings.get();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_cards2);
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();

        mDeckView = $(R.id.deck);
        mImageLoader = ImageLoader.get(this);
        mCardLoader = new CardLoader(this);
        mCardLoader.setCallBack(this);
        mCardSelector = new CardSearcher($(R.id.nav_view_list), mCardLoader);
        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            String path = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            if (!TextUtils.isEmpty(path)) {
                mPreLoad = path;
            }
        }

        DialogPlus dlg = DialogPlus.show(this, null, getString(R.string.loading));
        VUiKit.defer().when(() -> {
            StringManager.get().load();//loadFile(stringfile.getAbsolutePath());
            LimitManager.get().load();//loadFile(stringfile.getAbsolutePath());
            if (mLimitManager.getCount() > 1) {
                mCardLoader.setLimitList(mLimitManager.getLimit(1));
            }
            mCardLoader.openDb();
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
                return DeckLoader.readDeck(mCardLoader, file, mLimitList);
            } else {
                return new DeckInfo();
            }
        }).done((rs) -> {
            isLoad = true;
            dlg.dismiss();
            mCardSelector.initItems();
            mLimitList = mCardLoader.getLimitList();
            isLoad = true;
            setCurYdkFile(mYdkFile, false);
//            initLimitListSpinners(mLimitSpinner);
//            initDecksListSpinners(mDeckSpinner);
            mDeckView.updateAll(rs);
        });
    }

    @Override
    protected void onDestroy() {
        ImageLoader.onDestory(this);
        super.onDestroy();
    }

    private void setCurYdkFile(File file) {
        setCurYdkFile(file, false);
    }

    private void setCurYdkFile(File file, boolean noSaveLast) {
        mYdkFile = file;
        if (file != null && file.exists()) {
            String name = IOUtils.tirmName(file.getName(), Constants.YDK_FILE_EX);
            setActionBarSubTitle(name);
            if (!noSaveLast) {
                mSettings.setLastDeck(name);
            }
        } else {
            setActionBarSubTitle(getString(R.string.noname));
        }
    }

    private File[] getYdkFiles() {
        File dir = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH);
        File[] files = dir.listFiles((file, s) -> {
            return s.toLowerCase(Locale.US).endsWith(Constants.YDK_FILE_EX);
        });
        return files;
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
}
