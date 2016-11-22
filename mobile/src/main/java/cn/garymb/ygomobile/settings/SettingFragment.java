package cn.garymb.ygomobile.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import cn.garymb.ygomobile.lite.R;

public class SettingFragment extends BasePreferenceFragment {
    private int mInitSettings;
    private String KEY_ABOUT;

    public SettingFragment() {

    }

    public SettingFragment initCategory(int settings) {
        mInitSettings = settings;
        return this;
    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        addPreferencesFromResource(R.xml.preference_game);
//        KEY_ABOUT = getString(R.string.settings_about);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (preference instanceof CheckBoxPreference) {

        }
//        else if (mMyPreference.KEY_STYLE_PATH.equals(key)) {
//            startActivityForResult(new Intent(getActivity(), StyleListActivity.class), Constants.REQUEST_STYLE);
//        } else if (KEY_ABOUT.equals(key)) {
//            showAboutInfo();
//        } else if (mMyPreference.KEY_PATH_CACHE.equals(key)) {
//            selectFolder(preference, MyPreference.DirType.Cache);
//        } else if (mMyPreference.KEY_PATH_IMAGE.equals(key)) {
//            selectFolder(preference, MyPreference.DirType.Images);
//        } else if (mMyPreference.KEY_PATH_STYLE.equals(key)) {
//            selectFolder(preference, MyPreference.DirType.Styles);
//        } else if (mMyPreference.KEY_PATH_TEMP.equals(key)) {
//            selectFolder(preference, MyPreference.DirType.Temp);
//        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference instanceof CheckBoxPreference) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            mSharedPreferences.edit().putBoolean(preference.getKey(), checkBoxPreference.isChecked()).apply();
            return true;
        }
        boolean rs = super.onPreferenceChange(preference, value);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            mSharedPreferences.edit().putString(preference.getKey(), listPreference.getValue()).apply();
        }
        return rs;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}

