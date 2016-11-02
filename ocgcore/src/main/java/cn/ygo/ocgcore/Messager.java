package cn.ygo.ocgcore;

public class Messager implements Api.IMessager {
    @Override
    public int handle(Duel duel, long msgid) {
        duel.OnMessage(msgid);
        return 0;
    }
}
