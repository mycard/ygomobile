package cn.ygo.ocgcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cn.garymb.ygomobile.utils.IOUtils;
import cn.ygo.ocgcore.enums.LimitType;

public class LimitManager {
    private static LimitManager sManager = new LimitManager();
    private Map<Integer, LimitList> mListMap = new HashMap<>();

    private boolean isLoad = false;

    private LimitManager() {

    }

    public boolean isLoad() {
        return isLoad;
    }

    public static LimitManager get() {
        return sManager;
    }

    public Collection<Integer> getLists() {
        return mListMap.keySet();
    }

    public LimitList getLimit(int pos) {
        return mListMap.get(Integer.valueOf(pos));
    }

    public synchronized boolean loadFile(String path) {
        if (path == null || path.length() == 0) {
            return false;
        }
        File file = new File(path);
        if (file.isDirectory() || !file.exists()) {
            return false;
        }
        mListMap.clear();
        isLoad = false;
        InputStreamReader in = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            in = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(in);
            String line = null;
            String name = null;
            LimitList tmp = null;
            int index = 1;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.startsWith("!")) {
                    name = line.substring(1);
                    if (tmp != null) {
                        mListMap.put(Integer.valueOf(index), tmp);
                    }
                    index++;
                    tmp = new LimitList(name);
                } else {
                    String[] words = line.trim().split("[\t| ]+");
                    if (words.length >= 2) {
                        long id = toNumber(words[0]);
                        int count = (int) toNumber(words[1]);
                        switch (count) {
                            case 0:
                                tmp.addForbidden(id);
                                break;
                            case 1:
                                tmp.addLimit(id);
                                break;
                            case 2:
                                tmp.addSemiLimit(id);
                                break;
                        }
                    }

                }
            }
        } catch (Exception e) {

        } finally {
            IOUtils.close(inputStream);
            IOUtils.close(in);
        }
        isLoad = true;
        return true;
    }

    private long toNumber(String str) {
        long i = 0;
        try {
            if (str.startsWith("0x")) {
                i = Long.parseLong(str.replace("0x", ""), 0x10);
            } else {
                i = Long.parseLong(str);
            }
        } catch (Exception e) {

        }
        return i;
    }
}
