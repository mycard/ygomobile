package cn.garymb.ygomobile.deck;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.lite.R;

public class DeckItemTouchHelper extends ItemTouchHelper.Callback {
    Drawable bg = null;
    private Context context;
    private DeckAdapater deckAdapater;

    public DeckItemTouchHelper(Context context, DeckAdapater deckAdapater) {
        this.context = context;
        this.deckAdapater = deckAdapater;
    }

    private int color(int id) {
        return context.getResources().getColor(id);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundDrawable(bg);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            bg = viewHolder.itemView.getBackground();
            viewHolder.itemView.setBackgroundColor(color(R.color.bg));
        }
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
                               RecyclerView.ViewHolder target) {
        int left = current.getAdapterPosition();
        int right = target.getAdapterPosition();
        DeckItem deckItem = deckAdapater.getItem(left);
        if (deckItem.getType() == DeckItemType.Space) {
            return false;
        }
        if (DeckItemUtils.isLabel(left) || DeckItemUtils.isLabel(right)) {
            return false;
        }
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int id = viewHolder.getAdapterPosition();
        if (DeckItemUtils.isLabel(id)) {
            return makeMovementFlags(0, 0);
        }
        if (id > DeckItem.SideStart) {
            if ((id - DeckItem.SideStart) >= deckAdapater.getSideCount()) {
                return makeMovementFlags(0, 0);
            }
        } else if (id > DeckItem.ExtraStart) {
            if ((id - DeckItem.ExtraStart) >= deckAdapater.getExtraCount()) {
                return makeMovementFlags(0, 0);
            }
        } else {
            if ((id - DeckItem.MainStart) >= deckAdapater.getMainCount()) {
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

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int left = viewHolder.getAdapterPosition();
        int right = target.getAdapterPosition();
        if (DeckItemUtils.isMain(left)) {
            if (DeckItemUtils.isSide(right)) {
                if (deckAdapater.getSideCount() >= (Constants.DECK_SIDE_MAX + 1)) {
                    return false;
                }
                deckAdapater.moveMainToSide(left, right);
                return true;
            }
            if (DeckItemUtils.isMain(right)) {
                deckAdapater.moveMain(left, right);
                return true;
            }
        }
        if (DeckItemUtils.isSide(left)) {
            if (DeckItemUtils.isMain(right)) {
                if (deckAdapater.getMainCount() >= Constants.DECK_MAIN_MAX) {
                    Log.i("kk", "move main max");
                    return false;
                }
                //判断类型
                deckAdapater.moveSideToMain(left, right);
                return true;
            }
            if (DeckItemUtils.isExtra(right)) {
                if (deckAdapater.getExtraCount() >= Constants.DECK_EXTRA_MAX) {
                    Log.i("kk", "move extra max:"+deckAdapater.getExtraCount());
                    return false;
                }
                //判断类型
                deckAdapater.moveSideToExtra(left, right);
                return true;
            }
            if (DeckItemUtils.isSide(right)) {
                deckAdapater.moveSide(left, right);
                return true;
            }
            Log.i("kk", "move extra fail " + left + "->" + right);
        }
        if (DeckItemUtils.isExtra(left)) {
            if (DeckItemUtils.isSide(right)) {
                if (deckAdapater.getSideCount() >= (Constants.DECK_SIDE_MAX + 1)) {
                    return false;
                }
                deckAdapater.moveExtraToSide(left, right);
                return true;
            }
            if (DeckItemUtils.isExtra(right)) {
                deckAdapater.moveExtra(left, right);
                return true;
            }
        }
        // mDeckAdapater.notifyItemChanged(left, right);
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i("kk", "onSwiped id= " + viewHolder.getAdapterPosition() + ",direction=" + direction);
    }
}
