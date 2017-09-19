package cn.garymb.ygomobile.ui.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.loader.ImageLoader;
import cn.garymb.ygomobile.ui.cards.deck.ImageTop;
import ocgcore.data.Card;
import ocgcore.data.LimitList;
import ocgcore.enums.LimitType;

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
        int p = (int) getResources().getDimension(R.dimen.card_padding);
        lp.setMargins(p, p, p, p);
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

    public void updateLimit(ImageTop imageTop, LimitList limitList) {
        if (mCard != null && imageTop != null) {
            mCountView.setVisibility(View.VISIBLE);
            if (limitList != null) {
                if (limitList.check(mCard, LimitType.Forbidden)) {
                    mCountView.setImageBitmap(imageTop.forbidden);
                } else if (limitList.check(mCard, LimitType.Limit)) {
                    mCountView.setImageBitmap(imageTop.limit);
                } else if (limitList.check(mCard, LimitType.SemiLimit)) {
                    mCountView.setImageBitmap(imageTop.semiLimit);
                } else {
                    mCountView.setVisibility(View.GONE);
                }
            } else {
                mCountView.setVisibility(View.GONE);
            }
        }
    }

    public void showCard(Card cardInfo) {
        setVisibility(VISIBLE);
        if (mCard != null && mCard.equals(cardInfo)) return;
        mCard = cardInfo;
        if (cardInfo != null) {
            ImageLoader.get(getContext()).bindImage(mCardView, cardInfo.Code);
        }
    }

    public Card getCard() {
        return mCard;
    }
}
