package cn.garymb.ygomobile.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.deck.ImageTop;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.garymb.ygomobile.core.AppsSettings;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardType;
import cn.ygo.ocgcore.enums.LimitType;

public class CardListAdapater extends BaseAdapterPlus<CardInfo> {
    private StringManager mStringManager;
    private ImageTop mImageTop;
    private boolean showAdd;
    private OnAddCardListener mOnAddCardListener;

    public interface OnAddCardListener {
        void onAdd(int pos);
    }

    public CardListAdapater(Context context) {
        super(context);
        mStringManager = StringManager.get();
    }

    public void setShowAdd(boolean showAdd) {
        this.showAdd = showAdd;
    }

    public void setOnAddCardListener(OnAddCardListener onAddCardListener) {
        mOnAddCardListener = onAddCardListener;
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
        holder.setPosition(position);
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
        View addView;
        View view_bar;
        private int position;

        void setPosition(int position){
            this.position = position;
            addView.setOnClickListener((v) -> {
                if (mOnAddCardListener != null) {
                    mOnAddCardListener.onAdd(position);
                }
            });
        }

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
            addView = findViewById(R.id.card_add);
            if (showAdd) {
                addView.setVisibility(View.VISIBLE);
            } else {
                addView.setVisibility(View.GONE);
            }
            File outFile = new File(AppsSettings.get().getCoreSkinPath(), Constants.UNKNOWN_IMAGE);
            ImageLoader.get().bind(context, outFile, cardImage, outFile.getName().endsWith(Constants.BPG), 0, null);
        }
    }
}
