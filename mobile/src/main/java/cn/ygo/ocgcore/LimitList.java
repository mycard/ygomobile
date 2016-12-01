package cn.ygo.ocgcore;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import cn.ygo.ocgcore.enums.LimitType;

public class LimitList {
    private String name = "?";
    /**
     * 0
     */
    public final List<Long> forbidden;
    /**
     * 1
     */
    public final List<Long> limit;
    /**
     * 2
     */
    public final List<Long> semiLimit;

    public final List<Long> allList;

    public LimitList() {
        forbidden = new ArrayList<>();
        limit = new ArrayList<>();
        semiLimit = new ArrayList<>();
        allList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public LimitList(String name) {
        this();
        this.name = name;
    }

    public void addSemiLimit(Long id) {
        if (!semiLimit.contains(id)) {
            semiLimit.add(id);
            allList.add(id);
        }
    }

    public boolean isSemiLimit(Long id) {
        return semiLimit.contains(id);
    }

    public void addLimit(Long id) {
        if (!limit.contains(id)) {
            limit.add(id);
            allList.add(id);
        }
    }

    public boolean has(Long id) {
        return allList.contains(id);
    }

    public boolean isLimit(Long id) {
        return limit.contains(id);
    }

    public void addForbidden(Long id) {
        if (!forbidden.contains(id)) {
            forbidden.add(id);
            allList.add(id);
        }
    }

    public boolean isForbidden(Long id) {
        return forbidden.contains(id);
    }

    public List<Long> getCodeList() {
        return allList;
    }

    public boolean check(Long code, LimitType type) {
        if (type == LimitType.None) {
            return allList.contains(code);
        } else if (type == LimitType.Limit) {
            return limit.contains(code);
        } else if (type == LimitType.SemiLimit) {
            return semiLimit.contains(code);
        } else if (type == LimitType.Forbidden) {
            return forbidden.contains(code);
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return "LimitList{" +
                "name='" + name + '\'' +
                ", forbidden=" + forbidden +
                ", limit=" + limit +
                ", semiLimit=" + semiLimit +
                '}';
    }
}
