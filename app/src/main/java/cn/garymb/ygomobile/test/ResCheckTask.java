package cn.garymb.ygomobile.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.garymb.ygomobile.R;

public class ResCheckTask extends AsyncTask<Void, Integer, Integer> {
    private static final String TAG = "ResCheckTask";
    public static final int ERROR_NONE = 0;
    public static final int ERROR_CORE_CONFIG = -1;
    public static final int ERROR_COPY = -2;
    public static final int ERROR_CORE_CONFIG_LOST = -3;
    protected int mError = ERROR_NONE;
    private static final String ASSETS_PATH  = "data/";
    private GameSettings mSettings;
    private Context mContext;
    private ResCheckListener mListener;
    private ProgressDialog dialog = null;
    private Handler handler;

    @SuppressWarnings("deprecation")
    public ResCheckTask(Context context, ResCheckListener listener) {
        mContext = context;
        mListener = listener;
        handler = new Handler(context.getMainLooper());
        mSettings = GameSettings.get();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(mContext,
                mContext.getString(R.string.check_res),
                mContext.getString(R.string.checking_res));
    }

    @Override
    protected void onPostExecute(final Integer result) {
        super.onPostExecute(result);
        dialog.dismiss();
        if (mListener != null) {
            mListener.onResCheckFinished(result);
        }
    }

    private void setMessage(String msg) {
        handler.post(() -> {
            dialog.setMessage(msg);
        });
    }

    private String getDatapath(String path) {
        if(TextUtils.isEmpty(ASSETS_PATH)){
            return path;
        }
        if (path.startsWith(ASSETS_PATH)) {
            return path;
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return ASSETS_PATH + path;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Log.d(TAG, "check start");
        boolean needsUpdate = false;
        //core config
        setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.core_config)));
        String newConfigVersion = null, currentConfigVersion = null;
        File verPath = new File(mSettings.getResourcePath(), GameSettings.CORE_CONFIG_PATH);
        if (!verPath.exists() || TextUtils.isEmpty(currentConfigVersion = getCurVersion(verPath))) {
            //
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.core_config)));
            int error = copyCoreConfig(verPath.getAbsolutePath());
            if (error != ERROR_NONE) {
                return error;
            }
            needsUpdate = true;
            currentConfigVersion = getCurVersion(verPath);
            if (TextUtils.isEmpty(currentConfigVersion)) {
                Log.e(TAG, "check core config currentConfigVersion is null:" + verPath);
                return ERROR_CORE_CONFIG;
            }
        }
        //check core config

        try {
            newConfigVersion = mContext.getAssets().list(getDatapath(GameSettings.CORE_CONFIG_PATH))[0];
        } catch (IOException e) {
            Log.e(TAG, "check core config", e);
            return ERROR_CORE_CONFIG;
        }
        mSettings.setCoreConfigVersion(newConfigVersion);
        needsUpdate = !currentConfigVersion.equals(newConfigVersion);

        //res
        try {
            String resPath = mSettings.getResourcePath();
            IOUtils.createNoMedia(resPath);
            Log.d(TAG, "check new deck");
            copyCoreConfig(verPath.getAbsolutePath());
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.new_deck)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(GameSettings.CORE_DECK_PATH),
                    new File(resPath, GameSettings.CORE_SINGLE_PATH).getAbsolutePath(), needsUpdate);
            Log.d(TAG, "check game skin");
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.game_skins)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(GameSettings.CORE_SKIN_PATH),
                    mSettings.getCoreSkinPath(), needsUpdate);
            Log.d(TAG, "check fonts");
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.font_files)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(GameSettings.FONT_DIRECTORY),
                    new File(resPath, GameSettings.FONT_DIRECTORY).getAbsolutePath(), needsUpdate);
            Log.d(TAG, "check single");
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.single_lua)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(GameSettings.CORE_SINGLE_PATH),
                    new File(resPath, GameSettings.CORE_SINGLE_PATH).getAbsolutePath(), needsUpdate);
            Log.d(TAG, "check scripts");
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.scripts)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(GameSettings.CORE_SCRIPTS_ZIP),
                    new File(resPath, GameSettings.CORE_SCRIPTS_PATH).getAbsolutePath(), needsUpdate);
            Log.d(TAG, "check cdb");
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.cards_cdb)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(GameSettings.DATABASE_NAME),
                    resPath, needsUpdate);
        } catch (Exception e) {
            Log.e(TAG, "check", e);
            return ERROR_COPY;
        }
        return ERROR_NONE;
    }

    private String getCurVersion(File verPath) {
        if (!verPath.exists()) {
            Log.e(TAG, "check core config no exists " + verPath);
            return null;
        }
        String[] files = verPath.list();
        for (String file : files) {
            File f = new File(verPath, file);
            if (f.isDirectory()) {
                return f.getName();
            } else {
                Log.e(TAG, "check core config is file " + f.getAbsolutePath());
            }
        }
        return null;
    }

    private int copyCoreConfig(String toPath) {
        try {
            String path = getDatapath(GameSettings.CORE_CONFIG_PATH);

            int count = IOUtils.copyFilesFromAssets(mContext, path, toPath, true);
            if (count < 3) {
                return ERROR_CORE_CONFIG_LOST;
            }
            return ERROR_NONE;
        } catch (IOException e) {
            Log.e(TAG, "copy", e);
            mError = ERROR_COPY;
            return ERROR_COPY;
        }
    }

    public interface ResCheckListener {
        void onResCheckFinished(int result);
    }

}
