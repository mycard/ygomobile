package ygo;

import org.junit.Test;

import cn.garymb.ygomobile.Constants;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardType;

public class StringTest {

    @Test
    public void test() {
        StringManager stringManager = StringManager.get();
        System.out.println(stringManager.loadFile("E:\\git\\YGOMobile\\mobile\\assets\\data\\core\\3.5\\config\\strings.conf"));
        System.out.println(stringManager.getSystem().size());
        CardType[] cardTypes = CardType.values();
        for (CardType type : cardTypes) {
            System.out.println(String.format("0x%X", type.value()) + "=" +
                    stringManager.getTypeString(type.value()));
        }
    }

    private int getPow(long type) {
        //0 1 2 3 4
        //1 2 4 8 16
        int i = 0;
        long start;
        do {
            start = (int) Math.pow(2, i);
            if (start == type) {
                return i;
            } else if (start > type) {
                return -1;
            }
            i++;
        }
        while (start < type);
        return i;
    }
}
