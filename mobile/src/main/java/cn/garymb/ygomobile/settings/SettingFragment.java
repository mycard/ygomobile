package cn.garymb.ygomobile.settings;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.core.ResCheckTask;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.DialogPlus;
import cn.garymb.ygomobile.plus.PreferenceFragmentPlus;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.utils.IOUtils;

import static cn.garymb.ygomobile.Constants.PREF_DECK_DELETE_DILAOG;
import static cn.garymb.ygomobile.Constants.PREF_FONT_ANTIALIAS;
import static cn.garymb.ygomobile.Constants.PREF_GAME_FONT;
import static cn.garymb.ygomobile.Constants.PREF_GAME_PATH;
import static cn.garymb.ygomobile.Constants.PREF_GAME_VERSION;
import static cn.garymb.ygomobile.Constants.PREF_IMAGE_QUALITY;
import static cn.garymb.ygomobile.Constants.PREF_IMMERSIVE_MODE;
import static cn.garymb.ygomobile.Constants.PREF_LOCK_SCREEN;
import static cn.garymb.ygomobile.Constants.PREF_OPENGL_VERSION;
import static cn.garymb.ygomobile.Constants.PREF_PENDULUM_SCALE;
import static cn.garymb.ygomobile.Constants.PREF_SENSOR_REFRESH;
import static cn.garymb.ygomobile.Constants.PREF_SOUND_EFFECT;
import static cn.garymb.ygomobile.Constants.PREF_USE_EXTRA_CARD_CARDS;
import static cn.garymb.ygomobile.Constants.SETTINGS_CARD_BG;
import static cn.garymb.ygomobile.Constants.SETTINGS_COVER;
import static cn.garymb.ygomobile.core.ResCheckTask.getDatapath;

public class SettingFragment extends PreferenceFragmentPlus {

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
        bind(PREF_SENSOR_REFRESH, mSettings.isSensorRefresh());
        bind(PREF_OPENGL_VERSION, mSettings.getOpenglVersion());
        bind(PREF_IMAGE_QUALITY, mSettings.getCardQuality());
        bind(PREF_GAME_FONT, mSettings.getFontPath());
        bind(PREF_DECK_DELETE_DILAOG, mSettings.isDialogDelete());
        bind(PREF_USE_EXTRA_CARD_CARDS, mSettings.isUseExtraCards());
        bind(SETTINGS_COVER, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_COVER).getAbsolutePath());
        bind(SETTINGS_CARD_BG, new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_BG).getAbsolutePath());
        isInit = false;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        super.onPreferenceChange(preference, value);
        if (!isInit) {
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (PREF_PENDULUM_SCALE.equals(key)) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            setPendlumScale(checkBoxPreference.isChecked());
        } else if (PREF_GAME_FONT.equals(key)) {
            //选择ttf字体文件，保存
            showFileChooser(preference, "*.ttf", mSettings.getFontDirPath(), getString(R.string.dialog_select_font));
        } else if (SETTINGS_COVER.equals(key)) {
            //显示图片对话框？
            String outFile = new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_COVER).getAbsolutePath();
            showImageDialog(preference, getString(R.string.card_cover),
                    outFile,
                    true, Constants.CORE_SKIN_CARD_COVER_SIZE[0], Constants.CORE_SKIN_CARD_COVER_SIZE[1]);
        } else if (SETTINGS_CARD_BG.equals(key)) {
            //显示图片对话框？
            String outFile = new File(mSettings.getCoreSkinPath(), Constants.CORE_SKIN_BG).getAbsolutePath();
            showImageDialog(preference, getString(R.string.game_bg), outFile, true, Constants.CORE_SKIN_BG_SIZE[0], Constants.CORE_SKIN_BG_SIZE[1]);
        } else if (PREF_USE_EXTRA_CARD_CARDS.equals(key)) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            if (checkBoxPreference.isChecked()) {
                checkBoxPreference.setChecked(false);
                mSettings.setUseExtraCards(false);
                showFileChooser(checkBoxPreference, "*.cdb", mSettings.getResourcePath(), getString(R.string.dialog_select_database));
            } else {
                mSettings.setUseExtraCards(false);
            }
        }
        return false;
    }

    @Override
    protected void onChooseFileFail(Preference preference) {
        super.onChooseFileFail(preference);
        String key = preference.getKey();
        if (PREF_USE_EXTRA_CARD_CARDS.equals(key)) {
            mSettings.setUseExtraCards(false);
            ((CheckBoxPreference) preference).setChecked(false);
        }
    }

    @Override
    protected void onChooseFileOk(Preference preference, String file) {
        String key = preference.getKey();
        if (Constants.DEBUG)
            Log.i("kk", "onChooseFileOk:" + key + ",file=" + file);
        if (SETTINGS_COVER.equals(key) || SETTINGS_CARD_BG.equals(key)) {
            super.onChooseFileOk(preference, file);
            onPreferenceClick(preference);
        }
        if (PREF_USE_EXTRA_CARD_CARDS.equals(key)) {
            ((CheckBoxPreference) preference).setChecked(true);
            mSettings.setUseExtraCards(true);
            copyDataBase(preference, file);
        } else {
            super.onChooseFileOk(preference, file);
        }
    }

    private void showImageDialog(Preference preference, String title, String outFile, boolean isJpeg, int outWidth, int outHeight) {
        int width = getResources().getDisplayMetrics().widthPixels;
        DialogPlus builder = new DialogPlus(getActivity());
        final ImageView imageView = new ImageView(getActivity());
        FrameLayout frameLayout = new FrameLayout(getActivity());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        builder.setTitle(title);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        frameLayout.addView(imageView, layoutParams);
        builder.setView(frameLayout);
        builder.setLeftButtonText(R.string.settings);
        builder.setLeftButtonListener((dlg, s) -> {
            showImageCropChooser(preference, getString(R.string.dialog_select_image), outFile,
                    isJpeg, outWidth, outHeight);
            dlg.dismiss();
        });
//        builder.setOnCancelListener((dlg) -> {
//            BitmapUtil.destroy(imageView.getDrawable());
//        });
        builder.show();
        File img = new File(outFile);
        if (img.exists()) {
            Glide.with(this).load(img).signature(new StringSignature(img.getName() + img.lastModified()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(outWidth, outHeight)
                    .into(imageView);
        }
    }

    private void copyDataBase(Preference preference, String file) {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
        ProgressDialog dlg = ProgressDialog.show(getActivity(), null, getString(R.string.copy_databse));
        VUiKit.defer().when(() -> {
            File db = new File(mSettings.getResourcePath(), Constants.DATABASE_NAME);
            InputStream in = null;
            try {
                if (!TextUtils.equals(file, db.getAbsolutePath())) {
                    if (db.exists()) {
                        db.delete();
                    }
                    in = new FileInputStream(file);
                    //复制
                    IOUtils.copyToFile(in, db.getAbsolutePath());
                }
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
            checkBoxPreference.setChecked(false);
            Toast.makeText(getActivity(), R.string.restart_app, Toast.LENGTH_SHORT).show();
        }).done((ok) -> {
            dlg.dismiss();
            checkBoxPreference.setChecked(ok);
            mSettings.setUseExtraCards(ok);
            Toast.makeText(getActivity(), R.string.restart_app, Toast.LENGTH_SHORT).show();
        });
    }

    private void setPendlumScale(boolean ok) {
        if (Constants.DEBUG)
            Log.i("kk", "setPendlumScale " + ok);
        File file = new File(mSettings.getResourcePath(), Constants.CORE_SKIN_PENDULUM_PATH);
        if (ok) {
            //rename
            ProgressDialog dlg = ProgressDialog.show(getActivity(), null, getString(R.string.coping_pendulum_image));
            VUiKit.defer().when(() -> {
                try {
                    IOUtils.createFolder(file);
                    IOUtils.copyFilesFromAssets(getActivity(), getDatapath(Constants.CORE_SKIN_PENDULUM_PATH),
                            file.getAbsolutePath(), false);
                } catch (IOException e) {
                }
            }).done((re) -> {
                dlg.dismiss();
            });
        } else {
            IOUtils.delete(file);
        }
    }
}

