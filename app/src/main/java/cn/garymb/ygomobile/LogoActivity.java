package cn.garymb.ygomobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LogoActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		float dp = getResources().getDisplayMetrics().density;
		int widhtAndHeight = (int) (dp * 150);
		ImageView image = new ImageView(this);
		image.setScaleType(ScaleType.FIT_XY);
		image.setLayoutParams(new LinearLayout.LayoutParams(widhtAndHeight, widhtAndHeight));
		image.setImageResource(R.drawable.ic_icon);
		AlphaAnimation anim = new AlphaAnimation(0.1f, 1.0f);  
		anim.setDuration(2000);  
		image.setAnimation(anim); 
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(LogoActivity.this, MainActivity.class));
				finish();
			}
		});
		anim.start();
		LinearLayout viewGroup = new LinearLayout(this);
		viewGroup.setGravity(Gravity.CENTER);
		viewGroup.addView(image);
		setContentView(viewGroup);
		
		Toast.makeText(this, "喵~＞▽＜", 0).show();
	}
	
}
