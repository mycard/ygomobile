package cn.garymb.ygomobile.test;

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

import cn.garymb.ygomobile.R;
import cn.garymb.ygomobile.StaticApplication;

public class ResCheckTask2 extends AsyncTask<Void, Integer, Integer> {
    private static final String TAG = "ResCheckTask";
    public static final int ERROR_NONE = 0;
    public static final int ERROR_CORE_CONFIG = -1;
    public static final int ERROR_COPY = -2;
    public static final int ERROR_CORE_CONFIG_LOST = -3;
    protected int mError = ERROR_NONE;
    private static final String ASSETS_PATH = "data/";
    private GameSettings mSettings;
    private Context mContext;
    private ResCheckListener mListener;
    private ProgressDialog dialog = null;
    private Handler handler;

    @SuppressWarnings("deprecation")
    public ResCheckTask2(Context context, ResCheckListener listener) {
        mContext = context;
        mListener = listener;
        handler = new Handler(context.getMainLooper());
        mSettings = GameSettings.get();
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

    private String getDatapath(String path) {
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
        } catch (Exception e) {
            Log.e(TAG, "check core config", e);
            return ERROR_CORE_CONFIG;
        }
        mSettings.setCoreConfigVersion(newConfigVersion);
        needsUpdate = needsUpdate || !currentConfigVersion.equals(newConfigVersion);
        StaticApplication mApp = StaticApplication.get();
        //res
        try {
            String resPath = mSettings.getResourcePath();
            IOUtils.createNoMedia(resPath);
            checkDirs();
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
            copyCdbFile(needsUpdate);
        } catch (Exception e) {
            Log.e(TAG, "check", e);
            return ERROR_COPY;
        }
        return ERROR_NONE;
    }

    void copyCdbFile(boolean needsUpdate) throws IOException {
        File dbFile = new File(mSettings.getDataBasePath(), GameSettings.DATABASE_NAME);
        boolean copyDb = true;
        if (dbFile.exists()) {
            copyDb = false;
            if (needsUpdate) {
                copyDb = true;
                dbFile.delete();
            }
        }
        if (copyDb) {
            IOUtils.copyFilesFromAssets(mContext, getDatapath(GameSettings.DATABASE_NAME), mSettings.getDataBasePath(), needsUpdate);
            doSomeTrickOnDatabase(dbFile.getAbsolutePath());
        }
    }

    void doSomeTrickOnDatabase(String myPath)
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
        String[] dirs = {GameSettings.CORE_SCRIPTS_PATH,
                GameSettings.CORE_SINGLE_PATH,
                GameSettings.CORE_DECK_PATH,
                GameSettings.CORE_REPLAY_PATH,
                GameSettings.FONT_DIRECTORY,
                GameSettings.CORE_IMAGE_PATH
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
