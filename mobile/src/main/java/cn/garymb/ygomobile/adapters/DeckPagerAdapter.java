package cn.garymb.ygomobile.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import cn.garymb.ygomobile.fragments.CardListFragment;
import cn.garymb.ygomobile.fragments.DeckManageFragment;
import cn.garymb.ygomobile.fragments.SearchFragment;
import cn.garymb.ygomobile.lite.R;

public class DeckPagerAdapter extends FragmentPagerAdapter {
    private int[] mTitles;
    private Context context;

    public DeckPagerAdapter(Context context, FragmentManager fm, int... titles) {
        super(fm);
        this.context = context;
        mTitles = titles;
    }

    private int getTitle(int pos) {
        return mTitles[pos];
    }

    @Override
    public Fragment getItem(int position) {
        int id = getTitle(position);
        switch (id) {
            case R.string.tab_manager:
                return new DeckManageFragment();
            case R.string.tab_result:
                return new CardListFragment();
            case R.string.tab_search:
                return new SearchFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTitles == null ? 0 : mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int id = getTitle(position);
        return context.getString(id);
    }
}
