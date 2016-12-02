package cn.garymb.ygomobile.deck;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;

public class DeckItem {
    public final static int MainLabel = 0;
    public final static int MainStart = MainLabel + 1;
    public final static int MainEnd = MainStart + Constants.DECK_MAIN_MAX - 1;
    public final static int ExtraLabel = MainEnd + 1;
    public final static int ExtraStart = ExtraLabel + 1;
    public final static int ExtraEnd = ExtraStart + Constants.DECK_EXTRA_COUNT - 1;
    public final static int SideLabel = ExtraEnd + 1;
    public final static int SideStart = SideLabel + 1;
    public final static int SideEnd = SideStart + Constants.DECK_SIDE_COUNT - 1;

    private DeckItemType mType;
    private CardInfo mCardInfo;
    private String mText;
    private int mColor;

    public DeckItem() {
        mType = DeckItemType.Space;
    }

    public DeckItem(CardInfo cardInfo, DeckItemType type) {
        mType = type;
        mCardInfo = cardInfo;
    }

    public DeckItem(String name, int color) {
        mType = DeckItemType.Label;
        mText = name;
        mColor = color;
    }

    public String getText() {
        return mText;
    }

    public int getColor() {
        return mColor;
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public DeckItemType getType() {
        return mType;
    }
}
