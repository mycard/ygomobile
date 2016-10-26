package cn.garymb.ygomobile.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.garymb.ygomobile.GameSettings;
import cn.garymb.ygomobile.utils.DatabaseUtils;
import cn.garymb.ygomobile.utils.FileOpsUtils;

public class ResCheckTask extends AsyncTask<Void, Integer, Integer> {
	public interface CallBack{
		void onFinish();
	}
	private final String SYSTEM_FONT_DIR = GameSettings.get().getFontDirPath();
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
	private GameSettings mApp;
	private Activity mContext;

	private SharedPreferences mSettingsPref;

	private ResCheckListener mListener;

	private Object mLock = new Object();

	public ResCheckTask(Activity context) {
		mContext = context;
		mApp = GameSettings.get();
		mSettingsPref = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void setResCheckListener(ResCheckListener listener) {
		mListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(final Integer result) {
		super.onPostExecute(result);
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
		initFontList();
		mApp.setCoreConfigVersion(newConfigVersion);
		checkAndCopyNewDeckFiles(needsUpdate);
		checkAndCopyCoreConfig(needsUpdate);
		checkAndCopyGameSkin(mApp.getCoreSkinPath());
		checkAndrCopyNewSingleFiles(needsUpdate);
		DatabaseUtils.checkAndCopyFromInternalDatabase(mContext, mApp.getDataBasePath(), needsUpdate);
		checkAndCopyScripts(needsUpdate);
		checkDirs();
		return 0;
	}

	private void checkDirs() {
		String[] dirs = { "script", "single", "deck", "replay", "fonts" };
		File dirFile = null;
		for (String dir : dirs) {
			dirFile = new File(mApp.getResourcePath(), dir);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
		}

	}

	private void checkAndCopyScripts(boolean isUpdateNeeded) {
		File scriptDir = new File(mApp.getCompatExternalFilesDir(), "/scripts");
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

	private boolean initFontList() {
		File systemFontDir = new File(SYSTEM_FONT_DIR);
		ArrayList<String> fontsPath = new ArrayList<String>();
		String[] fonts = systemFontDir.list();
		for (String name : fonts) {
			fontsPath.add(new File(systemFontDir, name).toString());
		}
		return true;
	}

	private void checkAndCopyCoreConfig(boolean needsUpdate) {
		File internalCacheDir = mContext.getCacheDir();
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
