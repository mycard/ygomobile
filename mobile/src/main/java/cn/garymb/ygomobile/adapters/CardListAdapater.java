package cn.garymb.ygomobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.activities.CardInfoActivity;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.IDataLoader;
import cn.garymb.ygomobile.core.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.garymb.ygomobile.utils.BitmapUtil;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.ygo.ocgcore.Card;
import cn.ygo.ocgcore.enums.CardType;

public class CardListAdapater extends BaseAdapterPlus<CardInfo> implements
        IDataLoader,
        AdapterView.OnItemClickListener {
    private volatile SQLiteDatabase db;

    public CardListAdapater(Context context) {
        super(context);
    }

    @Override
    public void loadData() {
        if (db == null) {
            File file = new File(AppsSettings.get().getDataBasePath(), Constants.DATABASE_NAME);
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
            VUiKit.defer().when(() -> {
                Cursor reader = null;
                try {
                    reader = db.rawQuery(CardInfo.SQL_BASE + " limit 100", null);
                } catch (Exception e) {
                    try {
                        reader = db.rawQuery(CardInfo.SQL_BASE2 + " limit 100", null);
                    } catch (Exception e2) {

                    }
                }
                List<CardInfo> tmp = new ArrayList<CardInfo>();
                if (reader != null) {
                    if (reader.moveToFirst()) {
                        while (reader.moveToNext()) {
                            tmp.add(new CardInfo(reader));
                        }
                    }
                    reader.close();
                }
                return tmp;
            }).done((tmp) -> {
                mItems.clear();
                mItems.addAll(tmp);
                if (Constants.DEBUG)
                    Log.d("kk", "find card count=" + tmp.size());
                notifyDataSetChanged();
            });
        } else {
            Log.w("kk", "open db fail");
        }
    }

    public void search(CardInfo cardInfo) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CardInfo cardInfo = getItem(position);
        if (cardInfo != null) {
            context.startActivity(
                    new Intent(context, CardInfoActivity.class)
                            .putExtra(CardInfo.TAG, cardInfo));
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
        ViewHolder holder = ViewHolder.from(view);
        ImageLoader.bindImage(context, holder.cardImage, item.Code);
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
        holder.cardType.setText(item.getAllTypeString(context));
    }

    static class ViewHolder {
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
        }

        static ViewHolder from(View view) {
            return (ViewHolder) view.getTag(view.getId());
        }
    }
}
