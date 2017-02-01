package cn.garymb.ygomobile.bean;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import cn.ygo.ocgcore.Card;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardType;

public class CardInfo extends Card implements Parcelable {
    public static final String TAG = "CardInfo";
    public static final String SQL_BASE;
    public static final String _ID = "_id";
    public static final String COL_TYPE = "type";
    public static final String SQL_CODE_BASE = "select " + _ID + ","+COL_TYPE+" from datas";
    public static final String COL_ID = "datas." + _ID;
    public static final String COL_STAR = "star";


    static {
        StringBuilder stringBuilder = new StringBuilder("select datas." + _ID + ",ot,alias,setcode,type,level,race,attribute,atk,def,category");
        stringBuilder.append(",texts.name,texts.desc");
//        for (int i = 1; i <= 0x10; i++) {
//            stringBuilder.append(",texts.str" + i);
//        }
//        stringBuilder.append(",level as "+COL_STAR+" ");
        stringBuilder.append(",(datas.level & 255) as " + COL_STAR + " ");
        stringBuilder.append(" from datas, texts  where datas." + _ID + " = texts." + _ID + " ");
        SQL_BASE = stringBuilder.toString();
    }

    public CardInfo(long id, String name) {
        this();
        this.Code = id;
        this.Name = name;
    }

    /***
     * select * from datas,texts where datas.id =texts.id and datas.id = code;
     *
     * @link cn.garymb.ygomobile.bean.CardInfo.SQL_BASE
     */
    public CardInfo(Cursor reader) {
        this();
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
//            for (int i = 0; i < this.Strs.length; i++) {
//                this.Strs[i] = reader.getString(13 + i);
//            }
        } catch (Exception e) {
            Log.e("card", "read text", e);
        }
    }

    public String getAllTypeString(StringManager stringManager) {
        StringBuilder stringBuilder = new StringBuilder();
        CardType[] cardTypes = CardType.values();
        boolean isFrst = true;
        if (isType(CardType.Spell)) {
            for (CardType type : cardTypes) {
                if (isType(type)) {
                    stringBuilder.append(stringManager.getTypeString(type.value()));
//                    break;
                }
            }
//            stringBuilder.append(stringManager.getTypeString(CardType.Spell.value()));
        } else if (isType(CardType.Trap)) {
            for (CardType type : cardTypes) {
                if (isType(type)) {
                    stringBuilder.append(stringManager.getTypeString(type.value()));
                }
//                break;
            }
//            stringBuilder.append(stringManager.getTypeString(CardType.Trap.value()));
        } else {
            for (CardType type : cardTypes) {
                if (isType(type)) {
                    if (!isFrst) {
                        stringBuilder.append("/");
                    } else {
                        isFrst = false;
                    }
                    String str = stringManager.getTypeString(type.value());
                    if (TextUtils.isEmpty(str)) {
                        stringBuilder.append("0x");
                        stringBuilder.append(String.format("%X", type.value()));
                    } else {
                        stringBuilder.append(str);
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    public CardInfo() {
        super();
    }

    @Override
    public String toString() {
        return "CardInfo{" + Code + Name + '}';
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
