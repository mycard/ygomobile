package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.graphics.Bitmap;

import cn.garymb.ygomobile.utils.BitmapUtil;

import static cn.garymb.ygomobile.Constants.ASSET_LIMIT_PNG;

class ImageTop {
    final Bitmap forbidden;
    final Bitmap limit;
    final Bitmap semiLimit;

    ImageTop(Context context) {
        this(BitmapUtil.getBitmapFormAssets(context, ASSET_LIMIT_PNG, 0, 0));
    }

    ImageTop(Bitmap img) {
        if (img != null) {
            int width = img.getWidth();
            int height = img.getHeight();
            forbidden = Bitmap.createBitmap(img, 0, 0, width / 2, height / 2);
            limit = Bitmap.createBitmap(img, width / 2, 0, width / 2, height / 2);
            semiLimit = Bitmap.createBitmap(img, 0, height / 2, width / 2, height / 2);
        } else {
            forbidden = null;
            limit = null;
            semiLimit = null;
        }
        BitmapUtil.destroy(img);
    }

    void clear() {
        BitmapUtil.destroy(forbidden);
        BitmapUtil.destroy(limit);
        BitmapUtil.destroy(semiLimit);
    }
}
