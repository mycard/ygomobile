package cn.garymb.ygomobile.lite;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.common.ResCheckTask;


public class MainActivity extends Activity implements ResCheckTask.ResCheckListener {
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
            YGOStarter.startGame(MainActivity.this);
        });
        layout.addView(button);
        dlg = ProgressDialog.show(this, "Wait", "check res...");
        //资源复制
        checkResourceDownload();
    }

    private void checkResourceDownload() {
        ResCheckTask task = new ResCheckTask(this);
        task.setResCheckListener(this);
        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    @Override
    public void onResCheckFinished(int result) {
        if (dlg.isShowing()) {
            dlg.dismiss();
        }
    }
}
