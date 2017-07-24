package cn.garymb.ygomobile.ui.online;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.xwalk.core.XWalkDownloadListener;
import org.xwalk.core.XWalkNavigationItem;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import cn.garymb.ygomobile.lite.BuildConfig;
import cn.garymb.ygomobile.ui.plus.WebViewPlus;

public class MyCardWebView extends XWalkView {
    public MyCardWebView(Context context) {
        super(context);
    }

    public MyCardWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCardWebView(Context context, Activity activity) {
        super(context, activity);
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        if (webViewClient == null) return;
        setResourceClient(new XWalkResourceClient(this) {
            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
                webViewClient.onPageStarted(null, url, null);
            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
                webViewClient.onPageFinished(null, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
                return webViewClient.shouldOverrideUrlLoading(null, url);
            }

        });
    }

    public void setWebChromeClient(final WebChromeClient webChromeClient) {
        if (webChromeClient == null) return;
        setUIClient(new XWalkUIClient(this) {
            @Override
            public boolean onConsoleMessage(XWalkView view, String message, int lineNumber, String sourceId, ConsoleMessageType messageType) {
                if (BuildConfig.DEBUG) {
                    Log.i("webview", sourceId + ":" + lineNumber + "\n" + message);
                }
                return super.onConsoleMessage(view, message, lineNumber, sourceId, messageType);
            }

            @Override
            public void onReceivedTitle(XWalkView view, String title) {
                super.onReceivedTitle(view, title);
                webChromeClient.onReceivedTitle(null, title);
            }
        });
    }

    public boolean canGoBack() {
        return getNavigationHistory().canGoBack();
    }

    public void enableHtml5() {
        getSettings().setSaveFormData(true);
        getSettings().setDatabaseEnabled(true);
        getSettings().setDomStorageEnabled(true);

        // HTML5 configuration settings.
//        getSettings().setAppCacheMaxSize(Long.MAX_VALUE);
//        getSettings().setAppCachePath(context.getDir("appcache", Context.MODE_PRIVATE).getPath());
//        getSettings().setDatabasePath(context.getDir("databases", Context.MODE_PRIVATE).getPath());
//        getSettings().setGeolocationDatabasePath(context.getDir("geolocation", 0).getPath());

        //
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setHorizontalScrollBarEnabled(false);

        getSettings().setAllowContentAccess(true);
        getSettings().setAllowFileAccess(true);

        CookieManager.getInstance().setAcceptCookie(true);
//        if (Build.VERSION.SDK_INT >= 21) {
//            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
//            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        }
        setDownloadListener(new XWalkDownloadListener(getContext()) {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void goBack() {
        int index = getNavigationHistory().getCurrentIndex();
        if (getNavigationHistory().hasItemAt(index - 1)) {
            XWalkNavigationItem item = getNavigationHistory().getItemAt(index - 1);
//            item.getUrl();
            if (item != null) {
                loadUrl(item.getOriginalUrl());
            }
        }
    }
}
