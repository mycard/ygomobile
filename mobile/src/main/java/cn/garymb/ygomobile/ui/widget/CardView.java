package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.loader.ImageLoader;
import ocgcore.data.Card;

public class CardView extends FrameLayout {
    private final ImageView mCardView, mCountView;
    private Card mCard;

    public CardView(Context context) {
        this(context, null);
    }

    public CardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCardView = new ImageView(context);
        mCardView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        lp.setMargins(1, 1, 1, 1);
        addView(mCardView, lp);

        LayoutParams lp2 = new LayoutParams((int) getResources().getDimension(R.dimen.right_size2), (int) getResources().getDimension(R.dimen.right_size2));
        lp2.gravity = Gravity.RIGHT;
        mCountView = new ImageView(context);
        mCountView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        addView(mCountView, lp);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setBackgroundResource(R.drawable.selected);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(null);
            } else {
                setBackgroundDrawable(null);
            }
        }
    }

    public void showCard(Card card) {
        mCard = card;
        if (card != null) {
            ImageLoader.get(getContext()).bindImage(mCardView, card.Code);
        }
    }

    public Card getCard() {
        return mCard;
    }
}
