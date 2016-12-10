package cn.garymb.ygomobile.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {

    public static List<String> readLines(String file, String encoding) {
        InputStreamReader in = null;
        FileInputStream inputStream = null;
        List<String> lines = new ArrayList<>();
        try {
            inputStream = new FileInputStream(file);
            in = new InputStreamReader(inputStream, encoding);
            BufferedReader reader = new BufferedReader(in);
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {

        } finally {
            IOUtils.close(in);
            IOUtils.close(inputStream);
        }
        return lines;
    }

    public static boolean writeLines(String file, List<String> lines, String encoding, String newLine) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            byte[] newL = newLine.getBytes(encoding);
            if (lines != null) {
                for (String line : lines) {
                    if (line != null) {
                        outputStream.write(line.getBytes(encoding));
                    }
                    outputStream.write(newL);
                }
            }
        } catch (Exception e) {
            return false;
        } finally {
            IOUtils.close(outputStream);
        }
        return true;
    }
}
