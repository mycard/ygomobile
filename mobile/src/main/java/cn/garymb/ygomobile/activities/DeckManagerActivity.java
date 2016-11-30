package cn.garymb.ygomobile.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.adapters.DeckPagerAdapter;
import cn.garymb.ygomobile.core.loader.ILoadCallBack;
import cn.garymb.ygomobile.lite.R;

public class DeckManagerActivity extends BaseActivity{
    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ImageView mCardImage;
    private TextView mCardName;
    private TextView mCardInfo;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private DeckPagerAdapter mDeckPagerAdapter;

    //    private CardListSearcher mCardListSearcher;
//    private CardImageListAdapater mCardListAdapater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deckmanager);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mDeckPagerAdapter = new DeckPagerAdapter(this, getSupportFragmentManager(), R.string.tab_manager, R.string.tab_search, R.string.tab_result);
        mViewPager.setAdapter(mDeckPagerAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabsFromPagerAdapter(mDeckPagerAdapter);//给Tabs设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
//        mRecyclerView = bind(R.id.grid_deck);
//        mDrawerLayout = bind(R.id.drawer_layout);
//        mNavigationView = bind(R.id.nav_view);
//        mCardImage = bind(R.id.card_image);
//        mCardName = bind(R.id.card_name);
//        mCardInfo = bind(R.id.card_info);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawerLayout, R.string.search_open, R.string.search_close);
//        toggle.setDrawerIndicatorEnabled(false);
//        mDrawerLayout.setDrawerListener(toggle);
//        toggle.syncState();
//        View head = mNavigationView.getHeaderView(0);
//        mViewPager = (ViewPager) head.findViewById(R.id.viewpager);
//        mTabLayout = (TabLayout) head.findViewById(R.id.tabs);
//        mDeckPagerAdapter = new DeckPagerAdapter(this, getSupportFragmentManager(), R.string.tab_manager, R.string.tab_search, R.string.tab_result);
//        mNavigationView.setNavigationItemSelectedListener(this);
//        mViewPager.setAdapter(mDeckPagerAdapter);
//        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        mTabLayout.setTabsFromPagerAdapter(mDeckPagerAdapter);//给Tabs设置适配器
//        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
//        mCardListAdapater = new CardImageListAdapater(this);
//        mCardListSearcher = new CardListSearcher(mDrawerLayout, head);
//        mCardListAdapater.setCallBack(this);
//        mCardListAdapater.loadData();
//        ListView listView = mCardListSearcher.getListView();
//        listView.setAdapter(mCardListAdapater);
//        listView.setOnItemClickListener(mCardListAdapater);
//        listView.setOnScrollListener(mCardListAdapater);
    }
}
