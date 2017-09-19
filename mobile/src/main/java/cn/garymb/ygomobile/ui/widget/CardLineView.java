package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import cn.garymb.ygomobile.lite.R;
import ocgcore.data.Card;

public class CardLineView extends FrameLayout {
    private int mCardWidth, mCardHeight;
    private int mMaxCount;
    private OnCardLineClickLinstener mOnCardLineClickLinstener;

    interface OnCardLineClickLinstener {
        void onClick(CardView cardView);
    }

    public CardLineView(Context context) {
        this(context, null);
    }

    public CardLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCardWidth = (int) getResources().getDimension(R.dimen.card_width);
        mCardHeight = (int) getResources().getDimension(R.dimen.card_height);
    }

    public CardLineView(Context context, @Nullable AttributeSet attrs, int width, int height) {
        super(context, attrs);
        mCardWidth = width;
        mCardHeight = height;
    }

    public void setCardSize(int width, int height) {
        Log.i("kk", "w=" + width + ",h=" + height);
        mCardWidth = width;
        mCardHeight = height;
    }

    public void setOnCardLineClickLinstener(OnCardLineClickLinstener onCardLineClickLinstener) {
        this.mOnCardLineClickLinstener = onCardLineClickLinstener;
    }

    private int getMaxWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    private boolean addCard(Card card, int index, boolean padding) {
        if (getMaxCardCount() > 0 && getCardCount() >= getMaxCardCount()) {
            return false;
        }
        CardView cardImage = new CardView(getContext());
        cardImage.showCard(card);
        addCard(cardImage, index);
        resizePadding();
        return true;
    }

    public void addCard(CardView cardImage) {
        addCard(cardImage, -1);
    }

    public void addCard(CardView cardImage, int index) {
        LayoutParams layoutParams = new LayoutParams(mCardWidth, mCardHeight);
        if (index >= 0) {
            addView(cardImage, index, layoutParams);
        } else {
            addView(cardImage, layoutParams);
        }
    }

    public boolean addCard(Card card) {
        return addCard(card, getCardCount());
    }

    public boolean addCard(Card card, int index) {
        return addCard(card, index, true);
    }

    public void onUpdateAll(List<Card> cardList) {
        removeAllViews();
        if (cardList != null) {
            int count = cardList.size();
            for (int i = 0; i < count; i++) {
                addCard(cardList.get(i), -1, false);
            }
        }
        resizePadding();
    }

    public int getLimitCardCount() {
        return 10;
    }

    public void setMaxCardCount(int maxCount) {
        mMaxCount = maxCount;
        resizePadding();
    }

    public int getMaxCardCount() {
        return mMaxCount;
    }

    public int getCardCount() {
        return super.getChildCount();
    }

    public CardView removeCardAt(int index) {
        CardView view = null;
        int count = getChildCount();
        if (index >= 0 && index < count) {
            view = (CardView) getChildAt(index);
            removeView(view);
        }
        resizePadding();
        return view;
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
    }

    public void cancelSelected() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).setSelected(false);
        }
        resizePadding();
    }

    public void setSelected(int index) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);
            if (i == index) {
                if (v.isSelected()) {
                    v.setSelected(false);
                } else {
                    v.setSelected(true);
                }
            } else {
                v.setSelected(false);
            }
        }
        resizePadding();
    }

    @Override
    public void addView(View child, final int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof CardView) {
            final CardView CardView = (CardView) child;
            CardView.setOnClickListener((v) -> {
                if (mOnCardLineClickLinstener != null) {
                    mOnCardLineClickLinstener.onClick(CardView);
                }
            });
        }
    }

    private int getIndex(View view) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (getChildAt(i) == view) {
                return i;
            }
        }
        return -1;
    }

    private void resizePadding() {
        int count = getChildCount();
        if (count <= 1) return;
        int needWidth = count * mCardWidth;
        int maxWidth = getMaxWidth();
        int p = (needWidth > maxWidth) ? (maxWidth - needWidth) / (count - 1) : 0;
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(mCardWidth, mCardHeight);
            }
//            int top = (v.isSelected() ? 0 : (mCardWidth / 3 * 2));
            layoutParams.setMargins(i * (mCardWidth + p), 0, 0, 0);
            v.setLayoutParams(layoutParams);
        }
        postInvalidate();
    }

}
