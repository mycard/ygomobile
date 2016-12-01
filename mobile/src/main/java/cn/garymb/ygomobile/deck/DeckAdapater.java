package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.CardInfo;
import cn.garymb.ygomobile.core.loader.ImageLoader;
import cn.garymb.ygomobile.lite.R;
import cn.ygo.ocgcore.enums.LimitType;


public class DeckAdapater extends RecyclerView.Adapter<DeckViewHolder> {
    private final List<DeckItem> mItems = new ArrayList<>();
    private DeckInfo mDeck = new DeckInfo();
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ImageTop mImageTop;
    private int mFullWidth;
    private int mWidth;
    private int mHeight;
    private int Padding = 1;
    private RecyclerView recyclerView;

    public DeckAdapater(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        mLayoutInflater = LayoutInflater.from(context);
    }

    private void makeHeight() {
        mFullWidth = recyclerView.getMeasuredWidth() - recyclerView.getPaddingRight() - recyclerView.getPaddingLeft();
        mWidth = mFullWidth / Constants.DECK_WIDTH_COUNT - 2 * Padding;
        mHeight = mWidth * Constants.CORE_SKIN_CARD_COVER_SIZE[1] / Constants.CORE_SKIN_CARD_COVER_SIZE[0];
    }

    public void setDeck(DeckInfo deck) {
        this.mDeck = deck;
        mItems.clear();
        mItems.addAll(DeckItemUtils.makeItems(context, deck));
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.card_image, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {
        DeckItem item = mItems.get(position);
        if (item.getType() == DeckItemType.Label) {
            holder.cardImage.setVisibility(View.GONE);
            holder.rightImage.setVisibility(View.GONE);
            holder.labelText.setVisibility(View.VISIBLE);
            holder.labelText.setText(item.getText());
            if (item.getColor() != 0) {
                holder.labelText.setTextColor(item.getColor());
            }
//            holder.labelText.setLayoutParams(new RelativeLayout.LayoutParams(mFullWidth, mHeight));
        } else {
            if (holder.cardImage.getMeasuredHeight() > 0) {
                mHeight = holder.cardImage.getMeasuredHeight();
            }
            if (mHeight <= 0) {
                makeHeight();
            }
//            holder.cardImage.setLayoutParams(new RelativeLayout.LayoutParams(holder.cardImage.getMeasuredWidth(), mHeight));
            holder.cardImage.setVisibility(View.VISIBLE);
            holder.labelText.setVisibility(View.GONE);
            if (item.getType() == DeckItemType.Space) {
                holder.setSize(mHeight);
                holder.cardImage.setImageDrawable(null);
//                holder.useDefault();
                holder.rightImage.setVisibility(View.GONE);
            } else {
                CardInfo cardInfo = item.getCardInfo();
                if (cardInfo != null) {
                    if (mImageTop == null) {
                        mImageTop = new ImageTop(context);
                    }
                    holder.rightImage.setVisibility(View.VISIBLE);
                    if (cardInfo.getLimitType() == LimitType.Forbidden) {
                        holder.rightImage.setImageBitmap(mImageTop.forbidden);
                    } else if (cardInfo.getLimitType() == LimitType.Limit) {
                        holder.rightImage.setImageBitmap(mImageTop.limit);
                    } else if (cardInfo.getLimitType() == LimitType.Limit) {
                        holder.rightImage.setImageBitmap(mImageTop.semiLimit);
                    } else {
                        holder.rightImage.setVisibility(View.GONE);
                    }
                    ImageLoader.get().bindImage(context, holder.cardImage, cardInfo.Code);
                } else {
                    holder.rightImage.setVisibility(View.GONE);
                    holder.useDefault();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
