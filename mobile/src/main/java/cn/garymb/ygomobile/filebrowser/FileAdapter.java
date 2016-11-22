package cn.garymb.ygomobile.filebrowser;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.garymb.ygomobile.lite.R;

class FileAdapter extends ArrayAdapter<File> {

    protected boolean isHighHint = false;

    protected Drawable folderIcon;
    protected Drawable fileIcon;
    protected int selectColor = 0xff0099cc;
    protected int normalColor = 0x00000000;
    protected Context context;
    protected File selectFile;

    @SuppressWarnings("deprecation")
    public FileAdapter(Context context, List<File> files) {
        super(context, android.R.layout.simple_list_item_1, files);
        this.context = context;
        folderIcon = ContextCompat.getDrawable(context, R.drawable.ic_folder_black);
        fileIcon = null;
    }

    public void setHighHint(boolean isHighHint) {
        this.isHighHint = isHighHint;
    }

    protected boolean canSelect(File file) {
        return file != null && file.isFile();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView view;
        if (convertView == null) {
            convertView = super.getView(position, convertView, parent);
            view = (TextView) convertView;
            view.setSingleLine();
            view.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            view = (TextView) convertView;
        }
        final File file = getItem(position);
        if (position != 0) {
            view.setText(" " + file.getName());
            if (file.isDirectory()) {
                setDrawable(view, folderIcon);
                if (isHighHint) {
                    view.setBackgroundColor(normalColor);
                }
            } else {
                //new SetDrawableTask(this, view).execute(file);
                setDrawable(view, fileIcon);
                if (isHighHint) {
                    if (selectFile != null &&
                            TextUtils.equals(selectFile.getAbsolutePath(), file.getAbsolutePath())) {
                        view.setBackgroundColor(selectColor);
                    } else {
                        view.setBackgroundColor(normalColor);
                    }
                }
            }
        } else {
            setDrawable(view, null);
            view.setText(". . .");
        }
        return view;
    }

    protected void setDrawable(TextView view, Drawable drawable) {
        setDrawable(view, drawable, 60);
    }

    public void setFolderIcon(Drawable folderIcon) {
        this.folderIcon = folderIcon;
    }

    public void setFileIcon(Drawable fileIcon) {
        this.fileIcon = fileIcon;
    }

    protected void setDrawable(TextView view, Drawable drawable, int w) {
        if (view != null) {
            if (drawable != null) {
                drawable.setBounds(0, 0, 60, w);
                view.setCompoundDrawables(drawable, null, null, null);
            } else {
                view.setCompoundDrawables(null, null, null, null);
            }
        }
    }
}