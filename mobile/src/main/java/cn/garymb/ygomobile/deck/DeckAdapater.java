package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.ygo.ocgcore.enums.LimitType;


public class DeckAdapater extends RecyclerView.Adapter<DeckViewHolder> {
    final List<DeckItem> mItems = new ArrayList<>();
    private DeckInfo mDeck = new DeckInfo();
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ImageTop mImageTop;
    private int mMainCount;
    private int mExtraCount;
    private int mSideCount;
    private int mFullWidth;
    private int mWidth;
    private int mHeight;
    private int Padding = 1;
    private RecyclerView recyclerView;
    private int mDragPosition = -1;

    public DeckAdapater(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(context);
    }

    private void makeHeight() {
        mFullWidth = recyclerView.getMeasuredWidth() - recyclerView.getPaddingRight() - recyclerView.getPaddingLeft();
        mWidth = mFullWidth / Constants.DECK_WIDTH_COUNT - 2 * Padding;
        mHeight = scaleHeight(mWidth);
    }

    private int scaleHeight(int width) {
        return width * Constants.CORE_SKIN_CARD_COVER_SIZE[1] / Constants.CORE_SKIN_CARD_COVER_SIZE[0];
    }

    public List<CardInfo> getMainCards() {
        return mDeck != null ? mDeck.getMainCards() : null;
    }

    public List<CardInfo> getExtraCards() {
        return mDeck != null ? mDeck.getExtraCards() : null;
    }

    public List<CardInfo> getSideCards() {
        return mDeck != null ? mDeck.getSideCards() : null;
    }

    public CardInfo removeMain(int pos) {
        List<CardInfo> list = getMainCards();
        if (list != null && pos >= 0 && pos <= list.size()) {
            mMainCount--;
            return list.remove(pos);
        }
        return null;
    }

    public CardInfo removeSide(int pos) {
        List<CardInfo> list = getSideCards();
        if (list != null && pos >= 0 && pos <= list.size()) {
            mSideCount--;
            return list.remove(pos);
        }
        return null;
    }

    public CardInfo removeExtra(int pos) {
        List<CardInfo> list = getExtraCards();
        if (list != null && pos >= 0 && pos <= list.size()) {
            mExtraCount--;
            return list.remove(pos);
        }
        return null;
    }

    public void addMain(int pos, CardInfo cardInfo) {
        List<CardInfo> list = getMainCards();
        if (list != null && mMainCount < Constants.DECK_MAIN_MAX) {
            list.add(pos, cardInfo);
            mMainCount++;
        }
    }

    public void addExtra(int pos, CardInfo cardInfo) {
        List<CardInfo> list = getExtraCards();
        if (list != null && mExtraCount < Constants.DECK_EXTRA_MAX) {
            list.add(pos, cardInfo);
            mExtraCount++;
        }
    }

    public void addSide(int pos, CardInfo cardInfo) {
        List<CardInfo> list = getSideCards();
        if (list != null && mSideCount < Constants.DECK_SIDE_MAX) {
            list.add(pos, cardInfo);
            mSideCount++;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public DeckItem getItem(int position) {
        return mItems.get(position);
    }

    public int getMainCount() {
        return mMainCount;
    }

    public int getExtraCount() {
        return mExtraCount;
    }

    public int getSideCount() {
        return mSideCount;
    }

    public void setDeck(DeckInfo deck) {
        this.mDeck = deck;
        loadData();
    }

    public DeckInfo getDeck(){
        return mDeck;
    }

    private <T> int length(List<T> list) {
        return list == null ? 0 : list.size();
    }

    private void loadData() {
        mMainCount = length(mDeck.getMainCards());
        mExtraCount = length(mDeck.getExtraCards());
        mSideCount = length(mDeck.getSideCards());
        mItems.clear();
        mItems.addAll(DeckItemUtils.makeItems(context, mDeck));
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.card_image, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {
        DeckItem item = mItems.get(position);
        holder.setItemType(item.getType());
        if (item.getType() == DeckItemType.Label) {
            holder.cardImage.setVisibility(View.GONE);
            holder.rightImage.setVisibility(View.GONE);
            holder.labelText.setText(item.getText());
            holder.textlayout.setVisibility(View.VISIBLE);
            if (item.getColor() != 0) {
                holder.labelText.setTextColor(item.getColor());
            }
//            holder.labelText.setLayoutParams(new RelativeLayout.LayoutParams(mFullWidth, mHeight));
        } else {
            if (holder.cardImage.getMeasuredHeight() > 0) {
                mHeight = holder.cardImage.getMeasuredHeight();
                mWidth = holder.cardImage.getMeasuredWidth();
                if (mHeight <= 0 && mWidth >= 0) {
                    mHeight = scaleHeight(mWidth);
                }
            }
            if (mHeight <= 0) {
                makeHeight();
            }
//            holder.cardImage.setLayoutParams(new RelativeLayout.LayoutParams(holder.cardImage.getMeasuredWidth(), mHeight));
            holder.textlayout.setVisibility(View.GONE);
            if (position == mDragPosition) {
                holder.cardImage.setVisibility(View.GONE);
            } else {
                holder.cardImage.setVisibility(View.VISIBLE);
            }
            if (item.getType() == DeckItemType.Space) {
                holder.setCardType(0);
                holder.setSize(mHeight);
                holder.cardImage.setImageDrawable(null);
//                holder.useDefault();
                holder.rightImage.setVisibility(View.GONE);
            } else {
                CardInfo cardInfo = item.getCardInfo();
                if (cardInfo != null) {
                    holder.setCardType(cardInfo.Type);
                    if (mImageTop == null) {
                        mImageTop = new ImageTop(context);
                    }
                    holder.rightImage.setVisibility(View.VISIBLE);
                    if (cardInfo.getLimitType() == LimitType.Forbidden) {
                        holder.rightImage.setImageBitmap(mImageTop.forbidden);
                    } else if (cardInfo.getLimitType() == LimitType.Limit) {
                        holder.rightImage.setImageBitmap(mImageTop.limit);
                    } else if (cardInfo.getLimitType() == LimitType.Limit) {
                        holder.rightImage.setImageBitmap(mImageTop.semiLimit);
                    } else {
                        holder.rightImage.setVisibility(View.GONE);
                    }
                    ImageLoader.get().bindImage(context, holder.cardImage, cardInfo.Code);
                } else {
                    holder.setCardType(0);
                    holder.rightImage.setVisibility(View.GONE);
                    holder.useDefault();
                }
            }
        }
    }

    public int getDragPosition() {
        return mDragPosition;
    }

    public void setDragPosition(int dragPosition) {
        mDragPosition = dragPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
