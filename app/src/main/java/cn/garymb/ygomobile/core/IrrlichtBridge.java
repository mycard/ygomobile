/*
 * IrrlichtBridge.java
 *
 *  Created on: 2014年3月18日
 *      Author: mabin
 */
package cn.garymb.ygomobile.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

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
        int rs = Integer.parseInt(str, 16);
        return rs;
    }

    private static int str2byte(String str) {
//        System.out.println(str);
        return Integer.parseInt(str, 16);
    }

    private static byte[] short2byte(short i) {
        String str = String.format("%04x", i);
        return new byte[]{
                (byte) str2byte(str.substring(2, 4)),
                (byte) str2byte(str.substring(0, 2)),
        };
    }

    private static byte[] int2byte(int i) {
        String str = String.format("%08x", i);
//        System.out.println(str);
        return new byte[]{
                (byte) str2byte(str.substring(6, 8)),
                (byte) str2byte(str.substring(4, 6)),
                (byte) str2byte(str.substring(2, 4)),
                (byte) str2byte(str.substring(0, 2)),
        };
    }

    public static void reverse(byte[] data) {
        int size = data.length;
        for (int i = 0; i < size / 2; i++) {
            byte tmp = data[i];
            data[i] = data[size - i - 1];
            data[size - i - 1] = tmp;
        }
    }

    public static Bitmap getBpgImage(byte[] bpg, String id) {
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
            int padding = (data.length - start) / 4;
            padding = (data.length - start) - (padding * 4);
            Log.i("kk", "zip image:w=" + w + ",h=" + h + ",padding=" + padding + ",size=" + (data.length - start + padding));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //head
            outputStream.write((byte) 0x42);
            outputStream.write((byte) 0x4d);
            outputStream.write(int2byte(14 + 40 + data.length - start + padding));//BMP图像文件的大小
            outputStream.write((byte) 0);
            outputStream.write((byte) 0);
            outputStream.write((byte) 0);
            outputStream.write((byte) 0);
            outputStream.write(int2byte(14 + 40));//BMP图像数据的地址
            //info
            outputStream.write(int2byte(40));//本结构的大小
            outputStream.write(int2byte(w));//BMP图像的宽度，单位像素
            outputStream.write(int2byte(h));//总为0
            outputStream.write((byte) 1);//biPlanes
            outputStream.write((byte) 0);
            outputStream.write((byte) 0x18);//biBitCount
            outputStream.write((byte) 0);//16
            outputStream.write(int2byte(0));//biCompression
            //文件大小
            outputStream.write(int2byte(data.length - start + padding));//biSizeImage 4的倍数，不足则补0
            outputStream.write(int2byte(0));//水平分辨率
            outputStream.write(int2byte(0));//垂直分辨率
            outputStream.write(int2byte(0));//BMP图像使用的颜色，0表示使用全部颜色，对于256色位图来说，此值为100h=256
            outputStream.write(int2byte(0));//重要的颜色数，此值为0时所有颜色都重要
//            reverse(data);
            //每一行的字节数必须是4的整数倍，如果不是，则需要补齐。
            outputStream.write(data, 8, data.length-8);
//            for (int i = 0; i < padding; i++) {
//                outputStream.write((byte) 0);//16
//            }
            byte[] img = outputStream.toByteArray();
//            FileOpsUtils.saveAsFile(img, new File(StaticApplication.get().getResourcePath(), id+".bmp").getAbsolutePath());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outWidth = w;
            options.outHeight = h;
            return BitmapFactory.decodeByteArray(img, 0, outputStream.size(), options);
//            int start = 8;
//            String header = String.format("%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
//                    data[0], data[1], data[2], data[3],
//                    data[4], data[5], data[6], data[7],
//                    data[8], data[9], data[10], data[11],
//                    data[12], data[13], data[14], data[15]);
//            int head = byte2int(Arrays.copyOfRange(data, start, start + 4));
//            int w = byte2int(Arrays.copyOfRange(data, 0, 4));
//            int h = byte2int(Arrays.copyOfRange(data, 4, 8));
//            int prefix = start;
//            int len = data.length - prefix;
//            int[] colors = new int[len / 3];
//            for (int i = 0; i < colors.length; i++) {
////                colors[i] = Color.rgb(data[prefix + i * 3 + 0], data[prefix + i * 3 + 1], data[prefix + i * 3 +2]);
//                int index = prefix + i * 3;
//                colors[i] = Color.rgb(data[index + 0], data[index + 1], data[index +2]);
//            }
//            return Bitmap.createBitmap(colors, w, h, Bitmap.Config.RGB_565);
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
