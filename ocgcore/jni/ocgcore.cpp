#include<jni.h>
#include <stdlib.h>
#include "ocgcore.h"
#include "ocgcore/ocgapi.h"

extern "C" {
    JNIEXPORT void JNICALL Java_cn_ygo_ocgcore_OcgCoreApi_init(JNIEnv *env, jclass clazz) {
        set_script_reader((script_reader)default_script_reader);
    }

    JNIEXPORT jint JNICALL Java_cn_ygo_ocgcore_OcgCoreApi_create_duel(JNIEnv *env, jclass clazz, jlong seed){
        return (jint) create_duel((jint) seed);
    }
}