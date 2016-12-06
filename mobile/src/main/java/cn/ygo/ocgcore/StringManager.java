package cn.ygo.ocgcore;

import android.text.TextUtils;

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
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.StringUtils;
import cn.ygo.ocgcore.enums.CardOt;

public class StringManager {
    private String PRE_SYSTEM = "!system";
    private String PRE_SETNAME = "!setname";
    private final Map<Integer, String> mSystem = new HashMap<>();
    private final Map<Long, CardSet> mCardSets = new HashMap<>();
    //    private final Map<Long, String> mSetname = new HashMap<>();
    private static StringManager sStringManager = new StringManager();
    private volatile boolean isLoad = false;

    private StringManager() {

    }

    public static StringManager get() {
        return sStringManager;
    }

    public boolean isLoad() {
        return isLoad;
    }

    public boolean load() {
        File stringfile = new File(AppsSettings.get().getResourcePath(),
                String.format(Constants.CORE_STRING_PATH, AppsSettings.get().getCoreConfigVersion()));
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
        mSystem.clear();
        mCardSets.clear();
        isLoad = false;
        InputStreamReader in = null;
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            in = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(in);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || (!line.startsWith(PRE_SYSTEM) && !line.startsWith(PRE_SETNAME))) {
                    continue;
                }
                String[] words = line.split("[\t| ]+");
//
                if (words.length >= 3) {
                    if (PRE_SETNAME.equals(words[0])) {
//                        System.out.println(Arrays.toString(words));
                        //setcode
                        long id = toNumber(words[1]);
                        mCardSets.put(id, new CardSet(id, words[2]));
                    } else {
                        mSystem.put((int) toNumber(words[1]), words[2]);
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

    public Map<Integer, String> getSystem() {
        return mSystem;
    }

    public List<CardSet> getCardSets() {
        List<CardSet> sets=new ArrayList<>();
        sets.addAll(mCardSets.values());
        Collections.sort(sets, CardSet.NAME_ASC);
        return sets;
    }

    public String getSetName(long key) {
        CardSet set = mCardSets.get(key);
        if (set != null) {
            set.getName();
        }
        return null;
    }

    public String getSystemString(int key) {
        return mSystem.get(Integer.valueOf(key));
    }

    public String getSystemString(int start, long value) {
        return getSystemString(start + value2Index(value));
    }

    public String getLimitString(long value) {
//        String str =
//        Log.d("kk", value + "=" + str);
        return getSystemString((int) (Constants.STRING_LIMIT_START + value));
    }

    public String getTypeString(long value) {
        return getSystemString(Constants.STRING_TYPE_START, value);
    }

    public String getAttributeString(long value) {
        return getSystemString(Constants.STRING_ATTRIBUTE_START, value);
    }

    public String getRaceString(long value) {
        String race = getSystemString(Constants.STRING_RACE_START, value);
        if (TextUtils.isEmpty(race)) {
            return String.format("0x%X", value);
        }
        return race;
    }

    public String getOtString(int ot) {
        if (ot == CardOt.All.ordinal()) {
            return "-";
        }
        try {
            String str = getSystemString(Constants.STRING_OT_START + ot);
            if (TextUtils.isEmpty(str)) {
                return "" + ot;//String.valueOf(CardOt.values()[ot]);
            }
            return StringUtils.toDBC(str);
        } catch (Exception e) {
            return "" + ot;
        }
    }

    public String getCategoryString(long value) {
        return getSystemString(Constants.STRING_CATEGORY_START, value);
    }

    public int value2Index(long type) {
        //0 1 2 3 4
        //1 2 4 8 16
        int i = 0;
        long start;
        do {
            start = (long) Math.pow(2, i);
            if (start == type) {
                return i;
            } else if (start > type) {
                return -1;
            }
            i++;
        }
        while (start < type);
        return i;
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
