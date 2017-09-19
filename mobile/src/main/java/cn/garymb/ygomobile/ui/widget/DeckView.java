package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import cn.garymb.ygomobile.bean.Deck;
import cn.garymb.ygomobile.bean.DeckInfo;
import cn.garymb.ygomobile.lite.R;
import ocgcore.data.Card;
import ocgcore.enums.CardType;

public class DeckView extends LinearLayout {
    private final DeckLabel mMainLabel, mExtraLabel, mSideLabel;
    private final CardGroupView mMainGroup, mExtraGroup, mSideGroup;
    private View mLastView;
    private CardGroupView.OnCardLinstener mOnCardLinstener;
    private final LabelInfo mLabelInfo;

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
        mLabelInfo = new LabelInfo(context);
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
        mMainGroup.setOnCardLinstener(mCardLinstenerProxy);
        mExtraGroup.setOnCardLinstener(mCardLinstenerProxy);
        mSideGroup.setOnCardLinstener(mCardLinstenerProxy);
    }

    public void setOnCardClickLinstener(CardGroupView.OnCardLinstener onCardLinstener) {
        mOnCardLinstener = onCardLinstener;
    }

    protected void updateLabel(CardGroupView cardGroupView, Card card, boolean remove) {
        if (cardGroupView == mExtraGroup) {
            mLabelInfo.updateExtra(card, remove);
            mExtraLabel.setText(mLabelInfo.getExtraString());
        } else if (cardGroupView == mSideGroup) {
            mLabelInfo.updateSide(card, remove);
            mSideLabel.setText(mLabelInfo.getSideString());
        } else {
            mLabelInfo.updateMain(card, remove);
            mMainLabel.setText(mLabelInfo.getMainString());
        }
    }

    public void clearAll() {
        mMainGroup.clear();
        mExtraGroup.clear();
        mSideGroup.clear();
        if (mLastView != null) {
            mLastView.setSelected(false);
            mLastView = null;
        }
        mLabelInfo.reset();
        mMainLabel.setText(mLabelInfo.getMainString());
        mExtraLabel.setText(mLabelInfo.getExtraString());
        mSideLabel.setText(mLabelInfo.getSideString());
    }

    public void updateAll(DeckInfo deckInfo) {
        if (mLastView != null) {
            mLastView.setSelected(false);
            mLastView = null;
        }
        mMainGroup.updateAll(deckInfo.getMainCards());
        mExtraGroup.updateAll(deckInfo.getExtraCards());
        mSideGroup.updateAll(deckInfo.getSideCards());
        mLabelInfo.update(deckInfo);
        mMainLabel.setText(mLabelInfo.getMainString());
        mExtraLabel.setText(mLabelInfo.getExtraString());
        mSideLabel.setText(mLabelInfo.getSideString());
    }

    private CardGroupView.OnCardLinstener mCardLinstenerProxy = new CardGroupView.OnCardLinstener() {
        @Override
        public void onClick(CardGroupView cardGroupView, CardView cardView) {
            if (mLastView != null) {
                mLastView.setSelected(false);
                mLastView = null;
            }
            mLastView = cardView;
            mLastView.setSelected(true);
            if (mOnCardLinstener != null) {
                mOnCardLinstener.onClick(cardGroupView, cardView);
            }
        }

        @Override
        public void onAdd(CardGroupView cardGroupView, Card card, int index) {
            updateLabel(cardGroupView, card, false);
            if (mOnCardLinstener != null) {
                mOnCardLinstener.onAdd(cardGroupView, card, index);
            }
        }

        @Override
        public void onRemove(CardGroupView cardGroupView, CardView cardView, int index) {
            updateLabel(cardGroupView, cardView.getCard(), true);
            if (mOnCardLinstener != null) {
                mOnCardLinstener.onRemove(cardGroupView, cardView, index);
            }
        }
    };

    private static class LabelInfo {
        private int mMainCount = 0;
        private int mExtraCount = 0;
        private int mSideCount = 0;
        private int mMainMonsterCount = 0;
        private int mMainSpellCount = 0;
        private int mMainTrapCount = 0;
        private int mExtraFusionCount = 0;
        private int mExtraXyzCount = 0;
        private int mExtraSynchroCount = 0;
        private int mExtraLinkCount = 0;
        private int mSideMonsterCount = 0;
        private int mSideSpellCount = 0;
        private int mSideTrapCount = 0;
        private Context mContext;

        private LabelInfo(Context context) {
            mContext = context;
        }

        public void reset() {
            mMainCount = 0;
            mExtraCount = 0;
            mSideCount = 0;
            mMainMonsterCount = 0;
            mMainSpellCount = 0;
            mMainTrapCount = 0;
            mExtraFusionCount = 0;
            mExtraXyzCount = 0;
            mExtraSynchroCount = 0;
            mExtraLinkCount = 0;
            mSideMonsterCount = 0;
            mSideSpellCount = 0;
            mSideTrapCount = 0;
        }

        public Context getContext() {
            return mContext;
        }

        private String getMainString() {
            return getContext().getString(R.string.deck_main, mMainCount, mMainMonsterCount, mMainSpellCount, mMainTrapCount);
        }

        private String getExtraString() {
            return getContext().getString(R.string.deck_extra, mExtraCount, mExtraFusionCount, mExtraSynchroCount, mExtraXyzCount, mExtraLinkCount);
        }

        private String getSideString() {
            return getContext().getString(R.string.deck_side, mSideCount, mSideMonsterCount, mSideSpellCount, mSideTrapCount);
        }

        private void update(DeckInfo deckInfo) {
            reset();
            for (Card card : deckInfo.getMainCards()) {
                updateMain(card, true);
            }
            for (Card card : deckInfo.getExtraCards()) {
                updateExtra(card, true);
            }
            for (Card card : deckInfo.getSideCards()) {
                updateSide(card, true);
            }
        }

        private void updateMain(Card card, boolean remove) {
            if (remove) {
                mMainCount--;
                if (card.isType(CardType.Spell)) {
                    mMainSpellCount--;
                } else if (card.isType(CardType.Trap)) {
                    mMainTrapCount--;
                } else {
                    mMainMonsterCount--;
                }
            } else {
                mMainCount++;
                if (card.isType(CardType.Spell)) {
                    mMainSpellCount++;
                } else if (card.isType(CardType.Trap)) {
                    mMainTrapCount++;
                } else {
                    mMainMonsterCount++;
                }
            }
        }

        private void updateExtra(Card card, boolean remove) {
            if (remove) {
                mExtraCount--;
                if (card.isType(CardType.Fusion)) {
                    mExtraFusionCount--;
                } else if (card.isType(CardType.Synchro)) {
                    mExtraSynchroCount--;
                } else if (card.isType(CardType.Xyz)) {
                    mExtraXyzCount--;
                } else if (card.isType(CardType.Link)) {
                    mExtraLinkCount--;
                }
            } else {
                mExtraCount++;
                if (card.isType(CardType.Fusion)) {
                    mExtraFusionCount++;
                } else if (card.isType(CardType.Synchro)) {
                    mExtraSynchroCount++;
                } else if (card.isType(CardType.Xyz)) {
                    mExtraXyzCount++;
                } else if (card.isType(CardType.Link)) {
                    mExtraLinkCount++;
                }
            }
        }

        private void updateSide(Card card, boolean remove) {
            if (remove) {
                mSideCount--;
                if (card.isType(CardType.Spell)) {
                    mSideSpellCount--;
                } else if (card.isType(CardType.Trap)) {
                    mSideTrapCount--;
                } else {
                    mSideMonsterCount--;
                }
            } else {
                mSideCount++;
                if (card.isType(CardType.Spell)) {
                    mSideSpellCount++;
                } else if (card.isType(CardType.Trap)) {
                    mSideTrapCount++;
                } else {
                    mSideMonsterCount++;
                }
            }
        }
    }

}
