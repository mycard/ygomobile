package cn.garymb.ygomobile.deck;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.util.List;

import cn.garymb.ygomobile.Constants;
import cn.ygo.ocgcore.Card;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class DeckItemTouchHelper extends ItemTouchHelper.Callback {
    private DeckDrager mDeckDrager;

    //    private RecyclerView.ViewHolder NULL;
    public interface CallBack {
        void onDragStart();

        void onDragEnd();
    }
    private CallBack mCallBack;

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public DeckItemTouchHelper(DeckAdapater deckAdapater) {
        this.mDeckDrager = new DeckDrager(deckAdapater);
//        NULL = new RecyclerView.ViewHolder(new View(deckAdapater.getContext())) {
//
//        };
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
                               RecyclerView.ViewHolder target) {
        int id = current.getAdapterPosition();
        int left = current.getAdapterPosition();
        int right = target.getAdapterPosition();
        if (DeckItemUtils.isLabel(left)) {
            return false;
        }
        if (DeckItemUtils.isMain(left)) {
            if (DeckItemUtils.isExtra(right)) {
                return false;
            }
        } else if (DeckItemUtils.isExtra(left)) {
            if (DeckItemUtils.isMain(right)) {
                return false;
            }
        }
        return super.canDropOver(recyclerView, current, target);
    }

    @Override
    public int getBoundingBoxMargin() {
        return -1;
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
//                Log.d("kk", "move is label or space " + id);
                return makeMovementFlags(0, 0);
            }
        } else {
            if (DeckItemUtils.isLabel(id)) {
//                Log.d("kk", "move is label " + id);
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
            if(mCallBack!=null){
                mCallBack.onDragStart();
            }
        } else if (actionState == ACTION_STATE_IDLE) {
            if(mCallBack!=null){
                mCallBack.onDragEnd();
            }
        } else if (actionState == ACTION_STATE_SWIPE) {

        }
    }


    @Override
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        return super.chooseDropTarget(selected, dropTargets, curX, curY);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//        if (target == NULL || !(target instanceof DeckViewHolder)) {
//            mDeckDrager.delete((DeckViewHolder) viewHolder);
//            return false;
//        }
        return mDeckDrager.move((DeckViewHolder) viewHolder, (DeckViewHolder) target);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i("kk", "onSwiped " + viewHolder.getAdapterPosition() + " -> " + direction);
//        mDeckDrager.remove(viewHolder.getAdapterPosition());
    }
}
