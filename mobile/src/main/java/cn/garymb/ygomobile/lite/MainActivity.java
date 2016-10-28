package cn.garymb.ygomobile.lite;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.task.ResCheckTask;


public class MainActivity extends Activity {
    private Dialog dlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        Button button = new Button(this);
        button.setText("start");
        button.setOnClickListener((v) -> {
//            button.setVisibility(View.INVISIBLE);
//            layout.setBackgroundResource(R.drawable.bg);
            YGOStarter.startGame(MainActivity.this);
        });
        button.setEnabled(false);
        layout.addView(button);
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkResourceDownload(ResCheckTask.ResCheckListener listener) {
        ResCheckTask task = new ResCheckTask(this, listener);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }
}
