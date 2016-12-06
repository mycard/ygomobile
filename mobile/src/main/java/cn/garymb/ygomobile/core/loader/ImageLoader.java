package cn.garymb.ygomobile.core.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapperResource;
import com.bumptech.glide.signature.StringSignature;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.core.IrrlichtBridge;
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.utils.BitmapUtil;
import cn.garymb.ygomobile.utils.IOUtils;

import static cn.garymb.ygomobile.Constants.CORE_SKIN_BG_SIZE;

public class ImageLoader {

    private static ImageLoader sImageLoader = new ImageLoader();
    private ZipFile mZipFile;
    private LruBitmapPool mLruBitmapPool;

    private ImageLoader() {
        mLruBitmapPool = new LruBitmapPool(100);
    }

    private class BpgResourceDecoder implements ResourceDecoder<ImageVideoWrapper, GifBitmapWrapper> {
        String id;

        private BpgResourceDecoder(String id) {
            this.id = id;
        }

        @Override
        public Resource<GifBitmapWrapper> decode(ImageVideoWrapper source, int width, int height) throws IOException {
//            Log.i("kk", "decode source:"+source);
            Bitmap bitmap = IrrlichtBridge.getBpgImage(source.getStream(), Bitmap.Config.RGB_565);
//            Log.i("kk", "decode bitmap:"+bitmap);
            BitmapResource resource = new BitmapResource(bitmap, mLruBitmapPool);
            return new GifBitmapWrapperResource(new GifBitmapWrapper(resource, null));
        }

        @Override
        public String getId() {
            return id;
        }
    }

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

    public void bind(Context context, byte[] data, ImageView imageview, boolean isbpg, long code, Drawable pre) {
        if (isbpg) {
            Glide.with(context).load(data).placeholder(pre).decoder(new BpgResourceDecoder("bpg@" + code)).into(imageview);
        } else {
            Glide.with(context).load(data).placeholder(pre).into(imageview);
        }
    }

    public void bind(Context context, final File file, ImageView imageview, boolean isbpg, long code, Drawable pre) {
        try {
            if (isbpg) {
                Glide.with(context).load(file).placeholder(pre).signature(new StringSignature(file.getName() + file.lastModified()))
                        .decoder(new BpgResourceDecoder("bpg@" + code)).into(imageview);
            } else {
                Glide.with(context).load(file).placeholder(pre).signature(new StringSignature(file.getName() + file.lastModified()))
                        .into(imageview);
            }
        } catch (Exception e) {
            Log.e("kk", "bind", e);
        }
    }

    public void bindImage(Context context, ImageView imageview, long code) {
        bindImage(context, imageview, code, null);
    }

    public void bindImage(Context context, ImageView imageview, long code, Drawable pre) {
        String name = Constants.CORE_IMAGE_PATH + "/" + code;
        String path = AppsSettings.get().getResourcePath();
        File zip = new File(path, Constants.CORE_PICS_ZIP);
        for (String ex : Constants.IMAGE_EX) {
            File file = new File(AppsSettings.get().getResourcePath(), name + ex);
            if (!file.exists()) {
                file = new File(context.getCacheDir(), name + ex);
            }
            if (file.exists()) {
                bind(context, file, imageview, Constants.BPG.equals(ex), code, pre);
                return;
            }
        }
        if (zip.exists()) {
            ZipEntry entry = null;
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            try {
                if (mZipFile == null) {
                    mZipFile = new ZipFile(zip);
                }
                for (String ex : Constants.IMAGE_EX) {
                    entry = mZipFile.getEntry(name + ex);
                    if (entry != null) {
                        inputStream = mZipFile.getInputStream(entry);
                        outputStream = new ByteArrayOutputStream();
                        IOUtils.copy(inputStream, outputStream);
                        bind(context, outputStream.toByteArray(), imageview, Constants.BPG.equals(ex), code, pre);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
