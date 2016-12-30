package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper2;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.CardListAdapater;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.bean.Deck;
import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.core.CardDetail;
import cn.garymb.ygomobile.deck.DeckAdapater;
import cn.garymb.ygomobile.deck.DeckItem;
import cn.garymb.ygomobile.deck.DeckItemTouchHelper;
import cn.garymb.ygomobile.deck.DeckItemType;
import cn.garymb.ygomobile.deck.DeckLayoutManager;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.DialogPlus;
import cn.garymb.ygomobile.plus.RecyclerViewItemListener;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.plus.spinner.SimpleSpinnerAdapter;
import cn.garymb.ygomobile.plus.spinner.SimpleSpinnerItem;
import cn.garymb.ygomobile.utils.BitmapUtil;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.ShareUtil;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.LimitType;

import static cn.garymb.ygomobile.Constants.YDK_FILE_EX;

public class DeckManagerActivity extends BaseCardsAcitivity implements RecyclerViewItemListener.OnItemListener,
        CardListAdapater.OnAddCardListener, ItemTouchHelper2.OnDragListner {
    private RecyclerView mRecyclerView;
    private DeckAdapater mDeckAdapater;
    private AppsSettings mSettings = AppsSettings.get();
    private LimitList mLimitList;
    private File mYdkFile;
    private DeckItemTouchHelper mDeckItemTouchHelper;
    private boolean isShowing = false;
    private AppCompatSpinner mDeckSpinner;
    private SimpleSpinnerAdapter mSimpleSpinnerAdapter;
    private String mPreLoad;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeckSpinner = bind(R.id.toolbar_list);
        mCardListAdapater.setOnAddCardListener(this);
        mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(), 0, mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());
        mRecyclerView.setAdapter((mDeckAdapater = new DeckAdapater(this, mRecyclerView)));
        mRecyclerView.setLayoutManager(new DeckLayoutManager(this, Constants.DECK_WIDTH_COUNT));
        mDeckItemTouchHelper = new DeckItemTouchHelper(mDeckAdapater);
        mDeckItemTouchHelper.setOnDragListner(this);
        ItemTouchHelper2 touchHelper = new ItemTouchHelper2(this, mDeckItemTouchHelper);
        touchHelper.setEnableClickDrag(Constants.DECK_SINGLE_PRESS_DRAG);
        touchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemListener(mRecyclerView, this));
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
        if (getIntent().hasExtra(Intent.EXTRA_TEXT)) {
            String path = getIntent().getStringExtra(Intent.EXTRA_TEXT);
            if (!TextUtils.isEmpty(path)) {
                mPreLoad = path;
            }
        }
        //
        ProgressDialog dlg = ProgressDialog.show(this, null, getString(R.string.loading));
        VUiKit.defer().when(() -> {
            StringManager.get().load();//loadFile(stringfile.getAbsolutePath());
            LimitManager.get().load();//loadFile(stringfile.getAbsolutePath());
            if (mLimitManager.getCount() > 0) {
                mCardLoader.setLimitList(mLimitManager.getLimitFromIndex(0));
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
                return mDeckAdapater.read(mCardLoader, file, mLimitList);
            } else {
                return new DeckInfo();
            }
        }).done((rs) -> {
            isLoad = true;
            dlg.dismiss();
            setLimitList(mLimitManager.getCount() > 0 ? mLimitManager.getLimit(0) : null);
            mCardSelector.initItems();
            isLoad = true;
            setCurYdkFile(mYdkFile, false);
            initDecksListSpinners(mDeckSpinner);
            mDeckAdapater.setDeck(rs);
            mDeckAdapater.notifyDataSetChanged();

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDragStart() {

    }

    @Override
    public void onDragLongPress(int pos) {
        Log.i("kk", "delete " + pos);
        if (mSettings.isDialogDelete()) {

            DeckItem deckItem = mDeckAdapater.getItem(pos);
            if (deckItem == null || deckItem.getCardInfo() == null) {
                return;
            }
            DialogPlus dialogPlus = new DialogPlus(this);
            dialogPlus.setTitle(R.string.question);
            dialogPlus.setMessage(getString(R.string.delete_card, deckItem.getCardInfo().Name));
            dialogPlus.setMessageGravity(Gravity.CENTER_HORIZONTAL);
            dialogPlus.setLeftButtonListener((dlg, v) -> {
                dlg.dismiss();
                mDeckItemTouchHelper.remove(pos);
            });
            dialogPlus.show();
        } else {
            getSupportActionBar().hide();
            mDeckAdapater.showHeadView();
        }
    }

    @Override
    public void onDragLongPressEnd() {
        getSupportActionBar().show();
        mDeckAdapater.hideHeadView();
    }

    @Override
    public void onDragEnd() {

    }

    private void setLimitList(LimitList limitList) {
        boolean nochanged = mLimitList != null && TextUtils.equals(mLimitList.getName(), limitList.getName());
        mLimitList = limitList;
        if (!nochanged) {
            mDeckAdapater.setLimitList(mLimitList);
            mDeckAdapater.notifyDataSetChanged();
        }
        mCardListAdapater.setLimitList(limitList);
        mCardLoader.setLimitList(mLimitList);
    }

    private void loadDeck(File file) {
        loadDeck(file, false);
    }

    private void loadDeck(File file, boolean noSaveLast) {
        ProgressDialog dlg = ProgressDialog.show(this, null, getString(R.string.loading));
        VUiKit.defer().when(() -> {
            if (file == null) {
                return new DeckInfo();
            }
            if (mCardLoader.isOpen() && file.exists()) {
                return mDeckAdapater.read(mCardLoader, file, mLimitList);
            } else {
                return new DeckInfo();
            }
        }).done((rs) -> {
            dlg.dismiss();
            setCurYdkFile(file, noSaveLast);
            mDeckAdapater.setDeck(rs);
            mDeckAdapater.notifyDataSetChanged();
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

    @Override
    public void onSearchStart(LimitList limitList) {
        setLimitList(limitList);
        hideDrawers();
    }

    @Override
    protected View getMainView() {
        if (mRecyclerView == null) {
            mRecyclerView = new RecyclerView(this);
        }
        return mRecyclerView;
    }

    @Override
    public void onAdd(int pos) {
        CardInfo cardInfo = mCardListAdapater.getItem(pos);
        if (cardInfo != null) {
            if (mDeckAdapater.getMainCount() >= Constants.DECK_MAIN_MAX) {
                addSideCard(cardInfo);
            } else {
                addMainCard(cardInfo);
            }
        }
    }

    @Override
    protected void onCardClick(CardInfo cardInfo, int pos) {
        showCardDialog(cardInfo, pos);
    }

    @Override
    protected void onCardLongClick(View view, CardInfo cardInfo, int pos) {

    }

    @Override
    public void onResetSearch() {
        super.onResetSearch();
    }

    @Override
    public void onSearchResult(List<CardInfo> cardInfos) {
        super.onSearchResult(cardInfos);
        showResult(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deck_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(View view, int pos) {
        if (!Constants.DECK_SINGLE_PRESS_DRAG) {
            DeckItem deckItem = mDeckAdapater.getItem(pos);
            if (deckItem != null) {
                showCardDialog(deckItem.getCardInfo(), pos);
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int pos) {
        //拖拽中，就不显示
        if (Constants.DECK_SINGLE_PRESS_DRAG) {
        }
    }

    @Override
    public void onItemDoubleClick(View view, int pos) {
        //拖拽中，就不显示
        if (Constants.DECK_SINGLE_PRESS_DRAG) {
            DeckItem deckItem = mDeckAdapater.getItem(pos);
            if (deckItem != null) {
                showCardDialog(deckItem.getCardInfo(), pos);
            }
        }
    }

    protected void showCardDialog(CardInfo cardInfo, int pos) {
        if (cardInfo != null) {
            if (isShowing) return;
            isShowing = true;
            CardDetail cardDetail = new CardDetail(this);
            cardDetail.showAdd();
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Dialog_Translucent);
            builder.setView(cardDetail.getView());
            builder.setOnCancelListener((dlg) -> {
                isShowing = false;
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                builder.setOnDismissListener((dlg) -> {
                    isShowing = false;
                });
            }
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
                    isShowing = false;
                }

                @Override
                public void onAddSideCard(CardInfo cardInfo) {
                    addSideCard(cardInfo);
                }

                @Override
                public void onAddMainCard(CardInfo cardInfo) {
                    addMainCard(cardInfo);
                }
            });
        }
    }

    private void addSideCard(CardInfo cardInfo) {
        if (checkLimit(cardInfo)) {
            boolean rs = mDeckAdapater.AddCard(cardInfo, DeckItemType.SideCard);
            if (rs) {
                Toast.makeText(DeckManagerActivity.this, R.string.add_card_tip_ok, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DeckManagerActivity.this, R.string.add_card_tip_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMainCard(CardInfo cardInfo) {
        if (checkLimit(cardInfo)) {
            boolean rs;
            if (cardInfo.isExtraCard()) {
                rs = mDeckAdapater.AddCard(cardInfo, DeckItemType.ExtraCard);
            } else {
                rs = mDeckAdapater.AddCard(cardInfo, DeckItemType.MainCard);
            }
            if (rs) {
                Toast.makeText(DeckManagerActivity.this, R.string.add_card_tip_ok, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(DeckManagerActivity.this, R.string.add_card_tip_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isExit = false;
//
//    @Override
//    public void finish() {
//        if (!isExit) {
//            if (mYdkFile != null && mYdkFile.exists()) {
//                DialogPlus builder = new DialogPlus(this);
//                builder.setTitle(R.string.question);
//                builder.setMessage(R.string.quit_deck_tip);
//                builder.setLeftButtonListener((dlg, s) -> {
//                    dlg.dismiss();
//                    isExit = true;
//                    finish();
//                });
//                builder.show();
//                return;
//            }
//        }
//        super.finish();
//    }

    @Override
    public void onBackPressed() {
        if (mDrawerlayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerlayout.closeDrawer(Gravity.RIGHT);
        } else if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        } else if (!isExit) {
            if (mYdkFile != null && mYdkFile.exists()) {
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.quit_deck_tip);
                builder.setMessageGravity(Gravity.CENTER_HORIZONTAL);
                builder.setLeftButtonListener((dlg, s) -> {
                    dlg.dismiss();
                    isExit = true;
                    finish();
                });
                builder.show();
            }
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkLimit(CardInfo cardInfo) {
        Map<Long, Integer> mCount = mDeckAdapater.getCardCount();
        if (mLimitList != null && mLimitList.check(cardInfo, LimitType.Forbidden)) {
            Toast.makeText(DeckManagerActivity.this, getString(R.string.tip_card_max, 0), Toast.LENGTH_SHORT).show();
            return false;
        }
        Long id = cardInfo.Alias > 0 ? cardInfo.Alias : cardInfo.Code;
        Integer count = mCount.get(id);
        if (count != null) {
            if (mLimitList != null && mLimitList.check(cardInfo, LimitType.Limit)) {
                if (count >= 1) {
                    Toast.makeText(DeckManagerActivity.this, getString(R.string.tip_card_max, 1), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (mLimitList != null && mLimitList.check(cardInfo, LimitType.SemiLimit)) {
                if (count >= 2) {
                    Toast.makeText(DeckManagerActivity.this, getString(R.string.tip_card_max, 2), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (count >= Constants.CARD_MAX_COUNT) {
                Toast.makeText(DeckManagerActivity.this, getString(R.string.tip_card_max, 3), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                mDeckAdapater.notifyDataSetChanged();
//                break;
            case R.id.action_search:
                //弹条件对话框
                showSearch(true);
                break;
            case R.id.action_card_list:
                showResult(true);
                break;
            case R.id.action_save:
                if (mYdkFile == null) {
                    inputDeckName();
                } else {
                    save();
                }
                break;
            case R.id.action_rename:
                inputDeckName();
                break;
            case R.id.action_deck_new: {
                setCurYdkFile(null);
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.question_keep_cur_deck);
                builder.setMessageGravity(Gravity.CLIP_HORIZONTAL);
                builder.setLeftButtonListener((dlg, rs) -> {
                    dlg.dismiss();
                    inputDeckName();
                });
                builder.setCloseLinster((dlg, rs) -> {
                    dlg.dismiss();
                    loadDeck(null);
                    inputDeckName();
                });
                builder.show();
            }
            break;
            case R.id.action_clear_deck: {
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.question_clear_deck);
                builder.setMessageGravity(Gravity.CLIP_HORIZONTAL);
                builder.setLeftButtonListener((dlg, rs) -> {
                    mDeckAdapater.setDeck(new DeckInfo());
                    mDeckAdapater.notifyDataSetChanged();
                    dlg.dismiss();
                });
                builder.show();
            }
            break;
            case R.id.action_delete_deck: {
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.question_delete_deck);
                builder.setMessageGravity(Gravity.CLIP_HORIZONTAL);
                builder.setLeftButtonListener((dlg, rs) -> {
                    if (mYdkFile != null && mYdkFile.exists()) {
                        mYdkFile.delete();
                    }
                    dlg.dismiss();
                    initDecksListSpinners(mDeckSpinner);
                    loadDeck(null);
                });
                builder.show();
            }
            break;
          /*  case R.id.action_manager: {
                //显示对话框:
                //选择禁卡表
                //卡组列表
                DialogPlus dialogPlus = new DialogPlus(this);
//                View view = LayoutInflater.from(this).inflate(R.layout.dialog_deck, null);
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                dialogPlus.setTitle(R.string.deck_manager);
                dialogPlus.setView(R.layout.dialog_deck);
                Spinner ydks = (Spinner) dialogPlus.findViewById(R.id.sp_ydk_list);
                initDecksListSpinners(ydks);
                Spinner limits = (Spinner) dialogPlus.findViewById(R.id.sp_limit_list);
                initLimitListSpinners(limits);
                dialogPlus.setLeftButtonListener((dlg, rs) -> {
                    LimitList limitList = getSelectLimitList(limits);
                    setLimitList(limitList);
                    File file = getSelectDeck(ydks);
                    if (file != null) {
                        dlg.dismiss();
                        loadDeck(file);
                    }
                });
                dialogPlus.show();
            }
            break;*/
            case R.id.action_unsort:
                //打乱
                mDeckAdapater.unSort();
                break;
            case R.id.action_sort:
                mDeckAdapater.sort();
                break;
//            case R.id.action_share_deck:
//                shareDeck();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareDeck() {
        Deck deck = mDeckAdapater.toDeck(mYdkFile);
        String label = TextUtils.isEmpty(deck.getName()) ? getString(R.string.share_deck) : deck.getName();
        final String uriString = deck.toAppUri().toString();
        final String httpUri = deck.toHttpUri().toString();
        shareUrl(uriString, label);
        /*
        RequestQueue mQueue = Volley.newRequestQueue(this);
        final String url = "http://dwz.wailian.work/api.php?url="
                + Base64.encodeToString(uriString.getBytes(), Base64.NO_WRAP)+"&site=sina";
        Log.i("kk", "url=" + url);
        StringRequest stringRequest = new StringRequest(url,
                (response) -> {
                    Log.i("kk", "json=" + response);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (null != jsonObject) {
                        if("error".equals(jsonObject.optString("result"))){
                            Log.e("kk", "json=" + jsonObject.optString("data"));
                        }else {
                            JSONObject data = jsonObject.optJSONObject("data");
                            if (data != null) {
                                shareUrl(data.optString("short_url"), label);
                                return;
                            }
                        }
                    }
                    shareUrl(httpUri, label);
                },
                (error) -> {
                    Log.e("kk", "error=" + error);
                    shareUrl(httpUri, label);
                }
        );
        mQueue.add(stringRequest);
        */
    }

    private void shareUrl(String uri, String label) {
        String url = getString(R.string.deck_share_head) + "  " + uri;
        ShareUtil.shareText(this, getString(R.string.share_deck), url, null);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (Build.VERSION.SDK_INT > 19) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(label, uri));
        } else {
            clipboardManager.setText(uri);
        }
        Toast.makeText(this, R.string.copy_to_clipbroad, Toast.LENGTH_SHORT).show();
    }

    private File getSelectDeck(Spinner spinner) {
        Object o = SimpleSpinnerAdapter.getSelectTag(spinner);
        if (o != null && o instanceof File) {
            return (File) o;
        }
        return null;
    }

    private LimitList getSelectLimitList(Spinner spinner) {
        int val = (int) SimpleSpinnerAdapter.getSelect(spinner);
        if (val > 0) {
            return mLimitManager.getLimitFromIndex(val);
        }
        return null;
    }

    private File[] getYdkFiles() {
        File dir = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH);
        File[] files = dir.listFiles((file, s) -> {
            return s.toLowerCase(Locale.US).endsWith(Constants.YDK_FILE_EX);
        });
        return files;
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
        List<Integer> ids = mLimitManager.getLists();
        int index = -1;
        int i = 0;
        for (Integer id : ids) {
            LimitList list = mLimitManager.getLimitFromIndex(id);
            if (list == mLimitList) {
                index = i;
            }
            items.add(new SimpleSpinnerItem(id, list.getName()));
            i++;
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(this);
        adapter.set(items);
        spinner.setAdapter(adapter);
        if (index >= 0) {
            spinner.setSelection(index);
        }
    }

    private void inputDeckName() {
        DialogPlus builder = new DialogPlus(this);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.intpu_name);
        EditText editText = new EditText(this);
        editText.setGravity(Gravity.TOP | Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setSingleLine();
        if (mYdkFile != null) {
            editText.setText(mYdkFile.getName());
        }
        builder.setView(editText);
        builder.setLeftButtonListener((dlg, s) -> {
            CharSequence name = editText.getText();
            if (!TextUtils.isEmpty(name)) {
                String filename = String.valueOf(name);
                if (!filename.endsWith(YDK_FILE_EX)) {
                    filename += YDK_FILE_EX;
                }
                File ydk = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH + "/" + filename);
                if (ydk.exists()) {
                    Toast.makeText(this, R.string.file_exist, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mYdkFile != null && mYdkFile.exists()) {
                    if (mYdkFile.renameTo(ydk)) {
                        mYdkFile = ydk;
                        initDecksListSpinners(mDeckSpinner);
                        dlg.dismiss();
                        loadDeck(ydk);
                    }
                } else {
                    dlg.dismiss();
                    try {
                        ydk.createNewFile();
                    } catch (IOException e) {
                    }
                    mYdkFile = ydk;
                    initDecksListSpinners(mDeckSpinner);
                    save();
                    setCurYdkFile(mYdkFile);
                }
            } else {
                dlg.dismiss();
            }
        });
        builder.show();
    }

    private void save() {
        if (mDeckAdapater.save(mYdkFile)) {
            Toast.makeText(this, R.string.save_tip_ok, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.save_tip_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
