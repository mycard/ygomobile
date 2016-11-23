package cn.garymb.ygomobile.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.plus.BaseAdapterPlus;

public class ServerListAdapater extends BaseAdapterPlus<ServerInfo> {
    public ServerListAdapater(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.item_server, parent, false);
        new ViewHolder(view);
        return view;
    }

    @Override
    protected void attach(View view, ServerInfo item, int position) {
        ViewHolder holder = ViewHolder.from(view);
        holder.serverName.setText(item.getName());
        holder.roomPwd.setText(item.getRoomPasswd());
        holder.userName.setText(item.getPlayerName());
        holder.roomName.setText(item.getRoomName());
    }

    static class ViewHolder {
        TextView serverName;
        EditText userName;
        EditText roomName;
        EditText roomPwd;
        Button okButton;
        Button enterButton;

        ViewHolder(View view) {
            view.setTag(view.getId(), this);
            serverName = (TextView) view.findViewById(R.id.server_name);
            roomName = (EditText) view.findViewById(R.id.edt_roomname);
            userName = (EditText) view.findViewById(R.id.edt_user_name);
            roomPwd = (EditText) view.findViewById(R.id.edt_room_pwd);
            okButton = (Button) view.findViewById(R.id.btn_ok);
            enterButton = (Button) view.findViewById(R.id.btn_enter);
        }

        static ViewHolder from(View view) {
            return (ViewHolder) view.getTag(view.getId());
        }
    }
}
