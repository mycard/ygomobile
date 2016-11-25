package cn.garymb.ygomobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.activities.CardInfoActivity;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.garymb.ygomobile.utils.BitmapUtil;
import cn.ygo.ocgcore.enums.CardType;

public class CardListAdapater extends BaseAdapterPlus<CardInfo> implements AdapterView.OnItemClickListener {
    public CardListAdapater(Context context) {
        super(context);
    }

    public void loadData() {

    }

    public void search() {

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
        BitmapUtil.destroy(holder.cardImage.getDrawable());
        holder.cardImage.setImageBitmap(ImageLoader.loadImage(Constants.CORE_IMAGE_PATH + "/" + item.Code,
                Constants.CORE_SKIN_CARD_COVER_SIZE[0], Constants.CORE_SKIN_CARD_COVER_SIZE[1]));
        holder.cardName.setText(item.Name);
        if (item.isType(CardType.Monster)) {
            holder.cardLevel.setVisibility(View.VISIBLE);
            holder.layout_atkdef.setVisibility(View.VISIBLE);
            String star = "";
            for (int i = 0; i < item.Level; i++) {
                star += "★";
            }
            holder.cardLevel.setText(star);
            holder.cardAtk.setText((item.Attack < 0 ? "?" : String.valueOf(item.Attack)));
            holder.cardDef.setText((item.Defense < 0 ? "?" : String.valueOf(item.Defense)));
        } else {
            holder.cardLevel.setVisibility(View.INVISIBLE);
            holder.layout_atkdef.setVisibility(View.INVISIBLE);
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

        ViewHolder(View view) {
            view.setTag(view.getId(), this);
            cardImage = (ImageView) view.findViewById(R.id.card_image);
            cardName = (TextView) view.findViewById(R.id.card_name);
            cardType = (TextView) view.findViewById(R.id.card_type);
            cardAtk = (TextView) view.findViewById(R.id.card_atk);
            cardDef = (TextView) view.findViewById(R.id.card_def);
            cardLevel = (TextView) view.findViewById(R.id.card_level);
            layout_atkdef = view.findViewById(R.id.layout_atkdef);
        }

        static ViewHolder from(View view) {
            return (ViewHolder) view.getTag(view.getId());
        }
    }
}
