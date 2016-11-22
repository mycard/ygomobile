package cn.garymb.ygomobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesPlus implements  SharedPreferences{

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

    public SharedPreferences.Editor edit(){
        return mSharedPreferences.edit();
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor  = edit().putString(key, value);
        if(autoSave) {
            if (isMultiProess) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }

    public void putStringSet(String key, Set<String> values) {
        SharedPreferences.Editor editor  = edit().putStringSet(key, values);
        if(autoSave) {
            if (isMultiProess) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }

    public void putInt(String key, int value) {
        SharedPreferences.Editor editor  = edit().putInt(key, value);
        if(autoSave) {
            if (isMultiProess) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }

    public void putLong(String key, long value) {
        SharedPreferences.Editor editor  = edit().putLong(key, value);
        if(autoSave) {
            if (isMultiProess) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }

    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor  = edit().putFloat(key, value);
        if(autoSave) {
            if (isMultiProess) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor  = edit().putBoolean(key, value);
        if(autoSave) {
            if (isMultiProess) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }

    public void remove(String key) {
        SharedPreferences.Editor editor  = edit().remove(key);
        if(autoSave) {
            if (isMultiProess) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }

    public void clear() {
        SharedPreferences.Editor editor  = edit().clear();
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
