package cn.garymb.ygomobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.BuildConfig;
import cn.garymb.ygomobile.lite.R;

public class LogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if(BuildConfig.DEBUG){
            startActivity(new Intent(LogoActivity.this, MainActivity.class));
            finish();
            return;
        }
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_logo);
        ImageView image = (ImageView) findViewById(R.id.logo);
        AlphaAnimation anim = new AlphaAnimation(0.1f, 1.0f);
        anim.setDuration(Constants.LOG_TIME);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                Toast.makeText(LogoActivity.this, R.string.logo_text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                startActivity(new Intent(LogoActivity.this, MainActivity.class));
                finish();
            }
        });
        image.setAnimation(anim);
        anim.start();
    }
}

