package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardDetail;
import cn.garymb.ygomobile.deck.DeckAdapater;
import cn.garymb.ygomobile.deck.DeckInfo;
import cn.garymb.ygomobile.deck.DeckItem;
import cn.garymb.ygomobile.deck.DeckItemTouchHelper;
import cn.garymb.ygomobile.deck.DeckItemType;
import cn.garymb.ygomobile.deck.DeckLayoutManager;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.RecyclerViewItemListener;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.plus.spinner.SimpleSpinnerAdapter;
import cn.garymb.ygomobile.plus.spinner.SimpleSpinnerItem;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.ygo.ocgcore.LimitList;

import static cn.garymb.ygomobile.Constants.YDK_FILE_EX;

public class DeckManagerActivity extends BaseCardsAcitivity implements RecyclerViewItemListener.OnItemListener {
    private RecyclerView mRecyclerView;
    private DeckAdapater mDeckAdapater;
    private AppsSettings mSettings = AppsSettings.get();
    private LimitList mLimitList;
    private File mYdkFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setAdapter((mDeckAdapater = new DeckAdapater(this, mRecyclerView)));
        mRecyclerView.setLayoutManager(new DeckLayoutManager(this, Constants.DECK_WIDTH_COUNT));
        mCardSelector.hideLimit();
        ItemTouchHelper touchHelper = new ItemTouchHelper(new DeckItemTouchHelper(mDeckAdapater));
        touchHelper.attachToRecyclerView(mRecyclerView);

//        mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        hideDrawers();
        mRecyclerView.addOnItemTouchListener(new RecyclerViewItemListener(mRecyclerView, this));
    }

    @Override
    protected void onInit() {
        super.onInit();
        setLimitList(mLimitManager.getCount() > 0 ? mLimitManager.getLimit(0) : null);
        File file = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH + "/" + mSettings.getLastDeck() + Constants.YDK_FILE_EX);
        loadDeck(file);
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
            mYdkFile = file;
            if (file != null && file.exists()) {
                setTitle(mYdkFile.getName());
                mSettings.setLastDeck(IOUtils.tirmName(file.getName(), Constants.YDK_FILE_EX));
            } else {
                setTitle(R.string.noname);
            }
            mDeckAdapater.setDeck(rs);
            mDeckAdapater.notifyDataSetChanged();
        });
    }

    @Override
    public void onSearchStart(LimitList limitList) {

    }

    @Override
    protected View getMainView() {
        if (mRecyclerView == null) {
            mRecyclerView = new RecyclerView(this);
        }
        return mRecyclerView;
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
        DeckItem deckItem = mDeckAdapater.getItem(pos);
        if (deckItem != null) {
            showCardDialog(deckItem.getCardInfo(), pos);
        }
    }

    @Override
    public void onItemLongClick(View view, int pos) {

    }

    @Override
    public void onItemDoubleClick(View view, int pos) {
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
            });
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.question);
                builder.setMessage(R.string.question_delete_deck);
                builder.setNegativeButton(android.R.string.ok, (dlg, rs) -> {
                    if (mYdkFile != null && mYdkFile.exists()) {
                        mYdkFile.delete();
                    }
                    dlg.dismiss();
                    loadDeck(null);
                });
                builder.setNeutralButton(android.R.string.cancel, (dlg, rs) -> {
                    dlg.dismiss();
                });
                builder.show();
            }
            break;
            case R.id.action_manager: {
                //显示对话框:

                //选择禁卡表
                //卡组列表
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_deck, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.deck_manager);
                builder.setView(view);
                Spinner ydks = (Spinner) view.findViewById(R.id.sp_ydk_list);
                initDecksListSpinners(ydks);
                Spinner limits = (Spinner) view.findViewById(R.id.sp_limit_list);
                initLimitListSpinners(limits);
                builder.setNegativeButton(android.R.string.ok, (dlg, rs) -> {
                    LimitList limitList = getSelectLimitList(limits);
                    setLimitList(limitList);
                    File file = getSelectDeck(ydks);
                    if (file != null) {
                        dlg.dismiss();
                        loadDeck(file);
                    }
                });
                builder.setNeutralButton(android.R.string.cancel, (dlg, rs) -> {
                    dlg.dismiss();
                });
                builder.show();
            }
            break;
            case R.id.action_unsort:
                //打乱
                mDeckAdapater.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);

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
        if (files != null) {
            int index = 0;
            for (File file : files) {
                items.add(new SimpleSpinnerItem(index++, file.getName()).setTag(file));
            }
        }
        SimpleSpinnerAdapter adapter = new SimpleSpinnerAdapter(this);
        adapter.set(items);
        spinner.setAdapter(adapter);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("input deck name");
        EditText editText = new EditText(this);
        editText.setGravity(Gravity.TOP | Gravity.LEFT);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setSingleLine();
        builder.setView(editText);
        builder.setNegativeButton(android.R.string.ok, (dlg, s) -> {
            CharSequence name = editText.getText();
            if (TextUtils.isEmpty(name)) {
                String filename = String.valueOf(name);
                if (!filename.endsWith(YDK_FILE_EX)) {
                    filename += YDK_FILE_EX;
                }
                File ydk = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH + "/" + filename);
                if (mYdkFile != null && mYdkFile.exists()) {
                    mYdkFile.renameTo(ydk);
                    mYdkFile = ydk;
                } else {
                    mYdkFile = ydk;
                    save();
                }
            } else {
                dlg.dismiss();
            }
        });
        builder.setNeutralButton(android.R.string.cancel, (dlg, s) -> {
            dlg.dismiss();
        });
        builder.show();
    }

    private void save() {
        if (mDeckAdapater.save(mYdkFile)) {
            Toast.makeText(this, R.string.save_tip_ok, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.save_tip_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
