package cn.garymb.ygomobile.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    private static final String TAG = "ioUtils";

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean rename(String src,String to){
        return new File(src).renameTo(new File(to));
    }
    public static boolean isDirectory(Context context, String assets) {
        String[] files = new String[0];
        try {
            files = context.getAssets().list(assets);
        } catch (IOException e) {

        }
        if (files != null && files.length > 0) {
            return true;
        }
        return false;
    }

    public static String join(String path1, String path2) {
        if (TextUtils.isEmpty(path1)) {
            return path2;
        }
        if (TextUtils.isEmpty(path2)) {
            return path1;
        }
        if (!path1.endsWith("/")) {
            path1 += "/";
        }
        if (path2.startsWith("/")) {
            path2 = path2.substring(1);
        }
        return path1 + path2;
    }

    public static String getName(String path) {
        return new File(path).getName();
    }

    public static int copyFilesFromAssets(Context context, String assets, String toPath, boolean update) throws IOException {
        AssetManager am = context.getAssets();
        String[] files = am.list(assets);
        if (files == null) {
            return 0;
        }
        if (files.length == 0) {
            //is file
            String file = getName(assets);
            String tofile = join(toPath, file);
            if (update || !new File(tofile).exists()) {
                Log.i(TAG, "copy1:" + assets + "-->" + tofile);
                copyToFile(am.open(assets), tofile);
            }
            return 1;
        } else {
            int count = 0;
            for (String file : files) {
                String path = join(assets, file);
                if (isDirectory(context, path)) {
                    Log.i(TAG, "copy dir:" + path + "-->" + join(toPath, file));
                    count += copyFilesFromAssets(context, path, join(toPath, file), update);
                } else {
                    File f = new File(join(toPath, file));
                    if (update || !f.exists()) {
                        Log.i(TAG, "copy2:" + path + "-->" + f.getAbsolutePath());
                        copyToFile(am.open(path), f.getAbsolutePath());
                    } else {
                        Log.i(TAG, "copy ignore:" + path + "-->" + f.getAbsolutePath());
                    }
                    count++;
                }
            }
            return count;
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] cache = new byte[1024 * 8];
        int len;
        while ((len = in.read(cache)) != -1) {
            out.write(cache, 0, len);
        }
    }

    public static boolean hasAssets(Context context,String name){
        try {
            context.getAssets().open(name);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void copyToFile(InputStream in, String file) {
        FileOutputStream outputStream = null;
        try {
            File dir = new File(file).getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            outputStream = new FileOutputStream(file);
            copy(in, outputStream);
        } catch (Exception e) {

        } finally {
            close(outputStream);
            close(in);
        }
    }

    public static boolean createNoMedia(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.isDirectory()) {
            //
            File n = new File(file, ".nomedia");
            if (n.exists()) {
                return true;
            }
            try {
                n.createNewFile();
                return true;
            } catch (IOException e) {
            }
        }
        return false;
    }

}
