package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.ygo.ocgcore.Card;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.enums.CardType;
import cn.ygo.ocgcore.enums.LimitType;


public class DeckAdapater extends RecyclerView.Adapter<DeckViewHolder> {
    final List<DeckItem> mItems = new ArrayList<>();
    private Map<Long, Integer> mCount = new HashMap<>();
    private DeckInfo mDeck = new DeckInfo();
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ImageTop mImageTop;

    private int mMainCount;
    private int mExtraCount;
    private int mSideCount;

    private int mMainMonsterCount;
    private int mMainSpellCount;
    private int mMainTrapCount;
    private int mExtraFusionCount;
    private int mExtraXyzCount;
    private int mExtraSynchroCount;
    private int mSideMonsterCount;
    private int mSideSpellCount;
    private int mSideTrapCount;

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

    CardInfo removeMain(int pos) {
        List<CardInfo> list = getMainCards();
        if (list != null && pos >= 0 && pos <= list.size()) {
            mMainCount--;
            CardInfo cardInfo = list.remove(pos);
            if (cardInfo.isType(CardType.Monster)) {
                mMainMonsterCount--;
            } else if (cardInfo.isType(CardType.Spell)) {
                mMainSpellCount--;
            } else if (cardInfo.isType(CardType.Trap)) {
                mMainTrapCount--;
            }
            return cardInfo;
        }
        return null;
    }

    CardInfo removeSide(int pos) {
        List<CardInfo> list = getSideCards();
        if (list != null && pos >= 0 && pos <= list.size()) {
            mSideCount--;
            CardInfo cardInfo = list.remove(pos);
            if (cardInfo.isType(CardType.Monster)) {
                mSideMonsterCount--;
            } else if (cardInfo.isType(CardType.Spell)) {
                mSideSpellCount--;
            } else if (cardInfo.isType(CardType.Trap)) {
                mSideTrapCount--;
            }
            return cardInfo;
        }
        return null;
    }

    CardInfo removeExtra(int pos) {
        List<CardInfo> list = getExtraCards();
        if (list != null && pos >= 0 && pos <= list.size()) {
            mExtraCount--;
            CardInfo cardInfo = list.remove(pos);
            if (cardInfo.isType(CardType.Fusion)) {
                mExtraFusionCount--;
            } else if (cardInfo.isType(CardType.Synchro)) {
                mExtraSynchroCount--;
            } else if (cardInfo.isType(CardType.Xyz)) {
                mExtraXyzCount--;
            }
            return cardInfo;
        }
        return null;
    }

    void addCount(CardInfo cardInfo, DeckItemType type) {
        if (cardInfo == null) return;
        pushCount(cardInfo);
        switch (type) {
            case MainCard:
                if (cardInfo.isType(CardType.Monster)) {
                    mMainMonsterCount++;
                } else if (cardInfo.isType(CardType.Spell)) {
                    mMainSpellCount++;
                } else if (cardInfo.isType(CardType.Trap)) {
                    mMainTrapCount++;
                }
                break;
            case ExtraCard:
                if (cardInfo.isType(CardType.Fusion)) {
                    mExtraFusionCount++;
                } else if (cardInfo.isType(CardType.Synchro)) {
                    mExtraSynchroCount++;
                } else if (cardInfo.isType(CardType.Xyz)) {
                    mExtraXyzCount++;
                }
                break;
            case SideCard:
                if (cardInfo.isType(CardType.Monster)) {
                    mSideMonsterCount++;
                } else if (cardInfo.isType(CardType.Spell)) {
                    mSideSpellCount++;
                } else if (cardInfo.isType(CardType.Trap)) {
                    mSideTrapCount++;
                }
                break;
        }
    }

    public boolean AddCard(CardInfo cardInfo, LimitList limitList, DeckItemType type) {
        if (limitList.isForbidden(cardInfo.Code)) {
            return false;
        }
        Integer count = mCount.get(Long.valueOf(cardInfo.Code));
        if (count != null) {
            if (limitList.isLimit(cardInfo.Code)) {
                if (count >= 1) {
                    return false;
                }
            } else if (limitList.isSemiLimit(cardInfo.Code)) {
                if (count >= 2) {
                    return false;
                }
            } else if (count >= Constants.CARD_MAX_COUNT) {
                return false;
            }
        }
        if (type == DeckItemType.MainCard) {
            if (getMainCount() >= Constants.DECK_MAIN_MAX) {
                return false;
            }
            mItems.remove(DeckItem.MainEnd);
            mItems.add(DeckItem.MainStart + getMainCount(), new DeckItem(cardInfo, type));
            notifyItemChanged(DeckItem.MainEnd);
            addCount(cardInfo, type);
            pushCount(cardInfo);
            return true;
        }
        if (type == DeckItemType.ExtraCard) {
            if (getExtraCount() >= Constants.DECK_EXTRA_MAX) {
                return false;
            }
            mItems.remove(DeckItem.ExtraEnd);
            mItems.add(DeckItem.ExtraStart + getExtraCount(), new DeckItem(cardInfo, type));
            notifyItemChanged(DeckItem.ExtraEnd);
            addCount(cardInfo, type);
            pushCount(cardInfo);
            return true;
        }
        if (type == DeckItemType.SideCard) {
            if (getSideCount() >= Constants.DECK_SIDE_MAX) {
                return false;
            }
            mItems.remove(DeckItem.SideEnd);
            mItems.add(DeckItem.SideStart + getSideCount(), new DeckItem(cardInfo, type));
            notifyItemChanged(DeckItem.SideEnd);
            addCount(cardInfo, type);
            pushCount(cardInfo);
            return true;
        }
        return false;
    }

    void pushCount(CardInfo cardInfo) {
        Integer i = mCount.get(Long.valueOf(cardInfo.Code));
        if (i == null) {
            mCount.put(Long.valueOf(cardInfo.Code), 1);
        } else {
            mCount.put(Long.valueOf(cardInfo.Code), i + 1);
        }
    }

    void addMain(int pos, CardInfo cardInfo) {
        List<CardInfo> list = getMainCards();
        if (list != null && mMainCount < Constants.DECK_MAIN_MAX) {
            list.add(pos, cardInfo);
            addCount(cardInfo, DeckItemType.MainCard);
            mMainCount++;
        }
    }

    void addExtra(int pos, CardInfo cardInfo) {
        List<CardInfo> list = getExtraCards();
        if (list != null && mExtraCount < Constants.DECK_EXTRA_MAX) {
            list.add(pos, cardInfo);
            addCount(cardInfo, DeckItemType.ExtraCard);
            mExtraCount++;
        }
    }

    void addSide(int pos, CardInfo cardInfo) {
        List<CardInfo> list = getSideCards();
        if (list != null) {
            list.add(pos, cardInfo);
            addCount(cardInfo, DeckItemType.SideCard);
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
        if (deck != null) {
            loadData();
        }
    }

    public DeckInfo getDeck() {
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
        mItems.addAll(DeckItemUtils.makeItems(mDeck, this));
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_deck_card, parent, false);
        return new DeckViewHolder(view);
    }

    private String getString(int id, Object... args) {
        return context.getString(id, args);
    }

    private String getMainString() {
        return getString(R.string.deck_main, mMainCount, mMainMonsterCount, mMainSpellCount, mMainTrapCount);
    }

    private String getExtraString() {
        return getString(R.string.deck_extra, mExtraCount, mExtraFusionCount, mExtraSynchroCount, mExtraXyzCount);
    }

    private String getSideString() {
        return getString(R.string.deck_side, mSideCount, mSideMonsterCount, mSideSpellCount, mSideTrapCount);
    }


    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {
        DeckItem item = mItems.get(position);
        holder.setItemType(item.getType());
        if (item.getType() == DeckItemType.MainLabel || item.getType() == DeckItemType.SideLabel
                || item.getType() == DeckItemType.ExtraLabel) {
            holder.cardImage.setVisibility(View.GONE);
            holder.rightImage.setVisibility(View.GONE);

            if (item.getType() == DeckItemType.MainLabel) {
                holder.labelText.setText(getMainString());
            } else if (item.getType() == DeckItemType.SideLabel) {
                holder.labelText.setText(getSideString());
            } else if (item.getType() == DeckItemType.ExtraLabel) {
                holder.labelText.setText(getExtraString());
            }

            holder.textlayout.setVisibility(View.VISIBLE);
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
                holder.setSize(mHeight);
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

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
