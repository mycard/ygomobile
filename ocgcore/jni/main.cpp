#include "main.h"
extern "C" {
    void jni_init(JNIEnv *env, jclass jclazz) {
        set_script_reader(my_script_reader);
        set_card_reader(my_card_reader);
        set_message_handler(my_message_handler);
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
    /***
     * 返回内容，输出长度slen
     */
    byte *my_script_reader(const char *script_name, int *slen) {
        //script_name转jstring,调用java方法byte[] readFile(String name)，返回jintArray
        // 根据jintArray获取长度，jintArray转byte *
        return 0;
    }
    uint32 my_card_reader(uint32 code, card_data *data) {
        //调用java方法
        //int _data = getCard(long code);
        //if(_data == 0)return 0;
        //data = _data;
        return code;
    }
    uint32 my_message_handler(void *pduel, uint32 message_type) {
        //调用java方法
        //int messageHandler(int duel,long msg);
        return 0;
    }
};