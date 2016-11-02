package cn.ygo.ocgcore;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

import jni.Struct;

public class CardData extends Struct {
    private static final int BUFFER_MAX_SIZE = 4 * 10 + 8;

    public CardData() {
        super(ByteBuffer.allocateDirect(BUFFER_MAX_SIZE));
    }

    public CardData(Buffer buffer) {
        super(buffer);
    }

    public long Code;
    public long Alias;
    public long Setcode;
    public long Type;
    public long Level;
    public long Attribute;
    public long Race;
    public int Attack;
    public int Defense;
    public int LScale;
    public int RScale;

    public String Name;
    public String Desc;
    public String[] Strs;

    public String toShortString(){
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
                ", Name='" + Name+
                '}';
    }
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
                ", Name='" + Name + '\'' +
                ", Desc='" + Desc + '\'' +
                ", Strs=" + Arrays.toString(Strs) +
                '}';
    }
}
