package cn.garymb.ygomobile.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ICardLoader;
import cn.garymb.ygomobile.core.loader.ILoadCallBack;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.garymb.ygomobile.core.CardDetail;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.LimitManager;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.LimitType;
import cn.ygo.ocgcore.enums.CardType;

public class CardListAdapater extends BaseAdapterPlus<CardInfo> implements
        ICardLoader,
        AdapterView.OnItemClickListener, ListView.OnScrollListener {
    private SQLiteDatabase db;
    private StringManager mStringManager;
    private AppsSettings mAppsSettings;
    private ILoadCallBack mILoadCallBack;
    private LimitManager mLimitManager;

    public CardListAdapater(Context context) {
        super(context);
        mStringManager = StringManager.get();
        mAppsSettings = AppsSettings.get();
        mLimitManager = LimitManager.get();
    }

    public void setILoadCallBack(ILoadCallBack ILoadCallBack) {
        mILoadCallBack = ILoadCallBack;
    }

    @Override
    public void loadData(ILoadCallBack loadCallBack) {
        loadData(CardInfo.SQL_BASE + " limit " + Constants.DEFAULT_CARD_COUNT + ";", 0, loadCallBack);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case SCROLL_STATE_IDLE:
                break;
            case SCROLL_STATE_TOUCH_SCROLL:
//                Glide.with(context).pauseRequests();
                break;
            case SCROLL_STATE_FLING:
//                Glide.with(context).resumeRequests();
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void loadString() {
        if (!mStringManager.isLoad()) {
//            File stringfile = new File(mAppsSettings.getResourcePath(), String.format(Constants.CORE_STRING_PATH, mAppsSettings.getCoreConfigVersion()));
            mStringManager.load();//loadFile(stringfile.getAbsolutePath());
        }
    }

    @Override
    public void loadLimitList() {
        if (!mLimitManager.isLoad()) {
//            File stringfile = new File(mAppsSettings.getResourcePath(), String.format(Constants.CORE_LIMIT_PATH, mAppsSettings.getCoreConfigVersion()));
            mLimitManager.load();//loadFile(stringfile.getAbsolutePath());
        }
    }

    private synchronized void loadData(String sql, long setcode, ILoadCallBack loadCallBack) {
        Log.v("kk", "sql=" + sql);
        if (db == null) {
            File file = new File(mAppsSettings.getDataBasePath(), Constants.DATABASE_NAME);
            if (file.exists()) {
                try {
                    db = SQLiteDatabase.openOrCreateDatabase(file, null);
                } catch (Exception e) {
                    if (Constants.DEBUG)
                        Log.e("kk", "open db", e);
                }
            } else if (Constants.DEBUG) {
                Log.w("kk", "no find " + file);
            }
        }
        if (db != null && db.isOpen()) {
            ProgressDialog wait = ProgressDialog.show(context, null, context.getString(R.string.searching));
            VUiKit.defer().when(() -> {
                loadString();
                loadLimitList();
                Cursor reader = null;
                try {
                    reader = db.rawQuery(sql, null);
                } catch (Exception e) {
                }
                List<CardInfo> tmp = new ArrayList<CardInfo>();
                if (reader != null) {
                    if (reader.moveToFirst()) {
                        Log.d("kk", "find card count=" + reader.getCount());
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
                if (loadCallBack != null) {
                    loadCallBack.onLoad(false);
                }
                wait.dismiss();
                if (mILoadCallBack != null) {
                    mILoadCallBack.onLoad(false);
                }
            }).done((tmp) -> {
                if (loadCallBack != null) {
                    loadCallBack.onLoad(true);
                }
                wait.dismiss();
                mItems.clear();
                mItems.addAll(tmp);
                if (Constants.DEBUG)
                    Log.v("kk", "find card count=" + tmp.size());
                notifyDataSetChanged();
                if (mILoadCallBack != null) {
                    mILoadCallBack.onLoad(true);
                }
            });
        } else {
            Log.w("kk", "open db fail");
        }
    }

    /***
     * @param prefixWord 前缀
     * @param suffixWord 后缀
     * @param types      类型
     * @param attribute  属性
     * @param level      等级
     * @param atk        攻击
     * @param def        等级
     * @param setcode    系列
     * @param category   分类
     * @param ot         ot
     */
    @Override
    public void search(String prefixWord, String suffixWord,
                       long attribute, long level, long race, long limitlist, long limit, String atk, String def, long setcode, long category, long ot, long... types) {
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

        LimitList limitList = mLimitManager.getLimit((int) limitlist);
        LimitType cardLimitType = LimitType.valueOf(limit);
        if (limitList != null) {
            List<Long> ids;
            if(cardLimitType==LimitType.Forbidden){
                ids= limitList.forbidden;
            }else if(cardLimitType==LimitType.Limit){
                ids= limitList.limit;
            }else if(cardLimitType==LimitType.SemiLimit){
                ids= limitList.semiLimit;
            }else{
                ids= limitList.getCodeList();
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
        loadData(stringBuilder.toString(), setcode, null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CardInfo cardInfo = getItem(position);
        if (cardInfo != null) {
            CardDetail cardDetail = new CardDetail(context);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(cardDetail.getView());
            final Dialog dialog = builder.show();
            cardDetail.bind(cardInfo, mStringManager, new CardDetail.OnClickListener() {
                @Override
                public void onOpenUrl(CardInfo cardInfo) {
                    //ourocg
                    String uri = Constants.WIKI_SEARCH_URL + String.format("%08d", cardInfo.Code);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, R.string.no_webbrowser, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onClose() {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_card, parent, false);
        new ViewHolder(view);
        return view;
    }

    @Override
    protected void attach(View view, CardInfo item, int position) {
        ViewHolder holder = (ViewHolder) view.getTag(view.getId());
//        if (!isScroll) {
        ImageLoader.get().bindImage(context, holder.cardImage, item.Code);
//        }
        holder.cardName.setText(item.Name);
        if (item.isType(CardType.Monster)) {
            holder.cardLevel.setVisibility(View.VISIBLE);
            holder.layout_atkdef.setVisibility(View.VISIBLE);
            holder.view_bar.setVisibility(View.VISIBLE);
            String star = "";
            for (int i = 0; i < item.Level; i++) {
                star += "★";
            }
            holder.cardLevel.setText(star);
            holder.cardAtk.setText((item.Attack < 0 ? "?" : String.valueOf(item.Attack)));
            holder.cardDef.setText((item.Defense < 0 ? "?" : String.valueOf(item.Defense)));
        } else {
            holder.view_bar.setVisibility(View.GONE);
            holder.cardLevel.setVisibility(View.GONE);
            holder.layout_atkdef.setVisibility(View.GONE);
        }
        //卡片类型
        holder.cardType.setText(item.getAllTypeString(mStringManager));
    }

    class ViewHolder {
        ImageView cardImage;
        TextView cardName;
        TextView cardLevel;
        TextView cardType;
        TextView cardAtk;
        TextView cardDef;
        View layout_atkdef;
        View view_bar;

        ViewHolder(View view) {
            view.setTag(view.getId(), this);
            cardImage = (ImageView) view.findViewById(R.id.card_image);
            cardName = (TextView) view.findViewById(R.id.card_name);
            cardType = (TextView) view.findViewById(R.id.card_type);
            cardAtk = (TextView) view.findViewById(R.id.card_atk);
            cardDef = (TextView) view.findViewById(R.id.card_def);
            cardLevel = (TextView) view.findViewById(R.id.card_level);
            layout_atkdef = view.findViewById(R.id.layout_atkdef);
            view_bar = view.findViewById(R.id.view_bar);
            File outFile = new File(AppsSettings.get().getCoreSkinPath(), Constants.CORE_SKIN_COVER);
            ImageLoader.get().bind(context, outFile, cardImage, outFile.getName().endsWith(Constants.BPG), 0);
        }
    }
}
