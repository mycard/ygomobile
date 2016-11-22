package cn.garymb.ygomobile.filebrowser;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


import java.io.File;

import cn.garymb.ygomobile.lite.R;

public class SaveFileDialog extends BaseFileDialog {
    private EditText mEditText;
    private Button mNewBtn;
    private boolean mHideCreateButton = false;

    public SaveFileDialog(Context context) {
        super(context);
        setHighHint(false);
    }

    public boolean isHideCreateButton() {
        return mHideCreateButton;
    }

    public void setHideCreateButton(boolean hideCreateButton) {
        mHideCreateButton = hideCreateButton;
        if (mEditText != null) {
            if (hideCreateButton) {
                mNewBtn.setVisibility(View.GONE);
            } else {
                mNewBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected LinearLayout createMainLayout(Context context) {
        LinearLayout linearLayout = super.createMainLayout(context);
        LinearLayout tmplayout = new LinearLayout(context);
        tmplayout.setOrientation(LinearLayout.HORIZONTAL);
        mEditText = new EditText(context);
        mEditText.setLayoutParams(new LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1));
        mEditText.setGravity(Gravity.CENTER_VERTICAL);
        mNewBtn = addDirButton(context);
        if (mHideCreateButton) {
            mNewBtn.setVisibility(View.GONE);
        }
        tmplayout.addView(mEditText);
        tmplayout.addView(mNewBtn);
        linearLayout.addView(tmplayout);
        return linearLayout;
    }

    protected void createDir(File file) {
        if (!file.exists()) {
            file.mkdirs();
        } else if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
    }


    @Override
    public File getSelectFile() {
        File file = super.getSelectFile();
        if (file == null) {
            if (!TextUtils.isEmpty(mEditText.getText())) {
                return new File(mCurPath, "" + mEditText.getText());
            }
        } else {
            return file;
        }
        return new File(mCurPath);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @SuppressWarnings("deprecation")
    private Button addDirButton(Context context) {
        Button btn = new Button(context);
        if (Build.VERSION.SDK_INT >= 23) {
            btn.setTextAppearance(android.R.style.TextAppearance_DeviceDefault_Small);
        } else {
            btn.setTextAppearance(context, android.R.style.TextAppearance_DeviceDefault_Small);
        }
        btn.setText(R.string.create_folder);
        btn.setGravity(Gravity.CENTER);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(mCurPath, mEditText.getText()
                        .toString());
                createDir(file);
                updateFile(file.getAbsolutePath());
                mEditText.setText("");
            }
        });
        return btn;
    }

    public SaveFileDialog setEditText(String str) {
        if (mEditText != null)
            mEditText.setText(str);
        return this;
    }
}