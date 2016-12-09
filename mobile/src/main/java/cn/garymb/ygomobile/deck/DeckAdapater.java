package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.CardLoader;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.enums.CardType;
import cn.ygo.ocgcore.enums.LimitType;


public class DeckAdapater extends RecyclerView.Adapter<DeckViewHolder> {
    private final List<DeckItem> mItems = new ArrayList<>();
    private Map<Long, Integer> mCount = new HashMap<>();
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
    private Random mRandom;
    private DeckViewHolder mHeadHolder;
    private DeckItem mRemoveItem;
    private int mRemoveIndex;

    public DeckAdapater(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(context);
        mRandom = new Random(System.currentTimeMillis() + SystemClock.elapsedRealtime());
    }

    public Context getContext() {
        return context;
    }

    private void makeHeight() {
        mFullWidth = recyclerView.getMeasuredWidth() - recyclerView.getPaddingRight() - recyclerView.getPaddingLeft();
        mWidth = mFullWidth / Constants.DECK_WIDTH_COUNT - 2 * Padding;
        mHeight = scaleHeight(mWidth);
    }

    private int scaleHeight(int width) {
        return width * Constants.CORE_SKIN_CARD_COVER_SIZE[1] / Constants.CORE_SKIN_CARD_COVER_SIZE[0];
    }

    public Map<Long, Integer> getCardCount() {
        return mCount;
    }

    public boolean AddCard(CardInfo cardInfo, DeckItemType type) {
        if (cardInfo == null) return false;
        if (cardInfo.isType(CardType.Token)) {
            return false;
        }
        if (type == DeckItemType.MainCard) {
            if (getMainCount() >= Constants.DECK_MAIN_MAX) {
                return false;
            }
            int id = DeckItem.MainStart + getMainCount();
            removeItem(DeckItem.MainEnd);
            addItem(id, new DeckItem(cardInfo, type));
            notifyItemChanged(DeckItem.MainEnd);
            notifyItemChanged(id);
            return true;
        }
        if (type == DeckItemType.ExtraCard) {
            if (getExtraCount() >= Constants.DECK_EXTRA_MAX) {
                return false;
            }
            int id = DeckItem.ExtraStart + getExtraCount();
            removeItem(DeckItem.ExtraEnd);
            addItem(id, new DeckItem(cardInfo, type));
            notifyItemChanged(DeckItem.ExtraEnd);
            notifyItemChanged(id);
            return true;
        }
        if (type == DeckItemType.SideCard) {
            if (getSideCount() >= Constants.DECK_SIDE_MAX) {
                return false;
            }
            int id = DeckItem.SideStart + getSideCount();
            removeItem(DeckItem.SideEnd);
            addItem(id, new DeckItem(cardInfo, type));
            notifyItemChanged(DeckItem.SideEnd);
            notifyItemChanged(id);
            return true;
        }
        return false;
    }

    public void unSort() {
        if (mMainCount == 0) return;
        for (int i = 0; i < Constants.UNSORT_TIMES; i++) {
            int index1 = mRandom.nextInt(mMainCount);
            int index2 = mRandom.nextInt(mMainCount);
            if (index1 != index2) {
                int sp = (mMainCount - Math.max(index1, index2));
                int count = sp > 1 ? mRandom.nextInt(sp - 1) : 1;
                for (int j = 0; j < count; j++) {
                    Collections.swap(mItems, DeckItem.MainStart + index1 + j, DeckItem.MainStart + index2 + j);
                }
            }
        }
        notifyItemRangeChanged(DeckItem.MainStart, DeckItem.MainStart + getMainCount());
    }

    private boolean comp(DeckItem d1, DeckItem d2) {
        if (d1.getType() == d2.getType()) {
            CardInfo c1 = d1.getCardInfo();
            CardInfo c2 = d2.getCardInfo();
            if (c1 == null) {
                return c2 != null;
            }
            if (c2 == null) {
                return false;
            }
//            boolean log = c1.Code == 74003290||c2.Code == 74003290;
            if (c1.isType(CardType.Spell)) {
                //region 魔法
                if (c2.isType(CardType.Monster)) {
                    return true;
                }
                if (c2.isType(CardType.Trap)) {
                    return false;
                }
                if (c1.onlyType(CardType.Spell)) {
                    if (!c2.onlyType(CardType.Spell)) {
                        return false;
                    }
                } else {
                    if (c2.onlyType(CardType.Spell)) {
                        return true;
                    }
                }
//                if (c2.isType(CardType.Spell)) {
//                    if (c1.Type - c2.Type > 0) {
//                        return true;
//                    }
//                }
                //endregion
            } else if (c1.isType(CardType.Trap)) {
                //region 陷阱
                if (c2.isType(CardType.Monster) || c2.isType(CardType.Spell)) {
                    return true;
                }
                if (c1.onlyType(CardType.Trap)) {
                    if (!c2.onlyType(CardType.Trap)) {
                        return false;
                    }
                } else {
                    if (c2.onlyType(CardType.Trap)) {
                        return true;
                    }
                }
//                if (c2.isType(CardType.Trap)) {
//                    if (c1.Type - c2.Type > 0) {
//                        return true;
//                    }
//                }
                //endregion
            } else if (c1.isType(CardType.Monster)) {
                if (c2.isSpellTrap()) {
                    return false;
                }
                //region 额外
                if (c1.isType(CardType.Fusion)) {
                    if (!c2.isExtraCard()) {
                        return true;
                    }
                    if (c2.isType(CardType.Synchro) || c2.isType(CardType.Xyz)) {
                        return false;
                    }
                    if (c2.isType(CardType.Fusion)) {
                        if (c1.Type - c2.Type > 0) {
                            return true;
                        }
                    }
                } else if (c1.isType(CardType.Synchro)) {
                    if (!c2.isExtraCard()) {
                        return true;
                    }
                    if (c2.isType(CardType.Fusion)) {
                        return true;
                    }
                    if (c2.isType(CardType.Xyz)) {
                        return false;
                    }
                    if (c2.isType(CardType.Synchro)) {
                        if (c1.Type - c2.Type > 0) {
                            return true;
                        }
                    }
                } else if (c1.isType(CardType.Xyz)) {
                    if (!c2.isExtraCard()) {
                        return true;
                    }
                    if (c2.isType(CardType.Fusion) || c2.isType(CardType.Synchro)) {
                        return true;
                    }
                    if (c2.isType(CardType.Xyz)) {
                        if (c1.Type - c2.Type > 0) {
                            return true;
                        }
                    }
                }
                //endregion
                //region 怪兽
                if (c1.Level != c2.Level) {
                    return c1.Level - c2.Level > 0;
                }
                if (c1.Attack != c2.Attack) {
                    return c1.Attack - c2.Attack > 0;
                }
                if (c1.Defense != c2.Defense) {
                    return c1.Defense - c2.Defense > 0;
                }
                if (c1.Attribute != c2.Attribute) {
                    return c1.Attribute - c2.Attribute > 0;
                }
                if (c1.Race != c2.Race) {
                    return c1.Race - c2.Race > 0;
                }
                if (c1.Ot != c2.Ot) {
                    return c1.Ot - c2.Ot > 0;
                }
//                if (c1.Type != c2.Type) {
//                    return c1.Type - c2.Type > 0;
//                }
                //endregion
            } else {
                return false;
            }
            return (c1.Code - c2.Code) > 0;
        }

        return (d1.getType().

                ordinal()

                - d2.getType().

                ordinal()

        ) > 0;
    }

    private int sortMain() {
        int len = getMainCount();
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                DeckItem d1 = mItems.get(DeckItem.MainStart + j);
                DeckItem d2 = mItems.get(DeckItem.MainStart + j + 1);
                if (comp(d1, d2)) {
                    DeckItem tmp = new DeckItem(d2);
                    d2.set(d1);
                    d1.set(tmp);
                }
            }
        }
        return len;
    }

    private int sortExtra() {
        int len = getExtraCount();
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                DeckItem d1 = mItems.get(DeckItem.ExtraStart + j);
                DeckItem d2 = mItems.get(DeckItem.ExtraStart + j + 1);
                if (comp(d1, d2)) {
//                    Log.d("kk", "swap extra:" + j + "->" + (j + 1));
                    DeckItem tmp = new DeckItem(d2);
                    d2.set(d1);
                    d1.set(tmp);
                }
            }
        }
        return len;
    }

    private int sortSide() {
        int len = getSideCount();
        for (int i = 0; i < len - 1; i++) {
            for (int j = 0; j < len - 1 - i; j++) {
                DeckItem d1 = mItems.get(DeckItem.SideStart + j);
                DeckItem d2 = mItems.get(DeckItem.SideStart + j + 1);
                if (comp(d1, d2)) {
                    DeckItem tmp = new DeckItem(d2);
                    d2.set(d1);
                    d1.set(tmp);
                }
            }
        }
        return len;
    }

    public void sort() {
        int main = sortMain();
        int extra = sortExtra();
        int side = sortSide();
        notifyItemRangeChanged(DeckItem.MainStart, DeckItem.MainStart + main);
        notifyItemRangeChanged(DeckItem.ExtraStart, DeckItem.ExtraStart + extra);
        notifyItemRangeChanged(DeckItem.SideStart, DeckItem.SideStart + side);
    }

    private void addCount(CardInfo cardInfo, DeckItemType type) {
        if (cardInfo == null) return;
        Integer i = mCount.get(Long.valueOf(cardInfo.Code));
        if (i == null) {
            mCount.put(Long.valueOf(cardInfo.Code), 1);
        } else {
            mCount.put(Long.valueOf(cardInfo.Code), i + 1);
        }
        switch (type) {
            case MainCard:
                mMainCount++;
                if (cardInfo.isType(CardType.Monster)) {
                    mMainMonsterCount++;
                } else if (cardInfo.isType(CardType.Spell)) {
                    mMainSpellCount++;
                } else if (cardInfo.isType(CardType.Trap)) {
                    mMainTrapCount++;
                }
                break;
            case ExtraCard:
                mExtraCount++;
                if (cardInfo.isType(CardType.Fusion)) {
                    mExtraFusionCount++;
                } else if (cardInfo.isType(CardType.Synchro)) {
                    mExtraSynchroCount++;
                } else if (cardInfo.isType(CardType.Xyz)) {
                    mExtraXyzCount++;
                }
                break;
            case SideCard:
                mSideCount++;
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

    private void removeCount(CardInfo cardInfo, DeckItemType type) {
        if (cardInfo == null) return;
        Integer i = mCount.get(Long.valueOf(cardInfo.Code));
        if (i == null) {
            mCount.put(Long.valueOf(cardInfo.Code), 0);
        } else {
            mCount.put(Long.valueOf(cardInfo.Code), Math.max(0, i - 1));
        }
        switch (type) {
            case MainCard:
                mMainCount--;
                if (cardInfo.isType(CardType.Monster)) {
                    mMainMonsterCount--;
                } else if (cardInfo.isType(CardType.Spell)) {
                    mMainSpellCount--;
                } else if (cardInfo.isType(CardType.Trap)) {
                    mMainTrapCount--;
                }
                break;
            case ExtraCard:
                mExtraCount--;
                if (cardInfo.isType(CardType.Fusion)) {
                    mExtraFusionCount--;
                } else if (cardInfo.isType(CardType.Synchro)) {
                    mExtraSynchroCount--;
                } else if (cardInfo.isType(CardType.Xyz)) {
                    mExtraXyzCount--;
                }
                break;
            case SideCard:
                mSideCount--;
                if (cardInfo.isType(CardType.Monster)) {
                    mSideMonsterCount--;
                } else if (cardInfo.isType(CardType.Spell)) {
                    mSideSpellCount--;
                } else if (cardInfo.isType(CardType.Trap)) {
                    mSideTrapCount--;
                }
                break;
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
        if (deck != null) {
            loadData(deck);
        }
    }

    public DeckInfo read(CardLoader cardLoader, File file, LimitList limitList) {
        return DeckItemUtils.readDeck(cardLoader, file, limitList);
    }

    public boolean save(File file) {
        return DeckItemUtils.save(mItems, file);
    }

    private <T> int length(List<T> list) {
        return list == null ? 0 : list.size();
    }

    private void loadData(DeckInfo deck) {
        mCount.clear();
        mMainCount = 0;
        mExtraCount = 0;
        mSideCount = 0;
        mMainMonsterCount = 0;
        mMainSpellCount = 0;
        mMainTrapCount = 0;
        mExtraFusionCount = 0;
        mExtraXyzCount = 0;
        mExtraSynchroCount = 0;
        mSideMonsterCount = 0;
        mSideSpellCount = 0;
        mSideTrapCount = 0;
        mItems.clear();
        DeckItemUtils.makeItems(deck, this);
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

    public void addItem(DeckItem deckItem) {
        addCount(deckItem.getCardInfo(), deckItem.getType());
        mItems.add(deckItem);
    }

    public void addItem(int pos, DeckItem deckItem) {
        if (deckItem.getCardInfo() != null) {
            if (pos >= DeckItem.MainStart && pos <= DeckItem.MainEnd) {
                deckItem.setType(DeckItemType.MainCard);
            } else if (pos >= DeckItem.ExtraStart && pos <= DeckItem.ExtraEnd) {
                deckItem.setType(DeckItemType.ExtraCard);
            } else if (pos >= DeckItem.SideStart && pos <= DeckItem.SideEnd) {
                deckItem.setType(DeckItemType.SideCard);
            }
        }
        synchronized (this) {
            addCount(deckItem.getCardInfo(), deckItem.getType());
            mItems.add(pos, deckItem);
        }
    }

    public int getRemoveIndex() {
        return mRemoveIndex;
    }

    public DeckItem getRemoveItem() {
        return mRemoveItem;
    }

    public DeckItem removeItem(int pos) {
        DeckItem deckItem = null;
        synchronized (this) {
            deckItem = mItems.remove(pos);
            removeCount(deckItem.getCardInfo(), deckItem.getType());
        }
        mRemoveIndex = pos;
        mRemoveItem = deckItem;
        return deckItem;
    }

    public DeckViewHolder getHeadHolder() {
        return mHeadHolder;
    }

    public int getItemHeight() {
        return mHeight;
    }

    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {
        if (position == DeckItem.HeadView) {
            if (mHeadHolder == null) {
                mHeadHolder = holder;
            }
        }
        DeckItem item = mItems.get(position);
        holder.setItemType(item.getType());
        if (position == DeckItem.HeadView) {
            holder.headView.setVisibility(View.VISIBLE);
            holder.cardImage.setVisibility(View.GONE);
            return;
        } else {
            holder.headView.setVisibility(View.GONE);
        }
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
            holder.cardImage.setVisibility(View.VISIBLE);
            holder.setSize(mHeight);
            if (item.getType() == DeckItemType.Space) {
                holder.setCardType(0);
                holder.cardImage.setVisibility(View.INVISIBLE);
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
                    } else if (cardInfo.getLimitType() == LimitType.SemiLimit) {
                        holder.rightImage.setImageBitmap(mImageTop.semiLimit);
                    } else {
                        holder.rightImage.setVisibility(View.GONE);
                    }
                    holder.useDefault();
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
