#ifndef OCGCORE_API_H
#define OCGCORE_API_H

#include <jni.h>
#include <stdlib.h>
#include <android/log.h>
#define TAG "Ocgcore"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,  TAG, __VA_ARGS__)
#define LOGDT(T, ...) __android_log_print(ANDROID_LOG_DEBUG,  T, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define MSG_SIZE 256
#define BUFF_SIZE 4096
#define MAX_SIZE 0x10000

#if defined(__arm__)
  #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #define ABI "armeabi-v7a/NEON"
    #else
      #define ABI "armeabi-v7a"
    #endif
  #elif defined(__ARM_ARCH_8A__)
    #define ABI "armeabi-v8a"
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
   #define ABI "x86"
#elif defined(__mips__)
   #define ABI "mips"
#else
   #define ABI "unknown"
#endif

#endif //OCGCORE_API_H