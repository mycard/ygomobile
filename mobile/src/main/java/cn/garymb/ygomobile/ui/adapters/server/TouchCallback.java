package cn.garymb.ygomobile.ui.adapters.server;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper2;
import android.util.Log;

import java.util.Collections;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.ServerInfo;

class TouchCallback extends ItemTouchHelper2.Callback implements ItemTouchHelper2.OnDragListner {
    ServerAdapater adapater;

    public TouchCallback(ServerAdapater adapater) {
        this.adapater = adapater;
        setLongTime(Constants.LONG_PRESS_DRAG);
        setOnDragListner(this);
    }

    @Override
    public void onDragStart() {

    }

    @Override
    public void onDragLongPress(int position) {
//            ServerInfo serverInfo = adapater.getItem(position);
//            if (serverInfo != null) {
//                adapater.showEditDialog(serverInfo, position);
//            }
    }

    @Override
    public void onDragLongPressEnd() {

    }

    @Override
    public void onDragEnd() {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (!adapater.isEditMode()) {
            return makeMovementFlags(0, 0);
        }
        return makeMovementFlags(adapater.isCanMove() ? (ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) : 0, ItemTouchHelper.END | ItemTouchHelper.START);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            adapater.saveItems();
//                adapater.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int left = viewHolder.getAdapterPosition();
        int right = target.getAdapterPosition();
        if (left >= 0) {
            Log.i("drag", left + "->" + right);
            adapater.notifyItemMoved(left, right);
            return true;
        }
        return true;
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos, RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
        int left = viewHolder.getAdapterPosition();
        int right = target.getAdapterPosition();
        if (left >= 0) {
            Collections.swap(adapater.getItems(), left, right);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        ServerInfo serverInfo = adapater.getItem(position);
        if (serverInfo != null) {
            ServerLists.showDelete(adapater.getContext(), serverInfo,(dlg, rs) -> {
                dlg.dismiss();
                adapater.removeItem(serverInfo);
                adapater.saveItems();
            },(dlg, rs) -> {
                dlg.dismiss();
                adapater.notifyDataSetChanged();
            });
        }
    }
}
