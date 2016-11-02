package cn.ygo.ocgcore.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader implements IFileReader {
    private String dir;

    public FileReader(String dir) {
        this.dir = dir;
    }

    @Override
    public int read(String name, byte[] buf) {
        File file = new File(dir, name);
        if (file.exists()) {
            FileInputStream inputStream = null;
            int len = 0;
            try {
                inputStream = new FileInputStream(file);
                len = inputStream.read(buf);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
            return len;
        }
        return 0;
    }
}
