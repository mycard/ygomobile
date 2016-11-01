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
}
