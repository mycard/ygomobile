package cn.garymb.ygomobile.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;

public class ServerListAdapater extends BaseAdapterPlus<ServerInfo> implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public ServerListAdapater(Context context) {
        super(context);
    }

    public void addServer() {
        //TODO 显示编辑对话框，完成则是添加
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ServerInfo serverInfo = getItem(position);
        if (serverInfo != null) {
            //TODO 显示信息? ip:port
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ServerInfo serverInfo = getItem(position);
        if (serverInfo != null) {
            //TODO 编辑信息? ip:port
        }
        return false;
    }


    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.dialog_server_info, parent, false);
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
    }

    static class ViewHolder {
        TextView serverName;
        TextView userName;
        TextView serverIp;
        TextView serverPort;
        TextView userPassword;

        ViewHolder(View view) {
            view.setTag(view.getId(), this);
            serverName = (TextView) view.findViewById(R.id.server_name);
            serverIp = (TextView) view.findViewById(R.id.text_ip);
            serverPort = (TextView) view.findViewById(R.id.text_port);
            userName = (TextView) view.findViewById(R.id.text_player);
            userPassword = (TextView) view.findViewById(R.id.text_player_pwd);
        }

        static ViewHolder from(View view) {
            return (ViewHolder) view.getTag(view.getId());
        }
    }
}
