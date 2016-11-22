package cn.garymb.ygomobile.filebrowser;

import android.content.Context;

public class OpenFileDialog extends BaseFileDialog {

    public OpenFileDialog(Context context) {
        super(context);
        setHighHint(true);
    }
}