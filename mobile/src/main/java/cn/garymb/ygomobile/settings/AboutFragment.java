package cn.garymb.ygomobile.settings;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;

import cn.garymb.ygomobile.lite.R;

public class AboutFragment extends BasePreferenceFragment {
    @Override
    protected SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        addPreferencesFromResource(R.xml.preference_about);
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bind("pref_key_about_version", (packageInfo == null) ? "?" : packageInfo.versionName);
    }
}
