package cn.garymb.ygomobile.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.garymb.ygomobile.utils.IOUtils;

import static cn.garymb.ygomobile.Constants.ASSETS_PATH;

public class ResCheckTask extends AsyncTask<Void, Integer, Integer> {
    private static final String TAG = "ResCheckTask";
    public static final int ERROR_NONE = 0;
    public static final int ERROR_CORE_CONFIG = -1;
    public static final int ERROR_COPY = -2;
    public static final int ERROR_CORE_CONFIG_LOST = -3;
    protected int mError = ERROR_NONE;
    private AppsSettings mSettings;
    private Context mContext;
    private ResCheckListener mListener;
    private ProgressDialog dialog = null;
    private Handler handler;

    @SuppressWarnings("deprecation")
    public ResCheckTask(Context context, ResCheckListener listener) {
        mContext = context;
        mListener = listener;
        handler = new Handler(context.getMainLooper());
        mSettings = AppsSettings.get();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(mContext, null, mContext.getString(R.string.check_res));
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

    public static String getDatapath(String path) {
        if (TextUtils.isEmpty(ASSETS_PATH)) {
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
        if (Constants.DEBUG)
            Log.d(TAG, "check start");
        boolean needsUpdate = false;
        //core config
        setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.core_config)));
        String newConfigVersion = null, currentConfigVersion = null;
        File verPath = new File(mSettings.getResourcePath(), Constants.CORE_CONFIG_PATH);
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
                if (Constants.DEBUG)
                    Log.e(TAG, "check core config currentConfigVersion is null:" + verPath);
                return ERROR_CORE_CONFIG;
            }
        }
        //check core config

        try {
            newConfigVersion = mContext.getAssets().list(getDatapath(Constants.CORE_CONFIG_PATH))[0];
        } catch (Exception e) {
            if (Constants.DEBUG)
                Log.e(TAG, "check core config", e);
            return ERROR_CORE_CONFIG;
        }
        mSettings.setCoreConfigVersion(newConfigVersion);
        needsUpdate = needsUpdate || !currentConfigVersion.equals(newConfigVersion);
        //res
        try {
            String resPath = mSettings.getResourcePath();
            IOUtils.createNoMedia(resPath);
            checkDirs();
            copyCoreConfig(verPath.getAbsolutePath());
//            copyCoreConfig(new File(mSettings.getResourcePath(), GameSettings.CORE_CONFIG_PATH).getAbsolutePath());
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.tip_new_deck)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(Constants.CORE_DECK_PATH),
                    new File(resPath, Constants.CORE_SINGLE_PATH).getAbsolutePath(), needsUpdate);
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.game_skins)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(Constants.CORE_SKIN_PATH),
                    mSettings.getCoreSkinPath(), needsUpdate, mSettings.isPendulumScale());
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.font_files)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(Constants.FONT_DIRECTORY),
                    mSettings.getFontDirPath(), needsUpdate);
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.single_lua)));
            IOUtils.copyFilesFromAssets(mContext, getDatapath(Constants.CORE_SINGLE_PATH),
                    new File(resPath, Constants.CORE_SINGLE_PATH).getAbsolutePath(), needsUpdate);
            if (IOUtils.hasAssets(mContext, getDatapath(Constants.CORE_SCRIPTS_ZIP))) {
                setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.scripts)));
                IOUtils.copyFilesFromAssets(mContext, getDatapath(Constants.CORE_SCRIPTS_ZIP),
                        resPath, needsUpdate);
            }
            setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.cards_cdb)));
            copyCdbFile(needsUpdate);
            if (IOUtils.hasAssets(mContext, getDatapath(Constants.CORE_PICS_ZIP))) {
                setMessage(mContext.getString(R.string.check_things, mContext.getString(R.string.images)));
                IOUtils.copyFilesFromAssets(mContext, getDatapath(Constants.CORE_PICS_ZIP),
                        resPath, needsUpdate);
            }
        } catch (Exception e) {
            if (Constants.DEBUG)
                Log.e(TAG, "check", e);
            return ERROR_COPY;
        }
        return ERROR_NONE;
    }

    void copyCdbFile(boolean needsUpdate) throws IOException {
        File dbFile = new File(mSettings.getDataBasePath(), Constants.DATABASE_NAME);
        boolean copyDb = true;
        if (dbFile.exists()) {
            copyDb = false;
            if (needsUpdate) {
                copyDb = true;
                dbFile.delete();
            }
        }
        if (copyDb) {
            IOUtils.copyFilesFromAssets(mContext, getDatapath(Constants.DATABASE_NAME), mSettings.getDataBasePath(), needsUpdate);
            doSomeTrickOnDatabase(dbFile.getAbsolutePath());
        }
    }

    public static void doSomeTrickOnDatabase(String myPath)
            throws SQLiteException {
        SQLiteDatabase db = null;
        db = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
        try {
            db.beginTransaction();
            db.execSQL("ALTER TABLE datas RENAME TO datas_backup;");
            db.execSQL("CREATE TABLE datas (_id integer PRIMARY KEY, ot integer, alias integer, setcode integer, type integer,"
                    + " atk integer, def integer, level integer, race integer, attribute integer, category integer);");
            db.execSQL("INSERT INTO datas (_id, ot, alias, setcode, type, atk, def, level, race, attribute, category) "
                    + "SELECT id, ot, alias, setcode, type, atk, def, level, race, attribute, category FROM datas_backup;");
            db.execSQL("DROP TABLE datas_backup;");
            db.execSQL("ALTER TABLE texts RENAME TO texts_backup;");
            db.execSQL("CREATE TABLE texts (_id integer PRIMARY KEY, name varchar(128), desc varchar(1024),"
                    + " str1 varchar(256), str2 varchar(256), str3 varchar(256), str4 varchar(256), str5 varchar(256),"
                    + " str6 varchar(256), str7 varchar(256), str8 varchar(256), str9 varchar(256), str10 varchar(256),"
                    + " str11 varchar(256), str12 varchar(256), str13 varchar(256), str14 varchar(256), str15 varchar(256), str16 varchar(256));");
            db.execSQL("INSERT INTO texts (_id, name, desc, str1, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16)"
                    + " SELECT id, name, desc, str1, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, str13, str14, str15, str16 FROM texts_backup;");
            db.execSQL("DROP TABLE texts_backup;");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if (db != null) {
            db.close();
        }
    }

    private void checkDirs() {
        String[] dirs = {Constants.CORE_SCRIPT_PATH,
                         Constants.CORE_SINGLE_PATH,
                         Constants.CORE_DECK_PATH,
                         Constants.CORE_REPLAY_PATH,
                         Constants.FONT_DIRECTORY,
                         Constants.CORE_IMAGE_PATH
        };
        File dirFile = null;
        for (String dir : dirs) {
            dirFile = new File(mSettings.getResourcePath(), dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        }
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
            String path = getDatapath(Constants.CORE_CONFIG_PATH);
            int count = IOUtils.copyFilesFromAssets(mContext, path, toPath, true);
            if (count < 3) {
                return ERROR_CORE_CONFIG_LOST;
            }
            return ERROR_NONE;
        } catch (IOException e) {
            if (Constants.DEBUG)
                Log.e(TAG, "copy", e);
            mError = ERROR_COPY;
            return ERROR_COPY;
        }
    }

    public interface ResCheckListener {
        void onResCheckFinished(int result);
    }

}
