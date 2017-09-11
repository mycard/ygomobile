package cn.garymb.ygomobile.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ocgcore.data.Card;

public class DeckInfo {
    private final List<Card> mainCards;
    private final List<Card> extraCards;
    private final List<Card> sideCards;

    public DeckInfo() {
        mainCards = new ArrayList<>();
        extraCards = new ArrayList<>();
        sideCards = new ArrayList<>();
    }

    public void addMainCards(Card card) {
        if (card != null) {
            this.mainCards.add(card);
        }
    }

    public void addExtraCards(Card card) {
        if (card != null) {
            this.extraCards.add(card);
        }
    }

    public void addSideCards(Card card) {
        if (card != null) {
            this.sideCards.add(card);
        }
    }

    public void setMainCards(Collection<Card> mainCards) {
        if (mainCards != null) {
            this.mainCards.addAll(mainCards);
        }
    }

    public void setExtraCards(Collection<Card> extraCards) {
        if (extraCards != null) {
            this.extraCards.addAll(extraCards);
        }
    }

    public void setSideCards(Collection<Card> sideCards) {
        if (sideCards != null) {
            this.sideCards.addAll(sideCards);
        }
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
