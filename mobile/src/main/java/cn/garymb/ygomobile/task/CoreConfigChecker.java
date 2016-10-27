package cn.garymb.ygomobile.task;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.utils.IOUtils;

class CoreConfigChecker extends BaseChecker {
    private boolean needsUpdate;

    public CoreConfigChecker(Context context, IMessage message) {
        super(context, message);
    }

    @Override
    public int start() {
        String newConfigVersion = null, currentConfigVersion = null;
        File verPath = new File(settings.getResourcePath(), GameSettings.CORE_CONFIG_PATH);
        IOUtils.createNoMedia(settings.getResourcePath());
        if (!verPath.exists() || TextUtils.isEmpty(currentConfigVersion = getCurVersion(verPath))) {
            //
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.core_config)));
            int error = copyCoreConfig(settings.getResourcePath());
            if (error != ERROR_NONE) {
                return error;
            }
            needsUpdate = true;
            currentConfigVersion = getCurVersion(verPath);
            if (TextUtils.isEmpty(currentConfigVersion)) {
                return ERROR_CORE_CONFIG;
            }
        }
        //check core config

        try {
            newConfigVersion = mContext.getAssets().list(getDatapath(GameSettings.CORE_CONFIG_PATH))[0];
        } catch (IOException e) {
            return ERROR_CORE_CONFIG;
        }
        settings.setCoreConfigVersion(newConfigVersion);
        needsUpdate = !currentConfigVersion.equals(newConfigVersion);
        return ERROR_NONE;
    }

    private String getCurVersion(File verPath) {
        if (!verPath.exists()) {
            return null;
        }
        String[] files = verPath.list();
        if (files == null || files.length == 0) {
            return null;
        }
        for (String file : files) {
            File f = new File(file);
            if (f.isDirectory()) {
                return f.getName();
            }
        }
        return null;
    }

    public boolean isNeedsUpdate() {
        return needsUpdate;
    }

    private int copyCoreConfig(String toPath) {
        try {
            String[] files = mContext.getAssets().list(getDatapath(GameSettings.CORE_CONFIG_PATH));
            File dir = new File(toPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            for (String file : files) {
                String name = reAssetName(file);
                File f = new File(dir, name);
                dir = f.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Log.i(TAG, "copy:assets/" + file + "-->" + f);
                IOUtils.copyToFile(mContext.getAssets().open(file), f.getAbsolutePath());
            }
            return ERROR_NONE;
        } catch (IOException e) {
            Log.e(TAG, "copy", e);
            mError = ERROR_COPY;
            return ERROR_COPY;
        }
    }

}
