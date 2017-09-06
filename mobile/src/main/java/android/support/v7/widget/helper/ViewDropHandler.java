package android.support.v7.widget.helper;

import android.view.View;

/**
 * An interface which can be implemented by LayoutManager for better integration with
 * {@link ItemTouchHelper2}.
 */
interface ViewDropHandler {

    /**
     * Called by the {@link ItemTouchHelper2} after a View is dropped over another View.
     * <p>
     * A LayoutManager should implement this interface to get ready for the upcoming move
     * operation.
     * <p>
     * For example, LinearLayoutManager sets up a "scrollToPositionWithOffset" calls so that
     * the View under drag will be used as an anchor View while calculating the next layout,
     * making layout stay consistent.
     *
     * @param view   The View which is being dragged. It is very likely that user is still
     *               dragging this View so there might be other
     *               {@link #prepareForDrop(View, View, int, int)} after this one.
     * @param target The target view which is being dropped on.
     * @param x      The <code>left</code> offset of the View that is being dragged. This value
     *               includes the movement caused by the user.
     * @param y      The <code>top</code> offset of the View that is being dragged. This value
     *               includes the movement caused by the user.
     */
    public void prepareForDrop(View view, View target, int x, int y);
}
