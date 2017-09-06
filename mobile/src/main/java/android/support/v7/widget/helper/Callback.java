package android.support.v7.widget.helper;

import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.Interpolator;

import java.util.List;

/**
 * This class is the contract between ItemTouchHelper and your application. It lets you control
 * which touch behaviors are enabled per each ViewHolder and also receive callbacks when user
 * performs these actions.
 * <p>
 * To control which actions user can take on each view, you should override
 * {@link #getMovementFlags(RecyclerView, RecyclerView.ViewHolder)} and return appropriate set
 * of direction flags. ({@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link #END},
 * {@link #UP}, {@link #DOWN}). You can use
 * {@link #makeMovementFlags(int, int)} to easily construct it. Alternatively, you can use
 * {@link SimpleCallback}.
 * <p>
 * If user drags an item, ItemTouchHelper will call
 * {@link Callback#onMove(RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder)
 * onMove(recyclerView, dragged, target)}.
 * Upon receiving this callback, you should move the item from the old position
 * ({@code dragged.getAdapterPosition()}) to new position ({@code target.getAdapterPosition()})
 * in your adapter and also call {@link RecyclerView.Adapter#notifyItemMoved(int, int)}.
 * To control where a View can be dropped, you can override
 * {@link #canDropOver(RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder)}. When a
 * dragging View overlaps multiple other views, Callback chooses the closest View with which
 * dragged View might have changed positions. Although this approach works for many use cases,
 * if you have a custom LayoutManager, you can override
 * {@link #chooseDropTarget(RecyclerView.ViewHolder, java.util.List, int, int)} to select a
 * custom drop target.
 * <p>
 * When a View is swiped, ItemTouchHelper animates it until it goes out of bounds, then calls
 * {@link #onSwiped(RecyclerView.ViewHolder, int)}. At this point, you should update your
 * adapter (e.g. remove the item) and call related Adapter#notify event.
 */
@SuppressWarnings("UnusedParameters")
public abstract class Callback {

    public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;

    public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;

    static final int RELATIVE_DIR_FLAGS = ItemTouchHelper2.START | ItemTouchHelper2.END |
            ((ItemTouchHelper2.START | ItemTouchHelper2.END) << ItemTouchHelper2.DIRECTION_FLAG_COUNT) |
            ((ItemTouchHelper2.START | ItemTouchHelper2.END) << (2 * ItemTouchHelper2.DIRECTION_FLAG_COUNT));

    private static final ItemTouchUIUtil sUICallback;

    private static final int ABS_HORIZONTAL_DIR_FLAGS = ItemTouchHelper2.LEFT | ItemTouchHelper2.RIGHT |
            ((ItemTouchHelper2.LEFT | ItemTouchHelper2.RIGHT) << ItemTouchHelper2.DIRECTION_FLAG_COUNT) |
            ((ItemTouchHelper2.LEFT | ItemTouchHelper2.RIGHT) << (2 * ItemTouchHelper2.DIRECTION_FLAG_COUNT));

    private static final Interpolator sDragScrollInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            return t * t * t * t * t;
        }
    };
    protected ItemTouchHelper2 mItemTouchHelper;
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    /**
     * Drag scroll speed keeps accelerating until this many milliseconds before being capped.
     */
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000;

    private int mCachedMaxScrollSpeed = -1;

    static {
        if (Build.VERSION.SDK_INT >= 21) {
            sUICallback = new ItemTouchUIUtilImpl.Lollipop();
        } else if (Build.VERSION.SDK_INT >= 11) {
            sUICallback = new ItemTouchUIUtilImpl.Honeycomb();
        } else {
            sUICallback = new ItemTouchUIUtilImpl.Gingerbread();
        }
    }

    /**
     * Returns the {@link ItemTouchUIUtil} that is used by the {@link Callback} class for
     * visual
     * changes on Views in response to user interactions. {@link ItemTouchUIUtil} has different
     * implementations for different platform versions.
     * <p>
     * By default, {@link Callback} applies these changes on
     * {@link RecyclerView.ViewHolder#itemView}.
     * <p>
     * For example, if you have a use case where you only want the text to move when user
     * swipes over the view, you can do the following:
     * <pre>
     *     public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
     *         getDefaultUIUtil().clearView(((ItemTouchViewHolder) viewHolder).textView);
     *     }
     *     public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
     *         if (viewHolder != null){
     *             getDefaultUIUtil().onSelected(((ItemTouchViewHolder) viewHolder).textView);
     *         }
     *     }
     *     public void onChildDraw(Canvas c, RecyclerView recyclerView,
     *             RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
     *             boolean isCurrentlyActive) {
     *         getDefaultUIUtil().onDraw(c, recyclerView,
     *                 ((ItemTouchViewHolder) viewHolder).textView, dX, dY,
     *                 actionState, isCurrentlyActive);
     *         return true;
     *     }
     *     public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
     *             RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
     *             boolean isCurrentlyActive) {
     *         getDefaultUIUtil().onDrawOver(c, recyclerView,
     *                 ((ItemTouchViewHolder) viewHolder).textView, dX, dY,
     *                 actionState, isCurrentlyActive);
     *         return true;
     *     }
     * </pre>
     *
     * @return The {@link ItemTouchUIUtil} instance that is used by the {@link Callback}
     */
    public static ItemTouchUIUtil getDefaultUIUtil() {
        return sUICallback;
    }

    public void setItemTouchHelper(ItemTouchHelper2 itemTouchHelper) {
        mItemTouchHelper = itemTouchHelper;
        mHandler = new Handler(itemTouchHelper.getContext().getMainLooper());
    }

    public void setLongTime(long longTime) {
        mLongTime = longTime;
    }

    private Handler mHandler;
    private int mSelectId;
    private long mLongTime = 1000;
    private boolean mLongPressMode;
    private volatile long longPressTime = 0;
    private boolean isLongPressCancel = false;
    protected ItemTouchHelper2.OnDragListner mOnDragListner;
    private int mDx = 2;
    private int mDy = 2;

    public void setOnDragListner(ItemTouchHelper2.OnDragListner onDragListner) {
        mOnDragListner = onDragListner;
    }

    public void setDragSize(int dx,int dy) {
        mDx = dx;
        mDy = dy;
    }

    public boolean isLongPressMode() {
        return mLongPressMode;
    }

    public int getSelectId() {
        return mSelectId;
    }

    /**
     * Replaces a movement direction with its relative version by taking layout direction into
     * account.
     *
     * @param flags           The flag value that include any number of movement flags.
     * @param layoutDirection The layout direction of the View. Can be obtained from
     *                        {@link ViewCompat#getLayoutDirection(android.view.View)}.
     * @return Updated flags which uses relative flags ({@link #START}, {@link #END}) instead
     * of {@link #LEFT}, {@link #RIGHT}.
     * @see #convertToAbsoluteDirection(int, int)
     */
    public static int convertToRelativeDirection(int flags, int layoutDirection) {
        int masked = flags & ABS_HORIZONTAL_DIR_FLAGS;
        if (masked == 0) {
            return flags;// does not have any abs flags, good.
        }
        flags &= ~masked; //remove left / right.
        if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
            // no change. just OR with 2 bits shifted mask and return
            flags |= masked << 2; // START is 2 bits after LEFT, END is 2 bits after RIGHT.
            return flags;
        } else {
            // add RIGHT flag as START
            flags |= ((masked << 1) & ~ABS_HORIZONTAL_DIR_FLAGS);
            // first clean RIGHT bit then add LEFT flag as END
            flags |= ((masked << 1) & ABS_HORIZONTAL_DIR_FLAGS) << 2;
        }
        return flags;
    }

    /**
     * Convenience method to create movement flags.
     * <p>
     * For instance, if you want to let your items be drag & dropped vertically and swiped
     * left to be dismissed, you can call this method with:
     * <code>makeMovementFlags(UP | DOWN, LEFT);</code>
     *
     * @param dragFlags  The directions in which the item can be dragged.
     * @param swipeFlags The directions in which the item can be swiped.
     * @return Returns an integer composed of the given drag and swipe flags.
     */
    public static int makeMovementFlags(int dragFlags, int swipeFlags) {
        return makeFlag(ItemTouchHelper2.ACTION_STATE_IDLE, swipeFlags | dragFlags) |
                makeFlag(ItemTouchHelper2.ACTION_STATE_SWIPE, swipeFlags) | makeFlag(ItemTouchHelper2.ACTION_STATE_DRAG,
                dragFlags);
    }

    /**
     * Shifts the given direction flags to the offset of the given action state.
     *
     * @param actionState The action state you want to get flags in. Should be one of
     *                    {@link #ACTION_STATE_IDLE}, {@link #ACTION_STATE_SWIPE} or
     *                    {@link #ACTION_STATE_DRAG}.
     * @param directions  The direction flags. Can be composed from {@link #UP}, {@link #DOWN},
     *                    {@link #RIGHT}, {@link #LEFT} {@link #START} and {@link #END}.
     * @return And integer that represents the given directions in the provided actionState.
     */
    public static int makeFlag(int actionState, int directions) {
        return directions << (actionState * ItemTouchHelper2.DIRECTION_FLAG_COUNT);
    }

    /**
     * Should return a composite flag which defines the enabled move directions in each state
     * (idle, swiping, dragging).
     * <p>
     * Instead of composing this flag manually, you can use {@link #makeMovementFlags(int,
     * int)}
     * or {@link #makeFlag(int, int)}.
     * <p>
     * This flag is composed of 3 sets of 8 bits, where first 8 bits are for IDLE state, next
     * 8 bits are for SWIPE state and third 8 bits are for DRAG state.
     * Each 8 bit sections can be constructed by simply OR'ing direction flags defined in
     * {@link ItemTouchHelper2}.
     * <p>
     * For example, if you want it to allow swiping LEFT and RIGHT but only allow starting to
     * swipe by swiping RIGHT, you can return:
     * <pre>
     *      makeFlag(ACTION_STATE_IDLE, RIGHT) | makeFlag(ACTION_STATE_SWIPE, LEFT | RIGHT);
     * </pre>
     * This means, allow right movement while IDLE and allow right and left movement while
     * swiping.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder   The ViewHolder for which the movement information is necessary.
     * @return flags specifying which movements are allowed on this ViewHolder.
     * @see #makeMovementFlags(int, int)
     * @see #makeFlag(int, int)
     */
    public abstract int getMovementFlags(RecyclerView recyclerView,
                                         RecyclerView.ViewHolder viewHolder);

    /**
     * Converts a given set of flags to absolution direction which means {@link #START} and
     * {@link #END} are replaced with {@link #LEFT} and {@link #RIGHT} depending on the layout
     * direction.
     *
     * @param flags           The flag value that include any number of movement flags.
     * @param layoutDirection The layout direction of the RecyclerView.
     * @return Updated flags which includes only absolute direction values.
     */
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        int masked = flags & RELATIVE_DIR_FLAGS;
        if (masked == 0) {
            return flags;// does not have any relative flags, good.
        }
        flags &= ~masked; //remove start / end
        if (layoutDirection == ViewCompat.LAYOUT_DIRECTION_LTR) {
            // no change. just OR with 2 bits shifted mask and return
            flags |= masked >> 2; // START is 2 bits after LEFT, END is 2 bits after RIGHT.
            return flags;
        } else {
            // add START flag as RIGHT
            flags |= ((masked >> 1) & ~RELATIVE_DIR_FLAGS);
            // first clean start bit then add END flag as LEFT
            flags |= ((masked >> 1) & RELATIVE_DIR_FLAGS) >> 2;
        }
        return flags;
    }

    final int getAbsoluteMovementFlags(RecyclerView recyclerView,
                                       RecyclerView.ViewHolder viewHolder) {
        final int flags = getMovementFlags(recyclerView, viewHolder);
        return convertToAbsoluteDirection(flags, ViewCompat.getLayoutDirection(recyclerView));
    }

    boolean hasDragFlag(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int flags = getAbsoluteMovementFlags(recyclerView, viewHolder);
        return (flags & ItemTouchHelper2.ACTION_MODE_DRAG_MASK) != 0;
    }

    boolean hasSwipeFlag(RecyclerView recyclerView,
                         RecyclerView.ViewHolder viewHolder) {
        final int flags = getAbsoluteMovementFlags(recyclerView, viewHolder);
        return (flags & ItemTouchHelper2.ACTION_MODE_SWIPE_MASK) != 0;
    }

    /**
     * Return true if the current ViewHolder can be dropped over the the target ViewHolder.
     * <p>
     * This method is used when selecting drop target for the dragged View. After Views are
     * eliminated either via bounds check or via this method, resulting set of views will be
     * passed to {@link #chooseDropTarget(RecyclerView.ViewHolder, java.util.List, int, int)}.
     * <p>
     * Default implementation returns true.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param current      The ViewHolder that user is dragging.
     * @param target       The ViewHolder which is below the dragged ViewHolder.
     * @return True if the dragged ViewHolder can be replaced with the target ViewHolder, false
     * otherwise.
     */
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current,
                               RecyclerView.ViewHolder target) {
        return true;
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to
     * the new position.
     * <p>
     * If this method returns true, ItemTouchHelper assumes {@code viewHolder} has been moved
     * to the adapter position of {@code target} ViewHolder
     * ({@link RecyclerView.ViewHolder#getAdapterPosition()
     * ViewHolder#getAdapterPosition()}).
     * <p>
     * If you don't support drag & drop, this method will never be called.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder   The ViewHolder which is being dragged by the user.
     * @param target       The ViewHolder over which the currently active item is being
     *                     dragged.
     * @return True if the {@code viewHolder} has been moved to the adapter position of
     * {@code target}.
     * @see #onMoved(RecyclerView, RecyclerView.ViewHolder, int, RecyclerView.ViewHolder, int, int, int)
     */
    public abstract boolean onMove(RecyclerView recyclerView,
                                   RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target);

    public void cancelLongPress(){
        if (!isLongPressMode() && !isLongPressCancel) {
            isLongPressCancel = true;
            endLongPressMode();
            if (ItemTouchHelper2.DEBUG)
                Log.w("kk", "cancel enter long press");
        }
    }
    /**
     * Returns whether ItemTouchHelper should start a drag and drop operation if an item is
     * long pressed.
     * <p>
     * Default value returns true but you may want to disable this if you want to start
     * dragging on a custom view touch using {@link #startDrag(RecyclerView.ViewHolder)}.
     *
     * @return True if ItemTouchHelper should start dragging an item when it is long pressed,
     * false otherwise. Default value is <code>true</code>.
     * @see #startDrag(RecyclerView.ViewHolder)
     */
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped
     * over the View.
     * <p>
     * Default value returns true but you may want to disable this if you want to start
     * swiping on a custom view touch using {@link #startSwipe(RecyclerView.ViewHolder)}.
     *
     * @return True if ItemTouchHelper should start swiping an item when user swipes a pointer
     * over the View, false otherwise. Default value is <code>true</code>.
     * @see #startSwipe(RecyclerView.ViewHolder)
     */
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     * When finding views under a dragged view, by default, ItemTouchHelper searches for views
     * that overlap with the dragged View. By overriding this method, you can extend or shrink
     * the search box.
     *
     * @return The extra margin to be added to the hit box of the dragged View.
     */
    public int getBoundingBoxMargin() {
        return 0;
    }

    /**
     * Returns the fraction that the user should move the View to be considered as swiped.
     * The fraction is calculated with respect to RecyclerView's bounds.
     * <p>
     * Default value is .5f, which means, to swipe a View, user must move the View at least
     * half of RecyclerView's width or height, depending on the swipe direction.
     *
     * @param viewHolder The ViewHolder that is being dragged.
     * @return A float value that denotes the fraction of the View size. Default value
     * is .5f .
     */
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return .5f;
    }

    /**
     * Returns the fraction that the user should move the View to be considered as it is
     * dragged. After a view is moved this amount, ItemTouchHelper starts checking for Views
     * below it for a possible drop.
     *
     * @param viewHolder The ViewHolder that is being dragged.
     * @return A float value that denotes the fraction of the View size. Default value is
     * .5f .
     */
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        return .5f;
    }

    /**
     * Defines the minimum velocity which will be considered as a swipe action by the user.
     * <p>
     * You can increase this value to make it harder to swipe or decrease it to make it easier.
     * Keep in mind that ItemTouchHelper also checks the perpendicular velocity and makes sure
     * current direction velocity is larger then the perpendicular one. Otherwise, user's
     * movement is ambiguous. You can change the threshold by overriding
     * {@link #getSwipeVelocityThreshold(float)}.
     * <p>
     * The velocity is calculated in pixels per second.
     * <p>
     * The default framework value is passed as a parameter so that you can modify it with a
     * multiplier.
     *
     * @param defaultValue The default value (in pixels per second) used by the
     *                     ItemTouchHelper.
     * @return The minimum swipe velocity. The default implementation returns the
     * <code>defaultValue</code> parameter.
     * @see #getSwipeVelocityThreshold(float)
     * @see #getSwipeThreshold(RecyclerView.ViewHolder)
     */
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue;
    }

    /**
     * Defines the maximum velocity ItemTouchHelper will ever calculate for pointer movements.
     * <p>
     * To consider a movement as swipe, ItemTouchHelper requires it to be larger than the
     * perpendicular movement. If both directions reach to the max threshold, none of them will
     * be considered as a swipe because it is usually an indication that user rather tried to
     * scroll then swipe.
     * <p>
     * The velocity is calculated in pixels per second.
     * <p>
     * You can customize this behavior by changing this method. If you increase the value, it
     * will be easier for the user to swipe diagonally and if you decrease the value, user will
     * need to make a rather straight finger movement to trigger a swipe.
     *
     * @param defaultValue The default value(in pixels per second) used by the ItemTouchHelper.
     * @return The velocity cap for pointer movements. The default implementation returns the
     * <code>defaultValue</code> parameter.
     * @see #getSwipeEscapeVelocity(float)
     */
    public float getSwipeVelocityThreshold(float defaultValue) {
        return defaultValue;
    }


    private Runnable enterLongPress = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - longPressTime >= mLongTime) {
                if (ItemTouchHelper2.DEBUG)
                    Log.i(ItemTouchHelper2.TAG, "enter delete");
                mLongPressMode = true;
                if(!isLongPressCancel) {
                    if (mOnDragListner != null && mSelectId >= 0) {
                        mOnDragListner.onDragLongPress(mSelectId);
                    }
                }
            } else {
                if (ItemTouchHelper2.DEBUG)
                    Log.i(ItemTouchHelper2.TAG, "no enter long press " + (System.currentTimeMillis() - longPressTime));
            }
        }
    };

    protected void endLongPressMode() {
        longPressTime = System.currentTimeMillis();
        mHandler.removeCallbacks(enterLongPress);
        if(mLongPressMode) {
            if (mOnDragListner != null) {
                mOnDragListner.onDragLongPressEnd();
            }
        }
        mLongPressMode = false;
    }

    /**
     * Called by ItemTouchHelper to select a drop target from the list of ViewHolders that
     * are under the dragged View.
     * <p>
     * Default implementation filters the View with which dragged item have changed position
     * in the drag direction. For instance, if the view is dragged UP, it compares the
     * <code>view.getTop()</code> of the two views before and after drag started. If that value
     * is different, the target view passes the filter.
     * <p>
     * Among these Views which pass the test, the one closest to the dragged view is chosen.
     * <p>
     * This method is called on the main thread every time user moves the View. If you want to
     * override it, make sure it does not do any expensive operations.
     *
     * @param selected    The ViewHolder being dragged by the user.
     * @param dropTargets The list of ViewHolder that are under the dragged View and
     *                    candidate as a drop.
     * @param curX        The updated left value of the dragged View after drag translations
     *                    are applied. This value does not include margins added by
     *                    {@link RecyclerView.ItemDecoration}s.
     * @param curY        The updated top value of the dragged View after drag translations
     *                    are applied. This value does not include margins added by
     *                    {@link RecyclerView.ItemDecoration}s.
     * @return A ViewHolder to whose position the dragged ViewHolder should be
     * moved to.
     */
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected,
                                                    List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        int right = curX + selected.itemView.getWidth();
        int bottom = curY + selected.itemView.getHeight();
        RecyclerView.ViewHolder winner = null;
        int winnerScore = -1;
        final int dx = curX - selected.itemView.getLeft();
        final int dy = curY - selected.itemView.getTop();
        final int targetsSize = dropTargets.size();
        for (int i = 0; i < targetsSize; i++) {
            final RecyclerView.ViewHolder target = dropTargets.get(i);
            if (dx > 0) {
                int diff = target.itemView.getRight() - right;
                if (diff < 0 && target.itemView.getRight() > selected.itemView.getRight()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }
            if (dx < 0) {
                int diff = target.itemView.getLeft() - curX;
                if (diff > 0 && target.itemView.getLeft() < selected.itemView.getLeft()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }
            if (dy < 0) {
                int diff = target.itemView.getTop() - curY;
                if (diff > 0 && target.itemView.getTop() < selected.itemView.getTop()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }

            if (dy > 0) {
                int diff = target.itemView.getBottom() - bottom;
                if (diff < 0 && target.itemView.getBottom() > selected.itemView.getBottom()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }
        }
        return winner;
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     * <p>
     * If you are returning relative directions ({@link #START} , {@link #END}) from the
     * {@link #getMovementFlags(RecyclerView, RecyclerView.ViewHolder)} method, this method
     * will also use relative directions. Otherwise, it will use absolute directions.
     * <p>
     * If you don't support swiping, this method will never be called.
     * <p>
     * ItemTouchHelper will keep a reference to the View until it is detached from
     * RecyclerView.
     * As soon as it is detached, ItemTouchHelper will call
     * {@link #clearView(RecyclerView, RecyclerView.ViewHolder)}.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped. It is one of
     *                   {@link #UP}, {@link #DOWN},
     *                   {@link #LEFT} or {@link #RIGHT}. If your
     *                   {@link #getMovementFlags(RecyclerView, RecyclerView.ViewHolder)}
     *                   method
     *                   returned relative flags instead of {@link #LEFT} / {@link #RIGHT};
     *                   `direction` will be relative as well. ({@link #START} or {@link
     *                   #END}).
     */
    public abstract void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

    /**
     * Called when the ViewHolder swiped or dragged by the ItemTouchHelper is changed.
     * <p/>
     * If you override this method, you should call super.
     *
     * @param viewHolder  The new ViewHolder that is being swiped or dragged. Might be null if
     *                    it is cleared.
     * @param actionState One of {@link ItemTouchHelper2#ACTION_STATE_IDLE},
     *                    {@link ItemTouchHelper2#ACTION_STATE_SWIPE} or
     *                    {@link ItemTouchHelper2#ACTION_STATE_DRAG}.
     * @see #clearView(RecyclerView, RecyclerView.ViewHolder)
     */
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            sUICallback.onSelected(viewHolder.itemView);
        }
        if (viewHolder != null && actionState == ItemTouchHelper2.ACTION_STATE_DRAG) {
            if (ItemTouchHelper2.DEBUG)
                Log.i(ItemTouchHelper2.TAG, "start drag");
            if (mOnDragListner != null) {
                mOnDragListner.onDragStart();
            }
            isLongPressCancel = false;
            mSelectId = viewHolder.getAdapterPosition();
            longPressTime = System.currentTimeMillis();
            mHandler.removeCallbacks(enterLongPress);
            if(mItemTouchHelper.enableClickDrag) {
                mHandler.postDelayed(enterLongPress, mLongTime);
            }
        } else if (actionState == ItemTouchHelper2.ACTION_STATE_IDLE) {
            if (ItemTouchHelper2.DEBUG)
                Log.i(ItemTouchHelper2.TAG, "end drag");
            endLongPressMode();
            if (mOnDragListner != null) {
                mOnDragListner.onDragEnd();
            }
        } else if (actionState == ItemTouchHelper2.ACTION_STATE_SWIPE) {
            if (ItemTouchHelper2.DEBUG)
                Log.i(ItemTouchHelper2.TAG, "cancel enter delete by swipe");
            endLongPressMode();
        }
    }

    private int getMaxDragScroll(RecyclerView recyclerView) {
        if (mCachedMaxScrollSpeed == -1) {
            mCachedMaxScrollSpeed = recyclerView.getResources().getDimensionPixelSize(
                    android.support.v7.recyclerview.R.dimen.item_touch_helper_max_drag_scroll_per_frame);
        }
        return mCachedMaxScrollSpeed;
    }

    /**
     * Called when {@link #onMove(RecyclerView, RecyclerView.ViewHolder, RecyclerView.ViewHolder)} returns true.
     * <p>
     * ItemTouchHelper does not create an extra Bitmap or View while dragging, instead, it
     * modifies the existing View. Because of this reason, it is important that the View is
     * still part of the layout after it is moved. This may not work as intended when swapped
     * Views are close to RecyclerView bounds or there are gaps between them (e.g. other Views
     * which were not eligible for dropping over).
     * <p>
     * This method is responsible to give necessary hint to the LayoutManager so that it will
     * keep the View in visible area. For example, for LinearLayoutManager, this is as simple
     * as calling {@link LinearLayoutManager#scrollToPositionWithOffset(int, int)}.
     * <p>
     * Default implementation calls {@link RecyclerView#scrollToPosition(int)} if the View's
     * new position is likely to be out of bounds.
     * <p>
     * It is important to ensure the ViewHolder will stay visible as otherwise, it might be
     * removed by the LayoutManager if the move causes the View to go out of bounds. In that
     * case, drag will end prematurely.
     *
     * @param recyclerView The RecyclerView controlled by the ItemTouchHelper.
     * @param viewHolder   The ViewHolder under user's control.
     * @param fromPos      The previous adapter position of the dragged item (before it was
     *                     moved).
     * @param target       The ViewHolder on which the currently active item has been dropped.
     * @param toPos        The new adapter position of the dragged item.
     * @param x            The updated left value of the dragged View after drag translations
     *                     are applied. This value does not include margins added by
     *                     {@link RecyclerView.ItemDecoration}s.
     * @param y            The updated top value of the dragged View after drag translations
     *                     are applied. This value does not include margins added by
     *                     {@link RecyclerView.ItemDecoration}s.
     */
    public void onMoved(final RecyclerView recyclerView,
                        final RecyclerView.ViewHolder viewHolder, int fromPos, final RecyclerView.ViewHolder target, int toPos, int x,
                        int y) {
        isLongPressCancel = true;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof ViewDropHandler) {
            ((ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView,
                    target.itemView, x, y);
            return;
        }

        // if layout manager cannot handle it, do some guesswork
        if (layoutManager.canScrollHorizontally()) {
            final int minLeft = layoutManager.getDecoratedLeft(target.itemView);
            if (minLeft <= recyclerView.getPaddingLeft()) {
                recyclerView.scrollToPosition(toPos);
            }
            final int maxRight = layoutManager.getDecoratedRight(target.itemView);
            if (maxRight >= recyclerView.getWidth() - recyclerView.getPaddingRight()) {
                recyclerView.scrollToPosition(toPos);
            }
        }

        if (layoutManager.canScrollVertically()) {
            final int minTop = layoutManager.getDecoratedTop(target.itemView);
            if (minTop <= recyclerView.getPaddingTop()) {
                recyclerView.scrollToPosition(toPos);
            }
            final int maxBottom = layoutManager.getDecoratedBottom(target.itemView);
            if (maxBottom >= recyclerView.getHeight() - recyclerView.getPaddingBottom()) {
                recyclerView.scrollToPosition(toPos);
            }
        }
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.ViewHolder selected,
                       List<RecoverAnimation> recoverAnimationList,
                       int actionState, float dX, float dY) {
        final int recoverAnimSize = recoverAnimationList.size();
        for (int i = 0; i < recoverAnimSize; i++) {
            final RecoverAnimation anim = recoverAnimationList.get(i);
            anim.update();
            final int count = c.save();
            onChildDraw(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState, false);
            c.restoreToCount(count);
        }
        if (selected != null) {
            final int count = c.save();
            onChildDraw(c, parent, selected, dX, dY, actionState, true);
            c.restoreToCount(count);
        }
    }


    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.ViewHolder selected,
                           List<RecoverAnimation> recoverAnimationList,
                           int actionState, float dX, float dY) {
        int recoverAnimSize = recoverAnimationList.size();
        for (int i = 0; i < recoverAnimSize; i++) {
            final RecoverAnimation anim = recoverAnimationList.get(i);
            final int count = c.save();
            onChildDrawOver(c, parent, anim.mViewHolder, anim.mX, anim.mY, anim.mActionState,
                    false);
            c.restoreToCount(count);
        }
        if (selected != null) {
            final int count = c.save();
            onChildDrawOver(c, parent, selected, dX, dY, actionState, true);
            c.restoreToCount(count);
        }
        recoverAnimSize = recoverAnimationList.size();
        boolean hasRunningAnimation = false;
        for (int i = recoverAnimSize - 1; i >= 0; i--) {
            final RecoverAnimation anim = recoverAnimationList.get(i);
            if (anim.mEnded && !anim.mIsPendingCleanup) {
                recoverAnimationList.remove(i);
            } else if (!anim.mEnded) {
                hasRunningAnimation = true;
            }
        }
        if (hasRunningAnimation) {
            parent.invalidate();
        }
    }

    /**
     * Called by the ItemTouchHelper when the user interaction with an element is over and it
     * also completed its animation.
     * <p>
     * This is a good place to clear all changes on the View that was done in
     * {@link #onSelectedChanged(RecyclerView.ViewHolder, int)},
     * {@link #onChildDraw(Canvas, RecyclerView, RecyclerView.ViewHolder, float, float, int,
     * boolean)} or
     * {@link #onChildDrawOver(Canvas, RecyclerView, RecyclerView.ViewHolder, float, float, int, boolean)}.
     *
     * @param recyclerView The RecyclerView which is controlled by the ItemTouchHelper.
     * @param viewHolder   The View that was interacted by the user.
     */
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        sUICallback.clearView(viewHolder.itemView);
    }

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     * <p>
     * If you would like to customize how your View's respond to user interactions, this is
     * a good place to override.
     * <p>
     * Default implementation translates the child by the given <code>dX</code>,
     * <code>dY</code>.
     * ItemTouchHelper also takes care of drawing the child after other children if it is being
     * dragged. This is done using child re-ordering mechanism. On platforms prior to L, this
     * is
     * achieved via {@link android.view.ViewGroup#getChildDrawingOrder(int, int)} and on L
     * and after, it changes View's elevation value to be greater than all other children.)
     *
     * @param c                 The canvas which RecyclerView is drawing its children
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     *                          interacted and simply animating to its original position
     * @param dX                The amount of horizontal displacement caused by user's action
     * @param dY                The amount of vertical displacement caused by user's action
     * @param actionState       The type of interaction on the View. Is either {@link
     *                          #ACTION_STATE_DRAG} or {@link #ACTION_STATE_SWIPE}.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     *                          false it is simply animating back to its original state.
     * @see #onChildDrawOver(Canvas, RecyclerView, RecyclerView.ViewHolder, float, float, int,
     * boolean)
     */
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        sUICallback.onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState,
                isCurrentlyActive);
    }

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     * <p>
     * If you would like to customize how your View's respond to user interactions, this is
     * a good place to override.
     * <p>
     * Default implementation translates the child by the given <code>dX</code>,
     * <code>dY</code>.
     * ItemTouchHelper also takes care of drawing the child after other children if it is being
     * dragged. This is done using child re-ordering mechanism. On platforms prior to L, this
     * is
     * achieved via {@link android.view.ViewGroup#getChildDrawingOrder(int, int)} and on L
     * and after, it changes View's elevation value to be greater than all other children.)
     *
     * @param c                 The canvas which RecyclerView is drawing its children
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     *                          interacted and simply animating to its original position
     * @param dX                The amount of horizontal displacement caused by user's action
     * @param dY                The amount of vertical displacement caused by user's action
     * @param actionState       The type of interaction on the View. Is either {@link
     *                          #ACTION_STATE_DRAG} or {@link #ACTION_STATE_SWIPE}.
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     *                          false it is simply animating back to its original state.
     * @see #onChildDrawOver(Canvas, RecyclerView, RecyclerView.ViewHolder, float, float, int,
     * boolean)
     */
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
        sUICallback.onDrawOver(c, recyclerView, viewHolder.itemView, dX, dY, actionState,
                isCurrentlyActive);
        if (isCurrentlyActive && actionState == ItemTouchHelper2.ACTION_STATE_DRAG) {
            if (dX > mDx || dY > mDy) {
                if (!isLongPressMode() && !isLongPressCancel) {
                    isLongPressCancel = true;
                    endLongPressMode();
                    if (ItemTouchHelper2.DEBUG)
                        Log.w(ItemTouchHelper2.TAG, "cancel enter long press");
//                    }
                }
            }
        }
    }

    /**
     * Called by the ItemTouchHelper when user action finished on a ViewHolder and now the View
     * will be animated to its final position.
     * <p>
     * Default implementation uses ItemAnimator's duration values. If
     * <code>animationType</code> is {@link #ANIMATION_TYPE_DRAG}, it returns
     * {@link RecyclerView.ItemAnimator#getMoveDuration()}, otherwise, it returns
     * {@link RecyclerView.ItemAnimator#getRemoveDuration()}. If RecyclerView does not have
     * any {@link RecyclerView.ItemAnimator} attached, this method returns
     * {@code DEFAULT_DRAG_ANIMATION_DURATION} or {@code DEFAULT_SWIPE_ANIMATION_DURATION}
     * depending on the animation type.
     *
     * @param recyclerView  The RecyclerView to which the ItemTouchHelper is attached to.
     * @param animationType The type of animation. Is one of {@link #ANIMATION_TYPE_DRAG},
     *                      {@link #ANIMATION_TYPE_SWIPE_CANCEL} or
     *                      {@link #ANIMATION_TYPE_SWIPE_SUCCESS}.
     * @param animateDx     The horizontal distance that the animation will offset
     * @param animateDy     The vertical distance that the animation will offset
     * @return The duration for the animation
     */
    public long getAnimationDuration(RecyclerView recyclerView, int animationType,
                                     float animateDx, float animateDy) {
        final RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator == null) {
            return animationType == ItemTouchHelper2.ANIMATION_TYPE_DRAG ? DEFAULT_DRAG_ANIMATION_DURATION
                    : DEFAULT_SWIPE_ANIMATION_DURATION;
        } else {
            return animationType == ItemTouchHelper2.ANIMATION_TYPE_DRAG ? itemAnimator.getMoveDuration()
                    : itemAnimator.getRemoveDuration();
        }
    }

    public boolean canAnimation(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    /**
     * Called by the ItemTouchHelper when user is dragging a view out of bounds.
     * <p>
     * You can override this method to decide how much RecyclerView should scroll in response
     * to this action. Default implementation calculates a value based on the amount of View
     * out of bounds and the time it spent there. The longer user keeps the View out of bounds,
     * the faster the list will scroll. Similarly, the larger portion of the View is out of
     * bounds, the faster the RecyclerView will scroll.
     *
     * @param recyclerView        The RecyclerView instance to which ItemTouchHelper is
     *                            attached to.
     * @param viewSize            The total size of the View in scroll direction, excluding
     *                            item decorations.
     * @param viewSizeOutOfBounds The total size of the View that is out of bounds. This value
     *                            is negative if the View is dragged towards left or top edge.
     * @param totalSize           The total size of RecyclerView in the scroll direction.
     * @param msSinceStartScroll  The time passed since View is kept out of bounds.
     * @return The amount that RecyclerView should scroll. Keep in mind that this value will
     * be passed to {@link RecyclerView#scrollBy(int, int)} method.
     */
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                            int viewSize, int viewSizeOutOfBounds,
                                            int totalSize, long msSinceStartScroll) {
        final int maxScroll = getMaxDragScroll(recyclerView);
        final int absOutOfBounds = Math.abs(viewSizeOutOfBounds);
        final int direction = (int) Math.signum(viewSizeOutOfBounds);
        // might be negative if other direction
        float outOfBoundsRatio = Math.min(1f, 1f * absOutOfBounds / viewSize);
        final int cappedScroll = (int) (direction * maxScroll *
                sDragViewScrollCapInterpolator.getInterpolation(outOfBoundsRatio));
        final float timeRatio;
        if (msSinceStartScroll > DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS) {
            timeRatio = 1f;
        } else {
            timeRatio = (float) msSinceStartScroll / DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS;
        }
        final int value = (int) (cappedScroll * sDragScrollInterpolator
                .getInterpolation(timeRatio));
        if (value == 0) {
            return viewSizeOutOfBounds > 0 ? 1 : -1;
        }
        return value;
    }
}
