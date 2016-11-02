package cn.ygo.ocgcore.manager;

import cn.ygo.ocgcore.CardData;

public interface ICardManager {
    CardData getCard(long code);
    void loadCards();
}
