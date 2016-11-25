package cn.garymb.ygomobile.core;

import android.graphics.Bitmap;

import java.io.File;

import cn.garymb.ygomobile.utils.BitmapUtil;

import static cn.garymb.ygomobile.Constants.CORE_SKIN_BG_SIZE;

public class ImageLoader {

    public static Bitmap loadImage(String path, int w, int h){
        File file = new File(path);
        if(file.exists()){
            return BitmapUtil.getBitmapFromFile(file.getAbsolutePath(), CORE_SKIN_BG_SIZE[0], CORE_SKIN_BG_SIZE[1]);
        }
        return null;
    }
}
