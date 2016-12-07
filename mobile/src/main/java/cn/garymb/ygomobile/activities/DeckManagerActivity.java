package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelperCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.CardListAdapater;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardDetail;
import cn.garymb.ygomobile.deck.DeckAdapater;
import cn.garymb.ygomobile.deck.DeckInfo;
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
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.utils.BitmapUtil;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.ShareUtil;
import cn.ygo.ocgcore.LimitList;

import static cn.garymb.ygomobile.Constants.YDK_FILE_EX;

public class DeckManagerActivity extends BaseCardsAcitivity implements RecyclerViewItemListener.OnItemListener,
        CardListAdapater.OnAddCardListener, DeckItemTouchHelper.CallBack {
    private RecyclerView mRecyclerView;
    private DeckAdapater mDeckAdapater;
    private AppsSettings mSettings = AppsSettings.get();
    private LimitList mLimitList;
    private File mYdkFile;
    private DeckItemTouchHelper mDeckItemTouchHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(), 0, mRecyclerView.getPaddingRight(), mRecyclerView.getPaddingBottom());
        mRecyclerView.setAdapter((mDeckAdapater = new DeckAdapater(this, mRecyclerView)));
        mRecyclerView.setLayoutManager(new DeckLayoutManager(this, Constants.DECK_WIDTH_COUNT));
        mDeckItemTouchHelper = new DeckItemTouchHelper(mDeckAdapater);
        mDeckItemTouchHelper.setCallBack(this);
        ItemTouchHelperCompat touchHelper = new ItemTouchHelperCompat(mDeckItemTouchHelper);
        touchHelper.setEnableClickDrag(Constants.DECK_SINGLE_PRESS_DRAG);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemListener(mRecyclerView, this));
        mCardListAdapater.setShowAdd(true);
        mCardListAdapater.setOnAddCardListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onInit() {
        super.onInit();
        setLimitList(mLimitManager.getCount() > 0 ? mLimitManager.getLimit(0) : null);
        isLoad = true;
        File file = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH + "/" + mSettings.getLastDeck() + Constants.YDK_FILE_EX);
        loadDeck(file);
    }

    @Override
    public void onDragStart() {
        getSupportActionBar().hide();
    }

    @Override
    public void onDragEnd() {
        getSupportActionBar().show();
    }

    private void setLimitList(LimitList limitList) {
        mLimitList = limitList;
        mCardLoader.setLimitList(mLimitList);
    }

    private void loadDeck(File file) {
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
            setFile(file);
            mDeckAdapater.setDeck(rs);
            mDeckAdapater.notifyDataSetChanged();
        });
    }

    private void setFile(File file) {
        mYdkFile = file;
        if (file != null && file.exists()) {
            String name = IOUtils.tirmName(file.getName(), Constants.YDK_FILE_EX);
            setActionBarSubTitle(name);
            mSettings.setLastDeck(name);
        } else {
            setActionBarSubTitle(getString(R.string.noname));
        }
    }

    @Override
    public void onSearchStart(LimitList limitList) {
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
            addMainCard(cardInfo);
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
            if (mSettings.getShowCard() == Constants.PREF_DECK_SHOW_CARD_LONG_PRESS) {
                DeckItem deckItem = mDeckAdapater.getItem(pos);
                if (deckItem != null) {
                    showCardDialog(deckItem.getCardInfo(), pos);
                }
            }
        }
    }

    @Override
    public void onItemDoubleClick(View view, int pos) {
        //拖拽中，就不显示
        if (Constants.DECK_SINGLE_PRESS_DRAG) {
            if (mSettings.getShowCard() == Constants.PREF_DECK_SHOW_CARD_DOUBLE) {
                DeckItem deckItem = mDeckAdapater.getItem(pos);
                if (deckItem != null) {
                    showCardDialog(deckItem.getCardInfo(), pos);
                }
            }
        }
    }

    protected void showCardDialog(CardInfo cardInfo, int pos) {
        if (cardInfo != null) {
            CardDetail cardDetail = new CardDetail(this);
            cardDetail.showAdd();
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
                    if (checkLimit(cardInfo)) {
                        boolean rs = mDeckAdapater.AddCard(cardInfo, DeckItemType.SideCard);
                        if (rs) {
                            Toast.makeText(DeckManagerActivity.this, R.string.add_card_tip_ok, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DeckManagerActivity.this, R.string.add_card_tip_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onAddMainCard(CardInfo cardInfo) {
                    addMainCard(cardInfo);
                }
            });
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
//                builder.setButtonListener((dlg, s) -> {
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
        if (!isExit) {
            if (mYdkFile != null && mYdkFile.exists()) {
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.quit_deck_tip);
                builder.setMessageGravity(Gravity.CENTER_HORIZONTAL);
                builder.setButtonListener((dlg, s) -> {
                    dlg.dismiss();
                    isExit = true;
                    finish();
                });
                builder.show();
                return;
            }
        }
        super.onBackPressed();
    }

    private boolean checkLimit(CardInfo cardInfo) {
        Map<Long, Integer> mCount = mDeckAdapater.getCardCount();
        if (mLimitList != null && mLimitList.isForbidden(cardInfo.Code)) {
            Toast.makeText(DeckManagerActivity.this, getString(R.string.tip_card_max, 0), Toast.LENGTH_SHORT).show();
            return false;
        }
        Integer count = mCount.get(Long.valueOf(cardInfo.Code));
        if (count != null) {
            if (mLimitList != null && mLimitList.isLimit(cardInfo.Code)) {
                if (count >= 1) {
                    Toast.makeText(DeckManagerActivity.this, getString(R.string.tip_card_max, 1), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (mLimitList != null && mLimitList.isSemiLimit(cardInfo.Code)) {
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
            case R.id.action_refresh:
                mDeckAdapater.notifyDataSetChanged();
                break;
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
            case R.id.action_deck_new:
                loadDeck(null);
                break;
            case R.id.action_delete_deck: {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                DialogPlus builder = new DialogPlus(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.question_delete_deck);
                builder.setButtonListener((dlg, rs) -> {
                    if (mYdkFile != null && mYdkFile.exists()) {
                        mYdkFile.delete();
                    }
                    dlg.dismiss();
                    loadDeck(null);
                });
                builder.show();
            }
            break;
            case R.id.action_manager: {
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
                dialogPlus.setButtonListener((dlg, rs) -> {
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
            break;
            case R.id.action_unsort:
                //打乱
                mDeckAdapater.unSort();
                break;
            case R.id.action_sort:
                mDeckAdapater.sort();
                break;
//            case R.id.action_share_image:
//                shareDeck();
//                mDeckAdapater.notifyDataSetChanged();
//                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void shareDeck() {
        ProgressDialog dialog = ProgressDialog.show(this, null, "share");
        Bitmap bmp = BitmapUtil.getBitmapFromView(mRecyclerView);
        if (bmp == null || mYdkFile == null) {
            Toast.makeText(this, "get image fail", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(mSettings.getResourcePath(), Constants.SHARE_FILE);
        VUiKit.defer().when(() -> {
            return BitmapUtil.saveBitmap(bmp, file.getAbsolutePath(), 100);
        }).done((rs) -> {
            dialog.dismiss();
            if (rs) {
                ShareUtil.shareImage(this, "share deck", file.getAbsolutePath(), null);
            } else {
                Toast.makeText(this, "save image fail", Toast.LENGTH_SHORT).show();
            }
        });
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
                items.add(new SimpleSpinnerItem(i++, file.getName()).setTag(file));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(this);
        adapter.set(items);
        spinner.setAdapter(adapter);
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
        builder.setView(editText);
        builder.setButtonListener((dlg, s) -> {
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
                        dlg.dismiss();
                        loadDeck(ydk);
                    }
                } else {
                    dlg.dismiss();
                    mYdkFile = ydk;
                    save();
                    setFile(mYdkFile);
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
