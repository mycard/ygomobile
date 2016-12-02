package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;
import cn.ygo.ocgcore.Card;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;

public class DeckItemTouchHelper extends ItemTouchHelper.Callback {
    private Drawable bg = null;
    private Context context;
    private DeckAdapater deckAdapater;
    private DeckDrager mDeckDrager;

    public DeckItemTouchHelper(Context context, DeckAdapater deckAdapater) {
        this.context = context;
        this.mDeckDrager = new DeckDrager(deckAdapater);
        this.deckAdapater = deckAdapater;
    }

    private int color(int id) {
        return context.getResources().getColor(id);
    }

//    @Override
//    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
//        viewHolder.itemView.setBackgroundDrawable(bg);
//    }
//
//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        super.onSelectedChanged(viewHolder, actionState);
//        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            bg = viewHolder.itemView.getBackground();
//            viewHolder.itemView.setBackgroundColor(color(R.color.bg));
//        }
//    }

//    @Override
//    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
//                               RecyclerView.ViewHolder target) {
//        int left = current.getAdapterPosition();
//        int right = target.getAdapterPosition();
//        DeckItem deckItem = deckAdapater.getItem(left);
//        if (deckItem.getType() == DeckItemType.Space) {
//            return false;
//        }
//        if (DeckItemUtils.isLabel(left) || DeckItemUtils.isLabel(right)) {
//            return false;
//        }
//        return true;
//    }

    /**
     * 控制哪些可以拖拽
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int id = viewHolder.getAdapterPosition();
        if (viewHolder instanceof DeckViewHolder) {
            DeckViewHolder deckholder = (DeckViewHolder) viewHolder;
            if (deckholder.getItemType() == DeckItemType.Space || deckholder.getItemType() == DeckItemType.Label) {
                Log.d("kk", "move is label or space " + id);
                return makeMovementFlags(0, 0);
            }
        } else {
            if (DeckItemUtils.isLabel(id)) {
                Log.d("kk", "move is label " + id);
                return makeMovementFlags(0, 0);
            }
        }

        int dragFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
//        int swipeFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    private boolean isExtra;
    private int mSelectId;

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_DRAG) {
            mSelectId = viewHolder.getAdapterPosition();
            DeckViewHolder deckViewHolder = (DeckViewHolder) viewHolder;
            isExtra = Card.isExtraCard(deckViewHolder.getCardType());
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int left = viewHolder.getAdapterPosition();
        int right = target.getAdapterPosition();
//        if (left == DeckItem.ExtraEnd) {
//            mDeckDrager.removeExtraEnd();
//            return true;
//        } else if (left == DeckItem.MainEnd) {
//            mDeckDrager.removeMainEnd();
//            return true;
//        }
        if (DeckItemUtils.isMain(left)) {
            Log.d("kk", "move main");
            if (DeckItemUtils.isSide(right)) {
                if (deckAdapater.getSideCount() >= (Constants.DECK_SIDE_MAX + 1)) {
                    Log.d("kk", "move main to side max " + deckAdapater.getSideCount());
                    return false;
                }
                Log.d("kk", "move main to side " + left + "/" + right);
                mDeckDrager.moveMainToSide(left, right);
                return true;
            }
            if (DeckItemUtils.isMain(right)) {
                mDeckDrager.moveMain(left, right);
                return true;
            }
        } else if (DeckItemUtils.isSide(left)) {
            Log.d("kk", "move side");
            if (DeckItemUtils.isMain(right)) {
                //判断类型
//                if (viewHolder instanceof DeckViewHolder) {
//                    DeckViewHolder deckViewHolder = (DeckViewHolder) viewHolder;
//                    if (Card.isExtraCard(deckViewHolder.getCardType())) {
//                        Log.d("kk", "move side to main is extra:" + right);
//                        return false;
//                    }
//                }
                if (isExtra) {
                    Log.d("kk", "move side to main is extra:" + right);
                    return false;
                }
                if (deckAdapater.getMainCount() >= Constants.DECK_MAIN_MAX) {
                    Log.d("kk", "move side to main max:" + deckAdapater.getMainCount());
                    return false;
                }
                Log.d("kk", "move side to main " + left + "/" + right);
                mDeckDrager.moveSideToMain(left, right);
                return true;
            }
            if (DeckItemUtils.isExtra(right)) {
                Log.d("kk", "move extra");
//                if (viewHolder instanceof DeckViewHolder) {
//                    DeckViewHolder deckViewHolder = (DeckViewHolder) viewHolder;
//                    if (!Card.isExtraCard(deckViewHolder.getCardType())) {
//                        Log.d("kk", "move side to main isn't extra:" + right);
//                        return false;
//                    }
//                }
                if (!isExtra) {
                    Log.d("kk", "move side to main is extra:" + right);
                    return false;
                }
                if (deckAdapater.getExtraCount() >= Constants.DECK_EXTRA_MAX) {
                    return false;
                }
                //判断类型
                mDeckDrager.moveSideToExtra(left, right);
                return true;
            }
            if (DeckItemUtils.isSide(right)) {
                mDeckDrager.moveSide(left, right);
                return true;
            }
            Log.d("kk", "move side fail " + left + "->" + right);
        } else if (DeckItemUtils.isExtra(left)) {
            if (DeckItemUtils.isSide(right)) {
                if (deckAdapater.getSideCount() >= (Constants.DECK_SIDE_MAX + 1)) {
                    return false;
                }
                mDeckDrager.moveExtraToSide(left, right);
                return true;
            }
            if (DeckItemUtils.isExtra(right)) {
                mDeckDrager.moveExtra(left, right);
                return true;
            }
        }
        // mDeckAdapater.notifyItemChanged(left, right);
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//        Log.i("kk", "onSwiped id= " + viewHolder.getAdapterPosition() + ",direction=" + direction);
    }
}
