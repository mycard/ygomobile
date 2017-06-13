package cn.garymb.ygomobile.ui.cards;

import cn.garymb.ygomobile.bean.CardInfo;

public interface CardListProvider {
    int getCardsCount();

    CardInfo getCard(int posotion);
}
