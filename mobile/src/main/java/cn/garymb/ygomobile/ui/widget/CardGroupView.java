package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.lite.R;
import ocgcore.data.Card;

public class CardGroupView extends LinearLayout {

    public interface OnCardLinstener {
        void onClick(CardGroupView cardGroupView, CardView cardView);

        void onAdd(CardGroupView cardGroupView, Card card, int index);

        void onRemove(CardGroupView cardGroupView, CardView cardView, int index);
    }

    private int mLine = 1;
    private CardLineView[] mCardLineViews;
    private OnCardLinstener mOnCardLinstener;

    public CardGroupView(Context context) {
        this(context, null);
    }

    public CardGroupView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardGroupView(Context context, int line, int widht, int height) {
        super(context);
        init(line, null, widht, height);
    }

    public CardGroupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CardGroupView);
            if (array != null) {
                mLine = array.getInteger(R.styleable.CardGroupView_lines, mLine);
            }
        }
        init(mLine, attrs);
    }

    private void init(int lines, AttributeSet attrs) {
        init(lines, attrs, -1, -1);
    }

    private void init(int lines, AttributeSet attrs, int w, int h) {
        setOrientation(VERTICAL);
        mLine = lines;
        mCardLineViews = new CardLineView[mLine];
        if (w <= 0) {
            w = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 10;
        }
        if (h <= 0) {
            h = getMeasuredHeight();
        }
        for (int i = 0; i < mLine; i++) {
            mCardLineViews[i] = new CardLineView(getContext(), attrs, w, h);
            addView(mCardLineViews[i]);
        }
    }

    public int getCardCount() {
        int count = 0;
        for (int i = 0; i < mLine; i++) {
            CardLineView cardLineView = mCardLineViews[i];
            count += cardLineView.getCardCount();
        }
        return count;
    }

    public int getMaxCount() {
        return mLine * 15;
    }

    public boolean addCard(Card card) {
        return addCard(card, -1);
    }

    public boolean addCard(Card card, int index) {
        //40 10
        //44 11
        //48 12
        //52 13
        //56 14
        //60 15
        //判断是第几行
        int count = getCardCount();
        if (index < 0) {
            index = count;
        }
        if (count + 1 > getMaxCount()) {
            return false;
        }
        int max = Math.max(10, (count + 1) / mLine);
        int targetLine = index / max;
        int targetIndex = index % max;
        if (index % max > 0) {
            targetLine++;
        }

        for (int i = 0; i < mLine; i++) {
            CardLineView cardLineView = mCardLineViews[i];
            cardLineView.setMaxCardCount(max);
            if (i == targetLine) {
                mCardLineViews[targetLine].addCard(card, targetIndex);
                if (mOnCardLinstener != null) {
                    mOnCardLinstener.onAdd(this, card, index);
                }
            }
        }
        CardLineView next;
        for (int i = 0; i < mLine; i++) {
            CardLineView cardLineView = mCardLineViews[i];
            if (i + 1 < mLine && cardLineView.getCardCount() < cardLineView.getMaxCardCount()) {
                next = mCardLineViews[i + 1];
                if (next.getCardCount() > 0) {
                    //补齐max
                    int t = Math.min(cardLineView.getMaxCardCount() - cardLineView.getCardCount(),
                            next.getCardCount());
                    for (int j = t - 1; j >= 0; j--) {
                        CardView cardView = next.removeCardAt(j);
                        cardLineView.addCard(cardView);
                    }
                } else {
                    break;
                }
            }
        }
        return false;
    }

    public void updateAll(List<Card> cardList) {
        int count = cardList.size();
        int max = Math.max(10, (count) / mLine);

        List<Card> target = new ArrayList<>(max);
        for (int i = 0; i < mLine; i++) {
            CardLineView cardLineView = mCardLineViews[i];
            cardLineView.setMaxCardCount(max);
            target.clear();
            for (int j = 0; j < max; j++) {
                int index = i * max + j;
                if (index < count) {
                    target.add(cardList.get(index));
                }
            }
            cardLineView.onUpdateAll(target);
        }
    }

    public void clear() {
        for (int i = 0; i < mLine; i++) {
            CardLineView cardLineView = mCardLineViews[i];
            cardLineView.removeAllViews();
        }
    }

    public void removeCardAt(int index) {
        int max = Math.max(10, (getCardCount() + index) / 4);
        int targetLine = index / max;
        int targetIndex = index % max;
        if (index % max > 0) {
            targetLine++;
        }

        for (int i = 0; i < mLine; i++) {
            CardLineView cardLineView = mCardLineViews[i];
            cardLineView.setMaxCardCount(-1);
            if (i == targetLine) {
                CardView cardView = mCardLineViews[targetLine].removeCardAt(targetIndex);
                if (mOnCardLinstener != null) {
                    mOnCardLinstener.onRemove(this, cardView, index);
                }
            }
        }
        CardLineView next;
        for (int i = 0; i < mLine; i++) {
            CardLineView cardLineView = mCardLineViews[i];
            if (i + 1 < mLine && cardLineView.getCardCount() > max) {
                next = mCardLineViews[i + 1];
                //超出max的移动到下一行
                int t = cardLineView.getCardCount() - max;
                for (int j = 0; j < t; j++) {
                    CardView cardView = cardLineView.removeCardAt(max + j);
                    next.addCard(cardView);
                }
            }
            cardLineView.setMaxCardCount(max);
        }
    }

    public void setOnCardLinstener(OnCardLinstener onCardLinstener) {
        mOnCardLinstener = onCardLinstener;
        CardLineClickProxyListener cardClickLinstener = new CardLineClickProxyListener(onCardLinstener);
        for (int i = 0; i < mLine; i++) {
            mCardLineViews[i].setOnCardLineClickLinstener(cardClickLinstener);
        }
    }

    private class CardLineClickProxyListener implements CardLineView.OnCardLineClickLinstener {
        private OnCardLinstener mOnCardLinstener;

        private CardLineClickProxyListener(OnCardLinstener onCardLinstener) {
            this.mOnCardLinstener = onCardLinstener;
        }

        @Override
        public void onClick(CardView cardView) {
            if (mOnCardLinstener != null) {
                mOnCardLinstener.onClick(CardGroupView.this, cardView);
            }
        }
    }
}
