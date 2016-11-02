//
// VirtualApp Native Project
//

#ifndef OCG_CORE_NDK_H
#define OCG_CORE_NDK_H
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>
#include "ocgcore/common.h"
#include "ocgcore/card.h"
#include "ocgcore/ocgapi.h"

#define TAG "Ocgcore"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,  TAG, __VA_ARGS__)
#define LOGDT(T, ...) __android_log_print(ANDROID_LOG_DEBUG,  T, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#define FREE(ptr, org_ptr) { if ((void*) ptr != NULL && (void*) ptr != (void*) org_ptr) { free((void*) ptr); } }
#define NATIVE_METHOD(func_ptr, func_name, signature) { func_name, signature, reinterpret_cast<void*>(func_ptr) }

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#define JAVA_CLASS "cn/ygo/ocgcore/OcgCoreApi"
#endif //OCG_CORE_NDK_H
