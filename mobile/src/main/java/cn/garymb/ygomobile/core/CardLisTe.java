package cn.garymb.ygomobile.core;

import cn.garymb.ygomobile.bean.CardInfo;

/**
 * Created by feihua on 2017/6/10.
 */

public interface CardLisTe {
    public abstract int getCardSize();
    public abstract CardInfo getCard(int posotion);
}
