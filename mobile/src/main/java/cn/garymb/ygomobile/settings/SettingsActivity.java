package cn.garymb.ygomobile.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import cn.garymb.ygomobile.activities.BaseActivity;
import cn.garymb.ygomobile.lite.R;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = bind(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        getFragmentManager().beginTransaction().replace(R.id.fragment,  new SettingFragment()).commit();
    }
}
