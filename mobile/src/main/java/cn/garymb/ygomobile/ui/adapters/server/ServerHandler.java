package cn.garymb.ygomobile.ui.adapters.server;

import android.app.Activity;
import android.support.v7.widget.RecyclerViewItemListener;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.AppsSettings;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.adapters.SimpleListAdapter;
import cn.garymb.ygomobile.ui.plus.DialogPlus;

class ServerHandler implements RecyclerViewItemListener.OnItemListener {
    private Activity context;
    private ServerAdapater adapater;
    //防止重复点击
    private boolean isEditing;

    public ServerHandler(Activity context, ServerAdapater adapater) {
        this.context = context;
        this.adapater = adapater;
    }

    public Activity getContext() {
        return context;
    }

    @Override
    public void onItemClick(View view, int position) {
        ServerInfo serverInfo = adapater.getItem(position);
        if (serverInfo == null) {
            return;
        }
        if (adapater.isEditMode()) {
            adapater.showEditDialog(serverInfo, position);
        } else {
            //进入房间
            if (isEditing) {
                return;
            }
            isEditing = true;
            DialogPlus builder = new DialogPlus(getContext());
            builder.setTitle(R.string.intput_room_name);
            builder.setView(R.layout.dialog_room_name);
            EditText editText = builder.findViewById(R.id.room_name);
            ListView listView = builder.findViewById(R.id.room_list);
            SimpleListAdapter simpleListAdapter = new SimpleListAdapter(getContext());
            simpleListAdapter.set(AppsSettings.get().getLastRoomList());
            listView.setAdapter(simpleListAdapter);
            listView.setOnItemClickListener((a, v, pos, index) -> {
//                builder.dismiss();
                String name = simpleListAdapter.getItemById(index);
                editText.setText(name);
//                joinGame(serverInfo, name);
            });
            editText.setOnEditorActionListener((v, actionId,
                                                event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    builder.dismiss();
                    String name = editText.getText().toString();
                    if (!TextUtils.isEmpty(name)) {
                        List<String> items = simpleListAdapter.getItems();
                        int index = items.indexOf(name);
                        if (index >= 0) {
                            items.remove(index);
                            items.add(0, name);
                        } else {
                            items.add(0, name);
                        }
                        AppsSettings.get().setLastRoomList(items);
                        simpleListAdapter.notifyDataSetChanged();
                    }
                    ServerLists.joinGame(context, serverInfo, name);
                    return true;
                }
                return false;
            });
            listView.setOnItemLongClickListener((a, v, i, index) -> {
                String name = simpleListAdapter.getItemById(index);
                int pos = simpleListAdapter.findItem(name);
                if (pos >= 0) {
                    simpleListAdapter.remove(pos);
                    simpleListAdapter.notifyDataSetChanged();
                    AppsSettings.get().setLastRoomList(simpleListAdapter.getItems());
                }
                return true;
            });
            builder.setLeftButtonText(R.string.join_game);
            builder.setLeftButtonListener((dlg, i) -> {
                dlg.dismiss();
                //保存名字
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    List<String> items = simpleListAdapter.getItems();
                    int index = items.indexOf(name);
                    if (index >= 0) {
                        items.remove(index);
                        items.add(0, name);
                    } else {
                        items.add(0, name);
                    }
                    AppsSettings.get().setLastRoomList(items);
                    simpleListAdapter.notifyDataSetChanged();
                }
                isEditing = false;
                ServerLists.joinGame(context, serverInfo, name);
            });
            builder.setCloseLinster((dlg, vs) -> {
                dlg.dismiss();
                isEditing = false;
            });
            builder.setOnCancelListener((dlg) -> {
                isEditing = false;
            });
            builder.show();
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        if (!adapater.isEditMode()) {
            adapater.setEditMode(true);
            return;
        }
        if (!adapater.isCanMove()) {
            ServerInfo serverInfo = adapater.getItem(position);
            if (serverInfo != null) {
                adapater.showEditDialog(serverInfo, position);
            }
        }
    }

    @Override
    public void onItemDoubleClick(View view, int position) {

    }
}
