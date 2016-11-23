package cn.garymb.ygomobile.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.Preference;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;

import static cn.garymb.ygomobile.Constants.REQUEST_CHOOSE_FILE;
import static cn.garymb.ygomobile.Constants.REQUEST_CHOOSE_IMG;

public abstract class PreferenceFragmentPlus extends BasePreferenceFragment {
    private Preference curPreference;
    private String mOutFile;

    protected void onChooseFileOk(Preference preference, String file) {
        onPreferenceChange(preference, file);
    }

    protected void onChooseFileFail(Preference preference) {

    }

    /***
     * @param preference
     * @param type       *\/*
     */
    protected void showFileChooser(Preference preference, String type, String title) {
        curPreference = preference;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(type);
        try {
            startActivityForResult(Intent.createChooser(intent, title),
                    REQUEST_CHOOSE_FILE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), R.string.no_find_file_selectotr, Toast.LENGTH_SHORT)
                    .show();
            onChooseFileFail(preference);
        }
    }

    /***
     */
    protected void showImageCropChooser(Preference preference, String title, String outFile, boolean isJpeg, int width, int height) {
        mOutFile = outFile;
        curPreference = preference;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);// 黑边
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(outFile)));
        intent.putExtra("outputFormat", isJpeg ? Bitmap.CompressFormat.JPEG.toString() : Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        try {
            startActivityForResult(Intent.createChooser(intent, title),
                    REQUEST_CHOOSE_IMG);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), R.string.no_find_file_selectotr, Toast.LENGTH_SHORT)
                    .show();
            onChooseFileFail(preference);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CHOOSE_IMG) {
            if (resultCode == Activity.RESULT_OK) {
                onChooseFileOk(curPreference, mOutFile);
            } else {
                onChooseFileFail(curPreference);
            }
        } else if (requestCode == Constants.REQUEST_CHOOSE_FILE) {
            //选择文件
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    File file =new File(uri.getPath());
                    if(file.exists()) {
                        onChooseFileOk(curPreference, file.getAbsolutePath());
                        return;
                    }
                }
            }
            onChooseFileFail(curPreference);
        }
    }
}
