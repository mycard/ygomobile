package cn.garymb.ygomobile.core.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
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
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.core.IrrlichtBridge;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.utils.BitmapUtil;
import cn.garymb.ygomobile.utils.IOUtils;

import static cn.garymb.ygomobile.Constants.CORE_SKIN_BG_SIZE;
import static com.bumptech.glide.Glide.with;

public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();
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
        DrawableTypeRequest<byte[]> resource = with(context).load(data);
        if (pre != null) {
            resource.placeholder(pre);
        } else {
            resource.placeholder(R.drawable.unknown);
        }
        if (isbpg) {
            resource.decoder(new BpgResourceDecoder("bpg@" + code));
        }
        resource.into(imageview);
    }

    public void bind(Context context, final File file, ImageView imageview, boolean isbpg, long code, Drawable pre) {
        try {
            DrawableTypeRequest<File> resource = with(context).load(file);
            if (pre != null) {
                resource.placeholder(pre);
            } else {
                resource.placeholder(R.drawable.unknown);
            }
            resource.signature(new StringSignature(file.getName() + file.lastModified()));
            if (isbpg) {
                resource.decoder(new BpgResourceDecoder("bpg@" + code));
            }

            resource.into(imageview);
        } catch (Exception e) {
            Log.e(TAG, "bind", e);
        }
    }

    public void bind(Context context, final String url, ImageView imageview, long code, Drawable pre) {
        DrawableTypeRequest<Uri> resource = with(context).load(Uri.parse(url));
        if (pre != null) {
            resource.placeholder(pre);
        } else {
            resource.placeholder(R.drawable.unknown);
        }
//        File file=new File(AppsSettings.get().getResourcePath(), Constants.CORE_IMAGE_PATH+"/"+code+".jpg");
        resource.into(imageview);
    }

    public void bindImage(Context context, ImageView imageview, long code) {
        bindImage(context, imageview, code, null);
    }


    public void bindImage(Context context, ImageView imageview, long code, Drawable pre) {
        String name = Constants.CORE_IMAGE_PATH + "/" + code;
        String path = AppsSettings.get().getResourcePath();
        boolean bind = false;
        File zip = new File(path, Constants.CORE_PICS_ZIP);
        for (String ex : Constants.IMAGE_EX) {
            File file = new File(AppsSettings.get().getResourcePath(), name + ex);
            if (!file.exists()) {
                file = new File(context.getCacheDir(), name + ex);
            }
            if (file.exists()) {
                bind(context, file, imageview, Constants.BPG.equals(ex), code, pre);
                bind = true;
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
                        bind = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(inputStream);
            }
        }
        if (!bind) {
            bind(context, String.format(Constants.IMAGE_URL, "" + code), imageview, code, pre);
        }
    }
}
