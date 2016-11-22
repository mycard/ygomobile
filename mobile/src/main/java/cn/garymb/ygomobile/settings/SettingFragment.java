package cn.garymb.ygomobile.settings;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.core.ResCheckTask;
import cn.garymb.ygomobile.filebrowser.DialogFileFilter;
import cn.garymb.ygomobile.filebrowser.OpenFileDialog;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.VUiKit;

import static cn.garymb.ygomobile.Constants.*;

public class SettingFragment extends BasePreferenceFragment {
    private String selectFile;
    private int selectWidth;
    private int selectHeigth;
    Preference selectPreference;

    public SettingFragment() {

    }

    @Override
    protected SharedPreferences getSharedPreferences() {
        return AppsSettings.get().getSharedPreferences();
    }

    private AppsSettings mSettings;
    private boolean isInit = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mSettings = AppsSettings.get();

        addPreferencesFromResource(R.xml.preference_game);
        bind(PREF_GAME_PATH, mSettings.getResourcePath());
        bind(PREF_GAME_VERSION, mSettings.getCoreConfigVersion());

        bind(PREF_SOUND_EFFECT, mSettings.isSoundEffect());
        bind(PREF_LOCK_SCREEN, mSettings.isLockSreenOrientation());
        bind(PREF_FONT_ANTIALIAS, mSettings.isFontAntiAlias());
        bind(PREF_IMMERSIVE_MODE, mSettings.isImmerSiveMode());
        bind(PREF_PENDULUM_SCALE, mSettings.isPendulumScale());
        bind(PREF_OPENGL_VERSION, Constants.PREF_DEF_OPENGL_VERSION);
        bind(PREF_IMAGE_QUALITY, Constants.PREF_DEF_IMAGE_QUALITY);

        bind(PREF_GAME_FONT, mSettings.getFontPath());
        bind(PREF_USE_EXTRA_CARD_CARDS, mSettings.isUseExtraCards());
        bind(SETTINGS_COVER, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_COVER).getAbsolutePath());
        bind(SETTINGS_CARD_BG, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_BG).getAbsolutePath());
        isInit = false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (PREF_GAME_FONT.equals(key)) {
            //选择ttf字体文件，保存
            onSelectFile(getString(R.string.dialog_select_font), ".ttf", (dialog, which) -> {
                File file = ((OpenFileDialog) dialog).getSelectFile();
                if (file != null) {
                    onPreferenceChange(preference, file.getAbsolutePath());
                }
            }, null);
        } else if (SETTINGS_COVER.equals(key)) {
            //选择图片，裁剪保存
            startPhotoCut(preference, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_COVER).getAbsolutePath()
                    , Constants.CORE_SKIN_CARD_COVER_SIZE[0], Constants.CORE_SKIN_CARD_COVER_SIZE[1]);
        } else if (SETTINGS_CARD_BG.equals(key)) {
            //选择图片，裁剪保存
            startPhotoCut(preference, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_BG).getAbsolutePath()
                    , Constants.CORE_SKIN_BG_SIZE[0], Constants.CORE_SKIN_BG_SIZE[1]);
        }
        return false;
    }

    private void setPendlumScale(boolean ok) {
        File file = new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_PENDLUM_PATH);
        File filebak = new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_PENDLUM_PATH + "_bak");
        if (ok) {
            //rename
            if (file.exists()) {
                IOUtils.delete(filebak);
                file.renameTo(filebak);
            }
        } else {
            //rename
            if (filebak.exists()) {
                IOUtils.delete(file);
                filebak.renameTo(file);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        super.onPreferenceChange(preference, value);
        if (!isInit) {
            String key = preference.getKey();
            if (PREF_PENDULUM_SCALE.equals(key)) {
                setPendlumScale((Boolean) value);
            } else if (PREF_USE_EXTRA_CARD_CARDS.equals(key)) {
                //选择数据库，复制到ygocore，
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                if (checkBoxPreference.isChecked()) {
                    onSelectFile(getString(R.string.dialog_select_database), ".cdb", (dialog, which) -> {
                        File file = ((OpenFileDialog) dialog).getSelectFile();
                        //复制
                        if (file != null) {
                            //处理数据库
                            ProgressDialog dlg = ProgressDialog.show(getActivity(), null, getString(R.string.copy_databse));
                            VUiKit.defer().when(() -> {
                                File db = new File(mSettings.getResourcePath(), Constants.DATABASE_NAME);
                                InputStream in = null;
                                try {
                                    if (db.exists()) {
                                        db.delete();
                                    }
                                    in = new FileInputStream(file);
                                    //复制
                                    IOUtils.copyToFile(in, db.getAbsolutePath());
                                    //处理数据
                                    ResCheckTask.doSomeTrickOnDatabase(db.getAbsolutePath());
                                    return true;
                                } catch (Exception e) {

                                } finally {
                                    IOUtils.close(in);
                                }
                                return false;
                            }).fail((e) -> {
                                dlg.dismiss();
                                mSettings.setUseExtraCards(false);
                            }).done((ok) -> {
                                dlg.dismiss();
                                if (ok) {
                                    mSettings.setUseExtraCards(true);
                                }
                            });
                        }
                    }, (dlg, s) -> {
                        checkBoxPreference.setChecked(false);
                    });
                } else {
                    mSettings.setUseExtraCards(false);
                }
                return true;
            }
            if (preference instanceof CheckBoxPreference) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
                mSharedPreferences.edit().putBoolean(preference.getKey(), checkBoxPreference.isChecked()).apply();
                return true;
            }
            boolean rs = super.onPreferenceChange(preference, value);
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                mSharedPreferences.edit().putString(preference.getKey(), listPreference.getValue()).apply();
            }
            return rs;
        }
        return true;
    }

    private void onSelectFile(String title, String ex, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener cancel) {
        //选择一个存档打开
        final OpenFileDialog fileDialog = new OpenFileDialog(getActivity());
        fileDialog.setDefTitle(title);
        fileDialog.setCurPath(mSettings.getResourcePath());
        fileDialog.setDialogFileFilter(new DialogFileFilter(false, false, ex));
        fileDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.ok), listener);
        fileDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getString(android.R.string.cancel), cancel);
        fileDialog.show();
    }

    public void startPhotoCut(Preference preference, String saveFile, int width, int height) {
        if (TextUtils.isEmpty(saveFile)) return;
        selectFile = saveFile;
        selectWidth = width;
        selectHeigth = height;
        selectPreference = preference;
        File file = new File(saveFile);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        try {
            this.startActivityForResult(intent, Constants.REQUEST_CHOOSE_IMG);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.no_find_image_selector, Toast.LENGTH_SHORT).show();
        }
    }

    protected void openPhotoCut(Uri uri, String saveFile, int width, int height) {
        // 裁剪图片
        if (TextUtils.isEmpty(saveFile)) return;
        File file = new File(saveFile);
        Uri saveimgUri = Uri.fromFile(file);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);// 黑边
        intent.putExtra("scaleUpIfNeeded", true);// 黑边

        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveimgUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        try {
            startActivityForResult(intent, Constants.REQUEST_CUT_IMG);
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.no_find_image_cutor, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHOOSE_IMG) {// 选择图片
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    openPhotoCut(uri, selectFile, selectWidth, selectHeigth);
                }
            }
        } else if (requestCode == Constants.REQUEST_CUT_IMG) {// 裁剪完图片
            onPreferenceChange(selectPreference, selectFile);
        }
    }
}

