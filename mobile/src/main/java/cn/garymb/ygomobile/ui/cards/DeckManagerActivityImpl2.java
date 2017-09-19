package cn.garymb.ygomobile.ui.cards;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;
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
import cn.garymb.ygomobile.ui.adapters.SimpleSpinnerAdapter;
import cn.garymb.ygomobile.ui.adapters.SimpleSpinnerItem;
import cn.garymb.ygomobile.ui.cards.deck.DeckItem;
import cn.garymb.ygomobile.ui.plus.DialogPlus;
import cn.garymb.ygomobile.ui.plus.VUiKit;
import cn.garymb.ygomobile.ui.widget.DeckGroupView;
import cn.garymb.ygomobile.ui.widget.DeckView;
import cn.garymb.ygomobile.utils.IOUtils;
import ocgcore.LimitManager;
import ocgcore.StringManager;
import ocgcore.data.Card;
import ocgcore.data.LimitList;

class DeckManagerActivityImpl2 extends BaseActivity implements CardLoader.CallBack {
    private DeckGroupView mDeckView;
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
    private AppCompatSpinner mLimitSpinner;
    private AppCompatSpinner mDeckSpinner;
    private SimpleSpinnerAdapter mSimpleSpinnerAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_cards2);
        Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        mLimitSpinner = $(R.id.sp_limit_list);
        mDeckSpinner = $(R.id.toolbar_list);
        mDeckView = $(R.id.deck_group);
        if (mDeckView == null) {
            throw new RuntimeException("no find DeckView");
        }
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

        mDeckSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                File file = getSelectDeck(mDeckSpinner);
                if (file != null) {
                    loadDeck(file);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
            initLimitListSpinners(mLimitSpinner);
            initDecksListSpinners(mDeckSpinner);
            mDeckView.updateAll(rs);
            mDeckView.notifyDataSetChanged();
        });
    }

    @Override
    protected void onDestroy() {
        ImageLoader.onDestory(this);
        super.onDestroy();
    }
    private File getSelectDeck(Spinner spinner) {
        Object o = SimpleSpinnerAdapter.getSelectTag(spinner);
        if (o != null && o instanceof File) {
            return (File) o;
        }
        return null;
    }

    private void loadDeck(File file) {
        loadDeck(file, false);
    }

    private void loadDeck(File file, boolean noSaveLast) {
        DialogPlus dlg = DialogPlus.show(this, null, getString(R.string.loading));
        VUiKit.defer().when(() -> {
            if (file == null) {
                return new DeckInfo();
            }
            if (mCardLoader.isOpen() && file.exists()) {
                return DeckLoader.readDeck(mCardLoader, file, mLimitList);
            } else {
                return new DeckInfo();
            }
        }).done((rs) -> {
            dlg.dismiss();
            setCurYdkFile(file, noSaveLast);
            mDeckView.updateAll(rs);
            mDeckView.notifyDataSetChanged();
        });
    }

    private void initDecksListSpinners(Spinner spinner) {
        File[] files = getYdkFiles();
        List<SimpleSpinnerItem> items = new ArrayList<>();
        String name = mYdkFile != null ? mYdkFile.getName() : null;
        int index = -1;
        if (files != null) {
            int i = 0;
            for (File file : files) {
                if (name != null && TextUtils.equals(name, file.getName())) {
                    index = i;
                }
                String filename = IOUtils.tirmName(file.getName(), Constants.YDK_FILE_EX);
                items.add(new SimpleSpinnerItem(i++, filename).setTag(file));
            }
        }
        mSimpleSpinnerAdapter = new SimpleSpinnerAdapter(this);
        mSimpleSpinnerAdapter.set(items);
        mSimpleSpinnerAdapter.setColor(Color.WHITE);
        mSimpleSpinnerAdapter.setSingleLine(true);
        spinner.setAdapter(mSimpleSpinnerAdapter);
        if (index >= 0) {
            spinner.setSelection(index);
        }
    }
    private void initLimitListSpinners(Spinner spinner) {
        List<SimpleSpinnerItem> items = new ArrayList<>();
        List<LimitList> limitLists = mLimitManager.getLimitLists();
        int index = -1;
        int count = mLimitManager.getCount();
        LimitList cur = mLimitList;
        for (int i = 0; i < count; i++) {
            LimitList list = limitLists.get(i);
            if (i == 0) {
                items.add(new SimpleSpinnerItem(i, getString(R.string.label_limitlist)));
            } else {
                items.add(new SimpleSpinnerItem(i, list.getName()));
            }
            if (cur != null) {
                if (TextUtils.equals(cur.getName(), list.getName())) {
                    index = i;
                }
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(this);
        adapter.setColor(Color.WHITE);
        adapter.set(items);
        spinner.setAdapter(adapter);
        if (index >= 0) {
            spinner.setSelection(index);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setLimitList(mLimitManager.getLimit(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setLimitList(LimitList limitList) {
        if (limitList == null) return;
        boolean nochanged = mLimitList != null && TextUtils.equals(mLimitList.getName(), limitList.getName());
        mLimitList = limitList;
        mDeckView.setLimitList(limitList);
        runOnUiThread(() -> {
            mDeckView.notifyDataSetChanged();
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deck_menu, menu);
        return true;
    }

}
