package cn.garymb.ygomobile.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.garymb.ygomobile.core.IrrlichtBridge;

public class BitmapUtils {

    public static Bitmap getBitmap(File zipFile, String id, int width, int height) {
        Bitmap bmp = null;
        try {
            ZipFile zf = new ZipFile(zipFile);
            boolean isbpg = false;
            ZipEntry ze = zf.getEntry("pics/" + id + ".bpg");
            if (ze == null) {
                ze = zf.getEntry("pics/" + id + ".jpg");
            } else {
                isbpg = true;
            }
            if (ze != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream inputStream = zf.getInputStream(ze);
                byte[] tmp = new byte[4096];
                int len;
                while ((len = inputStream.read(tmp)) != -1) {
                    outputStream.write(tmp, 0, len);
                }
                byte[] data = outputStream.toByteArray();
                if (isbpg) {
                    bmp = IrrlichtBridge.getBpgImage(data, width, height);
                } else {
                    android.graphics.BitmapFactory.Options options = new BitmapFactory.Options();
                    options.outWidth = width;
                    options.outHeight = height;
                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * Mix two Bitmap as one.
     *
     * where the second bitmap is painted.
     *
     * @return
     */
    public static Drawable mixtureBitmap(Context context, Bitmap first, Bitmap second, PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, 0, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        BitmapDrawable bitmapDrawable = new BitmapDrawable(context.getResources(), newBitmap);
        Drawable drawable = bitmapDrawable;
        return drawable;
    }

    public static int[] decodeImageSize(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDither = false;
        BitmapFactory.decodeFile(filePath, options);
        return new int[]{options.outWidth, options.outHeight};
    }

    public static Bitmap createNewBitmapWithResource(Resources res, int resID,
                                                     int wh[], boolean forceResize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 为true里只读图片的信息，如果长宽，返回的bitmap为null
        options.inDither = false;

        BitmapFactory.decodeResource(res, resID, options);
        int requestWidth = wh[0];
        int bmpWidth = options.outWidth;
        float inSampleSize = (float) bmpWidth / (float) requestWidth;
        if (inSampleSize > 1.0 && inSampleSize < 2.0) {
            options.inSampleSize = 2;// 设置缩放比例
        } else if (inSampleSize >= 2.0) {
            options.inSampleSize = (int) inSampleSize;
        }
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(res, resID, options);
        return bitmap;
    }

    public static Bitmap createNewBitmapAndCompressByFile(String filePath,
                                                          int wh[], boolean forceResize) {
        int offset = 100;
        File file = new File(filePath);
        long fileSize = 0;
        if (file.exists()) {
            fileSize = file.length();
            if (200 * 1024 < fileSize && fileSize <= 1024 * 1024)
                offset = 90;
            else if (1024 * 1024 < fileSize)
                offset = 85;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 为true里只读图片的信息，如果长宽，返回的bitmap为null
//		options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        options.inDither = false;

        BitmapFactory.decodeFile(filePath, options);
        Bitmap bitmap = decodeWithFixDimension(wh[0], wh[1], file, options, forceResize);
        if (bitmap != null) {
            bitmap = compressBitmapFile(offset, fileSize, bitmap);
        }
        return bitmap;
    }

    public static Bitmap createNewBitmapAndCompressByFile(String filePath,
                                                          int width, boolean forceResize) {
        return createNewBitmapAndCompressByFile(filePath, new int[]{width, -1}, forceResize);
    }

    private static Bitmap compressBitmapFile(int offset, long fileSize,
                                             Bitmap bitmap) {
        if (offset == 100)
            return bitmap;// 缩小质量
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, offset, baos);
        byte[] buffer = baos.toByteArray();
        if (buffer.length >= fileSize)
            return bitmap;
        return BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
    }

    private static Bitmap decodeWithFixDimension(int width, int height, File file,
                                                 BitmapFactory.Options options, boolean forceResize) {
        int requestWidth = width;
        int requestHeight = height;
        int bmpWidth = options.outWidth;
        float wScaleFactor = (float) bmpWidth / (float) requestWidth;
        if (wScaleFactor > 1.0 && wScaleFactor < 2.0) {
            options.inSampleSize = 2;// 设置缩放比例
        } else if (wScaleFactor >= 2.0) {
            options.inSampleSize = (int) wScaleFactor;
        }
        options.inJustDecodeBounds = false;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(is, null, options);
            if (bitmap != null) {
                float xScale = ((float) requestWidth / options.outWidth);
                float yScale = ((float) requestHeight / options.outHeight);
                Matrix matrix = new Matrix();
                matrix.setScale(xScale, yScale);
                //以下3种情况执行矩阵变换；
                //1.缩放
                //2.强制变换到所需大小（一般用于缩略图）
                //3.前面为了减少图片质量，将图片缩小到了所需size之下，需要变换放大来还原
                if (xScale < 1.0f && yScale < 1.0f || forceResize || (wScaleFactor > 1.0f && wScaleFactor < 2.0f)) {
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, options.outWidth, options.outHeight, matrix, true);
                }
                if (requestHeight != -1 && bitmap.getHeight() > requestHeight) {
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), requestHeight);
                }
            }
        } catch (OutOfMemoryError e) {
            System.gc();
            bitmap = null;
        }
        return bitmap;
    }

}
