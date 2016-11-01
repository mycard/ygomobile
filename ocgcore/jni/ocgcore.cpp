#include "ocgcore.h"
#include "main.h"

extern "C" {
JavaVM *g_vm;
jclass g_jclass;
JNIEnv *g_env;

JNIEnv* getJEnv(){
    return g_env;
}
JavaVM* getJVM(){
    return g_vm;
}
jclass getJClass(){
    return g_jclass;
}

//函数声明
/*
V           void          void
Z         jboolean      boolean
I            jint            int
J           jlong          long
D         jdouble       double
F          jfloat          float
B          jbyte          byte
C          jchar           char
S          jshort         short
 [Z     jbooleanArray      boolean[]
[I        jintArray            int[]
[F       jfloatArray         float[]
[B      jbyteArray          byte[]
[C      jcharArray          char[]
[S      jshortArray         short[]
[D     jdoubleArray       double[]
[J        jlongArray          long[]
 */
static JNINativeMethod gMethods[] = {
        NATIVE_METHOD((void *) jni_init,             "initCore",        "()V"),
        NATIVE_METHOD((void *) jni_create_card,      "createCard",      "(JJJJJJJIIII)V"),
        NATIVE_METHOD((void *) jni_create_duel,      "createDuel",      "(J)J"),
        NATIVE_METHOD((void *) jni_start_duel,       "startDuel",       "(JJ)V"),
        NATIVE_METHOD((void *) jni_end_duel,         "endDuel",         "(J)V"),
        NATIVE_METHOD((void *) jni_process,          "process",         "(J)I"),
        NATIVE_METHOD((void *) jni_set_player_info,  "setPlayerInfo",   "(JIIII)V"),
        NATIVE_METHOD((void *) jni_set_responsei,    "setResponseI",    "(JI)V"),
        NATIVE_METHOD((void *) jni_query_field_count,    "queryFieldCount",    "(JII)I"),
        //void jni_new_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint playerid, jint location, jint sequence, jint position)
        NATIVE_METHOD((void *) jni_new_card,    "newCard",    "(JJIIIII)V"),
        //void jni_new_tag_card(JNIEnv *env, jclass jclazz,jlong pduel, jlong code, jint owner, jint location)
        NATIVE_METHOD((void *) jni_new_tag_card,    "newTagCard",    "(JJII)V"),
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass javaClass = env->FindClass(JAVA_CLASS);
    if (javaClass == NULL) {
        LOGE("Ops: Unable to find hook class.");
        return JNI_ERR;
    }
    if (env->RegisterNatives(javaClass, gMethods, NELEM(gMethods)) < 0) {
        LOGE("Ops: Unable to register the native methods.");
        return JNI_ERR;
    }
    g_env = env;
    g_vm = vm;
    g_jclass = (jclass) env->NewGlobalRef(javaClass);
    env->DeleteLocalRef(javaClass);
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void* reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }
    g_env = NULL;
    env->DeleteGlobalRef((jobject)g_vm);
    env->DeleteGlobalRef((jobject)g_jclass);
}

};
