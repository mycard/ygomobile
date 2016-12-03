package cn.garymb.ygomobile.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.activities.WebActivity;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ICardLoader;
import cn.garymb.ygomobile.core.loader.ILoadCallBack;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.deck.ImageTop;
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

public class CardListAdapater extends BaseAdapterPlus<CardInfo> {
    private StringManager mStringManager;
    private ImageTop mImageTop;

    public CardListAdapater(Context context) {
        super(context);
        mStringManager = StringManager.get();
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_list_card, parent, false);
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
        if (mImageTop == null) {
            mImageTop = new ImageTop(context);
        }
        holder.rightImage.setVisibility(View.VISIBLE);
        if (item.getLimitType() == LimitType.Forbidden) {
            holder.rightImage.setImageBitmap(mImageTop.forbidden);
        } else if (item.getLimitType() == LimitType.Limit) {
            holder.rightImage.setImageBitmap(mImageTop.limit);
        } else if (item.getLimitType() == LimitType.SemiLimit) {
            holder.rightImage.setImageBitmap(mImageTop.semiLimit);
        } else {
            holder.rightImage.setVisibility(View.GONE);
        }
        //卡片类型
        holder.cardType.setText(item.getAllTypeString(mStringManager));
    }

    class ViewHolder extends BaseViewHolder {
        ImageView cardImage;
        TextView cardName;
        TextView cardLevel;
        TextView cardType;
        TextView cardAtk;
        TextView cardDef;
        ImageView rightImage;
        View layout_atkdef;
        View view_bar;

        ViewHolder(View view) {
            super(view);
            view.setTag(view.getId(), this);
            cardImage = findViewById(R.id.card_image);
            cardName = findViewById(R.id.card_name);
            cardType = findViewById(R.id.card_type);
            cardAtk = findViewById(R.id.card_atk);
            cardDef = findViewById(R.id.card_def);
            cardLevel = findViewById(R.id.card_level);
            layout_atkdef = findViewById(R.id.layout_atkdef);
            view_bar = findViewById(R.id.view_bar);
            rightImage = findViewById(R.id.right_top);
            File outFile = new File(AppsSettings.get().getCoreSkinPath(), Constants.CORE_SKIN_COVER);
            ImageLoader.get().bind(context, outFile, cardImage, outFile.getName().endsWith(Constants.BPG), 0, null);
        }
    }
}
