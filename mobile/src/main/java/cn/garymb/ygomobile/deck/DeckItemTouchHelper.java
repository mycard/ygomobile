package cn.garymb.ygomobile.deck;

import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import java.util.List;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class DeckItemTouchHelper extends ItemTouchHelper.Callback {
    private DeckDrager mDeckDrager;

    //    private RecyclerView.ViewHolder NULL;
    public interface CallBack {
        void onDragStart(boolean isdelete);

        void onDragEnd();
    }

    private CallBack mCallBack;
    private DeckAdapater deckAdapater;
    private Handler mHandler;

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public DeckItemTouchHelper(DeckAdapater deckAdapater) {
        this.mDeckDrager = new DeckDrager(deckAdapater);
        this.deckAdapater = deckAdapater;
        mHandler = new Handler(deckAdapater.getContext().getMainLooper());
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    private boolean isDeleteMode;

    public void setDeleteMode(boolean deleteMode) {
        isDeleteMode = deleteMode;
    }

    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
                               RecyclerView.ViewHolder target) {
        int id = target.getAdapterPosition();
        if (isDeleteMode) {
            return id == DeckItem.HeadView;
        }
        return id != DeckItem.HeadView;
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
                    || deckholder.getItemType() == DeckItemType.ExtraLabel
                    || deckholder.getItemType() == DeckItemType.HeadView) {
//                Log.d("kk", "move is label or space " + id);
                return makeMovementFlags(0, 0);
            }
        } else {
            if (DeckItemUtils.isLabel(id) || id == DeckItem.HeadView) {
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
            isCencel = false;
            lasttime = System.currentTimeMillis();
            mHandler.postDelayed(enterDelete, 1000);
            mDeckDrager.onDragStart();
            if (mCallBack != null) {
                mCallBack.onDragStart(isDeleteMode);
            }
        } else if (actionState == ACTION_STATE_IDLE) {
            mDeckDrager.onDragEnd();
            if (mCallBack != null) {
                mCallBack.onDragEnd();
            }
            mHandler.removeCallbacks(enterDelete);
        } else if (actionState == ACTION_STATE_SWIPE) {

        }
    }

    private Runnable enterDelete = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - lasttime >= 800) {
                Log.i("drag", "enter delete");
                isDeleteMode = true;
                if (mCallBack != null) {
                    mCallBack.onDragStart(isDeleteMode);
                }
            } else {
                Log.w("drag", "no enter delete " + (System.currentTimeMillis() - lasttime));
            }
        }
    };

    private volatile long lasttime = 0;
    private boolean isCencel = false;

    @Override
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected, List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        if (!isCencel) {
            isCencel = true;
            if (!isDeleteMode) {
                lasttime = System.currentTimeMillis();
                mHandler.removeCallbacks(enterDelete);
                Log.w("drag", "cancel enter delete");
            }
        }
        return super.chooseDropTarget(selected, dropTargets, curX, curY);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return mDeckDrager.move((DeckViewHolder) viewHolder, (DeckViewHolder) target);
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        if (toPos == DeckItem.HeadView) {
            if (isDeleteMode) {
                DeckItem deckItem = mDeckDrager.delete(fromPos);
                Log.i("drag", "delete " + fromPos + " " + deckItem + "  " + (deckItem != null ? deckItem.getCardInfo() : null));
            }
        }
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.i("kk", "onSwiped " + viewHolder.getAdapterPosition() + " -> " + direction);
    }
}
