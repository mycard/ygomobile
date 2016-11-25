package cn.garymb.ygomobile.bean;

import android.content.Context;
import android.database.Cursor;

import cn.ygo.ocgcore.Card;

public class CardInfo extends Card {
    public static final String TAG = "CardInfo";
    private static final String SQL_BASE = "select * from datas,texts where datas.id = texts.id ";

    public String getAllTypeString(Context context) {
        return "" + Type;
    }

    public CardInfo() {
        super();
    }

    /***
     * select * from datas,texts where datas.id =texts.id and datas.id = code;
     * @link cn.garymb.ygomobile.bean.CardInfo.SQL_BASE
     */
    private CardInfo(Cursor reader) {
        this.Code = reader.getLong(0);
        this.Ot = reader.getInt(1);
        this.Alias = reader.getInt(2);
        this.Setcode = reader.getLong(3);
        this.Type = reader.getLong(4);
        int levelInfo = reader.getInt(5);
        this.Level = levelInfo & 0xff;
        this.LScale = (levelInfo >> 24) & 0xff;
        this.RScale = (levelInfo >> 16) & 0xff;
        this.Race = reader.getLong(6);
        this.Attribute = reader.getInt(7);
        this.Attack = reader.getInt(8);
        this.Defense = reader.getInt(9);
        this.Name = reader.getString(12);
        this.Desc = reader.getString(13);
        for (int i = 0; i < this.Strs.length; i++) {
            this.Strs[i] = reader.getString(14 + i);
        }
    }
}
