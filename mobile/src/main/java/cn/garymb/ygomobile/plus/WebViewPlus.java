package cn.garymb.ygomobile.plus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebViewPlus extends WebView {
    private Context context;

    public WebViewPlus(Context context) {
        this(context, null);
    }

    public WebViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public WebViewPlus(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init(Context context) {
        getSettings().setJavaScriptEnabled(true);
        getSettings().setLoadsImagesAutomatically(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setSupportZoom(true);
        getSettings().setDisplayZoomControls(false);
        getSettings().setBuiltInZoomControls(true);
        getSettings().setSupportMultipleWindows(false);
        getSettings().setEnableSmoothTransition(true);
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    }

    public void enableHtml5() {
        getSettings().setGeolocationEnabled(true);
        getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        getSettings().setSaveFormData(true);
        getSettings().setSavePassword(true);
// HTML5 API flags
        getSettings().setAppCacheEnabled(true);
        getSettings().setDatabaseEnabled(true);
        getSettings().setDomStorageEnabled(true);

        // HTML5 configuration settings.
        getSettings().setAppCacheMaxSize(10 * 1024 * 1024);
        getSettings().setAppCachePath(context.getDir("appcache", Context.MODE_PRIVATE).getPath());
        getSettings().setDatabasePath(context.getDir("databases", Context.MODE_PRIVATE).getPath());
        getSettings().setGeolocationDatabasePath(context.getDir("geolocation", 0).getPath());

        //
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setHorizontalScrollBarEnabled(false);
        setHorizontalScrollbarOverlay(true);

        getSettings().setAllowContentAccess(true);
        getSettings().setAllowFileAccess(true);

        CookieSyncManager.createInstance(context);
        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
        }
        setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setWebViewClient(new DefWebViewClient());
    }

    public static class DefWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
    public static class DefWebChromeClient extends WebChromeClient {

    }
}
