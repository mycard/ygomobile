package cn.garymb.ygomobile.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.garymb.ygomobile.App;
import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.garymb.ygomobile.utils.BitmapUtil;
import cn.garymb.ygomobile.utils.IOUtils;

import static cn.garymb.ygomobile.Constants.CORE_SKIN_BG_SIZE;

public class ImageLoader {

    private static ImageLoader sImageLoader = new ImageLoader();
    private ZipFile mZipFile;

    public static ImageLoader get() {
        return sImageLoader;
    }

    private Bitmap loadImage(String path, int w, int h) {
        File file = new File(path);
        if (file.exists()) {
            return BitmapUtil.getBitmapFromFile(file.getAbsolutePath(), CORE_SKIN_BG_SIZE[0], CORE_SKIN_BG_SIZE[1]);
        }
        return null;
    }

    public void bindImage(Context context, ImageView imageview, long code) {
        String name = Constants.CORE_IMAGE_PATH + "/" + code;
        String path = AppsSettings.get().getResourcePath();
        File zip = new File(path, Constants.CORE_PICS_ZIP);
        for (String ex : Constants.IMAGE_EX) {
            File file = new File(AppsSettings.get().getResourcePath(), name + ex);
            if (file.exists()) {
                Glide.with(context).load(file).into(imageview);
                return;
            }
            file = new File(context.getCacheDir(), name + ex);
            if (file.exists()) {
                Glide.with(context).load(file).into(imageview);
                return;
            }
        }
        if (zip.exists()) {
            ZipEntry entry = null;
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream=null;
            try {
                if (mZipFile == null) {
                    mZipFile = new ZipFile(zip);
                }
                for (String ex : Constants.IMAGE_EX) {
                    entry = mZipFile.getEntry(name + ex);
                    if (entry != null) {
                        inputStream = mZipFile.getInputStream(entry);
                        outputStream = new ByteArrayOutputStream();
//                        cache = new File(context.getCacheDir(), name + ex);
//                        IOUtils.createFolder(cache);
//                        if (!cache.exists()) {
//                            cache.createNewFile();
//                        }
//                        outputStream = new FileOutputStream(cache);
                        IOUtils.copy(inputStream, outputStream);
                        Glide.with(context).load(outputStream.toByteArray()).into(imageview);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                IOUtils.close(inputStream);
            }
//            File cache = null;
//            ZipFile zipFile = null;
//            String file = zip.getAbsolutePath();
//            InputStream inputStream = null;
//            OutputStream outputStream = null;
//            try {
//                zipFile = new ZipFile(file);
//                ZipEntry entry = null;
//                for (String ex : Constants.IMAGE_EX) {
//                    entry = zipFile.getEntry(name + ex);
//                    if (entry != null) {
//                        inputStream = zipFile.getInputStream(entry);
//                        cache = new File(context.getCacheDir(), name + ex);
//                        IOUtils.createFolder(cache);
//                        if (!cache.exists()) {
//                            cache.createNewFile();
//                        }
//                        outputStream = new FileOutputStream(cache);
//                        IOUtils.copy(inputStream, outputStream);
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                IOUtils.close(outputStream);
//                IOUtils.close(inputStream);
//                IOUtils.closeZip(zipFile);
//            }
//            if (cache == null) {
//                cache = new File(AppsSettings.get().getCoreSkinPath(), Constants.CORE_SKIN_COVER);
//            }
//            Glide.with(context).load(cache).into(imageview);
        }
    }
}
