package cn.garymb.ygomobile.ui.cards;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.BuildConfig;
import cn.garymb.ygomobile.ui.activities.PhotoViewActivity;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.activities.WebActivity;
import cn.garymb.ygomobile.ui.adapters.BaseAdapterPlus;
import ocgcore.StringManager;
import ocgcore.enums.CardType;

/***
 * 卡片详情
 */
public class CardDetail extends BaseAdapterPlus.BaseViewHolder {
    private static final String TAG = "CardDetail";
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
    private Context mContext;
    private StringManager mStringManager;

    public interface OnClickListener {
        void onOpenUrl(CardInfo cardInfo);

        void onAddMainCard(CardInfo cardInfo);

        void onAddSideCard(CardInfo cardInfo);

        void onClose();
    }

    public CardDetail(Context context, ImageLoader imageLoader, StringManager stringManager) {
        super(LayoutInflater.from(context).inflate(R.layout.dialog_cardinfo, null));
        mContext = context;
        cardImage = bind(R.id.card_image);
        this.imageLoader = imageLoader;
        mStringManager = stringManager;
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

    public Context getContext() {
        return mContext;
    }

    private void setCardInfo(CardInfo cardInfo) {
        if (cardInfo == null) return;
        imageLoader.bindImage(cardImage, cardInfo.Code, null, true);
        cardImage.setOnClickListener((v) -> {
            PhotoViewActivity.showImage(context, cardInfo.Code, cardInfo.Name);
        });
        name.setText(cardInfo.Name);
        desc.setText(cardInfo.Desc);
        cardcode.setText(String.format("%08d", cardInfo.Code));
        type.setText(cardInfo.getAllTypeString(mStringManager).replace("/", "|"));
        attrView.setText(mStringManager.getAttributeString(cardInfo.Attribute));
        otView.setText(mStringManager.getOtString(cardInfo.Ot, "" + cardInfo.Ot));
        long[] sets = cardInfo.getSetCode();
        setname.setText("");
        int index = 0;
        for (long set : sets) {
            if (set > 0) {
                if (index != 0) {
                    setname.append(" | ");
                }
                setname.append("" + mStringManager.getSetName(set));
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
            } else {
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
                cardDef.setText((cardInfo.Level < 0 ? "?" : "LINK-" + String.valueOf(cardInfo.Level)));
            } else {
                cardDef.setText((cardInfo.Defense < 0 ? "?" : String.valueOf(cardInfo.Defense)));
            }
            race.setText(mStringManager.getRaceString(cardInfo.Race));
        } else {
            race.setVisibility(View.GONE);
            monsterlayout.setVisibility(View.GONE);
            level.setVisibility(View.GONE);
        }
    }

    public void bind(CardInfo cardInfo, final int position, final CardListProvider provider, final OnClickListener listener) {
        Log.d("CardDetail", "bind " + position+",cardInfo="+cardInfo);
        if (cardInfo != null) {
            setCardInfo(cardInfo);
        }

        close.setOnClickListener((v) -> {
            if (listener != null) {
                listener.onClose();
            }
        });
        addMain.setOnClickListener((v) -> {
            if (listener != null) {
                if (cardInfo == null) {
                    return;
                }
                listener.onAddMainCard(cardInfo);
            }
        });
        addSide.setOnClickListener((v) -> {
            if (listener != null) {
                if (cardInfo == null) {
                    return;
                }
                listener.onAddSideCard(cardInfo);
            }
        });
        faq.setOnClickListener((v) -> {
            if (listener != null) {
                if (cardInfo == null) {
                    return;
                }
                listener.onOpenUrl(cardInfo);
            }
        });

        lastone.setOnClickListener((v) -> {
            if (position == 0) {
                Toast.makeText(getContext(), "已经是第一张啦", Toast.LENGTH_SHORT).show();
            } else {
                final int index = position - 1;
                bind(provider.getCard(index), index, provider, listener);
            }
        });

        nextone.setOnClickListener((v) -> {
            if (position < provider.getCardsCount() - 1) {
                final int index = position + 1;
                bind(provider.getCard(index), index, provider, listener);
            } else {
                Toast.makeText(getContext(), "已经是最后一张啦", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private <T extends View> T bind(int id) {
        return (T) findViewById(id);
    }

    public static class DefaultOnClickListener implements OnClickListener {
        public DefaultOnClickListener() {
        }

        @Override
        public void onOpenUrl(CardInfo cardInfo) {

        }

        @Override
        public void onClose() {
        }

        @Override
        public void onAddSideCard(CardInfo cardInfo) {

        }

        @Override
        public void onAddMainCard(CardInfo cardInfo) {

        }
    }

    ;
}
