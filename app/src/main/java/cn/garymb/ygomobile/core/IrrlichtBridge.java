/*
 * IrrlichtBridge.java
 *
 *  Created on: 2014年3月18日
 *      Author: mabin
 */
package cn.garymb.ygomobile.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author mabin
 */
public final class IrrlichtBridge {
    static {
        System.loadLibrary("YGOMobile");
    }

    public interface IrrlichtApplication {
        String getCardImagePath();

        void setLastDeck(String name);

        String getFontPath();

        String getLastDeck();

        float getScreenWidth();

        float getScreenHeight();

        void playSoundEffect(String path);
    }

    public interface IrrlichtHost {
        void toggleOverlayView(boolean isShow);

        ByteBuffer getInitOptions();

        ByteBuffer getNativeInitOptions();

        void toggleIME(String hint, boolean isShow);

        void showComboBoxCompat(String[] items, boolean isShow, int mode);

        void performHapticFeedback();

        /** 签名 */
        byte[] performTrick();

        int getLocalAddress();

        void setNativeHandle(int nativeHandle);
    }

    public static int sNativeHandle;

    private static native byte[] nativeBpgImage(byte[] data);

    private static native void nativeInsertText(int handle, String text);

    private static native void nativeRefreshTexture(int handle);

    private static native void nativeIgnoreChain(int handle, boolean begin);

    private static native void nativeReactChain(int handle, boolean begin);

    private static native void nativeCancelChain(int handle);

    private static native void nativeSetCheckBoxesSelection(int handle, int idx);

    private static native void nativeSetComboBoxSelection(int handle, int idx);

    private static native void nativeJoinGame(int handle, ByteBuffer buffer, int length);

    public static native String getAccessKey();

    public static native String getSecretKey();

    public static void cancelChain() {
        nativeCancelChain(sNativeHandle);
    }

    public static void ignoreChain(boolean begin) {
        nativeIgnoreChain(sNativeHandle, begin);
    }

    public static void reactChain(boolean begin) {
        nativeReactChain(sNativeHandle, begin);
    }

    public static void insertText(String text) {
        nativeInsertText(sNativeHandle, text);
    }

    private static int byte2int(byte[] res) {
        String str = String.format("%02x%02x%02x%02x", res[3], res[2], res[1], res[0]);
        int rs= Integer.parseInt(str, 16);
        return rs;
    }

    public static Bitmap getBpgImage(byte[] bpg, int width, int height) {
        try {
            byte[] data = nativeBpgImage(bpg);
            int start = 8;
//            String header = String.format("%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
//                    data[0], data[1], data[2], data[3],
//                    data[4], data[5], data[6], data[7],
//                    data[8], data[9], data[10], data[11],
//                    data[12], data[13], data[14], data[15]);
//            int head = byte2int(Arrays.copyOfRange(data, start, start + 4));
            int w = byte2int(Arrays.copyOfRange(data, 0, 4));
            int h = byte2int(Arrays.copyOfRange(data, 4, 8));
            int prefix = start;
            int len = data.length - prefix;
            int[] colors = new int[len / 3];
            for (int i = 0; i < colors.length; i++) {
//                colors[i] = Color.rgb(data[prefix + i * 3 + 0], data[prefix + i * 3 + 1], data[prefix + i * 3 +2]);
                int index = prefix + i * 3;
                colors[i] = Color.rgb(data[index + 0], data[index + 1], data[index +2]);
            }
            return Bitmap.createBitmap(colors, w, h, Bitmap.Config.RGB_565);
        } catch (Throwable e) {
            Log.e("kk", "zip image", e);
            return null;
        }
    }

    public static void setComboBoxSelection(int idx) {
        nativeSetComboBoxSelection(sNativeHandle, idx);
    }

    public static void refreshTexture() {
        nativeRefreshTexture(sNativeHandle);
    }

    public static void setCheckBoxesSelection(int idx) {
        nativeSetCheckBoxesSelection(sNativeHandle, idx);
    }

    public static void joinGame(ByteBuffer buffer, int length) {
        nativeJoinGame(sNativeHandle, buffer, length);
    }
}
