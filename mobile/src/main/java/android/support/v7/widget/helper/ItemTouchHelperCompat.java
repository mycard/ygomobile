package android.support.v7.widget.helper;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class ItemTouchHelperCompat extends ItemTouchHelper2 {

    private boolean enableClickDrag = false;
    private Context mContext;

    public ItemTouchHelperCompat(Callback callback) {
        super(callback);
    }

    /***
     * 单击拖拽
     */
    public void setEnableClickDrag(boolean enableClickDrag) {
        this.enableClickDrag = enableClickDrag;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        this.mContext = recyclerView.getContext();
        super.attachToRecyclerView(recyclerView);
    }

    @Override
    protected void initGestureDetector() {
//        super.initGestureDetector();
        if (mGestureDetector != null) {
            return;
        }
        mGestureDetector = new GestureDetectorCompat(mContext,
                new ItemTouchHelperGestureListener());
    }

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {

        ItemTouchHelperGestureListener() {
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if (enableClickDrag) {
                startDrag(e);
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (!enableClickDrag) {
                startDrag(e);
            }
        }

        private void startDrag(MotionEvent e) {
            View child = findChildView(e);
            if (child != null) {
                RecyclerView.ViewHolder vh = mRecyclerView.getChildViewHolder(child);
                if (vh != null) {
                    if (!mCallback.hasDragFlag(mRecyclerView, vh)) {
                        return;
                    }
                    int pointerId = e.getPointerId(0);
                    // Long press is deferred.
                    // Check w/ active pointer id to avoid selecting after motion
                    // event is canceled.
                    if (pointerId == mActivePointerId) {
                        final int index = e.findPointerIndex(mActivePointerId);
                        final float x = e.getX(index);
                        final float y = e.getY(index);
                        mInitialTouchX = x;
                        mInitialTouchY = y;
                        mDx = mDy = 0f;
                        if (DEBUG) {
                            Log.d(TAG,
                                    "onlong press: x:" + mInitialTouchX + ",y:" + mInitialTouchY);
                        }
                        if (mCallback.isLongPressDragEnabled()) {
                            select(vh, ACTION_STATE_DRAG);
                        }
                    }
                }
            }
        }
    }
}
