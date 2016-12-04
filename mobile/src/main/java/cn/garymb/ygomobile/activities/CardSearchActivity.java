package cn.garymb.ygomobile.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardDetail;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.ygo.ocgcore.LimitList;

public class CardSearchActivity extends BaseCardsAcitivity {
    private CardDetail mCardDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View getMainView() {
        if (mCardDetail == null) {
            mCardDetail = new CardDetail(this);
            mCardDetail.hideClose();
            File outFile = new File(AppsSettings.get().getCoreSkinPath(), Constants.UNKNOWN_IMAGE);
            Glide.with(this).load(outFile).into(mCardDetail.getCardImage());
        }
        return mCardDetail.getView();
    }

    @Override
    protected void onCardClick(CardInfo cardInfo, int pos) {
        if (mDrawerlayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerlayout.closeDrawer(Gravity.LEFT);
        }
        mCardDetail.bind(cardInfo, mStringManager, new CardDetail.OnClickListener() {
            @Override
            public void onOpenUrl(CardInfo cardInfo) {
                String uri = Constants.WIKI_SEARCH_URL + String.format("%08d", cardInfo.Code);
                WebActivity.open(getContext(), cardInfo.Name, uri);
            }

            @Override
            public void onClose() {
            }

            @Override
            public void onAddMainCard(CardInfo cardInfo) {

            }

            @Override
            public void onAddSideCard(CardInfo cardInfo) {

            }
        });
    }

    @Override
    protected void onCardLongClick(View view, CardInfo cardInfo, int pos) {

    }

    @Override
    public void onSearchStart(LimitList limitList) {
    }

    @Override
    public void onSearchResult(List<CardInfo> cardInfos) {
        super.onSearchResult(cardInfos);
        showResult(false);
    }

    @Override
    protected void onInit() {
        super.onInit();
        VUiKit.defer().when(()->{
           mCardLoader.loadData();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_search, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
