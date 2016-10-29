package cn.garymb.ygomobile;


import android.content.ContextWrapper;

import java.io.File;
import java.lang.reflect.Field;

import cn.garymb.ygomobile.model.AppsSettings;

public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        updataAppOutCacheDir();
    }
    boolean updataAppOutCacheDir() {
        String OutPath = getSettings().getResourcePath();
        String contextImplFieldName = "mBase";
        String cacheDirFieleName = "mCacheDir";
        String contextImplClassPath = "android.app.ContextImpl";
        File newFile = new File(OutPath);

        //修改cache路径
        Class classType = ContextWrapper.class;
        Field[] fields = classType.getDeclaredFields();
        Object object = null;
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.getName().equals(contextImplFieldName)) {
                try {
                    object = f.get(this);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        if (!newFile.exists())
            newFile.mkdirs();

        //修改mCacheDir变量
        try {
            classType = Class.forName(contextImplClassPath);
            fields = classType.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                if (f.getName().equals(cacheDirFieleName)) {
                    f.set(object, new File(OutPath));
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    protected GameSettings getSettings() {
        return new AppsSettings(this);
    }
}
