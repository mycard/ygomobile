package cn.garymb.ygomobile.plus.file;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.garymb.ygomobile.activities.BaseActivity;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;

public class FileActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
        , FileAdapter.OnPathChangedListener {
    private ListView mListView;
    private TextView lastPath;
    private Intent mIntent;
    private ImageButton newFolderButton;
    private ImageButton saveFileButton;
    //    private EditText inputText;
    private TextView headText;
    private LinearLayout footView;
    private FileOpenInfo mFileOpenInfo;
    private FileAdapter mFileAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (doIntent(getIntent())) {
            super.onCreate(savedInstanceState);
            enableBackHome();
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            initViews();
//            mListView.addFooterView(footView, null, false);
//            mListView.addHeaderView(footView, null, false);
//            mListView.addHeaderView(lastPath, null, false);
            mListView.setAdapter(mFileAdapter);
            mFileAdapter.setOnPathChangedListener(this);
            mListView.setOnItemClickListener(this);
            mListView.setOnItemLongClickListener(this);
            layout.addView(footView);
            layout.addView(lastPath);
            layout.addView(mListView);
            setContentView(layout);
            updateUI();
        } else {
            finish();
        }
    }

    private void initViews() {

        mFileAdapter = new FileAdapter(this);
        mListView = new ListView(this);
        footView = new LinearLayout(this);
        footView.setOrientation(LinearLayout.HORIZONTAL);
        lastPath = new TextView(this);
        lastPath.setPadding(VUiKit.dpToPx(8), 0, 0, 0);
        lastPath.setSingleLine();
        lastPath.setGravity(Gravity.CENTER_VERTICAL);
        lastPath.setMinHeight((int) getResources().getDimension(R.dimen.item_height));
//        lastPath.setTextColor(getResources().getColor(R.color.colorPrimary));
        lastPath.setText(R.string.last_path);
        lastPath.setOnClickListener((v) -> {
            File path = mFileAdapter.getCurPath();
            File dir = path == null ? null : path.getParentFile();
            if (dir != null) {
                if (mFileAdapter.setPath(dir.getAbsolutePath())) {
                    mFileAdapter.loadFiles();
                }
            }
        });

        newFolderButton = new ImageButton(this);
        newFolderButton.setPadding(VUiKit.dpToPx(4), 0, 0, VUiKit.dpToPx(8));
        newFolderButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        newFolderButton.setImageResource(R.drawable.ic_create_new_folder);
        newFolderButton.setBackgroundDrawable(null);

        saveFileButton = new ImageButton(this);
        saveFileButton.setPadding(VUiKit.dpToPx(4), 0, 0, VUiKit.dpToPx(8));
        saveFileButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        saveFileButton.setImageResource(R.drawable.ic_mode_save);
        saveFileButton.setBackgroundDrawable(null);
//        inputText = new EditText(this);
//        inputText.setPadding(VUiKit.dpToPx(8), 0, 0, 0);
//        inputText.setSingleLine();
//        inputText.setGravity(Gravity.CENTER_VERTICAL);
        headText = new TextView(this);
        headText.setPadding(VUiKit.dpToPx(8), 0, 0, 0);
        headText.setSingleLine();
        headText.setGravity(Gravity.CENTER_VERTICAL);
        headText.setMinHeight((int) getResources().getDimension(R.dimen.item_height));
        headText.setTextColor(getResources().getColor(R.color.colorPrimary));
        newFolderButton.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setSingleLine();
            builder.setTitle(R.string.create_folder);
            builder.setView(editText);
            builder.setNegativeButton(android.R.string.ok, (d, s) -> {
                if (editText.getText() != null) {
                    String name = String.valueOf(editText.getText());
                    if (TextUtils.isEmpty(name)) {
                        return;
                    }
                    File dir = new File(mFileAdapter.getCurPath(), name);
                    dir.mkdirs();
                    if (dir.isDirectory()) {
                        mFileAdapter.setPath(dir.getAbsolutePath());
                    }
                    mFileAdapter.loadFiles();
                }
                d.dismiss();
            });
            builder.setNeutralButton(android.R.string.cancel, (d, s) -> {
                d.dismiss();
            });
            builder.show();
        });
        saveFileButton.setOnClickListener((v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setSingleLine();
            builder.setTitle(R.string.intpu_name);
            builder.setView(editText);
            builder.setNegativeButton(android.R.string.ok, (d, s) -> {
                if (editText.getText() != null) {
                    String name = String.valueOf(editText.getText());
                    if (TextUtils.isEmpty(name)) {
                        return;
                    }
                    File file = new File(mFileAdapter.getCurPath(), name);
                    if (!file.isDirectory()) {
                        selectFile(file);
                    } else {
                        Toast.makeText(this, R.string.the_name_is_folder, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                d.dismiss();
            });
            builder.setNeutralButton(android.R.string.cancel, (d, s) -> {
                d.dismiss();
            });
            builder.show();
        });
        footView.addView(headText, new LinearLayout.LayoutParams(0,
                (int) (getResources().getDimension(R.dimen.item_height)), 1));
        footView.addView(saveFileButton, new ViewGroup.LayoutParams((int) getResources().getDimension(R.dimen.label_width_small),
                (int) (getResources().getDimension(R.dimen.item_height))));
        footView.addView(newFolderButton, new ViewGroup.LayoutParams((int) getResources().getDimension(R.dimen.label_width_small),
                (int) (getResources().getDimension(R.dimen.item_height))));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (doIntent(intent)) {
            updateUI();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = mFileAdapter.getItemById(id);
        if (file != null && file.isDirectory()) {
            if (mFileOpenInfo.getType() != FileOpenType.SelectFolder || mFileAdapter.isParent(file)) {
                if (mFileAdapter.setPath(file.getAbsolutePath())) {
                    mFileAdapter.loadFiles();
                }
                return;
            }
        }
        selectFile(file);
    }

    @Override
    public void onBackPressed() {
        File curPath = mFileAdapter.getCurPath();
        File dir = curPath.getParentFile();
        if (dir != null && mFileAdapter.setPath(dir.getAbsolutePath())) {
            mFileAdapter.loadFiles();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onChanged(File path) {
        if (path != null) {
            headText.setText(path.getPath());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        mFileAdapter.setMultiSelect(!mFileAdapter.isMultiSelect());
//        mFileAdapter.loadFiles();
        return true;
    }

    private void selectFile(File file) {
//        Log.i("kk", "select " + file);
        if (file != null) {
            Intent intent = new Intent().setData(Uri.fromFile(file));
            if (mIntent != null) {
                intent.putExtras(mIntent);
            }
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    private boolean doIntent(Intent intent) {
        mIntent = intent;
        if (intent != null && intent.hasExtra(Intent.EXTRA_STREAM)) {
            mFileOpenInfo = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            return true;
        }
        return false;
    }

    private void updateUI() {
        setTitle(mFileOpenInfo.getTitle());
        if (mFileOpenInfo.getType() == FileOpenType.SaveFile) {
            saveFileButton.setVisibility(View.VISIBLE);
        } else {
            saveFileButton.setVisibility(View.GONE);
        }
        if (mFileOpenInfo.getType() == FileOpenType.SelectFile) {
            newFolderButton.setVisibility(View.GONE);
        } else {
            newFolderButton.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(mFileOpenInfo.getDefPath())) {
            mFileAdapter.setPath(mFileOpenInfo.getDefPath());
        } else {
            mFileAdapter.setPath(mFileOpenInfo.getDefPath());
        }
        mFileAdapter.setFilefilter(mFileOpenInfo.getFileFilter());
        mFileAdapter.setOnlyFolder(mFileOpenInfo.getType() == FileOpenType.SelectFolder);
        mFileAdapter.loadFiles();
    }

    /***
     * @param title    标题
     * @param filetype 格式过滤
     * @param defPath  默认路径
     * @param showHide 显示隐藏文件
     * @param type     类型
     */
    public static Intent getIntent(Context context, String title, String filetype, String defPath, boolean showHide, FileOpenType type) {
        Intent intent = new Intent(context, FileActivity.class);
        intent.putExtra(Intent.EXTRA_STREAM, new FileOpenInfo(title, filetype, showHide, defPath, type));
        return intent;
    }
}
