package cn.garymb.ygomobile.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import java.io.File;

import cn.garymb.ygomobile.App;
import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;

import static cn.garymb.ygomobile.Constants.*;

public class SettingFragment extends BasePreferenceFragment {

    public SettingFragment() {

    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return AppsSettings.get().getSharedPreferences();
    }

    private AppsSettings mSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mSettings = AppsSettings.get();

        addPreferencesFromResource(R.xml.preference_game);
        bind(PREF_GAME_PATH, mSettings.getResourcePath());
        bind(PREF_GAME_VERSION, mSettings.getCoreConfigVersion());

        bind(PREF_SOUND_EFFECT, mSettings.isSoundEffect());
        bind(PREF_LOCK_SCREEN, mSettings.isLockSreenOrientation());
        bind(PREF_FONT_ANTIALIAS, mSettings.isFontAntiAlias());
        bind(PREF_IMMERSIVE_MODE, mSettings.isImmerSiveMode());
        bind(PREF_PENDULUM_SCALE, mSettings.isPendulumScale());
        bind(PREF_OPENGL_VERSION, Constants.PREF_DEF_OPENGL_VERSION);
        bind(PREF_IMAGE_QUALITY, Constants.PREF_DEF_IMAGE_QUALITY);

        bind(PREF_GAME_FONT, mSettings.getFontPath());
        bind(PREF_USE_EXTRA_CARD_CARDS);
        bind(SETTINGS_COVER, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_COVER).getAbsolutePath());
        bind(SETTINGS_CARD_BG, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_BG).getAbsolutePath());
        bind(SETTINGS_DIY_CDB);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
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
        super.onPreferenceChange(preference, value);
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

