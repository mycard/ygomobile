package ygo;

import org.junit.Test;

import java.util.Locale;

import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardType;

public class StringTest {
    int MASK = 0x1000;
    public static String tirmName(String name, String ex) {
        if (name.toLowerCase(Locale.US).endsWith(ex)) {
            int i = name.lastIndexOf(".");
            if (i >= 0) {
                return name.substring(0, i);
            }
        }
        return name;
    }
    @Test
    public void testName(){
        System.out.println(tirmName("a\\a.YDK",".ydk"));
    }
    @Test
    public void testMask() {
        int i = 1;
        i = i | MASK;
        System.out.println(i);
        i ^= MASK;
        System.out.println(i);
    }

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
