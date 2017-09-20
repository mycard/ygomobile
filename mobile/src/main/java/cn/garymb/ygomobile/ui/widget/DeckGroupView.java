package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.cards.deck.ImageTop;
import cn.garymb.ygomobile.ui.cards.deck.LabelInfo;
import ocgcore.data.Card;
import ocgcore.data.LimitList;

public class DeckGroupView extends FrameLayout implements View.OnClickListener, View.OnLongClickListener {
    private final DeckLabel mMainLabel, mExtraLabel, mSideLabel;
    private final LabelInfo mLabelInfo;
    private final DeckInfo mDeckInfo;
    private ImageTop mImageTop;
    private LimitList mLimitList;
    private int mainTop, extraTop, sideTop;
    int mCardWidth = 0;
    int mCardHeight = 0;
    private final SparseArray<CardView> mMainViews = new SparseArray<>();
    private final SparseArray<CardView> mExtraViews = new SparseArray<>();
    private final SparseArray<CardView> mSideViews = new SparseArray<>();
    private boolean mEditMode;
    private int mMainLimit = 15, mExtraLimit = 15, mSideLimit = 15;
    private OnCardClickListener mOnCardClickListener;

    public interface OnCardClickListener {
        void onClick(Type type, CardView cardView);
    }

    public DeckGroupView(@NonNull Context context) {
        this(context, null);
    }

    public DeckGroupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeckGroupView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLabelInfo = new LabelInfo(context);
        mDeckInfo = new DeckInfo();

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DeckView);
            if (array != null) {
                mCardWidth = array.getInteger(R.styleable.DeckView_card_width, 0);
            }
        }
        if (mCardWidth <= 0) {
            int width = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
            mCardWidth = width / 10;
        }
        if (mCardWidth <= 0) {
            mCardWidth = (getResources().getDisplayMetrics().widthPixels - getPaddingLeft() - getPaddingRight()) / 10;
        }
        mCardHeight = Math.round((255.0f / 177.0f) * mCardWidth);

        mMainLabel = new DeckLabel(context);
        mSideLabel = new DeckLabel(context);
        mExtraLabel = new DeckLabel(context);
        int labelHeight = (int) getResources().getDimension(R.dimen.deck_label_height);
        int top = 0;
        addView(mMainLabel, makeLayoutParams(LayoutParams.MATCH_PARENT, labelHeight, top));
        mainTop = labelHeight;
        top = mainTop + 4 * mCardHeight;
        addView(mExtraLabel, makeLayoutParams(LayoutParams.MATCH_PARENT, labelHeight, top));
        extraTop = labelHeight + top;
        top = extraTop + mCardHeight;
        addView(mSideLabel, makeLayoutParams(LayoutParams.MATCH_PARENT, labelHeight, top));
        sideTop = top + labelHeight;

        int line, pos;
        for (int i = 0; i < Constants.DECK_MAIN_MAX; i++) {
            line = i / Constants.DECK_WIDTH_MAX_COUNT;
            pos = i % Constants.DECK_WIDTH_MAX_COUNT;
            CardView cardView = new CardView(context, mCardWidth);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            cardView.setLongClickable(true);
            cardView.setTag(Type.Main);
            addView(cardView, makeLayoutParams(mCardWidth, mCardHeight, mainTop + line * mCardHeight, pos * mCardWidth));
            mMainViews.put(i, cardView);
        }
        for (int i = 0; i < Constants.DECK_EXTRA_MAX; i++) {
            CardView cardView = new CardView(context, mCardWidth);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            cardView.setLongClickable(true);
            cardView.setTag(Type.Extra);
            addView(cardView, makeLayoutParams(mCardWidth, mCardHeight, extraTop, i * mCardWidth));
            mExtraViews.put(i, cardView);
        }
        for (int i = 0; i < Constants.DECK_SIDE_MAX; i++) {
            CardView cardView = new CardView(context, mCardWidth);
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            cardView.setLongClickable(true);
            cardView.setTag(Type.Side);
            addView(cardView, makeLayoutParams(mCardWidth, mCardHeight, sideTop, i * mCardWidth));
            mSideViews.put(i, cardView);
        }
    }

    private FrameLayout.LayoutParams makeLayoutParams(int w, int h, int top, int left) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(w, h);
        layoutParams.topMargin = top;
        layoutParams.leftMargin = left;
        return layoutParams;
    }

    private FrameLayout.LayoutParams makeLayoutParams(int w, int h, int top) {
        return makeLayoutParams(w, h, top, 0);
    }

    private FrameLayout.LayoutParams makeLayoutParams(int top) {
        return makeLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, top);
    }

    public void setOnCardClickListener(OnCardClickListener onCardClickListener) {
        mOnCardClickListener = onCardClickListener;
    }

    public ImageTop getImageTop() {
        if (mImageTop == null) {
            mImageTop = new ImageTop(getContext());
        }
        return mImageTop;
    }

    public void setDeck(DeckInfo deck) {
        setEditMode(false);
        updateAll(deck);
    }

    public void unSort() {
        mDeckInfo.unSort();
        updateAll(mDeckInfo);
    }

    public void sort() {
        mDeckInfo.sort();
        updateAll(mDeckInfo);
    }

    public void updateAll(DeckInfo deckInfo) {
        if (deckInfo != mDeckInfo) {
            mDeckInfo.update(deckInfo);
        }
        mLabelInfo.update(deckInfo);

        mMainLimit = (int) Math.max(10, Math.ceil(mDeckInfo.getMainCount() / 4.0f));
        mExtraLimit = Math.max(10, mDeckInfo.getExtraCount());
        mSideLimit = Math.max(10, mDeckInfo.getSideCount());
    }

    public DeckInfo getDeckInfo() {
        return mDeckInfo;
    }

    public void updateLastCard(Type type) {
        updateAll(mDeckInfo);
        if (type == Type.Main) {
            mMainLabel.setText(mLabelInfo.getMainString());
        } else if (type == Type.Extra) {
            mExtraLabel.setText(mLabelInfo.getExtraString());
        } else {
            mSideLabel.setText(mLabelInfo.getSideString());
        }
        if (type == Type.Extra) {
            updateCard(type, getDeckInfo().getExtraCount() - 1, 1);
        } else if (type == Type.Main) {
            updateCard(type, getDeckInfo().getMainCount() - 1, 1);
        } else {
            updateCard(type, getDeckInfo().getSideCount() - 1, 1);
        }
    }

    public void updateCard(Type type, int index, int count) {
        if (type == Type.Extra) {
            int all = mExtraViews.size();
            for (int i = 0; i < all; i++) {
                CardView cardView = mExtraViews.get(i);
                cardView.setSelected(false);
                if (i < mDeckInfo.getExtraCount()) {
                    if (i == index && count > 0) {
                        cardView.showCard(mDeckInfo.getExtraCard(i));
                        index++;
                        count--;
                    }
                    if (mLimitChanged) {
                        cardView.updateLimit(getImageTop(), mLimitList);
                    }
                } else {
                    cardView.showCard(null);
                }
            }
            resizePadding(Type.Extra, mExtraViews);
        } else if (type == Type.Main) {
            int targetIndex, orgPos, all = mMainViews.size();
            //59
            targetIndex = (index / mMainLimit) * Constants.DECK_WIDTH_MAX_COUNT + (index % mMainLimit);
            for (int i = 0; i < all; i++) {
                CardView cardView = mMainViews.get(i);
                cardView.setSelected(false);
                orgPos = i % Constants.DECK_WIDTH_MAX_COUNT;
                if (orgPos >= mMainLimit) {
                    cardView.showCard(null);
                } else {
                    if (index < mDeckInfo.getMainCount()) {
                        if (targetIndex == i && count > 0) {
                            cardView.showCard(mDeckInfo.getMainCard(index));
                            index++;
                            targetIndex = (index / mMainLimit) * Constants.DECK_WIDTH_MAX_COUNT + (index % mMainLimit);
                            count--;
//                            line = i / mMainLimit;
//                            pos = i - line * mMainLimit;
//                            int vindex = line * Constants.DECK_WIDTH_MAX_COUNT + pos;

                        }
                        if (mLimitChanged) {
                            mMainViews.get(i).updateLimit(getImageTop(), mLimitList);
                        }
                    } else {
                        cardView.showCard(null);
                    }
                }
            }
            resizePadding(Type.Main, mMainViews);
        } else if (type == Type.Side) {
            int all = mSideViews.size();
            for (int i = 0; i < all; i++) {
                CardView cardView = mSideViews.get(i);
                cardView.setSelected(false);
                if (i < mDeckInfo.getSideCount()) {
                    if (i == index && count > 0) {
                        cardView.showCard(mDeckInfo.getSideCard(i));
                        index++;
                        count--;
                    }
                    if (mLimitChanged) {
                        cardView.updateLimit(getImageTop(), mLimitList);
                    }
                } else {
                    cardView.showCard(null);
                }
            }
            resizePadding(Type.Side, mSideViews);
        }
    }

    private int getMaxWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private void resizePadding(Type type, SparseArray<CardView> list) {
        int count = list.size();
        if (count <= 1) return;
        int needWidth, limit;
        if (type == Type.Main) {
            limit = mMainLimit;
        } else if (type == Type.Extra) {
            limit = mExtraLimit;
        } else {
            limit = mSideLimit;
        }
        needWidth = limit * mCardWidth;
        int maxWidth = getMaxWidth();
        int p = (needWidth > maxWidth) ? (maxWidth - needWidth) / (limit - 1) : 0;
        int top;
        if (type == Type.Side) {
            top = sideTop;
        } else if (type == Type.Extra) {
            top = extraTop;
        } else {
            top = mainTop;
        }
        int all = list.size();
        int orgLine, orgPos;
        for (int i = 0; i < all; i++) {
            View v = list.get(i);
            orgLine = i / Constants.DECK_WIDTH_MAX_COUNT;
            orgPos = i % Constants.DECK_WIDTH_MAX_COUNT;
            if (orgPos >= limit) {
                v.setVisibility(GONE);
                continue;
            } else {
                v.setVisibility(VISIBLE);
            }
            LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(mCardWidth, mCardHeight);
                layoutParams.topMargin = top + orgLine * mCardHeight;
            }
            layoutParams.setMargins(orgPos * (mCardWidth + p), layoutParams.topMargin, 0, 0);
            v.setLayoutParams(layoutParams);
        }
        postInvalidate();
    }

    public void notifyDataSetChanged() {
        updateCard(Type.Main, 0, Constants.DECK_MAIN_MAX);
        updateCard(Type.Extra, 0, Constants.DECK_EXTRA_MAX);
        updateCard(Type.Side, 0, Constants.DECK_SIDE_MAX);
        mMainLabel.setText(mLabelInfo.getMainString());
        mExtraLabel.setText(mLabelInfo.getExtraString());
        mSideLabel.setText(mLabelInfo.getSideString());
        mLimitChanged = false;
    }

    public enum Type {
        Main, Extra, Side
    }

    private boolean mLimitChanged = false;
    private final Map<Type, List<Card>> mChooseList = new HashMap<>();

    public void setLimitList(LimitList limitList) {
        mLimitList = limitList;
        mLimitChanged = true;
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
        if (mEditMode) {
            mChooseList.clear();
        }
    }

    public void deleteChoose() {
        mLimitChanged = true;
        List<Card> cards = mChooseList.get(Type.Main);
        if (cards != null) {
            for (Card c : cards) {
                mDeckInfo.removeMain(c);
            }
            cards.clear();
        }
        cards = mChooseList.get(Type.Extra);
        if (cards != null) {
            for (Card c : cards) {
                mDeckInfo.removeExtra(c);
            }
            cards.clear();
        }
        cards = mChooseList.get(Type.Side);
        if (cards != null) {
            for (Card c : cards) {
                mDeckInfo.removeSide(c);
            }
            cards.clear();
        }
        updateAll(mDeckInfo);
    }

    @Override
    public void onClick(View v) {
        CardView cardView = (CardView) v;
        Type type = (Type) v.getTag();
        if (isEditMode()) {
            if (cardView.getCard() != null) {
                v.setSelected(!v.isSelected());
                List<Card> cards = mChooseList.get(type);
                if (cards == null) {
                    cards = new ArrayList<>();
                    mChooseList.put(type, cards);
                }
                if (v.isSelected()) {
                    cards.add(cardView.getCard());
                } else {
                    cards.remove(cardView.getCard());
                }
            }
        }
        if (mOnCardClickListener != null) {
            mOnCardClickListener.onClick(type, cardView);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
