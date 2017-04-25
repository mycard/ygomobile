package cn.garymb.ygomobile.core.loader;

import cn.ygo.ocgcore.LimitList;

public interface ICardLoader{
    void search(String prefixWord, String suffixWord,
                long attribute, long level, long race,long limitlist,long limit,
                String atk, String def,long pscale,
                long setcode, long category, long ot,boolean isLink, long... types);
    void onReset();
    void setLimitList(LimitList limit);
    LimitList getLimitList();
}
