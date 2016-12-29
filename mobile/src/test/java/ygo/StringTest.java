package ygo;

import org.junit.Test;

import java.io.File;
import java.util.List;

import cn.garymb.ygomobile.utils.FileUtils;
import cn.ygo.ocgcore.ConfigManager;

public class StringTest {

    @Test
    public void fixStrings() {
//        String encoding = "utf-8";
//        String path = "assets\\data\\core\\3.5\\config\\strings.conf";
//        File file = new File(path);
//        if (file.exists()) {
//            List<String> lines = FileUtils.readLines(file.getAbsolutePath(), encoding);
//            FileUtils.writeLines(file.getAbsolutePath(), lines, encoding, "\n");
//        } else {
//            System.out.println(file.getAbsoluteFile());
//        }
    }
    @Test
    public void setFontiSize() {
        String path = "assets\\data\\core\\3.5\\config\\system.conf";
        File file = new File(path);
        new ConfigManager(file).setFontSize(14);
//        if (file.exists()) {
//            List<String> lines = FileUtils.readLines(file.getAbsolutePath(), encoding);
//            FileUtils.writeLines(file.getAbsolutePath(), lines, encoding, "\n");
//        } else {
//            System.out.println(file.getAbsoluteFile());
//        }
    }
}
