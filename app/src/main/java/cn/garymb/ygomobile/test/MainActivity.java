package cn.garymb.ygomobile.test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.garymb.ygomobile.NativeInitOptions;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        Button button = new Button(this);
        button.setText("start");
        button.setOnClickListener((v) -> {
            YGOStarter.startGame(MainActivity.this);
        });
        button.setEnabled(false);
        layout.addView(button);
        GameSettings.init(new GameSettings(this){
            @Override
            public NativeInitOptions getNativeInitOptions() {
                NativeInitOptions options= super.getNativeInitOptions();
//                options.mCacheDir = context.getCacheDir().getAbsolutePath();
                Log.i("Irrlicht",""+options.toString());
                return options;
            }
        });
        //资源复制
        checkResourceDownload((error) -> {
            if (error < 0) {
                button.setEnabled(false);
                Toast.makeText(this, "check completed:" + error, Toast.LENGTH_SHORT).show();
            } else {
                button.setEnabled(true);
            }
        });
    }

    private void checkResourceDownload(ResCheckTask2.ResCheckListener listener) {
        ResCheckTask2 task = new ResCheckTask2(this, listener);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
