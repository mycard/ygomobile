package cn.garymb.ygomobile;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.garymb.ygodata.YGOGameOptions;

public class YGOStarter {
    public static void startGame(Activity context) {
        startGameWithOptions(context, null);
    }

    public static void startGameWithOptions(Activity context, YGOGameOptions options) {
        Intent intent = new Intent(context, YGOMobileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (options != null) {
            intent.putExtra(YGOGameOptions.YGO_GAME_OPTIONS_BUNDLE_KEY, options);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void connectServer(Activity context, String ipAddrString, int port, String userName, String serverInfoString) {
        YGOGameOptions options = new YGOGameOptions();
        options.mServerAddr = ipAddrString;
        options.mPort = port;
        options.mName = userName;
        options.mHostInfo = serverInfoString;
        startGameWithOptions(context, options);
    }
}
