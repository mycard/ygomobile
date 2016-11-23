package cn.garymb.ygomobile.plus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.Preference;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.util.Map;
import java.util.Set;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;

import static cn.garymb.ygomobile.Constants.REQUEST_CHOOSE_FILE;
import static cn.garymb.ygomobile.Constants.REQUEST_CHOOSE_IMG;

public abstract class PreferenceFragmentPlus extends BasePreferenceFragment {
    private Preference curPreference;
    private String mOutFile;

    protected void onChooseFileOk(Preference preference, String file) {
        onPreferenceChange(preference, file);
    }

    protected void onChooseFileFail(Preference preference) {

    }

    /***
     * @param preference
     * @param type       *\/*
     */
    protected void showFileChooser(Preference preference, String type, String title) {
        curPreference = preference;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        try {
            startActivityForResult(Intent.createChooser(intent, title),
                    REQUEST_CHOOSE_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), R.string.no_find_file_selectotr, Toast.LENGTH_SHORT)
                    .show();
            onChooseFileFail(preference);
        }
    }

    /***
     */
    protected void showImageCropChooser(Preference preference, String title, String outFile, boolean isJpeg, int width, int height) {
        mOutFile = outFile;
        curPreference = preference;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);// 黑边
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(outFile)));
        intent.putExtra("outputFormat", isJpeg ? Bitmap.CompressFormat.JPEG.toString() : Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        try {
            startActivityForResult(Intent.createChooser(intent, title),
                    REQUEST_CHOOSE_IMG);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), R.string.no_find_file_selectotr, Toast.LENGTH_SHORT)
                    .show();
            onChooseFileFail(preference);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHOOSE_IMG) {
            if (resultCode == Activity.RESULT_OK) {
                onChooseFileOk(curPreference, mOutFile);
            } else {
                onChooseFileFail(curPreference);
            }
        } else if (requestCode == Constants.REQUEST_CHOOSE_FILE) {
            //选择文件
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    File file =new File(uri.getPath());
                    if(file.exists()) {
                        onChooseFileOk(curPreference, file.getAbsolutePath());
                        return;
                    }
                }
            }
            onChooseFileFail(curPreference);
        }
    }

    public static class SharedPreferencesPlus implements SharedPreferences {

        public static SharedPreferencesPlus create(Context context, String name) {
            return create(context, name, Context.MODE_PRIVATE);
        }

        public static SharedPreferencesPlus create(Context context, String name, int mode) {
            return new SharedPreferencesPlus(context, name, mode);
        }

        private SharedPreferences mSharedPreferences;
        private boolean autoSave = false;
        private boolean isMultiProess = false;

        private SharedPreferencesPlus(Context context, String name, int mode) {
            mSharedPreferences = context.getSharedPreferences(name, mode);
            isMultiProess = (mode & Context.MODE_MULTI_PROCESS) == Context.MODE_MULTI_PROCESS;
        }

        public SharedPreferences getSharedPreferences() {
            return mSharedPreferences;
        }

        public boolean isAutoSave() {
            return autoSave;
        }

        public void setAutoSave(boolean autoSave) {
            this.autoSave = autoSave;
        }

        public Editor edit(){
            return mSharedPreferences.edit();
        }

        public void putString(String key, String value) {
            Editor editor  = edit().putString(key, value);
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        public void putStringSet(String key, Set<String> values) {
            Editor editor  = edit().putStringSet(key, values);
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        public void putInt(String key, int value) {
            Editor editor  = edit().putInt(key, value);
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        public void putLong(String key, long value) {
            Editor editor  = edit().putLong(key, value);
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        public void putFloat(String key, float value) {
            Editor editor  = edit().putFloat(key, value);
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        public void putBoolean(String key, boolean value) {
            Editor editor  = edit().putBoolean(key, value);
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        public void remove(String key) {
            Editor editor  = edit().remove(key);
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        public void clear() {
            Editor editor  = edit().clear();
            if(autoSave) {
                if (isMultiProess) {
                    editor.commit();
                } else {
                    editor.apply();
                }
            }
        }

        @Override
        public Map<String, ?> getAll() {
            return mSharedPreferences.getAll();
        }

        @Override
        public String getString(String key, String defValue) {
            return mSharedPreferences.getString(key, defValue);
        }

        @Override
        public Set<String> getStringSet(String key, Set<String> defValues) {
            return mSharedPreferences.getStringSet(key, defValues);
        }

        @Override
        public int getInt(String key, int defValue) {
            return  mSharedPreferences.getInt(key, defValue);
        }

        @Override
        public long getLong(String key, long defValue) {
            return mSharedPreferences.getLong(key, defValue);
        }

        @Override
        public float getFloat(String key, float defValue) {
            return mSharedPreferences.getFloat(key, defValue);
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            return  mSharedPreferences.getBoolean(key, defValue);
        }

        @Override
        public boolean contains(String key) {
            return mSharedPreferences.contains(key);
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
            mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
            mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }
}
