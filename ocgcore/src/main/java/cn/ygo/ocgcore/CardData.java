package cn.ygo.ocgcore;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import jni.Struct;

public class CardData extends Struct {
    private static final int BUFFER_MAX_SIZE = 4 * 10 + 8;

    public CardData() {
        super(ByteBuffer.allocateDirect(BUFFER_MAX_SIZE));
    }

    public CardData(Buffer buffer) {
        super(buffer);
    }

    public int Code;
    public int Alias;
    public long Setcode;
    public int Type;
    public int Level;
    public int Attribute;
    public int Race;
    public int Attack;
    public int Defense;
    public int LScale;
    public int RScale;
}
