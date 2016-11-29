package cn.garymb.ygomobile.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardType;

/***
 * 卡片详情
 */
public class CardDetail {
    private ImageView cardImage;
    private TextView name;
    private TextView desc;
    private TextView level;
    private TextView type;
    private TextView race;
    private TextView cardAtk;
    private TextView cardDef;
    private TextView setname;
    private Context mContext;
    private View atkdeflayout1;
    private View atkdeflayout2;
    private View view_bar;
    private View view;
    private View close;
    private View faq;
    private TextView cardcode;

    public interface OnClickListener {
        void onOpenUrl(CardInfo cardInfo);

        void onClose();
    }

    public CardDetail(Context context) {
        this.mContext = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.dialog_card, null);
        cardImage = bind(R.id.card_image);
        name = bind(R.id.text_name);
        desc = bind(R.id.text_desc);
        close = bind(R.id.btn_close);
        cardcode = bind(R.id.card_code);
        level = bind(R.id.card_level);
        type = bind(R.id.card_type);
        faq = bind(R.id.btn_faq);
        cardAtk = bind(R.id.card_atk);
        cardDef = bind(R.id.card_def);
        atkdeflayout1 = bind(R.id.layout_atkdef1);
        atkdeflayout2 = bind(R.id.layout_atkdef2);
        view_bar = bind(R.id.view_bar);
        race = bind(R.id.card_race);
        setname = bind(R.id.card_setname);
    }

    public View getView() {
        return view;
    }

    public void bind(CardInfo cardInfo, StringManager stringManager, final OnClickListener listener) {
        ImageLoader.get().bindImage(mContext, cardImage, cardInfo.Code);
        name.setText(cardInfo.Name);
        desc.setText(cardInfo.Desc);
        cardcode.setText(String.format("%08d", cardInfo.Code));
        type.setText(cardInfo.getAllTypeString(stringManager).replace("/", "\n"));
        long[] sets = cardInfo.getSetCode();
        setname.setText("");
        int index = 0;
        for (long set : sets) {
            if (set > 0) {
                setname.append("" + stringManager.getSetName(set));
                if (index == 0) {
                    setname.append("\n");
                }
                index++;
            }
        }
        if (cardInfo.isType(CardType.Monster)) {
            level.setVisibility(View.VISIBLE);
            atkdeflayout1.setVisibility(View.VISIBLE);
            atkdeflayout2.setVisibility(View.VISIBLE);
            view_bar.setVisibility(View.VISIBLE);
            race.setVisibility(View.VISIBLE);
            String star = "";
            for (int i = 0; i < cardInfo.Level; i++) {
                star += "★";
            }
            level.setText(star);
            if(cardInfo.isType(CardType.Xyz)){
                level.setTextColor(mContext.getResources().getColor(R.color.star_rank));
            }else{
                level.setTextColor(mContext.getResources().getColor(R.color.star));
            }
            cardAtk.setText((cardInfo.Attack < 0 ? "?" : String.valueOf(cardInfo.Attack)));
            cardDef.setText((cardInfo.Defense < 0 ? "?" : String.valueOf(cardInfo.Defense)));
            race.setText(stringManager.getRaceString(cardInfo.Race));
        } else {
            race.setVisibility(View.GONE);
            view_bar.setVisibility(View.GONE);
            level.setVisibility(View.GONE);
            atkdeflayout1.setVisibility(View.GONE);
            atkdeflayout2.setVisibility(View.GONE);
        }
        close.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onClose();
            }
        });
        faq.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onOpenUrl(cardInfo);
            }
        });
    }


    private <T extends View> T bind(int id) {
        return (T) view.findViewById(id);
    }
}
