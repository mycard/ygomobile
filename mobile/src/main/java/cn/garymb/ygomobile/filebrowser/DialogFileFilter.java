package cn.garymb.ygomobile.filebrowser;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;

public class DialogFileFilter implements FilenameFilter {
    protected String[] filters;
    protected boolean isAll;
    protected boolean onlyDir;
    protected boolean showHide;

    /***
     * @param filters 后缀格式
     * @param onlyDir 仅文件夹
     */
    public DialogFileFilter(boolean onlyDir, String... filters) {
        this(onlyDir, false, filters);
    }

    /***
     * @param filters  后缀格式
     * @param onlyDir  仅文件夹
     * @param showHide 显示隐藏文件
     */
    public DialogFileFilter(boolean onlyDir, boolean showHide, String... filters) {
        this.isAll = (filters == null || filters.length == 0);
        if (!this.isAll) {
            this.filters = filters;
        }
        this.onlyDir = onlyDir;
        this.showHide = showHide;
    }

    @Override
    public boolean accept(File dir, String filename) {
        if (filename == null) return false;
        if (!showHide) {
            if (filename.startsWith(".")) return false;
        }
        File file = new File(dir, filename);
        if (onlyDir) {
            return file.isDirectory();
        }
        if (file.isDirectory())
            return true;
        if (isAll) return true;
        filename = filename.toLowerCase(Locale.US);
        for (String f : filters) {
            if (filename.endsWith(f)) {
                return true;
            }
        }
        return false;
    }
}