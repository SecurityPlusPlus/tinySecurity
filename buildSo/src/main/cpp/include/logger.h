//
// Created by wei on 17-12-27.
//

#ifndef LOGGER
#define LOGGER

#include <android/log.h>

#define LOG_TAG "jni_log"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGW(...)  __android_log_write(ANDROID_LOG_WARN,LOG_TAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define LOG_DATA(d, l)\
do\
{\
    int i;\
    for(i=0;i<l;i++)\
    {\
        if((i+1) % 16) \
            LOGD("%02X ", d[i]);\
        else\
            LOGD("%02X\n", d[i]);\
    }\
    if(i % 16) LOGD("\n");\
}\
while(0)

#endif//LOGGER
