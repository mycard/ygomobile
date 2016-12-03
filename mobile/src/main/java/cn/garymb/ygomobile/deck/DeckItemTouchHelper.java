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
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;

public class DeckItemTouchHelper extends ItemTouchHelper.Callback {
    private DeckDrager mDeckDrager;

    public DeckItemTouchHelper(DeckAdapater deckAdapater) {
        this.mDeckDrager = new DeckDrager(deckAdapater);
    }

    /**
     * 控制哪些可以拖拽
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int id = viewHolder.getAdapterPosition();
        if (viewHolder instanceof DeckViewHolder) {
            DeckViewHolder deckholder = (DeckViewHolder) viewHolder;
            if (deckholder.getItemType() == DeckItemType.Space
                    || deckholder.getItemType() == DeckItemType.MainLabel
                    || deckholder.getItemType() == DeckItemType.SideLabel
                    || deckholder.getItemType() == DeckItemType.ExtraLabel) {
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
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_DRAG) {
            mDeckDrager.onDragStart((DeckViewHolder) viewHolder);
        } else if (actionState == ACTION_STATE_IDLE) {
            mDeckDrager.onDragEnd((DeckViewHolder) viewHolder);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int left = viewHolder.getAdapterPosition();
        int right = target.getAdapterPosition();
        boolean move= mDeckDrager.move(left, right);
        if(move) {
            Log.d("kk", "move " + left + "<->" + right);
        }else{
            Log.d("kk", "don't move " + left + "<->" + right);
        }
        return move;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
