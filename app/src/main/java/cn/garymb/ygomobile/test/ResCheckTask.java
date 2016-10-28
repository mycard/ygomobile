package cn.garymb.ygomobile.test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.R;
import cn.garymb.ygomobile.StaticApplication;
import cn.garymb.ygomobile.common.Constants;
import cn.garymb.ygomobile.common.FileDownloadHelper;
import cn.garymb.ygomobile.controller.Controller;
import cn.garymb.ygomobile.core.BaseTask.TaskStatusCallback;
import cn.garymb.ygomobile.core.SimpleDownloadTask;
import cn.garymb.ygomobile.data.wrapper.IBaseJob;
import cn.garymb.ygomobile.data.wrapper.SimpleDownloadJob;
import cn.garymb.ygomobile.model.data.ResourcesConstants;
import cn.garymb.ygomobile.setting.Settings;
import cn.garymb.ygomobile.utils.DatabaseUtils;
import cn.garymb.ygomobile.utils.FileOpsUtils;
import cn.garymb.ygomobile.widget.ProgressUpdateDialog;
import cn.garymb.ygomobile.widget.ProgressUpdateDialogController;
import de.greenrobot.event.EventBus;

public class ResCheckTask extends AsyncTask<Void, Integer, Integer> {

    public static final int RES_ERR_NO_FONT_AVAIL = -1;

    private static final int RES_CHECK_TYPE_MESSAGE_UPDATE = 1;
    private static final int RES_CHECK_TYPE_PROGRESS_UPDATE = 2;
    private static final int RES_CHECK_TYPE_DOWNLOAD_HINT = 3;

    public interface ResCheckListener {
        void onResCheckFinished(int result);
    }

    private static final int CORE_CONFIG_COPY_COUNT = 3;
    private static final int ASSET_EXTRA_VERSION = 2;

    private static final String TAG = "ResCheckTask";
    private Activity mContext;

    private SharedPreferences mSettingsPref;

    private ProgressDialog mWaitDialog;
    private ProgressUpdateDialog mProgressUpdateDialog;

    private ResCheckListener mListener;

    private GameSettings mApp;

    public ResCheckTask(Activity context) {
        this(context, null);
    }

    public ResCheckTask(Activity context, ResCheckListener listener) {
        mContext = context;
        mApp = GameSettings.get();
        mSettingsPref = PreferenceManager.getDefaultSharedPreferences(context);
        View content = mContext.getLayoutInflater().inflate(R.layout.image_dl_dialog, null);
        mProgressUpdateDialog = new ProgressUpdateDialog(mContext, null, content, null);
        mWaitDialog = new ProgressDialog(context);
        mWaitDialog.setMessage(mContext.getString(R.string.checking_resource));
        mWaitDialog.setCancelable(false);
        mProgressUpdateDialog.setCancelable(false);
        setResCheckListener(listener);
    }

    public void setResCheckListener(ResCheckListener listener) {
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mWaitDialog.show();
    }

    @Override
    protected void onPostExecute(final Integer result) {
        super.onPostExecute(result);
        mWaitDialog.dismiss();
        if (mListener != null) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mListener.onResCheckFinished(result);
                    mListener = null;
                }
            });
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        String newConfigVersion = null, currentConfigVersion = null;
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_FILE_COMMON, Context.MODE_PRIVATE);
        currentConfigVersion = sp.getString(Constants.PREF_KEY_DATA_VERSION, "");
        try {
            newConfigVersion = mContext.getAssets().list(Constants.CORE_CONFIG_PATH)[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean needsUpdate = !currentConfigVersion.equals(newConfigVersion);
        publishProgress(RES_CHECK_TYPE_MESSAGE_UPDATE, R.string.updating_fonts);
////        initFontList(mApp.getFontDirPath());
        saveCoreConfigVersion(newConfigVersion);
        publishProgress(RES_CHECK_TYPE_MESSAGE_UPDATE, R.string.updating_decks);
//        checkAndCopyNewDeckFiles(needsUpdate);
        publishProgress(RES_CHECK_TYPE_MESSAGE_UPDATE, R.string.updating_config);
//        checkAndCopyCoreConfig(needsUpdate);
        publishProgress(RES_CHECK_TYPE_MESSAGE_UPDATE, R.string.updating_skin);
//        checkAndCopyGameSkin(mApp.getCoreSkinPath());
        publishProgress(RES_CHECK_TYPE_MESSAGE_UPDATE, R.string.updating_single);
//        checkAndrCopyNewSingleFiles(needsUpdate);
        publishProgress(R.string.updating_card_data_base);
        DatabaseUtils.checkAndCopyFromInternalDatabase(mContext, mApp.getDataBasePath(), needsUpdate);
        publishProgress(RES_CHECK_TYPE_MESSAGE_UPDATE, R.string.updating_scripts);
//        checkAndCopyScripts(needsUpdate);
        publishProgress(RES_CHECK_TYPE_MESSAGE_UPDATE, R.string.updating_dirs);
        checkDirs();
        return 0;
    }

    private void checkDirs() {
        String[] dirs = {"script", "single", "deck", "replay", "fonts"};
        File dirFile = null;
        for (String dir : dirs) {
            dirFile = new File(mApp.getResourcePath(), dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        }

    }

    private void checkAndCopyScripts(boolean isUpdateNeeded) {
        File scriptDir = new File(mApp.getResourcePath(), "/scripts");
        File scriptFile = new File(scriptDir, "main.zip");
        if (!scriptDir.exists()) {
            scriptDir.mkdirs();
        }
        if (isUpdateNeeded || !scriptFile.exists()) {
            int assetcopycount = 0;
            while (assetcopycount++ < CORE_CONFIG_COPY_COUNT) {
                try {
                    FileOpsUtils.assetsCopy(mContext, "main.zip", scriptFile.toString(), false);
                } catch (IOException e) {
                    Log.w(TAG, "copy scripts failed, retry count = " + assetcopycount);
                    continue;
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (values[0] == RES_CHECK_TYPE_MESSAGE_UPDATE) {
            mWaitDialog.setMessage(mContext.getString(values[1]));
        } else if (values[0] == RES_CHECK_TYPE_PROGRESS_UPDATE) {
            if (values[1] == 1) {
                mWaitDialog.dismiss();
                if (!mProgressUpdateDialog.isShowing()) {
                    mProgressUpdateDialog.show();
                }
            } else {
                if (mProgressUpdateDialog != null) {
                    mProgressUpdateDialog.dismiss();
                }
                mWaitDialog.show();
            }
        } else if (values[0] == RES_CHECK_TYPE_DOWNLOAD_HINT) {
            Toast.makeText(mContext, R.string.font_download_via_mobile_not_allowed, Toast.LENGTH_SHORT).show();
        }
    }


    private void checkAndrCopyNewSingleFiles(boolean isUpdateNeeded) {
        File deckDir = new File(mApp.getResourcePath(), Constants.CORE_SINGLE_PATH);
        if (!deckDir.exists()) {
            deckDir.mkdirs();
        }
        if (isUpdateNeeded) {
            int assetcopycount = 0;
            while (assetcopycount++ < CORE_CONFIG_COPY_COUNT) {
                try {
                    FileOpsUtils.assetsCopy(mContext, Constants.CORE_SINGLE_PATH, deckDir.toString(), false);
                } catch (IOException e) {
                    Log.w(TAG, "copy single files failed, retry count = " + assetcopycount);
                    continue;
                }
            }
        }

    }

    private void checkAndCopyNewDeckFiles(boolean isUpdateNeeded) {
        File deckDir = new File(mApp.getResourcePath(), Constants.CORE_DECK_PATH);
        if (!deckDir.exists()) {
            deckDir.mkdirs();
        }
        if (isUpdateNeeded) {
            int assetcopycount = 0;
            while (assetcopycount++ < CORE_CONFIG_COPY_COUNT) {
                try {
                    FileOpsUtils.assetsCopy(mContext, Constants.CORE_DECK_PATH, deckDir.toString(), false);
                } catch (IOException e) {
                    Log.w(TAG, "copy deck files failed, retry count = " + assetcopycount);
                    continue;
                }
            }
        }

    }

    private void saveCoreConfigVersion(String version) {
        SharedPreferences sp = mContext.getSharedPreferences(Constants.PREF_FILE_COMMON, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.PREF_KEY_DATA_VERSION, version);
        editor.commit();
        mApp.setCoreConfigVersion(version);
    }

    private void checkAndCopyCoreConfig(boolean needsUpdate) {
        File internalCacheDir = new File(mApp.getResourcePath());
        if (internalCacheDir != null) {
            File coreConfigDir = new File(internalCacheDir, Constants.CORE_CONFIG_PATH);
            if (coreConfigDir != null && coreConfigDir.exists() && coreConfigDir.isDirectory() && !needsUpdate) {
                return;
            }
            if (needsUpdate || (coreConfigDir != null && coreConfigDir.exists() && !coreConfigDir.isDirectory())) {
                coreConfigDir.delete();
            }
            // we need to copy from configs from assets;
            int assetcopycount = 0;
            while (assetcopycount++ < CORE_CONFIG_COPY_COUNT) {
                try {
                    FileOpsUtils.assetsCopy(mContext, Constants.CORE_CONFIG_PATH, coreConfigDir.getAbsolutePath(), false);
                    break;
                } catch (IOException e) {
                    Log.w(TAG, "copy core config failed, retry count = " + assetcopycount);
                    continue;
                }
            }
        }
    }

    private void checkAndCopyGameSkin(String path) {
        File coreSkinDir = new File(path);
        if (coreSkinDir != null && coreSkinDir.exists() && coreSkinDir.isDirectory()) {
            return;
        }
        if (coreSkinDir != null && coreSkinDir.exists() && !coreSkinDir.isDirectory()) {
            coreSkinDir.delete();
        }
        // we need to copy from configs from assets;
        int assetcopycount = 0;
        while (assetcopycount++ < CORE_CONFIG_COPY_COUNT) {
            try {
                FileOpsUtils.assetsCopy(mContext, Constants.CORE_SKIN_PATH, coreSkinDir.getAbsolutePath(), false);
                break;
            } catch (IOException e) {
                Log.w(TAG, "copy core skin failed, retry count = " + assetcopycount);
                continue;
            }
        }
    }

}
