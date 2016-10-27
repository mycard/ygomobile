package cn.garymb.ygomobile.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.common.Constants;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.utils.IOUtils;

public class ResCheckTask extends AsyncTask<Void, Integer, Integer> implements BaseChecker.IMessage {
    private static final String TAG = "ResCheckTask";

    private GameSettings mApp;
    private Context mContext;
    private ResCheckListener mListener;
    private ProgressDialog dialog = null;
    private Handler handler;

    @SuppressWarnings("deprecation")
    public ResCheckTask(Context context, ResCheckListener listener) {
        mContext = context;
        mListener = listener;
        handler = new Handler(context.getMainLooper());
        mApp = GameSettings.get();
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

    @Override
    public void setMessage(String msg) {
        handler.post(() -> {
            dialog.setMessage(msg);
        });
    }

    @Override
    protected Integer doInBackground(Void... params) {
        CoreConfigChecker coreConfigChecker = new CoreConfigChecker(mContext, this);
        coreConfigChecker.start();
        if (coreConfigChecker.getError() != BaseChecker.ERROR_NONE) {
            return coreConfigChecker.getError();
        }
        boolean needsUpdate = coreConfigChecker.isNeedsUpdate();
        int errror = checkAndCopyNewDeckFiles(needsUpdate);
        if (errror != BaseChecker.ERROR_NONE) {
            return errror;
        }
        errror = checkAndCopyGameSkin();
        if (errror != BaseChecker.ERROR_NONE) {
            return errror;
        }
        errror = checkAndCopyExtraFiles();
        if (errror != BaseChecker.ERROR_NONE) {
            return errror;
        }
        errror = checkAndCopyFontsFiles();
        if (errror != BaseChecker.ERROR_NONE) {
            return errror;
        }
        errror = checkAndrCopyNewSingleFiles(needsUpdate);
        if (errror != BaseChecker.ERROR_NONE) {
            return errror;
        }
        errror = checkAndCopyScripts(needsUpdate);
        if (errror != BaseChecker.ERROR_NONE) {
            return errror;
        }
        errror = checkAndCopyDb(needsUpdate);
        if (errror != BaseChecker.ERROR_NONE) {
            return errror;
        }
        return BaseChecker.ERROR_NONE;
    }

    private int checkAndCopyDb(boolean isUpdateNeeded) {
        File scriptDir = new File(mApp.getResourcePath());
        File scriptFile = new File(scriptDir, GameSettings.DATABASE_NAME);
        if (!scriptDir.exists()) {
            scriptDir.mkdirs();
        }
        if (isUpdateNeeded || !scriptFile.exists()) {
            InputStream in = null;
            try {
                in = mContext.getAssets().open(Constants.ASSETS_PATH + GameSettings.DATABASE_NAME);
                IOUtils.copyToFile(in, scriptFile.getAbsolutePath());
            } catch (IOException e) {
                return BaseChecker.ERROR_COPY;
            }
        }
        return BaseChecker.ERROR_NONE;
    }

    private int checkAndCopyScripts(boolean isUpdateNeeded) {
        File scriptDir = new File(mApp.getResourcePath(), GameSettings.CORE_SCRIPTS_PATH);
        File scriptFile = new File(scriptDir, GameSettings.CORE_SCRIPTS_ZIP);
        if (!scriptDir.exists()) {
            scriptDir.mkdirs();
        }
        if (isUpdateNeeded || !scriptFile.exists()) {
            InputStream in = null;
            try {
                in = mContext.getAssets().open(Constants.ASSETS_PATH + GameSettings.CORE_SCRIPTS_ZIP);
                IOUtils.copyToFile(in, scriptFile.getAbsolutePath());
            } catch (IOException e) {
                return BaseChecker.ERROR_COPY;
            }
        }
        return BaseChecker.ERROR_NONE;
    }

    private int checkAndrCopyNewSingleFiles(boolean isUpdateNeeded) {
        return new FilesChecker(mContext, this, isUpdateNeeded)
                .setPath(
                        new File(mApp.getResourcePath(), GameSettings.CORE_SINGLE_PATH).getAbsolutePath(),
                        GameSettings.CORE_SINGLE_PATH
                ).start();
    }

    private int checkAndCopyNewDeckFiles(boolean isUpdateNeeded) {
        return new FilesChecker(mContext, this, isUpdateNeeded)
                .setPath(
                        new File(mApp.getResourcePath(), GameSettings.CORE_DECK_PATH).getAbsolutePath(),
                        GameSettings.CORE_DECK_PATH
                ).start();
    }

    private int checkAndCopyFontsFiles() {
        return new FilesChecker(mContext, this, false)
                .setPath(
                        new File(mApp.getResourcePath(), GameSettings.FONT_DIRECTORY).getAbsolutePath(),
                        GameSettings.FONT_DIRECTORY
                ).start();
    }

    private int checkAndCopyExtraFiles() {
        return new FilesChecker(mContext, this, false)
                .setPath(
                        new File(mApp.getResourcePath(), GameSettings.CORE_EXTRA_PATH).getAbsolutePath(),
                        GameSettings.CORE_EXTRA_PATH
                ).start();
    }

    private int checkAndCopyGameSkin() {
        return new FilesChecker(mContext, this, false)
                .setPath(
                        mApp.getCoreSkinPath(),
                        GameSettings.CORE_SKIN_PATH
                ).start();
    }

    public interface ResCheckListener {
        void onResCheckFinished(int result);
    }

}
