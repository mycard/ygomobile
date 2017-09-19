package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.lite.R;
import ocgcore.data.Card;

public class DeckView extends LinearLayout {
    private final DeckLabel mMainLabel, mExtraLabel, mSideLabel;
    private final CardGroupView mMainGroup, mExtraGroup, mSideGroup;

    public enum Type {
        Main, Extra, Side
    }

    public DeckView(Context context) {
        this(context, null);
    }

    public DeckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        int card_width = 0;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DeckView);
            if (array != null) {
                card_width = array.getInteger(R.styleable.DeckView_card_width, 0);
            }
        }
        if (card_width <= 0) {
            int width = (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
            card_width = width / 10;
        }
        if (card_width <= 0) {
            card_width = (getResources().getDisplayMetrics().widthPixels - getPaddingLeft() - getPaddingRight()) / 10;
        }
        int card_height = Math.round((255.0f / 177.0f) * card_width);
        mMainLabel = new DeckLabel(context);
        mSideLabel = new DeckLabel(context);
        mExtraLabel = new DeckLabel(context);
        mMainGroup = new CardGroupView(context, 4, card_width, card_height);
        mExtraGroup = new CardGroupView(context, 1, card_width, card_height);
        mSideGroup = new CardGroupView(context, 1, card_width, card_height);
        addView(mMainLabel);
        addView(mMainGroup, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, card_height * 4));
        addView(mExtraLabel);
        addView(mExtraGroup, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, card_height));
        addView(mSideLabel);
        addView(mSideGroup, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, card_height));
    }

    public void setOnCardClickLinstener(CardGroupView.OnCardLinstener onCardLinstener) {
        CardGroupView.OnCardLinstener mProxy = new CardGroupView.OnCardLinstener() {
            @Override
            public void onClick(CardGroupView cardGroupView, CardView cardView) {
                if (onCardLinstener != null) {
                    onCardLinstener.onClick(cardGroupView, cardView);
                }
            }

            @Override
            public void onAdd(CardGroupView cardGroupView, Card card, int index) {
                updateLabel(getType(cardGroupView), card, false);
                if (onCardLinstener != null) {
                    onCardLinstener.onAdd(cardGroupView, card, index);
                }
            }

            @Override
            public void onRemove(CardGroupView cardGroupView, CardView cardView, int index) {
                updateLabel(getType(cardGroupView), cardView.getCard(), true);
                if (onCardLinstener != null) {
                    onCardLinstener.onRemove(cardGroupView, cardView, index);
                }
            }
        };
        mMainGroup.setOnCardLinstener(onCardLinstener);
        mExtraGroup.setOnCardLinstener(onCardLinstener);
        mSideGroup.setOnCardLinstener(onCardLinstener);
    }

    private Type getType(CardGroupView cardGroupView) {
        if (cardGroupView == mExtraGroup) {
            return Type.Extra;
        }
        if (cardGroupView == mSideGroup) {
            return Type.Side;
        }
        return Type.Main;
    }

    protected void updateLabel(Type type, Card card, boolean remove) {
        //TODO
    }

    public void clearAll() {
        mMainGroup.clear();
        mExtraGroup.clear();
        mSideGroup.clear();
    }

    public void updateAll(DeckInfo deckInfo) {
        mMainGroup.updateAll(deckInfo.getMainCards());
        mExtraGroup.updateAll(deckInfo.getExtraCards());
        mSideGroup.updateAll(deckInfo.getSideCards());
        //TODO update label
    }

}
