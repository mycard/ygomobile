package cn.garymb.ygomobile.model.data;

/**
 * @author mabin
 *
 */
public interface ResourcesConstants {
	
	String FORUM_URL = "https://forum.my-card.in/";
	
	String WIKI_SEARCH_URL = "http://www.ourocg.cn/S.aspx?key=";
	
	String DOWNLOAD_BASE_URL = "http://7xito2.com1.z0.glb.clouddn.com/";
	
	String FONTS_DOWNLOAD_URL = "ygo.ttf";
	
	String CARD_IMAGE_DOWNLOAD_URL = DOWNLOAD_BASE_URL + "card_images/";
	
	String UPDATE_SERVER_URL = "http://23.252.108.13";

	String VERSION_UPDATE_CACHE_DIR = "updates";

	String DONATE_URL_WAP = "http://shenghuo.alipay.com/send/payment/fill.htm?optEmail=garymabin@hotmail.com";
	
	String DONATE_URL_MOBILE = "https://qr.alipay.com/apjod7orwpzd7jy734";

	String DEFAULT_MC_SERVER_NAME = "YGOPro233先行专区";
	
	String DEFAULT_MC_SERVER_ADDR = "118.178.111.167";
	
	int DEFAULT_MC_SERVER_PORT = 23333;
	
	String JSON_KEY_ID = "id";
	String JSON_KEY_NAME = "name";
	
	/**
	 * For version info
	 */
	String JSON_KEY_VERSION = "version";
	String JSON_KEY_VERSION_URL = "url";
	
	/**
	 * For server info.
	 */
	String JSON_KEY_SERVER_IP_ADDR = "ip";
	String JSON_KEY_SERVER_PORT = "port";
	String JSON_KEY_SERVER_AUTH = "auth";
	String JSON_KEY_SERVER_INDEX_URL = "index";
	String JSON_KEY_SERVER_MAX_ROOMS = "max_rooms";
	String JSON_KEY_SERVER_TYPE = "server_type";
	
	/**
	 * For room info
	 */
	String JSON_KEY_ROOM_STATUS = "status";
	String JSON_KEY_ROOM_SERVER_ID = "server_id";
	String JSON_KEY_ROOM_MODE = "mode";
	String JSON_KEY_ROOM_USERS = "users";
	
	//Optional
	String JSON_KEY_ROOM_PRIVACY = "private";
	String JSON_KEY_ROOM_RULE = "rule";
	String JSON_KEY_ROOM_START_LP = "start_lp";
	String JSON_KEY_ROOM_START_HAND = "start_hand";
	String JSON_KEY_ROOM_DRAW_COUNT = "draw_count";
	String JSON_KEY_ROOM_ENABLE_PRIORITY = "enable_priority";
	String JSON_KEY_ROOM_NO_CHECK_DECK = "no_check_deck";
	String JSON_KEY_ROOM_NO_SHUFFLE_DECK = "no_shuffle_deck";
	String JSON_KEY_ROOM_DELETED = "_deleted";
	
	/**
	 * For user info
	 */
	String JSON_KEY_USER_CERTIFIED = "certified";
	String JSON_KEY_USER_PLAYER_ID = "player";
	
	/**
	 * For card image
	 */
	String JSON_KEY_ZH_IMAGE_URL = "url";
	String JSON_KEY_ZH_THUMBNAIL_URL = "thumbnail_url";
	String JSON_KEY_EN_IMAGE_URL = "en";
	String JSON_KEY_EN_LQ_IMAGE_URL = "en-lq";
	
	
	int GAME_MODE_SINGLE = 0;
	int GAME_MODE_MATCH = 1;
	int GAME_MODE_TAG = 2;
	
	
	String GAME_STATUS_START = "start";
	String GAME_STATUS_WAIT = "wait";
	
	int GAME_RULE_OCG_ONLY = 0;
	int GAME_RULE_TCG_ONLY = 1;
	int GAME_RULE_OCG_TCG = 2;
	
	
	String MODE_OPTIONS = "mode.options";
	String GAME_OPTIONS = "game.options";
	String PRIVATE_OPTIONS = "private.options";
	
	int DIALOG_MODE_SIMPLE = -1;
	int DIALOG_MODE_CREATE_ROOM = 0;
	int DIALOG_MODE_QUICK_JOIN = 1;
	int DIALOG_MODE_JOIN_GAME = 2;
	int DIALOG_MODE_DONATE = 3;
	int DIALOG_MODE_FILTER_ATK = 4;
	int DIALOG_MODE_FILTER_DEF = 5;
	int DIALOG_MODE_FILTER_LEVEL = 6;
	int DIALOG_MODE_FILTER_EFFECT = 7;
	int DIALOG_MODE_ADD_NEW_SERVER = 8;
	int DIALOG_MODE_EDIT_SERVER = 9;
	int DIALOG_MODE_DIRECTORY_CHOOSE = 10;
	int DIALOG_MODE_APP_UPDATE = 11;
	
	String ROOM_INFO_NAME = "room.info.name";
	String ROOM_INFO_RULE = "room.info.rule";
	String ROOM_INFO_MODE = "room.info.mode";
	String ROOM_INFO_LIFEPOINTS = "room.info.lp";
	String ROOM_INFO_INITIALHAND = "room.info.inithand";
	String ROOM_INFO_DRAWCARDS = "room.info.drawcards";

}
