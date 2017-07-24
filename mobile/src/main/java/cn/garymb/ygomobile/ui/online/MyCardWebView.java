package cn.garymb.ygomobile.ui.online;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import cn.garymb.ygomobile.ui.plus.XWebView;

public class MyCardWebView extends XWebView {
    public MyCardWebView(Context context) {
        super(context);
    }

    public MyCardWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCardWebView(Context context, Activity activity) {
        super(context, activity);
    }
}
