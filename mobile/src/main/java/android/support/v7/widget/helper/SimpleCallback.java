package android.support.v7.widget.helper;

import android.support.v7.widget.RecyclerView;

/**
 * A simple wrapper to the default Callback which you can construct with drag and swipe
 * directions and this class will handle the flag callbacks. You should still override onMove
 * or
 * onSwiped depending on your use case.
 * <p>
 * <pre>
 * ItemTouchHelper mIth = new ItemTouchHelper(
 *     new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
 *         ItemTouchHelper.LEFT) {
 *         public abstract boolean onMove(RecyclerView recyclerView,
 *             ViewHolder viewHolder, ViewHolder target) {
 *             final int fromPos = viewHolder.getAdapterPosition();
 *             final int toPos = target.getAdapterPosition();
 *             // move item in `fromPos` to `toPos` in adapter.
 *             return true;// true if moved, false otherwise
 *         }
 *         public void onSwiped(ViewHolder viewHolder, int direction) {
 *             // remove from adapter
 *         }
 * });
 * </pre>
 */
public abstract class SimpleCallback extends Callback {

    private int mDefaultSwipeDirs;

    private int mDefaultDragDirs;

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, RecyclerView.ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, RecyclerView.ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */
    public SimpleCallback(int dragDirs, int swipeDirs) {
        mDefaultSwipeDirs = swipeDirs;
        mDefaultDragDirs = dragDirs;
    }

    /**
     * Updates the default swipe directions. For example, you can use this method to toggle
     * certain directions depending on your use case.
     *
     * @param defaultSwipeDirs Binary OR of directions in which the ViewHolders can be swiped.
     */
    public void setDefaultSwipeDirs(int defaultSwipeDirs) {
        mDefaultSwipeDirs = defaultSwipeDirs;
    }

    /**
     * Updates the default drag directions. For example, you can use this method to toggle
     * certain directions depending on your use case.
     *
     * @param defaultDragDirs Binary OR of directions in which the ViewHolders can be dragged.
     */
    public void setDefaultDragDirs(int defaultDragDirs) {
        mDefaultDragDirs = defaultDragDirs;
    }

    /**
     * Returns the swipe directions for the provided ViewHolder.
     * Default implementation returns the swipe directions that was set via constructor or
     * {@link #setDefaultSwipeDirs(int)}.
     *
     * @param recyclerView The RecyclerView to which the ItemTouchHelper is attached to.
     * @param viewHolder   The RecyclerView for which the swipe direction is queried.
     * @return A binary OR of direction flags.
     */
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return mDefaultSwipeDirs;
    }

    /**
     * Returns the drag directions for the provided ViewHolder.
     * Default implementation returns the drag directions that was set via constructor or
     * {@link #setDefaultDragDirs(int)}.
     *
     * @param recyclerView The RecyclerView to which the ItemTouchHelper is attached to.
     * @param viewHolder   The RecyclerView for which the swipe direction is queried.
     * @return A binary OR of direction flags.
     */
    public int getDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return mDefaultDragDirs;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(getDragDirs(recyclerView, viewHolder),
                getSwipeDirs(recyclerView, viewHolder));
    }
}
