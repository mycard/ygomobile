package cn.garymb.ygomobile.adapters;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper2;
import android.support.v7.widget.helper.ItemTouchHelperCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import cn.garymb.ygodata.YGOGameOptions;
import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.bean.ServerList;
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.core.YGOStarter;
import cn.garymb.ygomobile.core.loader.IDataLoader;
import cn.garymb.ygomobile.core.loader.ILoadCallBack;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseRecyclerAdapterPlus;
import cn.garymb.ygomobile.plus.DialogPlus;
import cn.garymb.ygomobile.plus.RecyclerViewItemListener;
import cn.garymb.ygomobile.plus.SimpleListAdapter;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.SystemUtils;
import cn.garymb.ygomobile.utils.XmlUtils;

import static cn.garymb.ygomobile.Constants.ASSET_SERVER_LIST;

public class ServerLists {
    static class ServerInfoViewHolder extends BaseRecyclerAdapterPlus.BaseViewHolder {
        public ServerInfoViewHolder(View itemView) {
            super(itemView);
            serverName = findViewById(R.id.server_name);
            serverIp = findViewById(R.id.text_ip);
            serverPort = findViewById(R.id.text_port);
            userName = findViewById(R.id.text_player);
        }

        final TextView serverName;
        final TextView userName;
        final TextView serverIp;
        final TextView serverPort;
    }

    private static void joinGame(Activity activity, ServerInfo serverInfo, String name) {
        YGOGameOptions options = new YGOGameOptions();
        options.mServerAddr = serverInfo.getServerAddr();
        options.mUserName = serverInfo.getPlayerName();
        options.mPort = serverInfo.getPort();
        options.mRoomName = name;
        YGOStarter.startGame(activity, options);
    }

    public static ServerAdapater attch(Activity context, RecyclerView recyclerView) {
        ServerAdapater serverAdapater = new ServerAdapater(context);
        recyclerView.setAdapter(serverAdapater);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemListener(recyclerView,
                new ServerHandler(context, serverAdapater)));
        ItemTouchHelperCompat helper = new ItemTouchHelperCompat(context, new TouchCallback(serverAdapater));
        helper.setEnableClickDrag(true);
        helper.attachToRecyclerView(recyclerView);
        return serverAdapater;
    }

    private static class TouchCallback extends ItemTouchHelperCompat.Callback implements ItemTouchHelper2.OnDragListner {
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
            ServerInfo serverInfo = adapater.getItem(position);
            if (serverInfo != null) {
                adapater.showEditDialog(serverInfo, position);
            }
        }

        @Override
        public void onDragLongPressEnd() {

        }

        @Override
        public void onDragEnd() {

        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.END | ItemTouchHelper.START);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                adapater.saveItems();
                adapater.notifyDataSetChanged();
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int left = viewHolder.getAdapterPosition();
            int right = viewHolder.getAdapterPosition();
            if (left >= 0) {
                ServerInfo serverInfo = adapater.remove(left);
                adapater.add(right, serverInfo, false);
                adapater.notifyItemRemoved(left);
                adapater.notifyItemInserted(right);
                return true;
            }
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            ServerInfo serverInfo = adapater.getItem(position);
            if (serverInfo != null) {
                DialogPlus dialogPlus = new DialogPlus(adapater.getContext());
                dialogPlus.setTitle(R.string.question);
                dialogPlus.setMessage(R.string.delete_server_info);
                dialogPlus.setMessageGravity(Gravity.CLIP_HORIZONTAL);
                dialogPlus.setLeftButtonListener((dlg, rs) -> {
                    dlg.dismiss();
                    adapater.removeItem(serverInfo);
                    adapater.saveItems();
                });
                dialogPlus.setCancelable(false);
                dialogPlus.setCloseLinster((dlg, rs) -> {
                    dlg.dismiss();
                    adapater.notifyDataSetChanged();
                });
                dialogPlus.show();
            }
        }
    }

    private static class ServerHandler implements RecyclerViewItemListener.OnItemListener {
        private Activity context;
        private ServerAdapater adapater;
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
            if (serverInfo != null) {
                if (isEditing)
                    return;
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
                        joinGame(context, serverInfo, name);
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
                    joinGame(context, serverInfo, name);
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

        }

        @Override
        public void onItemDoubleClick(View view, int position) {

        }
    }

    public static class ServerAdapater extends BaseRecyclerAdapterPlus<ServerInfo, ServerInfoViewHolder> implements IDataLoader {
        private final File xmlFile;
        private ILoadCallBack loadCallBack;

        public ServerAdapater(Context context) {
            super(context);
            xmlFile = new File(context.getFilesDir(), Constants.SERVER_FILE);
        }


        @Override
        public void setCallBack(ILoadCallBack loadCallBack) {
            this.loadCallBack = loadCallBack;
        }

        public void addServer() {
            showEditDialog(null, -1);
        }

        @Override
        public void loadData() {
            VUiKit.defer().when(() -> {
                ServerList assetList = readList(getContext().getAssets().open(ASSET_SERVER_LIST));
                ServerList fileList = xmlFile.exists() ? readList(new FileInputStream(xmlFile)) : null;
                if (fileList == null) {
                    return assetList;
                }
                if (fileList.getVercode() < assetList.getVercode()) {
                    xmlFile.delete();
                    return assetList;
                }
                return fileList;
            }).done((list) -> {
                if (loadCallBack != null) {
                    loadCallBack.onLoad(list != null);
                }
                if (list != null) {
                    addAll(list.getServerInfoList());
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public ILoadCallBack getCallBack() {
            return loadCallBack;
        }

        @Override
        public ServerInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ServerInfoViewHolder(inflate(R.layout.item_server_info, parent, false));
        }

        @Override
        public void onBindViewHolder(ServerInfoViewHolder holder, int position) {
            ServerInfo item = getItem(position);
            holder.serverName.setText(item.getName());
            holder.serverIp.setText(item.getServerAddr());
            holder.userName.setText(item.getPlayerName());
            holder.serverPort.setText(String.valueOf(item.getPort()));
        }


        private void showEditDialog(ServerInfo serverInfo, int position) {
            final boolean isAdd = position < 0;
            DialogPlus builder = new DialogPlus(getContext());
            builder.setView(R.layout.dialog_server_edit);
            ServerInfoViewHolder editViewHolder = new ServerInfoViewHolder(builder.getContentView());
            final Dialog dialog = builder.show();
            if (serverInfo != null) {
                editViewHolder.serverName.setText(serverInfo.getName());
                editViewHolder.serverIp.setText(serverInfo.getServerAddr());
                editViewHolder.userName.setText(serverInfo.getPlayerName());
                editViewHolder.serverPort.setText(String.valueOf(serverInfo.getPort()));
//            editViewHolder.userPassword.setText(serverInfo.getPassword());
            }
            if (isAdd) {
                builder.setTitle(R.string.action_add_server);
            } else {
                builder.setTitle(R.string.server_info_edit);
            }
            builder.setLeftButtonListener((dlg, v) -> {
                //保存
                String serverName = "" + editViewHolder.serverName.getText();
                ServerInfo info;
                if (!isAdd) {
                    info = getItem(position);
//                if(TextUtils.isEmpty(serverName)){
//                    if(!info.isKeep()) {
//                        //自带的，不能删除
//                        mItems.remove(info);
//                        saveItems();
//                        dialog.dismiss();
//                        return;
//                    }
//                }
                } else {
                    info = new ServerInfo();
                }
                info.setName("" + serverName);
                info.setServerAddr("" + editViewHolder.serverIp.getText());
                info.setPlayerName("" + editViewHolder.userName.getText());
                if (TextUtils.isEmpty(info.getName())
                        || TextUtils.isEmpty(info.getServerAddr())
                        || TextUtils.isEmpty(editViewHolder.serverPort.getText())) {
                    Toast.makeText(getContext(), R.string.server_is_exist, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isAdd) {
                    mItems.add(info);
                }
                info.setPort(Integer.valueOf("" + editViewHolder.serverPort.getText()));
//            info.setPassword("" + editViewHolder.userPassword.getText());
                saveItems();
                dialog.dismiss();
            });
        }

        private ServerList readList(InputStream in) {
            ServerList list = null;
            try {
                list = XmlUtils.get().getObject(ServerList.class, in);
            } catch (Exception e) {

            } finally {
                IOUtils.close(in);
            }
            return list;
        }

        private void saveItems() {
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(xmlFile);
                XmlUtils.get().saveXml(new ServerList(SystemUtils.getVersion(context), mItems), outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(outputStream);
            }
            notifyDataSetChanged();
        }
    }
}
