package cn.garymb.ygomobile.ui.online;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.nio.charset.Charset;

import cn.garymb.ygodata.YGOGameOptions;
import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.ui.cards.DeckManagerActivity;
import cn.garymb.ygomobile.ui.plus.WebViewPlus;

public class MyCard {

    //    private static final String sso_url = "https://accounts.moecube.com";
//    private static final String key = "zsZv6LXHDwwtUAGa";
    private static final String return_sso_url = "https://r.my-card.in/mobile/index.html";
    private static final String bbs_url = "https://ygobbs.com";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final WebViewPlus.DefWebViewClient mDefWebViewClient;
    private String mHomeUrl = "https://r.my-card.in/mobile/index.html";
    private String mNewRoomUrl = "https://r.my-card.in/mobile/index.html#/ygopro/rooms/new";
    private final User mUser = new User();
    private MyCardListener mMyCardListener;
    private Activity mContext;

    public interface MyCardListener {
        void onLogin(User user);

        void watchReplay();

        void puzzleMode();

        void openDrawer();

        void closeDrawer();

        void backHome();

        void share(String text);

        void onHome();
    }

    public MyCard(Activity context) {
        mContext = context;
        mDefWebViewClient = new WebViewPlus.DefWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(return_sso_url)) {
                    String sso = Uri.parse(url).getQueryParameter("sso");
                    String data = new String(Base64.decode(Uri.parse(url).getQueryParameter("sso"), Base64.NO_WRAP), UTF_8);
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
                    return false;
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

    public WebViewPlus.DefWebViewClient getWebViewClient() {
        return mDefWebViewClient;
    }

    private static String byteArrayToHexString(byte[] array) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : array) {
            int intVal = b & 0xff;
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }

//    public String getLoginUrl() throws NoSuchAlgorithmException, InvalidKeyException {
//        Uri.Builder payloadBuilder = new Uri.Builder();
//        payloadBuilder.appendQueryParameter("return_sso_url", return_sso_url);
//        byte[] payload = Base64.encode(payloadBuilder.build().getQuery().getBytes(UTF_8), Base64.NO_WRAP);
//
//        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(UTF_8), "HmacSHA256");
//        sha256_HMAC.init(secret_key);
//        String signature = byteArrayToHexString(sha256_HMAC.doFinal(payload));
//        Uri.Builder requestBuilder = Uri.parse(sso_url).buildUpon();
//        requestBuilder.appendQueryParameter("sso", new String(payload, UTF_8));
//        requestBuilder.appendQueryParameter("sig", signature);
//        return requestBuilder.build().toString();
//    }

    public String getHomeUrl() {
        return mHomeUrl;
    }

    public String getBBSUrl() {
        return bbs_url;
    }

    @SuppressLint("AddJavascriptInterface")
    public void attachWeb(WebView webView, MyCardListener myCardListener) {
        mMyCardListener = myCardListener;
        webView.setWebViewClient(getWebViewClient());
        webView.addJavascriptInterface(new MyCard.Ygopro(mContext, myCardListener), "ygopro");
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

    public boolean check(WebView webView) {
        try {
            webView.loadUrl(getHomeUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static class Ygopro {
        Activity activity;
        MyCardListener mListener;

        private Ygopro(Activity activity, MyCardListener listener) {
            this.activity = activity;
            mListener = listener;
        }

        @JavascriptInterface
        public void edit_deck() {
            activity.startActivity(new Intent(activity, DeckManagerActivity.class));
        }

        @JavascriptInterface
        public void watch_replay() {
            if (mListener != null) {
                activity.runOnUiThread(mListener::watchReplay);
            }
        }

        @JavascriptInterface
        public void puzzle_mode() {
            if (mListener != null) {
                activity.runOnUiThread(mListener::puzzleMode);
            }
        }

        @JavascriptInterface
        public void openDrawer() {
            if (mListener != null) {
                activity.runOnUiThread(mListener::openDrawer);
            }
        }

        @JavascriptInterface
        public void backHome() {
            if (mListener != null) {
                activity.runOnUiThread(mListener::backHome);
            }
        }

        @JavascriptInterface
        public void share(String text) {
            if (mListener != null) {
                activity.runOnUiThread(() -> {
                    mListener.share(text);
                });
            }
        }

        @JavascriptInterface
        public void closeDrawer() {
            if (mListener != null) {
                activity.runOnUiThread(mListener::closeDrawer);
            }
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


