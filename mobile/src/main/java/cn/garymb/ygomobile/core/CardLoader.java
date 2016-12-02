package cn.garymb.ygomobile.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ICardLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.enums.LimitType;

public class CardLoader implements ICardLoader {
    //    private StringManager mStringManager = StringManager.get();
    private LimitManager mLimitManager = LimitManager.get();
    private AppsSettings mSettings = AppsSettings.get();
    private Context context;
    private SQLiteDatabase db;
    private CallBack mCallBack;
    private String defSQL = CardInfo.SQL_BASE + " limit " + Constants.DEFAULT_CARD_COUNT + ";";

    public interface CallBack {
        void onSearchStart();

        void onSearchResult(List<CardInfo> cardInfos);

        void onResetSearch();
    }

    public CardLoader(Context context) {
        this.context = context;
    }

    public List<CardInfo> readCards(List<Long> ids, LimitList limitList) {
        if (!isOpen()) return null;
        StringBuilder stringBuilder = new StringBuilder(CardInfo.SQL_BASE);
        stringBuilder.append(" and " + CardInfo.COL_ID + " in (");
        int i = 0;
        for (Long id : ids) {
            if (i != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(id);
            i++;
        }
        stringBuilder.append(")");
        String sql = stringBuilder.toString();
        Cursor reader = null;
        try {
            reader = db.rawQuery(sql, null);
        } catch (Exception e) {
        }
        List<CardInfo> tmp = new ArrayList<CardInfo>();
        if (reader != null) {
            if (reader.moveToFirst()) {
//                Log.d("kk", "find card count=" + reader.getCount());
                do {
                    CardInfo cardInfo = new CardInfo(reader);
                    if (limitList != null) {
                        if (limitList.isForbidden(cardInfo.Code)) {
                            cardInfo.setLimitType(LimitType.Forbidden);
                        } else if (limitList.isLimit(cardInfo.Code)) {
                            cardInfo.setLimitType(LimitType.Limit);
                        } else if (limitList.isSemiLimit(cardInfo.Code)) {
                            cardInfo.setLimitType(LimitType.SemiLimit);
                        }
                    }
                    tmp.add(cardInfo);

                } while (reader.moveToNext());
            }
            reader.close();
        }
        return tmp;
    }

    public boolean openDb() {
        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {

            }
        }
        File file = new File(mSettings.getDataBasePath(), Constants.DATABASE_NAME);
        if (file.exists()) {
            try {
                db = SQLiteDatabase.openOrCreateDatabase(file, null);
                return true;
            } catch (Exception e) {
                if (Constants.DEBUG)
                    Log.e("kk", "open db", e);
            }
        } else if (Constants.DEBUG) {
            Log.w("kk", "no find " + file);
        }
        return false;
    }

    public boolean isOpen() {
        return db != null;
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public void loadData() {
        loadData(defSQL, 0);
    }

    private void loadData(String sql, long setcode) {
        if (!isOpen()) {
            return;
        }
        if (mCallBack != null) {
            mCallBack.onSearchStart();
        }
        ProgressDialog wait = ProgressDialog.show(context, null, context.getString(R.string.searching));
        VUiKit.defer().when(() -> {
            Cursor reader = null;
            try {
                reader = db.rawQuery(sql, null);
            } catch (Exception e) {
            }
            List<CardInfo> tmp = new ArrayList<CardInfo>();
            if (reader != null) {
                if (reader.moveToFirst()) {
                    do {
                        CardInfo cardInfo = new CardInfo(reader);
                        if (setcode > 0) {
                            if (!cardInfo.isSetCode(setcode)) {
                                continue;
                            }
                        }
                        tmp.add(cardInfo);

                    } while (reader.moveToNext());
                }
                reader.close();
            }
            return tmp;
        }).fail((e) -> {
            if (mCallBack != null) {
                mCallBack.onSearchResult(null);
            }
            wait.dismiss();
        }).done((tmp) -> {
            if (mCallBack != null) {
                mCallBack.onSearchResult(tmp);
            }
            wait.dismiss();
        });
    }

    @Override
    public void onReset() {
        if (mCallBack != null) {
            mCallBack.onResetSearch();
        }
    }

    @Override
    public void search(String prefixWord, String suffixWord,
                       long attribute, long level, long race,
                       long limitlist, long limit,
                       String atk, String def,
                       long setcode, long category, long ot, long... types) {
        StringBuilder stringBuilder = new StringBuilder(CardInfo.SQL_BASE);
        if (!TextUtils.isEmpty(prefixWord) && !TextUtils.isEmpty(suffixWord)) {
            stringBuilder.append(" and name like '%");
            stringBuilder.append(prefixWord);
            stringBuilder.append("%");
            stringBuilder.append(suffixWord);
            stringBuilder.append("%' ");
        } else if (!TextUtils.isEmpty(prefixWord)) {
            stringBuilder.append(" and name like '%");
            stringBuilder.append(prefixWord);
            stringBuilder.append("%' ");
        } else if (!TextUtils.isEmpty(suffixWord)) {
            stringBuilder.append(" and name like '%");
            stringBuilder.append(suffixWord);
            stringBuilder.append("%' ");
        }
        if (attribute != 0) {
            stringBuilder.append(" and attribute=" + attribute);
        }
        if (level != 0) {
            stringBuilder.append(" and (level &" + level + ") =" + level);
        }
        if (!TextUtils.isEmpty(atk)) {
            stringBuilder.append(" and atk=" + (TextUtils.isDigitsOnly(atk) ? atk : -2));
        }
        if (!TextUtils.isEmpty(def)) {
            stringBuilder.append(" and def=" + (TextUtils.isDigitsOnly(def) ? def : -2));
        }
        if (ot > 0) {
            stringBuilder.append(" and ot=" + ot);
        }
        for (long type : types) {
            if (type > 0) {
                stringBuilder.append(" and (type & " + type + ") =" + type);
            }
        }
        if (category != 0) {
            stringBuilder.append(" and (category &" + category + ") =" + category);
        }
        if (race != 0) {
            stringBuilder.append(" and race=" + race);
        }

        LimitList limitList = mLimitManager.getLimitFromIndex((int) limitlist);
        LimitType cardLimitType = LimitType.valueOf(limit);
        if (limitList != null) {
            List<Long> ids;
            if (cardLimitType == LimitType.Forbidden) {
                ids = limitList.forbidden;
            } else if (cardLimitType == LimitType.Limit) {
                ids = limitList.limit;
            } else if (cardLimitType == LimitType.SemiLimit) {
                ids = limitList.semiLimit;
            } else {
                ids = limitList.getCodeList();
            }
            stringBuilder.append(" and " + CardInfo.COL_ID + " in (");
            int i = 0;
            for (Long id : ids) {
                if (i != 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(id);
                i++;
            }
            stringBuilder.append(")");
        }
        loadData(stringBuilder.toString(), setcode);
    }
}
