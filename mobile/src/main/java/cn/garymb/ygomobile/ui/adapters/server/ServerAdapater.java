package cn.garymb.ygomobile.ui.adapters.server;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.bean.ServerList;
import cn.garymb.ygomobile.loader.IDataLoader;
import cn.garymb.ygomobile.loader.ILoadCallBack;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.adapters.BaseRecyclerAdapterPlus;
import cn.garymb.ygomobile.ui.plus.DialogPlus;
import cn.garymb.ygomobile.ui.plus.VUiKit;
import cn.garymb.ygomobile.utils.IOUtils;
import cn.garymb.ygomobile.utils.SystemUtils;
import cn.garymb.ygomobile.utils.XmlUtils;

import static cn.garymb.ygomobile.Constants.ASSET_SERVER_LIST;

public class ServerAdapater extends BaseRecyclerAdapterPlus<ServerInfo, ServerInfoViewHolder> implements IDataLoader {
    private final File xmlFile;
    private ILoadCallBack loadCallBack;
    private volatile boolean mEditMode;
    private ServerLists.OnEditListener mOnEditListener;

    public ServerAdapater(Context context) {
        super(context);
        xmlFile = new File(context.getFilesDir(), Constants.SERVER_FILE);
    }

    public void setOnEditListener(ServerLists.OnEditListener onEditListener) {
        mOnEditListener = onEditListener;
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
        if (mOnEditListener != null) {
            mOnEditListener.onEdit(editMode);
        }
    }

    private boolean canMove = false;

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean isCanMove() {
        return canMove;
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


    void showEditDialog(ServerInfo serverInfo, int position) {
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
                this.notifyDataSetChanged();
            }else{
                if(position >= 0) {
                    this.notifyItemChanged(position);
                }
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

    void saveItems() {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(xmlFile);
            XmlUtils.get().saveXml(new ServerList(SystemUtils.getVersion(context), mItems), outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(outputStream);
        }
        //修复 拖动 异常
//            notifyDataSetChanged();
    }
}
