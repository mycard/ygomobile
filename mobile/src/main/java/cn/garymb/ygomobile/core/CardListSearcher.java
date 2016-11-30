package cn.garymb.ygomobile.core;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;

import cn.garymb.ygomobile.lite.R;

public class CardListSearcher extends CardSearcher {
    private View layout;
    private ListView mListView;

    public CardListSearcher(DrawerLayout drawerlayout, View view) {
        super(drawerlayout, view);
        layout = findViewById(R.id.layout_search);
        mListView = findViewById(R.id.list_cards);
    }

    public ListView getListView() {
        return mListView;
    }

    @Override
    public void onLoad(boolean ok) {
        super.onLoad(ok);
    }

    @Override
    public void onOpen() {
        super.onOpen();
        layout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    @Override
    protected void onSearch() {
        layout.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        search();
    }
}
