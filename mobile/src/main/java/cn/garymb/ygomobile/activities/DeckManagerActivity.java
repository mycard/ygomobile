package cn.garymb.ygomobile.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.io.File;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardDetail;
import cn.garymb.ygomobile.deck.DeckAdapater;
import cn.garymb.ygomobile.deck.DeckItemTouchHelper;
import cn.garymb.ygomobile.deck.DeckItemUtils;
import cn.garymb.ygomobile.deck.DeckLayoutManager;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.ygo.ocgcore.LimitList;

public class DeckManagerActivity extends BaseCardsAcitivity {
    private RecyclerView mRecyclerView;
    private DeckAdapater mDeckAdapater;
    private AppsSettings mSettings = AppsSettings.get();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setAdapter((mDeckAdapater = new DeckAdapater(this, mRecyclerView)));
        mRecyclerView.setLayoutManager(new DeckLayoutManager(this, Constants.DECK_WIDTH_COUNT));
        mCardSelector.hideLimit();
        mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        ItemTouchHelper touchHelper = new ItemTouchHelper(new DeckItemTouchHelper(mDeckAdapater));
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onInit() {
        super.onInit();
        VUiKit.defer().when(() -> {
            if (mCardLoader.isOpen()) {
                File file = new File(mSettings.getResourcePath(), Constants.CORE_DECK_PATH + "/911+YA02.ydk");
                LimitList limitList = mLimitManager.getCount() > 0 ? mLimitManager.getLimit(0) : null;
                return DeckItemUtils.readDeck(mCardLoader, file, limitList);
            } else {
                return null;
            }
        }).done((rs) -> {
            mDeckAdapater.setDeck(rs);
            mDeckAdapater.notifyDataSetChanged();
        });
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
        showCardDialog(cardInfo);
    }

    @Override
    protected void onCardLongClick(View view, CardInfo cardInfo, int pos) {

    }

    @Override
    public void onSearchStart() {
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

    protected void showCardDialog(CardInfo cardInfo) {
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
            });
        }
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
            case R.id.action_manager:
                //显示对话框:
                //选择禁卡表
                //卡组列表
                //重命名
                //新建
                //删除
                break;
            case R.id.action_unsort:
                //打乱
                mDeckAdapater.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
