package cn.ygo.ocgcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.garymb.ygomobile.utils.IOUtils;

public class LimitManager {
    private static LimitManager sManager = new LimitManager();
    private Map<Integer, LimitList> mListMap = new HashMap<>();

    private volatile boolean isLoad = false;

    private LimitManager() {

    }

    public boolean isLoad() {
        return isLoad;
    }

    public static LimitManager get() {
        return sManager;
    }

    public int getCount() {
        return mListMap.size();
    }

    public List<Integer> getLists() {
        List<Integer> ids = new ArrayList<>();
        ids.addAll(mListMap.keySet());
        Collections.sort(ids, (o1, o2) -> {
            return o1 - o2;
        });
        return ids;
    }

    public LimitList getLimit(int postion) {
        return mListMap.get(getLists().get(postion));
    }

    public LimitList getLimitFromIndex(int pos) {
        return mListMap.get(Integer.valueOf(pos));
    }


    public boolean load() {
        File stringfile = new File(AppsSettings.get().getResourcePath(),
                String.format(Constants.CORE_LIMIT_PATH, AppsSettings.get().getCoreConfigVersion()));
        return loadFile(stringfile.getAbsolutePath());
    }

    public boolean loadFile(String path) {
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
        List<LimitList> limitListList = new ArrayList<>();
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
                        mListMap.put(index, tmp);
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
//        Collections.sort(limitListList, (o1, o2) -> {
//            if (o1.getName() == null || o2.getName() == null) {
//                return 0;
//            }
//            String date1 = o1.getName().split("\\b")[0];
//            String date2 = o2.getName().split("\\b")[0];
//
//            String[] dates1 = date1.split("\\.");
//            String[] dates2 = date2.split("\\.");
//            if( == 0){
//                return
//            }
//            return date2.compareTo(date1);
//        });
//        int i = 0;
//        for (LimitList limitList : limitListList) {
//            i++;
//            mListMap.put(Integer.valueOf(i), limitList);
//        }
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
