package cn.garymb.ygomobile.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.settings.AboutFragment;

public class AboutActivity extends BaseActivity {
    private AboutFragment mFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackHome();
        int type = 0;
        if(getIntent().hasExtra(Constants.SETTINGS_CATEGORY)){
            type = getIntent().getIntExtra(Constants.SETTINGS_CATEGORY, 0);
        }
        mFragment = new AboutFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, mFragment).commit();
    }
}
