package ocgcore.handler;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

import ocgcore.data.Card;


public class CardManager {
    private String dbDir, exDbPath;
    private final HashMap<Long, Card> cardDataHashMap = new HashMap<>();

    public CardManager(String dbDir, String exPath) {
        this.dbDir = dbDir;
        this.exDbPath = exPath;
    }

    public Card getCard(long code) {
        return cardDataHashMap.get(Long.valueOf(code));
    }

    public int getCount() {
        return cardDataHashMap.size();
    }

    public HashMap<Long, Card> getAllCards() {
        return cardDataHashMap;
    }

    @WorkerThread
    public void loadCards() {
        File[] dirs = {new File(dbDir), new File(exDbPath)};
        for (File dir : dirs) {
            if (dir.exists()) {
                File[] files = dir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        File file = new File(dir, name);
                        return file.isFile() && name.endsWith(".cdb");
                    }
                });
                //读取全部卡片
                if (files != null) {
                    for (File file : files) {
                        int count = readAllCards(file, cardDataHashMap);
                        Log.i("Irrlicht", "load " + count + " cdb:" + file);
                    }
                }
            }
        }
    }

    @WorkerThread
    protected int readAllCards(File file, Map<Long, Card> cardMap) {
        if (!file.exists()) {
            return 0;
        }
        int i = 0;
        Cursor reader = null;
        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file, null);
            if (db.isOpen()) {
                try {
                    reader = db.rawQuery("select datas.id, ot, alias, setcode, type, level, race, attribute, atk, def,category,name,desc from datas,texts where datas.id = texts.id;", null);
                } catch (Throwable e) {
                    //ignore
                    reader = db.rawQuery("select datas._id, ot, alias, setcode, type, level, race, attribute, atk, def,category,name,desc from datas,texts where datas._id = texts._id;", null);
                }
                if (reader != null && reader.moveToFirst()) {
                    do {
                        Card cardData = new Card();
                        cardData.Code = reader.getLong(0);
                        cardData.Ot = reader.getInt(1);
                        cardData.Alias = reader.getInt(2);
                        cardData.Setcode = reader.getLong(3);
                        cardData.Type = reader.getLong(4);
                        int levelInfo = reader.getInt(5);
                        cardData.Level = levelInfo & 0xff;
                        cardData.LScale = (levelInfo >> 24) & 0xff;
                        cardData.RScale = (levelInfo >> 16) & 0xff;
                        cardData.Race = reader.getLong(6);
                        cardData.Attribute = reader.getInt(7);
                        cardData.Attack = reader.getInt(8);
                        cardData.Defense = reader.getInt(9);
                        cardData.Category = reader.getLong(10);
                        cardData.Name = reader.getString(11);
                        cardData.Desc = reader.getString(12);
                        //put
                        i++;
                        cardMap.put(cardData.Code, cardData);
                    } while (reader.moveToNext());
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("Irrlicht", "read cards " + file, e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return i;
    }
}
