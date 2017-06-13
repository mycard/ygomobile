package cn.garymb.ygomobile.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.events.ServerInfoEvent;
import cn.garymb.ygomobile.ui.home.ServerInfoViewHolder;

public class ServerListAdapter extends BaseRecyclerAdapterPlus<ServerInfo, ServerInfoViewHolder> {
    public ServerListAdapter(Context context) {
        super(context);
    }

    @Override
    public ServerInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServerInfoViewHolder(inflate(R.layout.item_server_info_swipe, parent, false));
    }

    @Override
    public void onBindViewHolder(ServerInfoViewHolder holder, int position) {
        ServerInfo item = getItem(position);
        holder.serverName.setText(item.getName());
        holder.serverIp.setText(item.getServerAddr());
        holder.userName.setText(item.getPlayerName());
        holder.serverPort.setText(String.valueOf(item.getPort()));
        bindMenu(holder, position);
    }

    public void bindMenu(ServerInfoViewHolder holder, int position) {
        if (holder.contentView != null) {
            holder.contentView.setOnClickListener((v) -> {
                ServerInfoEvent event = new ServerInfoEvent(position, false);
                event.join = true;
                EventBus.getDefault().post(event);
                holder.mMenuLayout.smoothCloseMenu();
            });
        }
        if (holder.btnEdit != null) {
            holder.btnEdit.setOnClickListener((v) -> {
                EventBus.getDefault().post(new ServerInfoEvent(position, false));
                holder.mMenuLayout.smoothCloseMenu();
            });
        }
        if (holder.btnDelete != null) {
            holder.btnDelete.setOnClickListener((v) -> {
                EventBus.getDefault().post(new ServerInfoEvent(position, true));
                holder.mMenuLayout.smoothCloseMenu();
            });
        }
    }
}

