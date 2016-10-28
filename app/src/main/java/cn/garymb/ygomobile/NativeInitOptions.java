package cn.garymb.ygomobile;

import android.content.SharedPreferences;
import android.text.TextUtils;

import java.nio.ByteBuffer;

import cn.garymb.ygomobile.common.Constants;
import cn.garymb.ygomobile.setting.Settings;

public final class NativeInitOptions {
	
	private static final int BUFFER_MAX_SIZE = 8192;
	
	public int mOpenglVersion;

	public boolean mIsSoundEffectEnabled;

	public String mCacheDir;

	public String mDBDir;

	public String mCoreConfigVersion;

	public String mResourcePath;

	public String mExternalFilePath;

	public int mCardQuality;

	public boolean mIsFontAntiAliasEnabled;

	public boolean mIsPendulumScaleEnabled;

	public NativeInitOptions(){

	}

	public static NativeInitOptions fromSettingsPref(final SharedPreferences pref) {
		StaticApplication app = StaticApplication.get();
		NativeInitOptions options = new NativeInitOptions();
		options.mOpenglVersion = Integer.parseInt(pref.getString(
				Settings.KEY_PREF_GAME_OGLES_CONFIG,
				Constants.DEFAULT_OGLES_CONFIG));
		options.mIsSoundEffectEnabled = pref.getBoolean(Settings.KEY_PREF_GAME_SOUND_EFFECT,
				true);
		options.mCacheDir = app.getCacheDir().getAbsolutePath();
		options.mDBDir = app.getDataBasePath();
		options.mCoreConfigVersion = app.getCoreConfigVersion();
		options.mResourcePath = app.getResourcePath();
		options.mExternalFilePath = app.getCompatExternalFilesDir();
		options.mCardQuality = Integer.parseInt(pref.getString(
				Settings.KEY_PREF_GAME_IMAGE_QUALITY,
				Constants.DEFAULT_CARD_QUALITY_CONFIG));
		options.mIsFontAntiAliasEnabled = pref.getBoolean(Settings.KEY_PREF_GAME_FONT_ANTIALIAS,
				true);
		options.mIsPendulumScaleEnabled = pref.getBoolean(Settings.KEY_PREF_GAME_LAB_PENDULUM_SCALE, false);
		return options;
	}
	public ByteBuffer toNativeBuffer() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_MAX_SIZE);
		putInt(buffer, mOpenglVersion);
		putInt(buffer, mIsSoundEffectEnabled ? 1 : 0);
		putString(buffer, mCacheDir);
		putString(buffer, mDBDir);
		putString(buffer, mCoreConfigVersion);
		putString(buffer, mResourcePath);
		putString(buffer, mExternalFilePath);
		putInt(buffer, mCardQuality);
		putInt(buffer, mIsFontAntiAliasEnabled ? 1 : 0);
		putInt(buffer, mIsPendulumScaleEnabled ? 1 : 0);
		return buffer;
	}
	
	
	private void putString(ByteBuffer buffer, String str) {
		if (TextUtils.isEmpty(str)) {
			buffer.putInt(Integer.reverseBytes(0));
		} else {
			buffer.putInt(Integer.reverseBytes(str.getBytes().length));
			buffer.put(str.getBytes());
		}
	}
	
	@SuppressWarnings("unused")
	private void putChar(ByteBuffer buffer, char value) {
		Short svalue = (short) value;
		buffer.putShort((Short.reverseBytes(svalue)));
	}
	
	private void putInt(ByteBuffer buffer, int value) {
		buffer.putInt((Integer.reverseBytes(value)));
	}
}
