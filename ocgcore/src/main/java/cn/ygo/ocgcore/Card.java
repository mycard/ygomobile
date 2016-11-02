package cn.ygo.ocgcore;


import java.util.Arrays;

import cn.ygo.ocgcore.enums.CardType;

public class Card extends CardData {

    public String Name;
    public String Desc;
    public String[] Strs;

    public Card() {
        Strs = new String[0x10];
    }

    public Card(CardData cardData) {
        this.Code = cardData.Code;
        this.Alias = cardData.Alias;
        this.Setcode = cardData.Setcode;
        this.Type = cardData.Type;
        this.Level = cardData.Level;
        this.Attribute = cardData.Attribute;
        this.Race = cardData.Race;
        this.Attack = cardData.Attack;
        this.Defense = cardData.Defense;
        this.LScale = cardData.LScale;
        this.RScale = cardData.RScale;
        this.dbFile = cardData.dbFile;
        this.Strs = new String[0x10];
    }

    public boolean HasType(CardType type) {
        return ((Type & type.value()) != 0);
    }

    public boolean isExtraCard() {
        return (HasType(CardType.Fusion) || HasType(CardType.Synchro) || HasType(CardType.Xyz));
    }

    @Override
    public String toString() {
        return "Card{" +
                "Code=" + Code +
                ", Alias=" + Alias +
                ", Setcode=" + Setcode +
                ", Type=" + Type +
                ", Level=" + Level +
                ", Attribute=" + Attribute +
                ", Race=" + Race +
                ", Attack=" + Attack +
                ", Defense=" + Defense +
                ", LScale=" + LScale +
                ", RScale=" + RScale +
                ", Name='" + Name + '\'' +
                ", Desc='" + Desc + '\'' +
                ", Strs=" + Arrays.toString(Strs) +
                '}';
    }
}
