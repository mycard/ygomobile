package cn.ygo.ocgcore.manager;

import cn.ygo.ocgcore.Duel;

public interface IMessager {
    int handle(Duel duel, long msgid);
}
