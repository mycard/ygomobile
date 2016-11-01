#ifndef MAIN_H
#define MAIN_H

#include "ocgcore.h"

extern "C" {
    byte *my_script_reader(const char *script_name, int *slen);
    uint32 my_card_reader(uint32 code, card_data *data);
    uint32 my_message_handler(void *pduel, uint32 message_type);

    void jni_init(JNIEnv *env, jclass jclazz);
    jlong jni_create_card(JNIEnv *env, jclass jclazz,
                     jlong code,jlong alias,jlong setcode,jlong type,jlong level,jlong attribute,
                     jlong race,jint attack,jint defense,jint lscale,jint rscale
    );
    jlong jni_create_duel(JNIEnv *env, jclass jclazz,jlong seed);
    void jni_start_duel(JNIEnv *env, jclass jclazz,jlong pduel, jlong options);
    void jni_end_duel(JNIEnv *env, jclass jclazz,jlong pduel);
    jint jni_process(JNIEnv *env, jclass jclazz,jlong pduel);
    void jni_set_player_info(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint lp, jint startcount, jint drawcount);
    void jni_set_responsei(JNIEnv *env, jclass jclazz,jlong pduel, jint value);
    jint jni_query_field_count(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint location);
    void jni_new_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint playerid, jint location, jint sequence, jint position);
    void jni_new_tag_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint location);
};
#endif //MAIN_H