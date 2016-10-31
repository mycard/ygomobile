package cn.garymb.ygomobile.common;

/**
 * @author mabin
 *
 */
public interface Constants {
    boolean UseBpgInZip = false;
	
	/*change this will affect C++ code, be careful!*/
	String WORKING_DIRECTORY = "/ygocore/";
	
	/*change this will affect C++ code, be careful!*/
	int COMPAT_GUI_MODE_COMBOBOX = 0;
    String CORE_PICS_ZIP = "pics.zip";
	/*change this will affect C++ code, be careful!*/
	int COMPAT_GUI_MODE_CHECKBOXES_PANEL = 1;
   String CORE_SCRIPTS_ZIP = "scripts.zip";
	
	String CONFIG_FILE = "system.conf";
	String CARD_DB_FILE = "cards.cdb";
	
	int RESOURCE_ERROR_SDCARD_NOT_AVAIL = -1;
	int RESOURCE_ERROR_NOT_EXIST = -2;
	int RESOURCE_ERROR_CONFIG_FILE_NOT_EXIST = -3;
	int RESOURCE_ERROR_CARDS_DB_FILE_NOT_EXIST = -4;
	int RESOURCE_ERROR_NONE = 0;

	int IO_BUFFER_SIZE = 8192;

	String ENCODING = "UTF-8";

	String INTENT_EXTRA_PATH_KEY = "ygomobile.extra.path";

	String OPENGL_PATH = "opengl";
	String CARD_QUALITY_PATH = "card_quality";
	
	//Virual Help overlay handle ops
	
	int MODE_CANCEL_CHAIN_OPTIONS = 0;
	int MODE_REFRESH_OPTION = 1;
	int MODE_IGNORE_CHAIN_OPTION = 2;
	int MODE_REACT_CHAIN_OPTION = 3;
	
	String FONT_DIRECTORY = "/fonts/";
	String DEFAULT_FONT_NAME = "ygo.ttf";
	String CARD_IMAGE_DIRECTORY = "/pics/";
//	public static final String THUMBNAIL_IMAGE_DIRECTORY = "/thumbnail/";
	
	String CORE_CONFIG_PATH = "core";
	
	String CORE_SKIN_PATH = "textures";
	
	String CORE_EXTRA_PATH = "extra";
	
	String CORE_DECK_PATH = "deck";
	
	String CORE_SINGLE_PATH = "single";
	
	String CORE_SKIN_COVER = "bg.jpg";
	String CORE_SKIN_CARD_BACK = "cover.jpg";
	int[]  CORE_SKIN_COVER_SIZE = new int[]{1024, 640};
	int[]  CORE_SKIN_CARD_BACK_SIZE = new int[]{177, 254};
	
	String DEFAULT_DECK_NAME = "new.ydk";
	
	String DEFAULT_OGLES_CONFIG = "1";
	
	String DEFAULT_CARD_QUALITY_CONFIG = "1";
	
	int TRANSACT_TIMEOUT = 2 * 60 * 1000;
	
	int FRAGMENT_ID_DUEL = 1;
	int FRAGMENT_ID_CARD_WIKI = 2;
	int FRAGMENT_ID_CARD_DECK = 3;
	int FRAGMENT_ID_CARD_IMAGE = 4;
	int FRAGMENT_ID_CARD_DETAIL = 8;
	int FRAGMENT_ID_USER_LOGIN = 9;
	
	
	int MSG_DOWN_EVENT_TASK_LIST_CHANGED = 0x1000;
	int MSG_DOWN_EVENT_STATUS_CHANGED = 0x1001;
	int MSG_DOWN_EVENT_PROGRESS = 0x1002;
	
	
	/**
	 * preference name
	 */
	//for compatiablity
	String PREF_FILE = "preferred-config";
	String RESOURCE_PATH = "resource";
	
	String PREF_FILE_COMMON = "pref_common";
	String PREF_KEY_DATA_VERSION = "pref_data_ver";
	String PREF_KEY_EXTRA_VERSION = "pref_extra_ver";
	String PREF_KEY_LAST_DECK = "pref_last_deck";
	String PREF_KEY_UPDATE_CHECK = "pref_last_update_check";
	
	String PREF_FILE_DOWNLOAD_TASK = "pref_download_task";
	
	String PREF_FILE_SERVER_LIST = "pref_server_list";
	
	String PREF_KEY_USER_DEF_SERVER_SIZE = "pref_server_size";
	String PREF_KEY_USER_NAME = "pref_user_name_";
	String PREF_KEY_SERVER_NAME = "pref_server_name_";
	String PREF_KEY_SERVER_ADDR = "pref_server_addr_";
	String PREF_KEY_SERVER_PORT = "pref_server_port_";
	String PREF_KEY_SERVER_INFO = "pref_server_info_";
	
	String PREF_KEY_VERSION_CHECK = "pref_version_check";
	
	long DAILY_MILLSECONDS = 24 * 3600 * 1000;
	
	
	
	int ACTION_BAR_CHANGE_TYPE_PAGE_CHANGE = 0x1000;
	int ACTION_BAR_CHANGE_TYPE_DATA_LOADING = 0x1001;
	
	int ACTION_BAR_EVENT_TYPE_NEW = 0x2000;
	int ACTION_BAR_EVENT_TYPE_SETTINGS = 0x2001;
	int ACTION_BAR_EVENT_TYPE_SEARCH = 0x2002;
	int ACTION_BAR_EVENT_TYPE_PLAY = 0x2003;
	int ACTION_BAR_EVENT_TYPE_FILTER = 0x2004;
	int ACTION_BAR_EVENT_TYPE_DONATE = 0x2005;
	int ACTION_BAR_EVENT_TYPE_PERSONAL_CENTER = 0x2006;
	int ACTION_BAR_EVENT_TYPE_RESET = 0x2006;
	int ACTION_BAR_EVENT_TYPE_CARD_IAMGE_DL = 0x2007;
	
	int REQUEST_TYPE_UPDATE_SERVER = 0x3000;
	int REQUEST_TYPE_UPDATE_ROOM = 0x3001;
	int REQUEST_TYPE_LOGIN = 0x3002;
	
	int REQUEST_TYPE_DOWNLOAD_IMAGE = 0x3003;

	int REQUEST_TYPE_LOAD_BITMAP = 0x3004;
	
	int REQUEST_TYPE_CHANGE_IMAGE_LOAD_PRIORITY = 0x3005;
	
	int REQUEST_TYPE_RESET_LOAD_QUEUE = 0x3006;
	
	int REQUEST_TYPE_RESET_DOWNLOAD_QUEUE = 0x3007;
	
	int REQUEST_TYPE_CLEAR_BITMAP_CACHE = 0x3008;
	
	int REQUEST_TYPE_MYCARD_API_GET_CARDIMAGE_URL = 0x3009;
	String REQUEST_RESULT_KEY_CARDIMAGE_URL = "request.result.cardimage.url";
	
	
	
	int IMAGE_DL_EVENT_TYPE_DOWNLOAD_COMPLETE = 0x4000;
	
	
	String BUNDLE_KEY_USER_NAME = "bundle.key.user.name";
	String BUNDLE_KEY_USER_PW = "bundle.key.user.pw";
	
	
	
	int IMAGE_TYPE_THUMNAIL = 0;
	int IMAGE_TYPE_ORIGINAL = 1;
	
	int BITMAP_LOAD_TYPE_PRELOAD = 0;
	int BITMAP_LOAD_TYPE_LOAD = 1;
	
	int MSG_ID_UPDATE_ROOM_LIST = 0;
	int MSG_ID_UPDATE_SERVER = 1;
	int MSG_ID_EXIT_CONFIRM_ALARM = 3;
	
	
	String SETTINGS_ACTION_COMMON = "cn.garymb.ygomobile.prefs.PREFS_COMMON";
	String SETTINGS_ACTION_GAME = "cn.garymb.ygomobile.prefs.PREFS_GAME";
	String SETTINGS_ACTION_ABOUT = "cn.garymb.ygomobile.prefs.PREFS_ABOUT";
	
	String SETTINGS_FARGMENT_COMMON = "cn.garymb.ygomobile.fragment.setting.CommonSettingsFragment";
	String SETTINGS_FARGMENT_GAME = "cn.garymb.ygomobile.fragment.setting.GameSettingsFragment";
	String SETTINGS_FARGMENT_ABOUT = "cn.garymb.ygomobile.fragment.setting.AboutSettingsFragment";
	String SETTINGS_FARGMENT_GAME_LAB = "cn.garymb.ygomobile.fragment.setting.GameLabSettingsFragment";

	String ACTION_VIEW_DOWNLOAD_STATUS = "action_view_download_status";

	String ACTION_VIEW_UPDATE = "action_view_update";

	String ACTION_NEW_CLIENT_VERSION = "action_new_client_version";

	String DEFAULT_ENCODING = "utf-8";

}
