package cn.garymb.ygomobile.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.List;

import cn.garymb.ygomobile.fragments.AboutSettingsFragment;
import cn.garymb.ygomobile.fragments.GameSettingsFragment;
import cn.garymb.ygomobile.lite.R;


public class SettingsActivity extends AppCompatPreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar(getSupportActionBar());
    }

    private void setupActionBar(ActionBar actionBar) {
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.action_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return AboutSettingsFragment.class.getName().equals(fragmentName)
                    || GameSettingsFragment.class.getName().equals(fragmentName);
        } else {
            return true;
        }
    }
}
