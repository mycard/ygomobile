package cn.garymb.ygomobile.ui.home;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;

import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import cn.garymb.ygomobile.Constants;
import cn.garymb.ygomobile.YGOStarter;
import cn.garymb.ygomobile.lite.R;
import cn.garymb.ygomobile.ui.activities.AboutActivity;
import cn.garymb.ygomobile.ui.activities.WebActivity;
import cn.garymb.ygomobile.ui.cards.CardSearchAcitivity;
import cn.garymb.ygomobile.ui.cards.DeckManagerActivity;
import cn.garymb.ygomobile.ui.online.MyCardActivity;
import cn.garymb.ygomobile.ui.plus.DefaultOnBoomListener;
import cn.garymb.ygomobile.ui.preference.SettingsActivity;

class HomeActivityMenu {
    public interface CallBack {
        HomeActivity getActivity();

        void addServerInfo();
    }

    private BoomMenuButton mBoomMenuButton;
    private final static int MENU_GAME = 0;
    private final static int MENU_CARD_SEARCH = 1;
    private final static int MENU_DECK_MANAGER = 2;

    private final static int MENU_ADD_SERVER = 3;
    private final static int MENU_MYCARD = 4;
    private final static int MENU_HELP = 5;

    private final static int MENU_UPDATE_IAMGES = 6;
    private final static int MENU_SETTINGS = 7;
    private final static int MENU_ABOUT = 8;


    private CallBack mCallBack;

    private void initMenus() {
        addMenuButton(getActivity().getString(R.string.action_game), R.drawable.start);
        addMenuButton(getActivity().getString(R.string.tab_search), R.drawable.search);
        addMenuButton(getActivity().getString(R.string.deck_manager), R.drawable.deck);

        addMenuButton(getActivity().getString(R.string.action_add_server), R.drawable.addsever);
        addMenuButton(getActivity().getString(R.string.mycard), R.drawable.mycard);
        addMenuButton(getActivity().getString(R.string.help), R.drawable.help);

        addMenuButton(getActivity().getString(R.string.download_images), R.drawable.downloadimages);
        addMenuButton(getActivity().getString(R.string.action_settings), R.drawable.setting);
        addMenuButton(getActivity().getString(R.string.action_about), R.drawable.about);

    }

    private DefaultOnBoomListener mDefaultOnBoomListener = new DefaultOnBoomListener() {
        @Override
        public void onClicked(int index, BoomButton boomButton) {
            switch (index) {
                case MENU_ADD_SERVER:
                    mCallBack.addServerInfo();
                    break;
                case MENU_CARD_SEARCH:
                    startActivity(new Intent(getActivity(), CardSearchAcitivity.class));
                    break;
                case MENU_DECK_MANAGER:
                    startActivity(new Intent(getActivity(), DeckManagerActivity.class));
                    break;
                case MENU_MYCARD:
                    if (Constants.SHOW_MYCARD) {
                        startActivity(new Intent(getActivity(), MyCardActivity.class));
                    }
                    break;
                case MENU_SETTINGS:
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                    break;
                case MENU_HELP:
                    WebActivity.open(getActivity(), getActivity().getString(R.string.help), Constants.URL_HELP);
                    break;
                case MENU_UPDATE_IAMGES:
                    getActivity().updateImages();
                    break;
                case MENU_ABOUT:
                    startActivity(new Intent(getActivity(), AboutActivity.class));
                    break;
                case MENU_GAME:
                    YGOStarter.startGame(getActivity(), null);
//                    DialogPlus builder = new DialogPlus(getActivity());
//                    builder.setTitle(R.string.question);
//                    builder.setMessage(R.string.quit_tip);
//                    builder.setMessageGravity(Gravity.CENTER_HORIZONTAL);
//                    builder.setLeftButtonListener((dlg, s) -> {
//                        dlg.dismiss();
//                        getActivity().finish();
//                    });
//                    builder.show();
                    break;
            }
        }
    };


    HomeActivityMenu(CallBack callBack, BoomMenuButton boomMenuButton) {
        mCallBack = callBack;
        mBoomMenuButton = boomMenuButton;
        mBoomMenuButton.setOnBoomListener(mDefaultOnBoomListener);
        initMenus();
    }

    public void startActivity(Intent intent) {
        getActivity().startActivity(intent);
    }

    public HomeActivity getActivity() {
        return mCallBack.getActivity();
    }

    public Resources getResources() {
        return getActivity().getResources();
    }

    private void addMenuButton(String text, int image) {
        TextOutsideCircleButton.Builder builder = new TextOutsideCircleButton.Builder()
                .shadowColor(Color.TRANSPARENT)
                .normalColor(Color.TRANSPARENT)
                .normalText(text)
                .normalImageRes(image);
        mBoomMenuButton.addBuilder(builder);
    }
}
