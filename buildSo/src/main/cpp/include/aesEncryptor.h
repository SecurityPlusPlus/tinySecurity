
#ifndef CIPHER_SO_ENCRYPTOR_H
#define CIPHER_SO_ENCRYPTOR_H


#include <jni.h>
#include <string>

class Encryptor {

private:
    JNIEnv *jniEnv;

public:
    Encryptor(JNIEnv *jniEnv);

    const char *decrypt(const char *key, const char *cipher_message);
};


#endif //CIPHER_SO_ENCRYPTOR_H
