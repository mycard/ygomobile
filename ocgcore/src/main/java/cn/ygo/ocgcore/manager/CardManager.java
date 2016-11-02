package cn.ygo.ocgcore.manager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.HashMap;

import cn.ygo.ocgcore.Card;
import cn.ygo.ocgcore.CardData;

public class CardManager implements ICardManager {
    private String dbDir;
    private final HashMap<Long, Card> cardDataHashMap = new HashMap<>();

    public CardManager(String db) {
        this.dbDir = db;
    }

    @Override
    public CardData getCard(long code) {
        Card cardData = null;
        synchronized (this) {
            cardData = cardDataHashMap.get(Long.valueOf(code));
            if (cardData == null) {
                cardData = read(code);
                cardDataHashMap.put(Long.valueOf(code), cardData);
            }
        }
        return null;
    }

    @Override
    public void loadCards() {

    }

    /***
     * 从不同文件夹去读取，直到读取卡片信息
     *
     * @param code id
     */
    private Card read(long code) {
        File file = new File(dbDir);
        if (file.isFile()) {
            return readFrom(file, code);
        } else {
            File[] files = file.listFiles();
            Card cardData = null;
            for (File f : files) {
                cardData = readFrom(f, code);
                if (cardData != null) {
                    return cardData;
                }
            }
        }
        return null;
    }

    private Card readFrom(File file, long code) {
//        if(!file.exists()){
//            return null;
//        }
        Card cardData = null;
        try {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file, null);
            if (db.isOpen()) {
                Cursor reader = db.rawQuery("select * from datas,texts where datas.id =texts.id and datas.id = " + code + ";", null);
                if (reader != null) {
                    if (reader.moveToFirst()) {


                        cardData = new Card();
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
                        cardData.Name = reader.getString(12);
                        cardData.Desc = reader.getString(13);
                        for (int i = 0; i < cardData.Strs.length; i++) {
                            cardData.Strs[i] = reader.getString(14 + i);
                        }
                        cardData.dbFile = file.getAbsolutePath();

                    }
                    reader.close();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return cardData;
    }
}
