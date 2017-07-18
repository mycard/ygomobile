package cn.garymb.ygomobile.ui.online;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cn.garymb.ygodata.YGOGameOptions;
import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.activities.BaseActivity;
import cn.garymb.ygomobile.ui.plus.WebViewPlus;

public class MyCardActivity extends BaseActivity {
    //大厅聊天
    //房间列表
    //个人页面
    //排行榜
    //新建房间
    private WebViewPlus mWebViewPlus;
    User user;
    static String sso_url = "https://accounts.moecube.com";
    static String key = "zsZv6LXHDwwtUAGa";
    static String return_sso_url = "https://mycard.moe/login_callback";
    static String bbs_url = "https://ygobbs.com";
    String mLoginUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webbrowser);
        final Toolbar toolbar = $(R.id.toolbar);
        setSupportActionBar(toolbar);
        enableBackHome();
        YGOStarter.onCreated(this);
        mWebViewPlus = $(R.id.webbrowser);
        mWebViewPlus.enableHtml5();
        mWebViewPlus.setWebChromeClient(new WebViewPlus.DefWebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (toolbar != null) {
                    toolbar.setSubtitle(title);
                } else {
                    setTitle(title);
                }
            }
        });
        mWebViewPlus.setWebViewClient(new WebViewPlus.DefWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(return_sso_url)) {
                    String sso = Uri.parse(url).getQueryParameter("sso");
                    String data = new String(Base64.decode(Uri.parse(url).getQueryParameter("sso"), Base64.DEFAULT), UTF_8);
                    Uri info = new Uri.Builder().encodedQuery(data).build();

                    user = new User();
                    user.external_id = Integer.parseInt(info.getQueryParameter("external_id"));
                    user.username = info.getQueryParameter("username");
                    user.name = info.getQueryParameter("name");
                    user.email = info.getQueryParameter("email");
                    user.avatar_url = info.getQueryParameter("avatar_url");
                    user.admin = info.getBooleanQueryParameter("admin", false);
                    user.moderator = info.getBooleanQueryParameter("moderator", false);

                    Uri route = new Uri.Builder().path("/ygopro/rooms/new").appendQueryParameter("sso", sso).build();
                    Uri whole = Uri.parse("https://r.my-card.in/mobile/index.html").buildUpon().encodedFragment(route.toString()).build();
                    Log.d("login", whole.toString());
                    mLoginUrl = whole.toString();
                    mycardHome();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWebViewPlus.setWebChromeClient(new WebViewPlus.DefWebChromeClient() {
            @Override
            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                super.onConsoleMessage(message, lineNumber, sourceID);
                Log.i("webview", sourceID + ":" + lineNumber + "\n" + message);
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.i("webview", consoleMessage.sourceId() + ":" + consoleMessage.lineNumber() + "\n" + consoleMessage.message());
                return true;
            }
        });
        mWebViewPlus.addJavascriptInterface(new Ygopro(this), "ygopro");
        try {
            mWebViewPlus.loadUrl(login());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onBackHome() {
        if (mWebViewPlus.canGoBack()) {
            mWebViewPlus.goBack();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebViewPlus.canGoBack()) {
            mWebViewPlus.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        mWebViewPlus.onResume();
        mWebViewPlus.resumeTimers();
        YGOStarter.onResumed(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        mWebViewPlus.onPause();
        mWebViewPlus.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebViewPlus.stopLoading();
        mWebViewPlus.setWebChromeClient(null);
        mWebViewPlus.setWebViewClient(null);
        mWebViewPlus.destroy();
        YGOStarter.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mycard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (doMenu(item.getItemId())) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ////////////////
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private void mycardHome() {
        mWebViewPlus.loadUrl("https://mycard.moe");
    }

    private boolean doMenu(int id) {
        switch (id) {
            case android.R.id.home:
                onBackHome();
                break;
            case R.id.action_home:
                mycardHome();
                break;
            case R.id.action_bbs:
                mWebViewPlus.loadUrl(bbs_url);
                break;
            case R.id.action_new_room:
                if (TextUtils.isEmpty(mLoginUrl)) {
                    mycardHome();
                } else {
                    mWebViewPlus.loadUrl(mLoginUrl);
                }
                break;
            case R.id.action_online_chat:
                break;
            default:
                return false;
        }
        return true;
    }

    private static String byteArrayToHexString(byte[] array) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

    private String login() throws NoSuchAlgorithmException, InvalidKeyException {
        Uri.Builder payloadBuilder = new Uri.Builder();
        payloadBuilder.appendQueryParameter("return_sso_url", return_sso_url);
        byte[] payload = Base64.encode(payloadBuilder.build().getQuery().getBytes(UTF_8), Base64.DEFAULT);

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        String signature = byteArrayToHexString(sha256_HMAC.doFinal(payload));
        Uri.Builder requestBuilder = Uri.parse(sso_url).buildUpon();
        requestBuilder.appendQueryParameter("sso", new String(payload, UTF_8));
        requestBuilder.appendQueryParameter("sig", signature);
        return requestBuilder.build().toString();
    }

    public static class Ygopro {
        Activity activity;

        private Ygopro(Activity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void join(String host, int port, String name, String room) {
            try {
                final YGOGameOptions options = new YGOGameOptions();
                options.mServerAddr = host;
                options.mUserName = name;
                options.mPort = port;
                options.mRoomName = room;
                Log.d("webview", "options=" + options);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        YGOStarter.startGame(activity, options);
                    }
                });
            } catch (Exception e) {
                Log.e("webview", "startGame", e);
            }
        }
    }
}
