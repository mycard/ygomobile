package cn.ygo.ocgcore;

import java.util.Arrays;

public class CardData {

    public CardData() {
    }

    public long Code;
    public int Ot;
    public long Alias;
    public long Setcode;
    public long Type;
    public int Level;
    public int Attribute;
    public long Race;
    public int Attack;
    public int Defense;
    public int LScale;
    public int RScale;
    public String dbFile;

    @Override
    public String toString() {
        return "CardData{" +
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
                '}';
    }
}
