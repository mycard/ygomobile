#include "api.h"
#include "ocgcore/common.h"
#include "ocgcore/card.h"
#include "ocgcore/ocgapi.h"

extern "C" {
jmethodID readFile;
jmethodID messageHandler;
jmethodID getCard;
jclass JClass;
JNIEnv *g_env;

//回调java
byte *my_script_reader(const char *script_name, int *slen) {
    //script_name转jstring,调用java方法byte[] readFile(String name)，返回jintArray
    // 根据jintArray获取长度，jintArray转byte *
    jstring file = g_env->NewStringUTF(script_name);
    jbyteArray buf = g_env->NewByteArray(MAX_SIZE);
    jint len = g_env->CallStaticIntMethod(JClass, readFile, file, buf);
    jbyte *jbarray = (jbyte *) malloc(len * sizeof(jbyte));
    g_env->GetByteArrayRegion(buf, 0, len, jbarray);
    return (byte *) jbarray;
}
//回调java
uint32 my_card_reader(uint32 code, card_data *data) {
    data = (card_data *) g_env->CallStaticLongMethod(JClass, getCard, (jlong) code);
    if (data) {
        return code;
    }
    return 0;
}
//回调java
uint32 my_message_handler(void *pduel, uint32 message_type) {
    jint rs = g_env->CallStaticIntMethod(JClass, messageHandler, (jlong) pduel,
                                         (jlong) message_type);
    return (uint32) rs;
}

void Java_cn_ygo_ocgcore_Api_init(JNIEnv *env, jclass jclazz) {
    g_env = env;
    JClass = jclazz;
    set_script_reader(my_script_reader);
    set_card_reader(my_card_reader);
    set_message_handler(my_message_handler);
    readFile = env->GetStaticMethodID(jclazz, "readFile", "(Ljava/lang/String;)[B");
    messageHandler = env->GetStaticMethodID(jclazz, "messageHandler", "(JJ)I");
    getCard = env->GetStaticMethodID(jclazz, "getCard", "(J)J");
}
jlong Java_cn_ygo_ocgcore_Api_createCard(JNIEnv *env, jclass jclazz,
                                         jlong code, jlong alias, jlong setcode, jlong type,
                                         jlong level, jlong attribute,
                                         jlong race, jint attack, jint defense, jint lscale,
                                         jint rscale
) {
    card_data *data = new card_data;
    data->code = (uint32) code;
    data->alias = (uint32) alias;
    data->setcode = (uint64) setcode;
    data->type = (uint32) type;
    data->level = (uint32) level;
    data->attribute = (uint32) attribute;
    data->race = (uint32) race;
    data->attack = attack;
    data->defense = defense;
    data->lscale = (uint32) lscale;
    data->rscale = (uint32) rscale;
    return (jint) data;
}
jlong Java_cn_ygo_ocgcore_Api_getCardCode(JNIEnv *env, jclass jclazz, jlong carddata) {
    card_data *data = (card_data *)carddata;
    return (jlong) data->code;
}
jlong Java_cn_ygo_ocgcore_Api_createDuel(JNIEnv *env, jclass jclazz, jlong seed) {
    return (jlong) create_duel((uint32)seed);
}
void Java_cn_ygo_ocgcore_Api_startDuel(JNIEnv *env, jclass jclazz, jlong pduel, jlong options) {
    start_duel((ptr) pduel, (int32) options);
}
void Java_cn_ygo_ocgcore_Api_endDuel(JNIEnv *env, jclass jclazz, jlong pduel) {
    end_duel((ptr) pduel);
}
jint Java_cn_ygo_ocgcore_Api_process(JNIEnv *env, jclass jclazz, jlong pduel) {
    return (jint) process((ptr) pduel);
}
void Java_cn_ygo_ocgcore_Api_setPlayerInfo(JNIEnv *env, jclass jclazz, jlong pduel, jint playerid,
                                           jint lp, jint startcount, jint drawcount) {
    set_player_info((ptr) pduel, playerid, lp, startcount, drawcount);
}
void Java_cn_ygo_ocgcore_Api_setResponseI(JNIEnv *env, jclass jclazz, jlong pduel, jint value) {
    set_responsei((ptr) pduel, value);
}
jint Java_cn_ygo_ocgcore_Api_queryFieldCount(JNIEnv *env, jclass jclazz, jlong pduel, jint playerid,
                                             jint location) {
    return (jint) query_field_count((ptr) pduel, (uint8) playerid, (uint8) location);
}
void Java_cn_ygo_ocgcore_Api_newCard(JNIEnv *env, jclass jclazz, jlong pduel, jlong code,
                                     jint owner, jint playerid, jint location, jint sequence,
                                     jint position) {
    new_card((ptr) pduel, (uint32)code, (uint8) owner, (uint8) playerid, (uint8) location,
             (uint8) sequence, (uint8) position);
}
void Java_cn_ygo_ocgcore_Api_newTagCard(JNIEnv *env, jclass jclazz, jlong pduel, jlong code,
                                        jint owner, jint location) {
    new_tag_card((ptr) pduel, (uint32) code, (uint8) owner, (uint8) location);
}
jbyteArray Java_cn_ygo_ocgcore_Api_getLogMessage(JNIEnv *env, jclass jclazz, jlong pduel) {
    byte *msg = (byte *) malloc(MSG_SIZE * sizeof(byte));
    get_log_message((ptr) pduel, msg);
    jbyteArray jbarray = env->NewByteArray(MSG_SIZE);
    jbyte *jy = (jbyte *) msg;
    env->SetByteArrayRegion(jbarray, 0, 256, jy);
    free(msg);
    return jbarray;
}
jbyteArray Java_cn_ygo_ocgcore_Api_getMessage(JNIEnv *env, jclass jclazz, jlong pduel) {
    byte *buf = (byte *) malloc(BUFF_SIZE * sizeof(byte));
    int len = get_message((ptr) pduel, buf);
    jbyteArray jbarray = env->NewByteArray(len);
    jbyte *jy = (jbyte *) buf;
    env->SetByteArrayRegion(jbarray, 0, len, jy);
    free(buf);
    return jbarray;
}
jbyteArray Java_cn_ygo_ocgcore_Api_queryFieldInfo(JNIEnv *env, jclass jclazz, jlong pduel) {
    byte *buf = (byte *) malloc(BUFF_SIZE * sizeof(byte));
    int len = query_field_info((ptr) pduel, buf);
    jbyteArray jbarray = env->NewByteArray(len);
    jbyte *jy = (jbyte *) buf;
    env->SetByteArrayRegion(jbarray, 0, len, jy);
    free(buf);
    return jbarray;
}
void Java_cn_ygo_ocgcore_Api_setResponseB(JNIEnv *env, jclass jclazz, jlong pduel,
                                          jbyteArray byarray) {
    jsize len = env->GetArrayLength(byarray);
    jbyte *jbarray = (jbyte *) malloc(len * sizeof(jbyte));
    env->GetByteArrayRegion(byarray, 0, len, jbarray);
    byte *buf = (byte *) jbarray;
    set_responseb((ptr) pduel, buf);
}
jint Java_cn_ygo_ocgcore_Api_preloadScript(JNIEnv *env, jclass jclazz, jlong pduel,
                                           jbyteArray script) {
    jsize len = env->GetArrayLength(script);
    jbyte *jbarray = (jbyte *) malloc(len * sizeof(jbyte));
    env->GetByteArrayRegion(script, 0, len, jbarray);
    char *buf = (char *) jbarray;
    return preload_script((ptr) pduel, buf, len);
}
jbyteArray Java_cn_ygo_ocgcore_Api_queryFieldCard(JNIEnv *env, jclass jclazz, jlong pduel,
                                                  jint playerid, jint location, jint query_flag,
                                                  jint use_cache) {
    byte *buf = (byte *) malloc(BUFF_SIZE * sizeof(byte));
    int len = query_field_card((ptr) pduel, (uint8) playerid, (uint8) location, query_flag, buf, use_cache);
    jbyteArray jbarray = env->NewByteArray(len);
    jbyte *jy = (jbyte *) buf;
    env->SetByteArrayRegion(jbarray, 0, len, jy);
    free(buf);
    return jbarray;
}
jbyteArray Java_cn_ygo_ocgcore_Api_queryCard(JNIEnv *env, jclass jclazz, jlong pduel, jint playerid,
                                             jint location, jint sequence, jint query_flag,
                                             jint use_cache) {
    byte *buf = (byte *) malloc(BUFF_SIZE * sizeof(byte));
    int len = query_card((ptr) pduel, (uint8) playerid, (uint8) location, (uint8) sequence, query_flag, buf, use_cache);
    jbyteArray jbarray = env->NewByteArray(len);
    jbyte *jy = (jbyte *) buf;
    env->SetByteArrayRegion(jbarray, 0, len, jy);
    free(buf);
    return jbarray;
}
};