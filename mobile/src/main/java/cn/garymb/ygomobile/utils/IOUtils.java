package cn.garymb.ygomobile.utils;


import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] cache = new byte[1024 * 8];
        int len;
        while ((len = in.read(cache)) != -1) {
            out.write(cache, 0, len);
        }
    }

    public static void copyToFile(InputStream in, String file) {
        FileOutputStream outputStream = null;
        try {
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
