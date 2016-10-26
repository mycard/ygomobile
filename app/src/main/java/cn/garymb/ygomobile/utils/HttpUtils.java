package cn.garymb.ygomobile.utils;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class HttpUtils {

	private static final String GZIP = "gzip";

	public static InputStream doOkGet(OkHttpClient client, String uri) {
		Response resp = null;
		ResponseBody body = null;
		InputStream respStream = null;
		GZIPInputStream gzipRespStream = null;

		try {
			Request req = new Request.Builder()
					.url(uri)
					.addHeader("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
					.addHeader("Accept-Encoding", "gzip,deflate,sdch")
					.addHeader("Accept-Language",
							"zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4")
					.addHeader("Content-Type",
							"application/x-www-form-urlencoded").build();
			resp = client.newCall(req).execute();
			int statusCode = resp.code();
			if (statusCode < 200 || statusCode >= 300) {
				Log.d("HttpUtils", "status code = " + statusCode);
				return null;
			}
			body = resp.body();
			InputStream in = null;
			if (body != null && (respStream = body.byteStream()) != null) {
				if (isGZipContent(resp)) {
					gzipRespStream = new GZIPInputStream(respStream);
					in = gzipRespStream;
				} else {
					in = respStream;
				}
			}
			return in;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isGZipContent(Response resp) {
		String headerValue = resp.header("content-encoding");
		if (headerValue != null && headerValue.toLowerCase().equals(GZIP)) {
			return true;
        }
		return false;
	}
}
