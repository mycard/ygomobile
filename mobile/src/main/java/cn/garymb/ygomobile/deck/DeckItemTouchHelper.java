package cn.garymb.ygomobile.deck;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelperCompat;
import android.util.Log;
import android.view.View;

import java.util.List;

import cn.garymb.ygomobile.Constants;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE;

public class DeckItemTouchHelper extends ItemTouchHelperCompat.Callback {
    private DeckDrager mDeckDrager;

    //    private RecyclerView.ViewHolder NULL;
    public interface CallBack {
        void onDragStart();

        void onDragDelete(int mSelectId);

        void onDragDeleteEnd();

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

    private volatile long lasttime = 0;
    private boolean isCencel = false;
    private int Min_Pos = -10;
    private int mSelectId;

    int mDeleteId;
    DeckItem mDeleteItem;

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

    /**
     * 控制哪些可以拖拽
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        } else {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected,
                                                    List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        if (!isCencel) {
            isCencel = true;
            if (!isDeleteMode) {
                disableDelete();
                Log.w("drag", "cancel enter delete");
            }
        }
        RecyclerView.ViewHolder viewHolder = super.chooseDropTarget(selected, dropTargets, curX, curY);
        if (viewHolder != null) {
            int id = viewHolder.getAdapterPosition();
            if (viewHolder instanceof DeckViewHolder) {
                DeckViewHolder deckholder = (DeckViewHolder) viewHolder;
                if (deckholder.getItemType() == DeckItemType.HeadView) {
                    if (isDeleteMode) {
                        return viewHolder;
                    }
                    return null;
                }
                if (deckholder.getItemType() == DeckItemType.Space
                        || deckholder.getItemType() == DeckItemType.MainLabel
                        || deckholder.getItemType() == DeckItemType.SideLabel
                        || deckholder.getItemType() == DeckItemType.ExtraLabel) {
//                Log.d("kk", "move is label or space " + id);
                    return null;
                }
            } else {
                if (DeckItemUtils.isLabel(id) || id == DeckItem.HeadView) {
//                Log.d("kk", "move is label " + id);
                    if (isDeleteMode) {
                        return viewHolder;
                    }
                    return null;
                }
            }
        }
        return viewHolder;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_DRAG) {
            mDeleteId = -1;
            Log.d("drag", "start drag");
            isCencel = false;
            mSelectId = viewHolder.getAdapterPosition();
            lasttime = System.currentTimeMillis();
            mHandler.postDelayed(enterDelete, Constants.LONG_PRESS_DRAG);
            mDeckDrager.onDragStart();
            if (mCallBack != null) {
                mCallBack.onDragStart();
            }
        } else if (actionState == ACTION_STATE_IDLE) {
            Log.d("drag", "end drag");
            disableDelete();
            mDeckDrager.onDragEnd();
            if (mCallBack != null) {
                mCallBack.onDragEnd();
            }
        } else if (actionState == ACTION_STATE_SWIPE) {
            Log.w("drag", "cancel enter delete by swipe");
            disableDelete();
        }
    }

    private Runnable enterDelete = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - lasttime >= Constants.LONG_PRESS_DRAG) {
                Log.i("drag", "enter delete");
                isDeleteMode = true;
                if (mCallBack != null) {
                    mCallBack.onDragDelete(mSelectId);
                }
            } else {
                Log.w("drag", "no enter delete " + (System.currentTimeMillis() - lasttime));
            }
        }
    };

    private void disableDelete() {
        isDeleteMode = false;
        lasttime = System.currentTimeMillis();
        mHandler.removeCallbacks(enterDelete);
        if (mCallBack != null) {
            mCallBack.onDragDeleteEnd();
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if (viewHolder.getAdapterPosition() < 0) {
            return false;
        }
        return mDeckDrager.move((DeckViewHolder) viewHolder, (DeckViewHolder) target);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.i("drag", "clearView");
        super.clearView(recyclerView, viewHolder);
        if (mDeleteId > 0) {
            //
            if (Constants.DRAG_END_DELETE) {
                mDeckDrager.delete(mDeleteId);
            }
            mDeleteId = -1;
        }
    }

    public void remove(int id) {
        mDeckDrager.delete(id);
    }

    @Override
    public boolean onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder.getAdapterPosition() < 0) {
            if (mDeleteId > 0) {
                //还原Canvas
                Log.w("drag", "resotre " + mDeleteId + " state=" + actionState);
                mDeleteId = -2;
                return false;
            }
        }
        return super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        if (toPos == DeckItem.HeadView) {
            if (isDeleteMode) {
                //防止删除后，弹回来
                disableDelete();
                if (y > Min_Pos) {
                    mDeleteId = fromPos;
                    Log.i("drag", "delete " + fromPos + " x=" + x + ",y=" + y);
                    if (!Constants.DRAG_END_DELETE) {
                        mDeleteItem = mDeckDrager.delete(mDeleteId);
                        return;
                    }
                } else {
                    Log.d("drag", "cancel delete " + fromPos + " x=" + x + ",y=" + y);
                }
            }
        }
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
