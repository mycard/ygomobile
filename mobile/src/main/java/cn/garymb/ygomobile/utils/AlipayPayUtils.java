package cn.garymb.ygomobile.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.net.URLEncoder;

import static cn.garymb.ygomobile.Constants.ALIPAY_URL;

public class AlipayPayUtils {
        public static boolean openAlipayPayPage(Context context, String qrcode) {
            try {
                qrcode = URLEncoder.encode(ALIPAY_URL, "utf-8");
                } catch (Exception e) {
            }
            try {
                final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrcode;
                openUri(context, alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis());
                return true;
                } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
    }

    /**
     * 发送一个intent
     *
     * @param context
     * @param s
     */
    private static void openUri(Context context, String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
