package cn.ygo.ocgcore;


import android.text.TextUtils;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class CardSet {
    private long code;
    private String name;

    public CardSet() {

    }

    public CardSet(long code, String name) {
        this.code = code;
        this.name = name;
    }

    public long getCode() {
        return code;
    }

    public String getName() {
        if (TextUtils.isEmpty(name)) {
            name = String.format("0x%x", code);
        }
        return name;
    }

    @Override
    public String toString() {
        return "CardSet{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }

    public static final Comparator<CardSet> CODE_ASC = new Comparator<CardSet>() {
        @Override
        public int compare(CardSet cardSet, CardSet t1) {
            long i = cardSet.getCode() - t1.getCode();
            if(i>0){
                return 1;
            }else if(i==0){
                return 0;
            }
            return -1;
        }
    };
    private static final Collator COMP_CHINA= Collator.getInstance(Locale.CHINA);
    public static final Comparator<CardSet> NAME_ASC = new Comparator<CardSet>() {
        @Override
        public int compare(CardSet cardSet, CardSet t1) {
            return COMP_CHINA.compare(cardSet.getName(), t1.getName());
        }
    };
}
