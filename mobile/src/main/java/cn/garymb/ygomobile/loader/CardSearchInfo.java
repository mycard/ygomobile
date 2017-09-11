package cn.garymb.ygomobile.loader;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ocgcore.data.Card;
import ocgcore.enums.CardType;

class CardSearchInfo {
    //名字或者描述
    String word, prefixWord, suffixWord;
    int attribute;
    int level, ot, pscale;
    long race, category;
    String atk, def;
    boolean islink;
    final List<Long> inCards;
    long[] types;

    CardSearchInfo() {
        inCards = new ArrayList<>();
    }

    public boolean check(Card card) {
        if (!TextUtils.isEmpty(word)) {
            if (!((card.Name != null && card.Name.contains(word))
                    || (card.Desc != null && card.Desc.contains(word)))) {
                return false;
            }
        } else if(!TextUtils.isEmpty(prefixWord)  && !TextUtils.isEmpty(suffixWord)){
            boolean has = false;
            if (card.Name != null) {
                int i1 = card.Name.indexOf(prefixWord);
                int i2 = card.Name.indexOf(suffixWord);
                if (i1 >= 0 && i2 >= 0 && i1 < i2) {
                    has = true;
                }
            }
            if (!has) {
                if (card.Desc != null) {
                    int i1 = card.Desc.indexOf(prefixWord);
                    int i2 = card.Desc.indexOf(suffixWord);
                    if (i1 >= 0 && i2 >= 0 && i1 < i2) {
                        has = true;
                    }
                }
            }
            if (!has) {
                return false;
            }
        }
        if (attribute != 0) {
            if (card.Attribute != attribute) {
                return false;
            }
        }
        if (level != 0) {
            if (card.getStar() != level) {
                return false;
            }
        }
        if (!TextUtils.isEmpty(atk)) {
            if (atk.contains("-")) {
                String[] atks = atk.split("-");
                if (!(card.Attack >= i(atks[0]) && i(atks[1]) <= card.Attack)) {
                    return false;
                }
            } else {
                if (card.Attack != ((TextUtils.isDigitsOnly(atk) ? i(atk) : -2))) {
                    return false;
                }
            }
        }
        if (!TextUtils.isEmpty(def)) {
            if (islink) {
                int link = Integer.parseInt(def, 2);
                if (!((card.Defense & link) == link && (card.isType(CardType.Link)))) {
                    return false;
                }
            } else {
                if (def.contains("-")) {
                    String[] defs = def.split("-");
                    if (!(card.Defense >= i(defs[0]) && i(defs[1]) <= card.Defense)) {
                        return false;
                    }
                } else {
                    if (card.Defense != ((TextUtils.isDigitsOnly(def) ? i(def) : -2))) {
                        return false;
                    }
                }
            }
        }
        if (ot > 0) {
            if (card.Ot != ot) {
                return false;
            }
        }

        if (pscale > 0) {
            if (!((card.Level >> 16 & 255) == pscale || (card.Level >> 24 & 255) == pscale)) {
                return false;
            }
        }

        if (race != 0) {
            if (card.Race != race) {
                return false;
            }
        }
        if (category != 0) {
            if ((card.Category & category) != category) {
                return false;
            }
        }
        if (types.length > 0) {
            //通常魔法
            boolean st = false;
            if (types[0] == CardType.Spell.value() || types[0] == CardType.Trap.value()
                    || types[0] == CardType.Normal.value()) {
                if (types.length > 2) {
                    if (types[2] == CardType.Normal.value()) {
                        if (!card.isType(CardType.Normal)) {
                            return false;
                        }
                        st = true;
                    }
                } else if (types.length > 1) {
                    if (types[1] == CardType.Normal.value()) {
                        if (!card.isType(CardType.Normal)) {
                            return false;
                        }
                        st = true;
                    }
                }
            }
            if (!st) {
                for (long type : types) {
                    if (type > 0) {
                        if ((card.Type & type) != type) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private int i(String str) {
        try {
            return Integer.valueOf(str);
        } catch (Exception e) {
            return 0;
        }
    }
}
