package cn.garymb.ygomobile.model;


public interface Settings {

    String KEY_PREF_COMMON_SETTINGS = "pref_key_common_settings";
    String KEY_PREF_GAME_SETTINGS = "pref_key_game_settings";
    String KEY_PREF_ABOUT_SETTINGS = "pref_key_about_settings";

    //about config
    String KEY_PREF_ABOUT_VERSION = "pref_key_about_version";
    String KEY_PREF_ABOUT_PROJ_LOC = "pref_key_about_project";
    String KEY_PREF_ABOUT_OPENSOURCE = "pref_key_opensource_cert";
    String KEY_PREF_ABOUT_CHECK_UPDATE = "pref_key_about_check_update";
    String KEY_PREF_ABOUT_FEED_BACK = "pref_key_feed_back";

    //common config
    String KEY_PREF_COMMON_NOT_DOWNLOAD_VIA_GPRS = "pref_key_common_download_condition";

    //game config
    String KEY_PREF_GAME_SCREEN_ORIENTATION = "pref_key_game_screen_orientation";
    String KEY_PREF_GAME_FONT_ANTIALIAS = "pref_key_game_font_antialias";
    String KEY_PREF_GAME_RESOURCE_PATH = "pref_key_game_res_path";
    String KEY_PREF_GAME_IMAGE_QUALITY = "pref_key_game_image_quality";
    String KEY_PREF_GAME_OGLES_CONFIG = "pref_key_game_ogles_config";
    String KEY_PREF_GAME_SOUND_EFFECT = "pref_key_game_sound_effect";
    String KEY_PREF_GAME_LABORATORY = "pref_key_game_lab";
    String KEY_PREF_GAME_LAB_PENDULUM_SCALE = "pref_key_game_lab_pendulum_scale";
    String KEY_PREF_GAME_IMMERSIVE_MODE = "pref_key_immersive_mode";

    //game diy
    String KEY_PREF_GAME_FONT_NAME = "pref_key_game_font_name";
    String KEY_PREF_GAME_DIY_COVER = "settings_game_diy_cover";
    String KEY_PREF_GAME_DIY_CARD_BACK = "settings_game_diy_card_back";
    String KEY_PREF_GAME_DIY_CARD_DB = "settings_game_diy_card_db";
    String KEY_PREF_GAME_RESET_CARD_DB = "settings_reset_card_db";
}