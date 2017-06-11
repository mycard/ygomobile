package cn.garymb.ygomobile.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.garymb.ygomobile.activities.PhotoViewActivity;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.ygo.ocgcore.StringManager;
import cn.ygo.ocgcore.enums.CardType;

/***
 * 卡片详情
 */
public class CardDetail extends BaseAdapterPlus.BaseViewHolder {
    private ImageView cardImage;
    private TextView name;
    private TextView desc;
    private TextView level;
    private TextView type;
    private TextView race;
    private TextView cardAtk;
    private TextView cardDef;

    private TextView setname;
    private TextView otView;
    private TextView attrView;
    private View monsterlayout;
    private View close;
    private View faq;
    private View addMain;
    private View addSide;
    private TextView cardcode;
    private View lb_setcode;
    private ImageLoader imageLoader;

    private Button lastone;
    private Button nextone;

    public interface OnClickListener {
        void onOpenUrl(CardInfo cardInfo);

        void onAddMainCard(CardInfo cardInfo);

        void onAddSideCard(CardInfo cardInfo);

        void onClose();

        void onLastone(Button lastone);

        void onNextone(Button nextone);
    }

    public CardDetail(Context context, ImageLoader imageLoader) {
        super(LayoutInflater.from(context).inflate(R.layout.dialog_cardinfo, null));
        cardImage = bind(R.id.card_image);
        this.imageLoader = imageLoader;
        name = bind(R.id.text_name);
        desc = bind(R.id.text_desc);
        close = bind(R.id.btn_close);
        cardcode = bind(R.id.card_code);
        level = bind(R.id.card_level);
        type = bind(R.id.card_type);
        faq = bind(R.id.btn_faq);
        cardAtk = bind(R.id.card_atk);
        cardDef = bind(R.id.card_def);

        monsterlayout = bind(R.id.layout_monster);
        race = bind(R.id.card_race);
        setname = bind(R.id.card_setname);
        addMain = bind(R.id.btn_add_main);
        addSide = bind(R.id.btn_add_side);
        otView = bind(R.id.card_ot);
        attrView = bind(R.id.card_attribute);
        lb_setcode = bind(R.id.label_setcode);

        lastone = bind(R.id.lastone);
        nextone = bind(R.id.nextone);
    }

    public ImageView getCardImage() {
        return cardImage;
    }

    public void hideClose() {
        close.setVisibility(View.GONE);
    }

    public void showAdd() {
        addSide.setVisibility(View.VISIBLE);
        addMain.setVisibility(View.VISIBLE);
    }

    public View getView() {
        return view;
    }

    public void bind(CardInfo cardInfo, StringManager stringManager, final OnClickListener listener) {
        imageLoader.bindImage(cardImage, cardInfo.Code, null, true);
        cardImage.setOnClickListener((v)->{
            PhotoViewActivity.showImage(context, cardInfo.Code, cardInfo.Name);
        });
        name.setText(cardInfo.Name);
        desc.setText(cardInfo.Desc);
        cardcode.setText(String.format("%08d", cardInfo.Code));
        type.setText(cardInfo.getAllTypeString(stringManager).replace("/", "|"));
        attrView.setText(stringManager.getAttributeString(cardInfo.Attribute));
        otView.setText(stringManager.getOtString(cardInfo.Ot, "" + cardInfo.Ot));
        long[] sets = cardInfo.getSetCode();
        setname.setText("");
        int index = 0;
        for (long set : sets) {
            if (set > 0) {
                if (index != 0) {
                    setname.append(" | ");
                }
                setname.append("" + stringManager.getSetName(set));
                index++;
            }
        }
        if (TextUtils.isEmpty(setname.getText())) {
            setname.setVisibility(View.INVISIBLE);
            lb_setcode.setVisibility(View.INVISIBLE);
        } else {
            setname.setVisibility(View.VISIBLE);
            lb_setcode.setVisibility(View.VISIBLE);
        }
        if (cardInfo.isType(CardType.Monster)) {
            if (cardInfo.isType(CardType.Link)) {
                level.setVisibility(View.INVISIBLE);
            }else {
                level.setVisibility(View.VISIBLE);
            }
            monsterlayout.setVisibility(View.VISIBLE);
            race.setVisibility(View.VISIBLE);
            String star = "";
            for (int i = 0; i < cardInfo.Level; i++) {
                star += "★";
            }
            level.setText(star);
            if (cardInfo.isType(CardType.Xyz)) {
                level.setTextColor(context.getResources().getColor(R.color.star_rank));
                } else {
                    level.setTextColor(context.getResources().getColor(R.color.star));
                }
            cardAtk.setText((cardInfo.Attack < 0 ? "?" : String.valueOf(cardInfo.Attack)));
            if (cardInfo.isType(CardType.Link)) {
                cardDef.setText((cardInfo.Level < 0 ? "?" : "LINK-"+String.valueOf(cardInfo.Level)));
            } else {
                cardDef.setText((cardInfo.Defense < 0 ? "?" : String.valueOf(cardInfo.Defense)));
            }
            race.setText(stringManager.getRaceString(cardInfo.Race));
        } else {
            race.setVisibility(View.GONE);
            monsterlayout.setVisibility(View.GONE);
            level.setVisibility(View.GONE);
        }
        close.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onClose();
            }
        });
        addMain.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onAddMainCard(cardInfo);
            }
        });
        addSide.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onAddSideCard(cardInfo);
            }
        });
        faq.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onOpenUrl(cardInfo);
            }
        });

        lastone.setOnClickListener((v) -> {
            if(listener==null){
                Log.i("Listener左箭头","Listener为空");
            }else{
                Log.i("Listener左箭头","左箭头被点击");
            }
            if (listener != null) {
                listener.onLastone(lastone);
            }
        });

        nextone.setOnClickListener((v) -> {
            if(listener==null){
                Log.i("Listener右箭头","Listener为空");
            }else{
                Log.i("Listener右箭头","右箭头被点击");
            }
            if (listener != null) {
                listener.onNextone(nextone);
            }
        });

    }

    private <T extends View> T bind(int id) {
        return (T) findViewById(id);
    }
}
