//
// Created by yu on 2019/4/10.
//
#include <jni.h>
#include <assert.h>

#ifndef REGISTER_NATIVE_METHOD_H
#define REGISTER_NATIVE_METHOD_H

// 获取数组的大小
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
// 指定注册的类的路径(包名路径 + 类名)，通过FindClass 方法查找到对应的类
#define JNIREG_CLASS "com/atom/lib/tinysecurity/TinySecurityCore"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL init(JNIEnv *, jclass);

JNIEXPORT jstring JNICALL getString(JNIEnv *, jclass, jstring);

JNIEXPORT jbyteArray JNICALL decrypt(JNIEnv *, jclass, jbyteArray, jbyteArray);

/**
 * 函数映射表
 * 需要注册的函数列表，放在JNINativeMethod 类型的数组中，以后如果需要增加函数，只需在这里添加就行了
 * 参数：
 * 1.java中用native关键字声明的函数名
 * 2.签名（传进来参数类型和返回值类型的说明）
 * 3.C/C++中对应函数的函数名（地址）
 */
static JNINativeMethod methods[] = {
        {"init",      "()V",                                    (void *) init},
        {"getString", "(Ljava/lang/String;)Ljava/lang/String;", (void *) getString},
        {"decrypt",   "([B[B)[B",                               (void *) decrypt},
        //这里可以有很多其他映射函数
};


//注册Native methods
static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods,
                                 int numMethods) {
    jclass clazz;
    //找到声明native方法的类
    clazz = env->FindClass(className);
    if (NULL == clazz) {
        return JNI_FALSE;
    }
    //注册函数 参数：java类 所要注册的函数数组 注册函数的个数
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

//注册Native
static int registerNatives(JNIEnv *env) {
    return registerNativeMethods(env, JNIREG_CLASS, methods, NELEM(methods));
}

//加载虚拟机
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return JNI_ERR;
    }
    assert(env != NULL);
    //动态注册，自定义函数
    if (!registerNatives(env)) {
        return JNI_ERR;
    }
    // 返回jni的版本
    return JNI_VERSION_1_4;
}

#ifdef __cplusplus
}
#endif

#endif //REGISTER_NATIVE_METHOD_H