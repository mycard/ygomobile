package cn.garymb.ygomobile;

import android.view.Gravity;

import cn.garymb.ygomobile.lite.BuildConfig;

public interface Constants {
    boolean DEBUG = BuildConfig.DEBUG;
    String PREF_LAST_YDK = "pref_last_ydk";
    String PREF_DEF_LAST_YDK = "new";
    String PREF_GAME_PATH = "pref_key_game_res_path";
    String PREF_DEF_GAME_DIR = "ygocore";


    String PREF_GAME_VERSION = "pref_key_core_version";
    String PREF_DEF_GAME_VERSION = "3.5";
    String PREF_IMAGE_QUALITY = "pref_key_game_image_quality";
    int PREF_DEF_IMAGE_QUALITY = 1;
    String PREF_GAME_FONT = "pref_key_game_font_name";
    String PREF_USE_EXTRA_CARD_CARDS = "settings_game_diy_card_db";
    boolean PREF_DEF_USE_EXTRA_CARD_CARDS = false;
    String PREF_FONT_ANTIALIAS = "pref_key_game_font_antialias";
    boolean PREF_DEF_FONT_ANTIALIAS = true;
    String PREF_OPENGL_VERSION = "pref_key_game_ogles_config";
    int PREF_DEF_OPENGL_VERSION = 1;
    String PREF_PENDULUM_SCALE = "pref_key_game_lab_pendulum_scale";
    boolean PREF_DEF_PENDULUM_SCALE = false;
    String PREF_SOUND_EFFECT = "pref_key_game_sound_effect";
    boolean PREF_DEF_SOUND_EFFECT = true;
    String PREF_LOCK_SCREEN = "pref_key_game_screen_orientation";
    boolean PREF_DEF_LOCK_SCREEN = true;
    String PREF_IMMERSIVE_MODE = "pref_key_immersive_mode";
    boolean PREF_DEF_IMMERSIVE_MODE = false;
    String PREF_SENSOR_REFRESH = "pref_key_sensor_refresh";
    boolean PREF_DEF_SENSOR_REFRESH = true;
    String PREF_DECK_SHOW_CARD = "pref_key_deck_show_card";
    int PREF_DECK_SHOW_CARD_LONG_PRESS = 1;
    int PREF_DECK_SHOW_CARD_DOUBLE = 0;
    int PREF_DEF_DECK_SHOW_CARD = PREF_DECK_SHOW_CARD_DOUBLE;
    String PREF_SHOW_CARD_SEARCH = "pref_key_card_search";
    int PREF_CARD_SEARCH1= 0;
    int PREF_CARD_SEARCH2 = 1;
    int PREF_DEF_CARD_SEARCH = PREF_CARD_SEARCH2;

    String PREF_LAST_ROOM_LIST = "pref_key_lastroom_list";

    int LAST_ROOM_MAX = 20;

    String SETTINGS_COVER = "settings_game_diy_card_cover";
    String SETTINGS_CARD_BG = "settings_game_diy_card_bg";
    String ASSETS_PATH = "data/";
    String ASSET_SERVER_LIST = "serverlist.xml";
    String ASSET_LIMIT_PNG = ASSETS_PATH + "textures/lim.png";
    String DEFAULT_FONT_NAME = "ygo.ttf";
    String DATABASE_NAME = "cards.cdb";
    String FONT_DIRECTORY = "fonts";
    String CORE_CONFIG_PATH = "core";
    String CORE_STRING_PATH = "core/%s/config/strings.conf";
    String CORE_LIMIT_PATH = "core/%s/config/lflist.conf";
    String CORE_SKIN_PATH = "textures";
    String CORE_SKIN_PENDULUM_PATH = CORE_SKIN_PATH + "/extra";
    String CORE_DECK_PATH = "deck";
    String CORE_SINGLE_PATH = "single";
    String CORE_IMAGE_PATH = "pics";
    String CORE_SCRIPT_PATH = "script";
    String CORE_REPLAY_PATH = "replay";
    String CORE_SCRIPTS_ZIP = "scripts.zip";
    String CORE_PICS_ZIP = "pics.zip";
    String CORE_SKIN_COVER = "cover.jpg";
    String CORE_SKIN_BG = "bg.jpg";
    String UNKNOWN_IMAGE = "unknown.jpg";
    String YDK_FILE_EX = ".ydk";
    int[] CORE_SKIN_BG_SIZE = new int[]{1024, 640};
    int[] CORE_SKIN_CARD_COVER_SIZE = new int[]{177, 254};
    boolean SUPPORT_BPG = true;
    String BPG = ".bpg";
    int CARD_MAX_COUNT = 3;
    String[] IMAGE_EX = SUPPORT_BPG ? new String[]{".bpg", ".jpg", ".png"}
            : new String[]{".jpg", ".png"};

    String[] FILE_IMAGE_EX = new String[]{".bmp", ".jpg", ".png", ".gif"};

    int REQUEST_CUT_IMG = 0x1000 + 0x10;
    int REQUEST_CHOOSE_FILE = 0x1000 + 0x20;
    int REQUEST_CHOOSE_IMG = 0x1000 + 0x21;

    int STRING_TYPE_START = 1050;

    int STRING_ATTRIBUTE_START = 1010;
    int STRING_RACE_START = 1020;
    int STRING_OT_START = 1239;

    int UNSORT_TIMES = 0x80;

    int CARD_SEARCH_GRAVITY = Gravity.RIGHT;
    int STRING_LIMIT_START = 1315;
    int STRING_CATEGORY_START = 1100;
    int DEFAULT_CARD_COUNT = 500;
    int DECK_WIDTH_COUNT = 10;
    int DECK_MAIN_MAX = 60;
    int DECK_EXTRA_MAX = 15;
    int DECK_SIDE_MAX = 15;
    int DECK_EXTRA_COUNT = (DECK_SIDE_MAX / DECK_WIDTH_COUNT * DECK_WIDTH_COUNT < DECK_SIDE_MAX) ? DECK_WIDTH_COUNT * 2 : DECK_WIDTH_COUNT;
    int DECK_SIDE_COUNT = DECK_EXTRA_COUNT;
    String URL_HELP = "http://www.jianshu.com/p/a43f5d951a25";
    String WIKI_SEARCH_URL = "http://www.ourocg.cn/S.aspx?key=";

    String SHARE_FILE = ".share_deck.png";
    /***
     * 如果是双击显示，则单击拖拽
     */
    boolean DECK_SINGLE_PRESS_DRAG = true;
}
