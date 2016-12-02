package cn.garymb.ygomobile.deck;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.settings.AppsSettings;
import cn.ygo.ocgcore.enums.CardType;

class DeckViewHolder extends RecyclerView.ViewHolder {
    private long mCardType;
    public DeckViewHolder(View view) {
        super(view);
        this.view = view;
        view.setTag(view.getId(), this);
        cardImage = findViewById(R.id.card_image);
        rightImage = findViewById(R.id.right_top);
        labelText = findViewById(R.id.label);
        textlayout = findViewById(R.id.layout_label);
    }

    public long getCardType() {
        return mCardType;
    }

    public void setCardType(long cardType) {
        mCardType = cardType;
    }

    public void setSize(int height) {
        if (height > 0) {
            cardImage.setMinimumHeight(height);
            rightImage.setMaxWidth(height / 5);
            rightImage.setMaxHeight(height / 5);
        }
    }

    public void useDefault() {
        File outFile = new File(AppsSettings.get().getCoreSkinPath(), Constants.CORE_SKIN_COVER);
        ImageLoader.get().bind(view.getContext(), outFile, cardImage, outFile.getName().endsWith(Constants.BPG), 0);
    }

    protected <T extends View> T findViewById(int id) {
        return (T) view.findViewById(id);
    }

    private final View view;
    public final View textlayout;
    public final TextView labelText;
    public final ImageView cardImage;
    public final ImageView rightImage;
}
