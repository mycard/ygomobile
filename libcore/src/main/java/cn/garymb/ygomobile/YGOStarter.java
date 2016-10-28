package cn.garymb.ygomobile;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.garymb.ygodata.YGOGameOptions;

public class YGOStarter {
    public static void startGameWithOptions(Activity context, YGOGameOptions options) {
        Intent intent = new Intent(context, YGOMobileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        if (options != null) {
            intent.putExtra(YGOGameOptions.YGO_GAME_OPTIONS_BUNDLE_KEY, options);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
