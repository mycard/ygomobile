package cn.garymb.ygomobile.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.garymb.ygomobile.settings.AboutFragment;

public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackHome();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AboutFragment()).commit();
    }
}
