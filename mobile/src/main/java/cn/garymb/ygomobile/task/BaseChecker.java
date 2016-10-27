package cn.garymb.ygomobile.task;


import android.content.Context;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.common.Constants;

abstract class BaseChecker {
    protected Context mContext;
    protected GameSettings settings;
    public static final int ERROR_NONE = 0;
    public static final int ERROR_CORE_CONFIG = -1;
    public static final int ERROR_COPY = -2;
    protected int mError = ERROR_NONE;

    public interface IMessage {
        void setMessage(String msg);
    }

    protected String getDatapath(String path) {
        if(path.startsWith(Constants.ASSETS_PATH)){
            return path;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return Constants.ASSETS_PATH + path;
    }

    protected String reAssetName(String file) {
        int i = file.indexOf(Constants.ASSETS_PATH);
        if (i > 0) {
            return file.substring(i + Constants.ASSETS_PATH.length());
        }
        return file;
    }

    private IMessage message;
    protected boolean needUpdate;

    public BaseChecker(Context context, IMessage message) {
        this(context, message, false);
    }

    public BaseChecker(Context context, IMessage message, boolean update) {
        this.mContext = context;
        this.settings = GameSettings.get();
        needUpdate = update;
    }

    protected void setMessage(String msg) {
        if (message != null) {
            message.setMessage(msg);
        }
    }

    public int getError() {
        return mError;
    }

    public abstract int start();
}
