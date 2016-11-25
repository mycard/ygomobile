package cn.garymb.ygomobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.garymb.ygomobile.bean.CardInfo;

public class CardInfoActivity extends BaseActivity {
    private CardInfo mCardInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(intent.hasExtra(CardInfo.TAG)){
            mCardInfo = intent.getParcelableExtra(CardInfo.TAG);
        }else{
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        enableBackHome();
    }
}
