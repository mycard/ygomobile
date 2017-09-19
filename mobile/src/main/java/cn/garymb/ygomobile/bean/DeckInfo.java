package cn.garymb.ygomobile.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import ocgcore.data.Card;

public class DeckInfo {
    private final List<Card> mainCards;
    private final List<Card> extraCards;
    private final List<Card> sideCards;

    private int mainCount, extraCount, sideCount;

    public DeckInfo() {
        mainCards = new ArrayList<>();
        extraCards = new ArrayList<>();
        sideCards = new ArrayList<>();
    }

    public void addMainCards(Card card) {
        if (card != null) {
            this.mainCards.add(card);
            mainCount++;
        }
    }

    public void addExtraCards(Card card) {
        if (card != null) {
            this.extraCards.add(card);
            extraCount++;
        }
    }

    public void addSideCards(Card card) {
        if (card != null) {
            this.sideCards.add(card);
            sideCount++;
        }
    }

    public void setMainCards(Collection<Card> mainCards) {
        this.mainCards.clear();
        if (mainCards != null) {
            this.mainCards.addAll(mainCards);
        }
        mainCount = Math.min(mainCards.size(), Constants.DECK_MAIN_MAX);
    }

    public void setExtraCards(Collection<Card> extraCards) {
        this.extraCards.clear();
        if (extraCards != null) {
            this.extraCards.addAll(extraCards);
        }
        extraCount = Math.min(extraCards.size(), Constants.DECK_EXTRA_MAX);
    }

    public void update(DeckInfo deck){
        setMainCards(deck.mainCards);
        setExtraCards(deck.extraCards);
        setSideCards(deck.sideCards);
    }

    public void setSideCards(Collection<Card> sideCards) {
        this.sideCards.clear();
        if (sideCards != null) {
            this.sideCards.addAll(sideCards);
        }
        sideCount = Math.min(sideCards.size(), Constants.DECK_SIDE_MAX);
    }

    public int getMainCount() {
        return mainCount;
    }

    public int getExtraCount() {
        return extraCount;
    }

    public int getSideCount() {
        return sideCount;
    }


    public Card getMainCard(int index) {
        if (index >= 0 && index < getMainCount()) {
            return mainCards.get(index);
        }
        return null;
    }

    public Card getExtraCard(int index) {
        if (index >= 0 && index < getExtraCount()) {
            return extraCards.get(index);
        }
        return null;
    }

    public Card getSideCard(int index) {
        if (index >= 0 && index < getSideCount()) {
            return sideCards.get(index);
        }
        return null;
    }

    @Override
    public String toString() {
        return "DeckInfo{" +
                "mainCards=" + mainCards.size() +
                ", extraCards=" + extraCards.size() +
                ", sideCards=" + sideCards.size() +
                '}';
    }

    public String toLongString() {
        return "DeckInfo{" +
                "mainCards=" + mainCards +
                ", extraCards=" + extraCards +
                ", sideCards=" + sideCards +
                '}';
    }

    public List<Card> getMainCards() {
        return mainCards;
    }

    public List<Card> getExtraCards() {
        return extraCards;
    }

    public List<Card> getSideCards() {
        return sideCards;
    }
}
