package cn.garymb.ygomobile.ui.online;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cn.garymb.ygodata.YGOGameOptions;
import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.ui.plus.WebViewPlus;

public class MyCard {

    private static final String sso_url = "https://accounts.moecube.com";
    private static final String key = "zsZv6LXHDwwtUAGa";
    private static final String return_sso_url = "https://mycard.moe/login_callback";
    private static final String bbs_url = "https://ygobbs.com";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final WebViewPlus.DefWebViewClient mDefWebViewClient;
    private String mNewRoomUrl;
    private final User mUser = new User();
    private MyCardListener mMyCardListener;
    private Activity mContext;

    public interface MyCardListener {
        void onLogin(User user);

        void onHome();
    }

    public MyCard(Activity context) {
        mContext = context;
        mDefWebViewClient = new WebViewPlus.DefWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(return_sso_url)) {
                    String sso = Uri.parse(url).getQueryParameter("sso");
                    String data = new String(Base64.decode(Uri.parse(url).getQueryParameter("sso"), Base64.DEFAULT), UTF_8);
                    Uri info = new Uri.Builder().encodedQuery(data).build();
                    mUser.external_id = Integer.parseInt(info.getQueryParameter("external_id"));
                    mUser.username = info.getQueryParameter("username");
                    mUser.name = info.getQueryParameter("name");
                    mUser.email = info.getQueryParameter("email");
                    mUser.avatar_url = info.getQueryParameter("avatar_url");
                    mUser.admin = info.getBooleanQueryParameter("admin", false);
                    mUser.moderator = info.getBooleanQueryParameter("moderator", false);
                    mUser.login = true;
                    if (getMyCardListener() != null) {
                        getMyCardListener().onLogin(mUser);
                    }
                    Uri route = new Uri.Builder().path("/ygopro/rooms/new").appendQueryParameter("sso", sso).build();
                    Uri whole = Uri.parse("https://r.my-card.in/mobile/index.html").buildUpon().encodedFragment(route.toString()).build();
                    Log.d("login", whole.toString());
                    mNewRoomUrl = whole.toString();
                    if (getMyCardListener() != null) {
                        getMyCardListener().onHome();
                    }
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
    }

    public String getNewRoomUrl() {
        return mNewRoomUrl;
    }

    public MyCardListener getMyCardListener() {
        return mMyCardListener;
    }

    public void setMyCardListener(MyCardListener myCardListener) {
        mMyCardListener = myCardListener;
    }

    public WebViewPlus.DefWebViewClient getWebViewClient() {
        return mDefWebViewClient;
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

    public String getLoginUrl() throws NoSuchAlgorithmException, InvalidKeyException {
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

    @SuppressLint("AddJavascriptInterface")
    public void attachWeb(WebView webView) {
        webView.setWebViewClient(getWebViewClient());
        webView.addJavascriptInterface(new MyCard.Ygopro(mContext), "ygopro");
    }

    public static class User {
        int external_id;
        String username;
        String name;
        String email;
        String avatar_url;
        boolean admin;
        boolean moderator;
        boolean login;

        public User() {

        }
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


