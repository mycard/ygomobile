package cn.garymb.ygomobile.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivityTest extends Activity {
    private boolean enableStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        GameSettings.init(new GameSettings(this));
        Button button=new Button(this);
        button.setText("start");
        layout.addView(button);
        button.setOnClickListener((v)->{
            if(enableStart){
                YGOStarter.startGame(this, null);
            }
        });
        //资源复制
        checkResourceDownload((error) -> {
            if (error < 0) {
                enableStart = false;
            } else {
                enableStart = true;
            }
        });
        YGOStarter.onCreated(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        YGOStarter.onResumed(this);
    }

    @Override
    protected void onDestroy() {
        YGOStarter.onDestroy(this);
        super.onDestroy();
    }

    /***
     * ResCheckTask2改为ResCheckTask后，字体正常
     * @param listener
     */
    private void checkResourceDownload(ResCheckTask2.ResCheckListener listener) {
        ResCheckTask2 task = new ResCheckTask2(this, listener);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
