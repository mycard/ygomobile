package cn.ygo.ocgcore.manager;

import cn.ygo.ocgcore.Duel;

public class Messager implements IMessager {
    @Override
    public int handle(Duel duel, long msgid) {
        duel.OnMessage(msgid);
        return 0;
    }
}
