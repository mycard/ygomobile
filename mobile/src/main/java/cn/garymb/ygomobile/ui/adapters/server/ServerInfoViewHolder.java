package cn.garymb.ygomobile.ui.adapters.server;

import android.view.View;
import android.widget.TextView;

import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.adapters.BaseRecyclerAdapterPlus;

class ServerInfoViewHolder extends BaseRecyclerAdapterPlus.BaseViewHolder {
    public ServerInfoViewHolder(View itemView) {
        super(itemView);
        serverName = $(R.id.server_name);
        serverIp = $(R.id.text_ip);
        serverPort = $(R.id.text_port);
        userName = $(R.id.text_player);
    }

    final TextView serverName;
    final TextView userName;
    final TextView serverIp;
    final TextView serverPort;
}
