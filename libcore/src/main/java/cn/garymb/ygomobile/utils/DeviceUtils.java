package cn.garymb.ygomobile.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;

import java.io.File;
import java.net.URI;

public class DeviceUtils {
	private static float mScreenHeight,mScreenWidth,mDensity;
	static{
		mDensity = Resources.getSystem().getDisplayMetrics().density;
		mScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
		mScreenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
	}

	public static float getScreenWidth() {
		return mScreenWidth;
	}
	
	public static float getSmallerSize() {
		return  mScreenHeight < mScreenWidth ? mScreenHeight : mScreenWidth;
	}
	
	public static float getScreenHeight() {
		return mScreenHeight;
	}

	public static float getDensity() {
		return mDensity;
	}

	public static float getXScale() {
		return (mScreenHeight > mScreenWidth ? mScreenHeight : mScreenWidth) / 1024.0f;
	}
	
	public static float getYScale() {
		return (mScreenHeight > mScreenWidth ? mScreenWidth : mScreenHeight) / 640.0f;
	}

	/**
	 * 调用系统安装界面
	 * @param context Context
	 */
	@SuppressWarnings("deprecation")
	public static void reqSystemInstall(Context context, URI uri) {
		if (uri == null)
			return;
		File targetFile = new File(uri);
		if (!targetFile.exists() || targetFile.isDirectory())
			return;
		
		Uri packageURI = Uri.fromFile(targetFile);      
		Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
		intent.setData(packageURI);
		intent.putExtra(Intent.EXTRA_ALLOW_REPLACE, true);
		try {
			if (context instanceof Activity && Build.VERSION.SDK_INT >= 14) {
				intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
				((Activity) context).startActivityForResult(intent, 0);
			} else {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} catch (ActivityNotFoundException anfe) { }
	}
}
