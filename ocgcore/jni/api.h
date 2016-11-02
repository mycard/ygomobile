#ifndef MAIN_H
#define MAIN_H

#define MSG_SIZE 256
#define BUFF_SIZE 4096
#define MAX_SIZE 0x10000
#include "core.h"

#ifdef ARM_X64
typedef int64_t c_ptr;
#else
typedef int32_t c_ptr;
#endif

extern "C" {
    byte *my_script_reader(const char *script_name, int *slen);
    uint32 my_card_reader(uint32 code, card_data *data);
    uint32 my_message_handler(void *pduel, uint32 message_type);

    void jni_init(JNIEnv *env, jclass jclazz);
    jlong jni_create_card(JNIEnv *env, jclass jclazz,
                     jlong code,jlong alias,jlong setcode,jlong type,jlong level,jlong attribute,
                     jlong race,jint attack,jint defense,jint lscale,jint rscale
    );
    jlong jin_get_card_code(JNIEnv *env, jclass jclazz,jlong carddata);
    jlong jni_create_duel(JNIEnv *env, jclass jclazz,jlong seed);
    void jni_start_duel(JNIEnv *env, jclass jclazz,jlong pduel, jlong options);
    void jni_end_duel(JNIEnv *env, jclass jclazz,jlong pduel);
    jint jni_process(JNIEnv *env, jclass jclazz,jlong pduel);
    void jni_set_player_info(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint lp, jint startcount, jint drawcount);
    void jni_set_responsei(JNIEnv *env, jclass jclazz,jlong pduel, jint value);
    jint jni_query_field_count(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint location);
    void jni_new_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint playerid, jint location, jint sequence, jint position);
    void jni_new_tag_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint location);
    jbyteArray jni_get_log_message(JNIEnv *env, jclass jclazz,jlong pduel);
    jbyteArray jni_get_message(JNIEnv *env, jclass jclazz,jlong pduel);
    jbyteArray jni_query_field_info(JNIEnv *env, jclass jclazz,jlong pduel);
    void jni_set_responseb(JNIEnv *env, jclass jclazz,jlong pduel, jbyteArray byarray);
    jint jni_preload_script(JNIEnv *env, jclass jclazz,jlong pduel, jbyteArray script);
    jbyteArray jni_query_field_card(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint location, jint query_flag, jint use_cache);
    jbyteArray jni_query_card(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint location, jint sequence, jint query_flag, jint use_cache);
};
#endif //MAIN_H