package cn.garymb.ygomobile.deck;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;

public class DeckItem {
    public final static int MainLabel = 0;
    public final static int MainStart = MainLabel + 1;
    public final static int MainEnd = MainStart + Constants.DECK_MAIN_MAX - 1;

    /**
     * main,side,extra
     */
    public final static boolean SeacondIsSide = false;


    public final static int SideLabel;
    public final static int SideStart;
    public final static int SideEnd;

    public final static int ExtraLabel;
    public final static int ExtraStart;
    public final static int ExtraEnd;

    static {
        if (SeacondIsSide) {
            SideLabel = MainEnd + 1;
            SideStart = SideLabel + 1;
            SideEnd = SideStart + Constants.DECK_EXTRA_COUNT - 1;
            ExtraLabel = SideEnd + 1;
            ExtraStart = ExtraLabel + 1;
            ExtraEnd = ExtraStart + Constants.DECK_SIDE_COUNT - 1;
        } else {
            ExtraLabel = MainEnd + 1;
            ExtraStart = ExtraLabel + 1;
            ExtraEnd = ExtraStart + Constants.DECK_SIDE_COUNT - 1;

            SideLabel = ExtraEnd + 1;
            SideStart = SideLabel + 1;
            SideEnd = SideStart + Constants.DECK_EXTRA_COUNT - 1;
        }
    }


    private DeckItemType mType;
    private CardInfo mCardInfo;

    public DeckItem() {
        mType = DeckItemType.Space;
    }

    public DeckItem(CardInfo cardInfo, DeckItemType type) {
        mType = type;
        mCardInfo = cardInfo;
    }

    public DeckItem(DeckItemType type) {
        this.mType = type;
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public DeckItemType getType() {
        return mType;
    }
}
