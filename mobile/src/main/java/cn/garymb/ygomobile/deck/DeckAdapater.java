package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
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
    private int mDragPosition = -1;

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

    @Override
    public long getItemId(int position) {
        return position;
    }

    public DeckItem getItem(int position) {
        return mItems.get(position);
    }

    public int getMainCount() {
        if (mDeck == null || mDeck.getMainCards() == null) {
            return 0;
        }
        return mDeck.getMainCards().size();
    }

    public int getExtraCount() {
        if (mDeck == null || mDeck.getExtraCards() == null) {
            return 0;
        }
        return mDeck.getExtraCards().size();
    }

    public int getSideCount() {
        if (mDeck == null || mDeck.getSideCards() == null) {
            return 0;
        }
        return mDeck.getSideCards().size();
    }

    public void setDeck(DeckInfo deck) {
        this.mDeck = deck;
        loadData();
    }

    private void loadData() {
        mItems.clear();
        mItems.addAll(DeckItemUtils.makeItems(context, mDeck));
    }

    @Override
    public DeckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.card_image, parent, false);
        return new DeckViewHolder(view);
    }

    public void moveMain(int src, int to) {
        if (mDeck == null || mDeck.getMainCards() == null) {
            return;
        }
        int left = src - DeckItem.MainStart;
        int right = to - DeckItem.MainStart;
        Log.i("kk", "move main " + left + "->" + right);
        int count = getMainCount();
        if (left >= count && right >= count) {
            return;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(mDeck.getMainCards(), left, right);
        Collections.swap(mItems, DeckItem.MainStart + left, DeckItem.MainStart + right);
        notifyItemMoved(DeckItem.MainStart + left, DeckItem.MainStart + right);
//        loadData();
//        notifyDataSetChanged();
    }

    public void moveSide(int src, int to) {
        if (mDeck == null || mDeck.getSideCards() == null) {
            return;
        }
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.SideStart;
        Log.i("kk", "move side " + left + "->" + right);
        int count = getSideCount();
        if (left >= count && right >= count) {
            return;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(mDeck.getSideCards(), left, right);
        Collections.swap(mItems, DeckItem.SideStart + left, DeckItem.SideStart + right);
        notifyItemMoved(DeckItem.SideStart + left, DeckItem.SideStart + right);
    }

    public void moveExtra(int src, int to) {
        if (mDeck == null || mDeck.getExtraCards() == null) {
            return;
        }
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.ExtraStart;
        Log.i("kk", "move extra " + left + "->" + right);
        int count = getExtraCount();
        if (left >= count && right >= count) {
            return;
        }
        if (left >= count) {
            left = count - 1;
        }
        if (right >= count) {
            right = count - 1;
        }
        Collections.swap(mDeck.getExtraCards(), left, right);
        Collections.swap(mItems, DeckItem.ExtraStart + left, DeckItem.ExtraStart + right);
        notifyItemMoved(DeckItem.ExtraStart + left, DeckItem.ExtraStart + right);
    }
    public void moveSideToExtra(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.ExtraStart;
        int maincount = getExtraCount();
        if (right >= maincount) {
            right = maincount - 1;
        }
        CardInfo cardInfo = mDeck.getSideCards().remove(left);
        mDeck.getMainCards().add(right, cardInfo);
//        Collections.swap(mDeck.getExtraCards(), left, right);
        Collections.swap(mItems, DeckItem.SideStart + left, DeckItem.ExtraStart + right);
        //多出一个空白
        notifyItemMoved(DeckItem.SideStart + left, DeckItem.ExtraStart + right);
        DeckItem deckItem = mItems.remove(DeckItem.ExtraEnd);
        notifyItemRemoved(DeckItem.ExtraEnd);
        mItems.add(DeckItem.SideEnd, new DeckItem());
        notifyItemInserted(DeckItem.SideEnd);
    }

    public void moveExtraToSide(int src, int to) {
        int left = src - DeckItem.ExtraStart;
        int right = to - DeckItem.SideStart;
        int maincount = getSideCount();
        if (right >= maincount) {
            right = maincount - 1;
        }
        //
        CardInfo cardInfo = mDeck.getExtraCards().remove(left);
        mDeck.getSideCards().add(right, cardInfo);
        Collections.swap(mItems, DeckItem.ExtraStart + left, DeckItem.SideStart + right);
        notifyItemMoved(DeckItem.ExtraStart + left, DeckItem.SideStart + right);
        //多出一个空白
        DeckItem deckItem = mItems.remove(DeckItem.SideEnd);
        mItems.add(DeckItem.ExtraEnd, new DeckItem());
        notifyItemRemoved(DeckItem.SideEnd);
        notifyItemInserted(DeckItem.ExtraEnd);
    }

    public void moveSideToMain(int src, int to) {
        int left = src - DeckItem.SideStart;
        int right = to - DeckItem.MainStart;
        int maincount = getMainCount();
        if (right >= maincount) {
            right = maincount - 1;
        }
        CardInfo cardInfo = mDeck.getSideCards().remove(left);
        mDeck.getMainCards().add(right, cardInfo);
//        Collections.swap(mDeck.getExtraCards(), left, right);
        Collections.swap(mItems, DeckItem.SideStart + left, DeckItem.MainStart + right);
        //多出一个空白
        notifyItemMoved(DeckItem.SideStart + left, DeckItem.MainStart + right);
        DeckItem deckItem = mItems.remove(DeckItem.MainEnd);
        notifyItemRemoved(DeckItem.MainEnd);
        mItems.add(DeckItem.SideEnd, new DeckItem());
        notifyItemInserted(DeckItem.SideEnd);
    }

    public void moveMainToSide(int src, int to) {
        int left = src - DeckItem.MainStart;
        int right = to - DeckItem.SideStart;
        int maincount = getSideCount();
        if (right >= maincount) {
            right = maincount - 1;
        }
        //
        CardInfo cardInfo = mDeck.getMainCards().remove(left);
        mDeck.getSideCards().add(right, cardInfo);
        Collections.swap(mItems, DeckItem.MainStart + left, DeckItem.SideStart + right);
        notifyItemMoved(DeckItem.MainStart + left, DeckItem.SideStart + right);
        //多出一个空白
        DeckItem deckItem = mItems.remove(DeckItem.SideEnd);
        mItems.add(DeckItem.MainEnd, new DeckItem());
        notifyItemRemoved(DeckItem.SideEnd);
        notifyItemInserted(DeckItem.MainEnd);
    }

    @Override
    public void onBindViewHolder(DeckViewHolder holder, int position) {
        DeckItem item = mItems.get(position);
        if (item.getType() == DeckItemType.Label) {
            holder.cardImage.setVisibility(View.GONE);
            holder.rightImage.setVisibility(View.GONE);
            holder.labelText.setText(item.getText());
            holder.textlayout.setVisibility(View.VISIBLE);
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
            holder.textlayout.setVisibility(View.GONE);
            if (position == mDragPosition) {
                holder.cardImage.setVisibility(View.GONE);
            } else {
                holder.cardImage.setVisibility(View.VISIBLE);
            }
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

    public int getDragPosition() {
        return mDragPosition;
    }

    public void setDragPosition(int dragPosition) {
        mDragPosition = dragPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
