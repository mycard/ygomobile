package cn.garymb.ygomobile.ui.plus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.garymb.ygomobile.lite.BuildConfig;


public class WebViewPlus extends XWebView {
    public WebViewPlus(Context context) {
        super(context);
    }

    public WebViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewPlus(Context context, Activity activity) {
        super(context, activity);
    }
}
