package cn.garymb.ygomobile.task;


import android.content.Context;
import android.util.Log;

import java.io.File;

import cn.garymb.ygomobile.utils.IOUtils;

class FilesChecker extends BaseChecker {
    private String toPath, assetsPath;

    public FilesChecker(Context context, IMessage message, boolean update) {
        super(context, message, update);
    }

    public FilesChecker setPath(String toPath, String assetsPath) {
        this.toPath = toPath;
        this.assetsPath = getDatapath(assetsPath);
        return this;
    }

    @Override
    public int start() {
        File deckDir = new File(toPath);
        if (!deckDir.exists()) {
            deckDir.mkdirs();
        }
        File dir = new File(toPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            String[] files = mContext.getAssets().list(assetsPath);
            if (files != null) {
                for (String file : files) {
                    String name = reAssetName(file);
                    File f = new File(dir, name);
                    if (!needUpdate && f.exists()) {
                        continue;
                    }
                    dir = f.getParentFile();
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    Log.i(TAG, "copy:assets/" + file + "-->" + f);
                    IOUtils.copyToFile(mContext.getAssets().open(file), f.getAbsolutePath());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "copy", e);
            return ERROR_COPY;
        }
        return ERROR_NONE;
    }
}
