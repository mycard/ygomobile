package cn.garymb.ygomobile.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.deck.ImageTop;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.ygo.ocgcore.LimitList;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardType;
import cn.ygo.ocgcore.enums.LimitType;

public class CardListAdapater extends BaseAdapterPlus<CardInfo> {
    private StringManager mStringManager;
    private ImageTop mImageTop;
    private OnAddCardListener mOnAddCardListener;
    private LimitList mLimitList;
    private boolean mItemBg;
    private ImageLoader imageLoader;

    public interface OnAddCardListener {
        void onAdd(int pos);
    }

    public CardListAdapater(Context context, ImageLoader imageLoader) {
        super(context);
        this.imageLoader = imageLoader;
        mStringManager = StringManager.get();
    }

    public void setOnAddCardListener(OnAddCardListener onAddCardListener) {
        mOnAddCardListener = onAddCardListener;
    }

    public void setItemBg(boolean itemBg) {
        this.mItemBg = itemBg;
    }

    public void setLimitList(LimitList limitList) {
        mLimitList = limitList;
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view;
        if (mItemBg) {
            view = inflate(R.layout.item_search_card, parent, false);
        } else {
            view = inflate(R.layout.item_search_card2, parent, false);
        }
        new ViewHolder(view);
        return view;
    }

    private int getColor(int id) {
        return context.getResources().getColor(id);
    }

    @Override
    protected void attach(View view, CardInfo item, int position) {
        ViewHolder holder = (ViewHolder) view.getTag(view.getId());
        holder.setPosition(position);
        imageLoader.bindImage(holder.cardImage, item.Code);
        holder.cardName.setText(item.Name);
        if (item.isType(CardType.Monster)) {
            holder.cardLevel.setVisibility(View.VISIBLE);
            holder.layout_atkdef.setVisibility(View.VISIBLE);
//            holder.view_bar.setVisibility(View.VISIBLE);
            String star = "";
            for (int i = 0; i < item.Level; i++) {
                star += "★";
            }
            holder.cardLevel.setText(star);
            if (item.isType(CardType.Xyz)) {
                holder.cardLevel.setTextColor(getColor(R.color.star_rank));
            } else {
                holder.cardLevel.setTextColor(getColor(R.color.star));
            }
            holder.cardAtk.setText((item.Attack < 0 ? "?" : String.valueOf(item.Attack)));
            holder.cardDef.setText((item.Defense < 0 ? "?" : String.valueOf(item.Defense)));
        } else {
//            if (!showCode) {
//                holder.view_bar.setVisibility(View.INVISIBLE);
//            }
            holder.cardLevel.setVisibility(View.INVISIBLE);
            holder.layout_atkdef.setVisibility(View.GONE);
        }
        if (mImageTop == null) {
            mImageTop = new ImageTop(context);
        }
        if (mLimitList != null) {
            holder.rightImage.setVisibility(View.VISIBLE);
            if (mLimitList.check(item, LimitType.Forbidden)) {
                holder.rightImage.setImageBitmap(mImageTop.forbidden);
            } else if (mLimitList.check(item, LimitType.Limit)) {
                holder.rightImage.setImageBitmap(mImageTop.limit);
            } else if (mLimitList.check(item, LimitType.SemiLimit)) {
                holder.rightImage.setImageBitmap(mImageTop.semiLimit);
            } else {
                holder.rightImage.setVisibility(View.GONE);
            }
        } else {
            holder.rightImage.setVisibility(View.GONE);
        }
        //卡片类型
        holder.cardType.setText(item.getAllTypeString(mStringManager));
        if (holder.codeView != null) {
            holder.codeView.setText(String.format("%08d", item.Code));
        }
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
        TextView codeView;
        private int position;

        void setPosition(int position) {
            this.position = position;
            if (cardImage != null) {
                cardImage.setOnClickListener((v) -> {
                    if (mOnAddCardListener != null) {
                        mOnAddCardListener.onAdd(position);
                    }
                });
            }
        }

        int getPosition() {
            return position;
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
            codeView = findViewById(R.id.card_code);
//            File outFile = new File(AppsSettings.get().getCoreSkinPath(), Constants.UNKNOWN_IMAGE);
//            ImageLoader.get().bind(context, outFile, cardImage, outFile.getName().endsWith(Constants.BPG), 0, null);
        }
    }
}
