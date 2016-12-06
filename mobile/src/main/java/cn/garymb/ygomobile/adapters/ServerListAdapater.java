package cn.garymb.ygomobile.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.bean.ServerList;
import cn.garymb.ygomobile.core.YGOStarter;
import cn.garymb.ygomobile.core.loader.IDataLoader;
import cn.garymb.ygomobile.core.loader.ILoadCallBack;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;
import cn.garymb.ygomobile.plus.DialogPlus;
import cn.garymb.ygomobile.plus.SimpleListAdapter;
import cn.garymb.ygomobile.plus.VUiKit;
import cn.garymb.ygomobile.core.AppsSettings;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.XmlUtils;

import static cn.garymb.ygomobile.Constants.ASSET_SERVER_LIST;

public class ServerListAdapater extends BaseAdapterPlus<ServerInfo> implements
        IDataLoader,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private final File xmlFile;
    private Activity mActivity;
    private ILoadCallBack loadCallBack;

    public ServerListAdapater(Activity context) {
        super(context);
        mActivity = context;
        xmlFile = new File(context.getFilesDir(), "server_list.xml");
    }

    public void addServer() {
        showDialog(null, -1);
    }

    @Override
    public void setCallBack(ILoadCallBack loadCallBack) {
        this.loadCallBack = loadCallBack;
    }

    @Override
    public ILoadCallBack getCallBack() {
        return loadCallBack;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ServerInfo serverInfo = getItem(position);
        if (serverInfo != null) {
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
                            Log.i("kk", "swap:" + index + "," + items);
                        } else {
                            items.add(0, name);
                            Log.i("kk", "add:" + index + "," + items);
                        }
                        AppsSettings.get().setLastRoomList(items);
                        simpleListAdapter.notifyDataSetChanged();
                    }
                    joinGame(serverInfo, name);
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
            builder.setButtonText(R.string.join_game);
            builder.setButtonListener((dlg, i) -> {
                dlg.dismiss();
                //保存名字
                String name = editText.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    List<String> items = simpleListAdapter.getItems();
                    int index = items.indexOf(name);
                    if (index >= 0) {
                        items.remove(index);
                        items.add(0, name);
                        Log.i("kk", "swap:" + index + "," + items);
                    } else {
                        items.add(0, name);
                        Log.i("kk", "add:" + index + "," + items);
                    }
                    AppsSettings.get().setLastRoomList(items);
                    simpleListAdapter.notifyDataSetChanged();
                }
                joinGame(serverInfo, name);
            });
            builder.show();

        }
    }

    private void joinGame(ServerInfo serverInfo, String name) {
        YGOGameOptions options = new YGOGameOptions();
        options.mServerAddr = serverInfo.getServerAddr();
        options.mUserName = serverInfo.getPlayerName();
        options.mPort = serverInfo.getPort();
        options.mRoomName = name;
        YGOStarter.startGame(mActivity, options);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ServerInfo serverInfo = getItem(position);
        if (serverInfo != null) {
            showDialog(serverInfo, position);
            return true;
        }
        return false;
    }

    @Override
    public void loadData() {
        VUiKit.defer().when(() -> {
            InputStream in = null;
            if (xmlFile.exists()) {
                in = new FileInputStream(xmlFile);
            } else {
                in = getContext().getAssets().open(ASSET_SERVER_LIST);
            }
            ServerList list = null;
            try {
                list = XmlUtils.get().getObject(ServerList.class, in);
            } catch (Exception e) {

            } finally {
                IOUtils.close(in);
            }
            return list;
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

    private void showDialog(ServerInfo serverInfo, int position) {
        final boolean isAdd = position < 0;
        DialogPlus builder = new DialogPlus(getContext());
        builder.setView(R.layout.dialog_server_edit);
        EditViewHolder editViewHolder = new EditViewHolder(builder.getContentView());
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
        builder.setButtonListener((dlg, v) -> {
            //保存
            ServerInfo info;
            if (!isAdd) {
                info = getItem(position);
            } else {
                info = new ServerInfo();
            }
            info.setName("" + editViewHolder.serverName.getText());
            info.setServerAddr("" + editViewHolder.serverIp.getText());
            info.setPlayerName("" + editViewHolder.userName.getText());
            if (TextUtils.isEmpty(info.getName())
                    || TextUtils.isEmpty(info.getServerAddr())
                    || TextUtils.isEmpty(editViewHolder.serverPort.getText())) {
                Toast.makeText(getContext(), R.string.server_is_exist, Toast.LENGTH_SHORT).show();
                return;
            }
            info.setPort(Integer.valueOf("" + editViewHolder.serverPort.getText()));
//            info.setPassword("" + editViewHolder.userPassword.getText());
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(xmlFile);
                XmlUtils.get().saveXml(new ServerList(mItems), outputStream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(outputStream);
            }
            notifyDataSetChanged();
            dialog.dismiss();
        });
    }


    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_server_info, parent, false);
        new ViewHolder(view);
        return view;
    }

    @Override
    protected void attach(View view, ServerInfo item, int position) {
        ViewHolder holder = ViewHolder.from(view);
        holder.serverName.setText(item.getName());
        holder.serverIp.setText(item.getServerAddr());
        holder.userName.setText(item.getPlayerName());
        holder.serverPort.setText(String.valueOf(item.getPort()));
        holder.edit.setOnClickListener((v) -> {
            showDialog(item, position);
        });
    }

    static class ViewHolder extends BaseViewHolder {
        TextView serverName;
        TextView userName;
        TextView serverIp;
        TextView serverPort;
        View edit;

        ViewHolder(View view) {
            super(view);
            view.setTag(view.getId(), this);
            serverName = findViewById(R.id.server_name);
            serverIp = findViewById(R.id.text_ip);
            serverPort = findViewById(R.id.text_port);
            userName = findViewById(R.id.text_player);
            edit = findViewById(R.id.btn_edit);
        }

        static ViewHolder from(View view) {
            return (ViewHolder) view.getTag(view.getId());
        }
    }

    static class EditViewHolder extends ViewHolder {
//        TextView userPassword;

        EditViewHolder(View view) {
            super(view);
//            userPassword = findViewById(R.id.text_player_pwd);
        }
    }
}
