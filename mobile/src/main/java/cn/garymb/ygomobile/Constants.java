package cn.garymb.ygomobile;

public interface Constants {
    String PREF_LAST_YDK = "pref_last_ydk";
    String PREF_DEF_LAST_YDK = "new.ydk";
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
    boolean PREF_DEF_IMMERSIVE_MODE = true;
    String SETTINGS_COVER = "settings_game_diy_card_cover";
    String SETTINGS_CARD_BG = "settings_game_diy_card_bg";

    String ASSET_SERVER_LIST="serverlist.xml";

    String DEFAULT_FONT_NAME = "ygo.ttf";
    String DATABASE_NAME = "cards.cdb";
    String FONT_DIRECTORY = "fonts";
    String CORE_CONFIG_PATH = "core";
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
    int[] CORE_SKIN_BG_SIZE = new int[]{1024, 640};
    int[] CORE_SKIN_CARD_COVER_SIZE = new int[]{177, 254};

    int REQUEST_CUT_IMG = 0x1000 + 0x10;
    int REQUEST_CHOOSE_FILE = 0x1000 + 0x20;
    int REQUEST_CHOOSE_IMG = 0x1000 + 0x21;
    int REQUEST_CHOOSE_TTF = 0x1000 + 0x22;
    int REQUEST_CHOOSE_CDB = 0x1000 + 0x23;
}
