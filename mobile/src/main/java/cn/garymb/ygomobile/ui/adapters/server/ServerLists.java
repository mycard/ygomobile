package cn.garymb.ygomobile.ui.adapters.server;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper2;
import android.view.Gravity;

import cn.garymb.ygodata.YGOGameOptions;
import cn.garymb.ygomobile.bean.ServerInfo;
import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.plus.DialogPlus;
import android.support.v7.widget.RecyclerViewItemListener;

public class ServerLists {

    static void joinGame(Activity activity, ServerInfo serverInfo, String name) {
        YGOGameOptions options = new YGOGameOptions();
        options.mServerAddr = serverInfo.getServerAddr();
        options.mUserName = serverInfo.getPlayerName();
        options.mPort = serverInfo.getPort();
        options.mRoomName = name;
        YGOStarter.startGame(activity, options);
    }

    public static ServerAdapater attch(Activity context, RecyclerView recyclerView) {
        ServerAdapater serverAdapater = new ServerAdapater(context);
        serverAdapater.setCanMove(true);
        recyclerView.setAdapter(serverAdapater);
        recyclerView.addOnItemTouchListener(new RecyclerViewItemListener(recyclerView,
                new ServerHandler(context, serverAdapater)));
        ItemTouchHelper2 helper = new ItemTouchHelper2(context, new TouchCallback(serverAdapater));
        helper.setEnableClickDrag(false);
        helper.attachToRecyclerView(recyclerView);
        return serverAdapater;
    }

    static void showDelete(Context context,ServerInfo serverInfo,
                                   DialogInterface.OnClickListener ok,
                                   DialogInterface.OnClickListener cancel){
        DialogPlus dialogPlus = new DialogPlus(context);
        dialogPlus.setTitle(R.string.question);
        dialogPlus.setMessage(R.string.delete_server_info);
        dialogPlus.setMessageGravity(Gravity.CLIP_HORIZONTAL);
        dialogPlus.setLeftButtonListener(ok);
        dialogPlus.setCancelable(false);
        dialogPlus.setCloseLinster(cancel);
        dialogPlus.show();
    }

    public interface OnEditListener {
        void onEdit(boolean enter);
    }

}
