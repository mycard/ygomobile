package cn.garymb.ygomobile.ui.cards.deck;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.garymb.ygomobile.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;

class DeckViewHolder extends RecyclerView.ViewHolder {
    private long mCardType;
    private DeckItemType mItemType;

    public DeckViewHolder(View view) {
        super(view);
        this.view = view;
        view.setTag(view.getId(), this);
        cardImage = findViewById(R.id.card_image);
        rightImage = findViewById(R.id.right_top);
        labelText = findViewById(R.id.label);
        textlayout = findViewById(R.id.layout_label);
        headView = findViewById(R.id.head);
    }

    public DeckItemType getItemType() {
        return mItemType;
    }

    public void setItemType(DeckItemType itemType) {
        mItemType = itemType;
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
            cardImage.setMaxHeight(height);
            rightImage.setMaxWidth(height / 5);
            rightImage.setMaxHeight(height / 5);
            ViewGroup.LayoutParams layoutParams = cardImage.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = height;
            }
        }
    }

    public void useDefault(ImageLoader imageLoader, int w, int h) {
        cardImage.setImageResource(R.drawable.unknown);
//        File outFile = new File(AppsSettings.get().getCoreSkinPath(), Constants.UNKNOWN_IMAGE);
//        ViewGroup.LayoutParams layoutParams = cardImage.getLayoutParams();
//        if (layoutParams != null) {
//            layoutParams.height = h;
//        }
//        imageLoader.$(outFile, cardImage, outFile.getName().endsWith(Constants.BPG), 0, null);
    }

    protected <T extends View> T findViewById(int id) {
        return (T) view.findViewById(id);
    }

    public void show() {
        view.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view.setVisibility(View.GONE);
    }

    public void setHeadVisibility(int visibility) {
        if (headView != null)
        headView.setVisibility(visibility);
    }

    private final View view;
    private final View headView;
    public final View textlayout;
    public final TextView labelText;
    public final ImageView cardImage;
    public final ImageView rightImage;
}
