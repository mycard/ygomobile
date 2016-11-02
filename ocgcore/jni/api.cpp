#include "api.h"
extern "C" {
    jmethodID readFile;
    jmethodID messageHandler;
    jmethodID getCard;
    jclass JClass;
//byte[] readFile(String name);
//int messageHandler(long duel,long msg);
//long _data = getCard(long code);
    JNIEnv *g_env;
    void jni_init(JNIEnv *env, jclass jclazz) {
        g_env = env;
        JClass = jclazz;
        set_script_reader(my_script_reader);
        set_card_reader(my_card_reader);
        set_message_handler(my_message_handler);
        readFile = env->GetStaticMethodID(jclazz, "readFile","(Ljava/lang/String;)[B");
        messageHandler = env->GetStaticMethodID(jclazz, "messageHandler","(JJ)I");
        getCard = env->GetStaticMethodID(jclazz, "getCard","(J)J");
    }
    jlong jni_create_card(JNIEnv *env, jclass jclazz,
                     jlong code,jlong alias,jlong setcode,jlong type,jlong level,jlong attribute,
                     jlong race,jint attack,jint defense,jint lscale,jint rscale
    ){
        card_data *data= new card_data;
        data->code = code;
        data->alias = alias;
        data->setcode = setcode;
        data->type = type;
        data->level = level;
        data->attribute = attribute;
        data->race = race;
        data->attack = attack;
        data->defense = defense;
        data->lscale = lscale;
        data->rscale = rscale;
        return (jint)data;
    }
    jlong jin_get_card_code(JNIEnv *env, jclass jclazz,jlong carddata){
        card_data *data=(card_data *)(int64_t)carddata;
        return (jlong)data->code;
    }
    jlong jni_create_duel(JNIEnv *env, jclass jclazz,jlong seed){
        return (jlong)create_duel((int64_t)seed);
    }
    void jni_start_duel(JNIEnv *env, jclass jclazz,jlong pduel, jlong options){
        start_duel((int64_t)pduel, (int64_t)options);
    }
    void jni_end_duel(JNIEnv *env, jclass jclazz,jlong pduel){
        end_duel((int64_t)pduel);
    }
    jint jni_process(JNIEnv *env, jclass jclazz,jlong pduel){
        return (jint)process((int64_t)pduel);
    }
    void jni_set_player_info(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint lp, jint startcount, jint drawcount){
        set_player_info((int64_t)pduel, playerid, lp,startcount,drawcount);
    }
    void jni_set_responsei(JNIEnv *env, jclass jclazz,jlong pduel, jint value){
        set_responsei((int64_t)pduel, value);
    }
    jint jni_query_field_count(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint location){
        return (jint)query_field_count((int64_t)pduel, playerid, location);
    }
    void jni_new_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint playerid, jint location, jint sequence, jint position){
        new_card((int64_t)pduel, code, owner, playerid, location, sequence, position);
    }
    void jni_new_tag_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint location){
        new_tag_card((int64_t)pduel, code, owner, location);
    }
    jbyteArray jni_get_log_message(JNIEnv *env, jclass jclazz,jlong pduel){
        byte *msg = (byte *)malloc(MSG_SIZE * sizeof(byte));
        get_log_message((int64_t)pduel, msg);
        jbyteArray  jbarray = env->NewByteArray(MSG_SIZE);
        jbyte *jy=(jbyte*)msg;  //BYTE强制转换成Jbyte；
        env->SetByteArrayRegion(jbarray, 0, 256, jy);            //将Jbyte 转换为jbarray数组
        free(msg);
        return jbarray;
    }
    jbyteArray jni_get_message(JNIEnv *env, jclass jclazz,jlong pduel){
        byte *buf = (byte *)malloc(BUFF_SIZE * sizeof(byte));
        int len = get_message((int64_t)pduel, buf);
        jbyteArray  jbarray = env->NewByteArray(len);
        jbyte *jy=(jbyte*)buf;  //BYTE强制转换成Jbyte；
        env->SetByteArrayRegion(jbarray, 0, len, jy);            //将Jbyte 转换为jbarray数组
        free(buf);
        return jbarray;
    }
    jbyteArray jni_query_field_info(JNIEnv *env, jclass jclazz,jlong pduel){
        byte *buf = (byte *)malloc(BUFF_SIZE * sizeof(byte));
        int len = query_field_info((int64_t)pduel, buf);
        jbyteArray  jbarray = env->NewByteArray(len);
        jbyte *jy=(jbyte*)buf;  //BYTE强制转换成Jbyte；
        env->SetByteArrayRegion(jbarray, 0, len, jy);            //将Jbyte 转换为jbarray数组
        free(buf);
        return jbarray;
    }
    void jni_set_responseb(JNIEnv *env, jclass jclazz,jlong pduel, jbyteArray byarray){
        jsize len  = env->GetArrayLength(byarray);
        jbyte *jbarray = (jbyte *)malloc(len * sizeof(jbyte));
        env->GetByteArrayRegion(byarray,0,len,jbarray);
        byte *buf=(byte*)jbarray;
        set_responseb((int64_t)pduel, buf);
    }
    jint jni_preload_script(JNIEnv *env, jclass jclazz,jlong pduel, jcharArray script){
        jsize len  = env->GetArrayLength(script);
        jchar *jbarray = (jchar *)malloc(len * sizeof(jchar));
        env->GetCharArrayRegion(script,0,len,jbarray);
        char *buf=(char*)jbarray;
        return preload_script((int64_t)pduel, buf, len);
    }
    jbyteArray jni_query_field_card(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint location, jint query_flag, jint use_cache){
        byte *buf = (byte *)malloc(BUFF_SIZE * sizeof(byte));
        int len = query_field_card((int64_t)pduel, playerid, location, query_flag, buf, use_cache);
        jbyteArray  jbarray = env->NewByteArray(len);
        jbyte *jy=(jbyte*)buf;  //BYTE强制转换成Jbyte；
        env->SetByteArrayRegion(jbarray, 0, len, jy);            //将Jbyte 转换为jbarray数组
        free(buf);
        return jbarray;
    }
    jbyteArray jni_query_card(JNIEnv *env, jclass jclazz,jlong pduel, jint playerid, jint location, jint sequence, jint query_flag, jint use_cache){
        byte *buf = (byte *)malloc(BUFF_SIZE * sizeof(byte));
        int len = query_card((int64_t)pduel, playerid, location, sequence, query_flag, buf, use_cache);
        jbyteArray  jbarray = env->NewByteArray(len);
        jbyte *jy=(jbyte*)buf;  //BYTE强制转换成Jbyte；
        env->SetByteArrayRegion(jbarray, 0, len, jy);            //将Jbyte 转换为jbarray数组
        free(buf);
        return jbarray;
    }
    //回调java
    byte *my_script_reader(const char *script_name, int *slen) {
        //script_name转jstring,调用java方法byte[] readFile(String name)，返回jintArray
        // 根据jintArray获取长度，jintArray转byte *
        jbyteArray buf = g_env->NewByteArray(MAX_SIZE);
        jint len = g_env->CallStaticIntMethod(JClass, readFile, buf);
        jbyte *jbarray = (jbyte *)malloc(len * sizeof(jbyte));
        g_env->GetByteArrayRegion(buf, 0, len, jbarray);
        return (byte*)jbarray;
    }
    //回调java
    uint32 my_card_reader(uint32 code, card_data *data) {
        data = (card_data *)g_env->CallStaticLongMethod(JClass, getCard, (jlong)code);
        if(data){
            return code;
        }
        return 0;
    }
    //回调java
    uint32 my_message_handler(void *pduel, uint32 message_type) {
        jint rs = g_env->CallStaticIntMethod(JClass, messageHandler, (jlong)pduel, (jlong)message_type);
        return (int32_t)rs;
    }
};