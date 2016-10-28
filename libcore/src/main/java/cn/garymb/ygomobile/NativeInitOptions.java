package cn.garymb.ygomobile;

import android.text.TextUtils;

import java.nio.ByteBuffer;

public final class NativeInitOptions {

    private static final int BUFFER_MAX_SIZE = 8192;

    public int mOpenglVersion;
    public boolean mIsSoundEffectEnabled;

    public String mCacheDir;

    /** 数据库 ${gamapath}/cards.cdb*/
    public String mDBDir;

    /** 核心版本 ${gamapath}/${coreversion}/config/*.conf*/
    public String mCoreConfigVersion;

    public String mResourcePath;

    public String mExternalFilePath;

    public int mCardQuality;

    public boolean mIsFontAntiAliasEnabled;

    public boolean mIsPendulumScaleEnabled;

    public NativeInitOptions() {

    }

    public ByteBuffer toNativeBuffer() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_MAX_SIZE);
        putInt(buffer, mOpenglVersion);
        putInt(buffer, mIsSoundEffectEnabled ? 1 : 0);
        putString(buffer, mCacheDir);
        putString(buffer, mDBDir);
        putString(buffer, mCoreConfigVersion);
        putString(buffer, mResourcePath);
        putString(buffer, mExternalFilePath);
        putInt(buffer, mCardQuality);
        putInt(buffer, mIsFontAntiAliasEnabled ? 1 : 0);
        putInt(buffer, mIsPendulumScaleEnabled ? 1 : 0);
        return buffer;
    }

    @Override
    public String toString() {
        return "NativeInitOptions{" +
                "mOpenglVersion=" + mOpenglVersion +
                ", mIsSoundEffectEnabled=" + mIsSoundEffectEnabled +
                ", mCacheDir='" + mCacheDir + '\'' +
                ", mDBDir='" + mDBDir + '\'' +
                ", mCoreConfigVersion='" + mCoreConfigVersion + '\'' +
                ", mResourcePath='" + mResourcePath + '\'' +
                ", mExternalFilePath='" + mExternalFilePath + '\'' +
                ", mCardQuality=" + mCardQuality +
                ", mIsFontAntiAliasEnabled=" + mIsFontAntiAliasEnabled +
                ", mIsPendulumScaleEnabled=" + mIsPendulumScaleEnabled +
                '}';
    }

    private void putString(ByteBuffer buffer, String str) {
        if (TextUtils.isEmpty(str)) {
            buffer.putInt(Integer.reverseBytes(0));
        } else {
            buffer.putInt(Integer.reverseBytes(str.getBytes().length));
            buffer.put(str.getBytes());
        }
    }

    @SuppressWarnings("unused")
    private void putChar(ByteBuffer buffer, char value) {
        Short svalue = (short) value;
        buffer.putShort((Short.reverseBytes(svalue)));
    }

    private void putInt(ByteBuffer buffer, int value) {
        buffer.putInt((Integer.reverseBytes(value)));
    }
}
