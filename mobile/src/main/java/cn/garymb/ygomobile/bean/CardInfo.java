package cn.garymb.ygomobile.bean;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import cn.ygo.ocgcore.Card;

public class CardInfo extends Card implements Parcelable{
    public static final String TAG = "CardInfo";
    public static final String SQL_DATA_BASE = "select * from datas";
    public static final String SQL_BASE;

    public static final String SQL_BASE2;

    static {
        StringBuilder stringBuilder = new StringBuilder(",ot,alias,setcode,type,level,race,attribute,atk,def,category");
        stringBuilder.append(",texts.name,texts.desc");
        for (int i = 1; i <= 0x10; i++) {
            stringBuilder.append(",texts.str" + i);
        }
        SQL_BASE = "select datas.id" + stringBuilder.toString() + " from datas, texts  where datas.id = texts.id ";
        SQL_BASE2 = "select datas._id" + stringBuilder.toString() + " from datas, texts  where datas._id = texts._id ";
    }

    /***
     * select * from datas,texts where datas.id =texts.id and datas.id = code;
     *
     * @link cn.garymb.ygomobile.bean.CardInfo.SQL_BASE
     */
    public CardInfo(Cursor reader) {
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
        this.Category = reader.getInt(10);
        try {
            this.Name = reader.getString(11);
            this.Desc = reader.getString(12);
            for (int i = 0; i < this.Strs.length; i++) {
                this.Strs[i] = reader.getString(13 + i);
            }
        } catch (Exception e) {
            Log.e("kk", "read text", e);
        }
    }

    public String getAllTypeString(Context context) {
        return "0x"+String.format("%X", Type);
    }

    public CardInfo() {
        super();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected CardInfo(Parcel in) {
        super(in);
    }

    public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {
        @Override
        public CardInfo createFromParcel(Parcel source) {
            return new CardInfo(source);
        }

        @Override
        public CardInfo[] newArray(int size) {
            return new CardInfo[size];
        }
    };
}
