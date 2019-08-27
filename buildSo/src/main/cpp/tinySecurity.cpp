#include "include/extern-keys.h"
#include "include/aesEncryptor.h"
#include "include/logger.h"
#include "include/register_jni_method.h"
#include "include/md5.h"
#include "include/base64.h"
#include "include/aes.h"
#include <string>
#include <cstdlib>
#include "map"

using namespace std;

map<string, string> _map;

void init(JNIEnv *env, jclass type) {
    LOAD_MAP(_map);
}

jstring getString(JNIEnv *env, jclass instance, jstring key_) {
    const char *key = env->GetStringUTFChars(key_, NULL);
    string keyStr(key);
    string value = _map[keyStr];
    Encryptor *encryptor = new Encryptor(env);
    const char *result = encryptor->decrypt(ENCRYPT_KEY, value.c_str());

    jstring str = env->NewStringUTF(result);

    env->ReleaseStringUTFChars(key_, key);
    delete encryptor;
    return str;
}

jbyteArray decrypt(JNIEnv *env, jclass instance, jbyteArray key_,jbyteArray cipher_) {

    jsize keyLen_ = env->GetArrayLength(key_);
    jbyte *keyVal_ = env->GetByteArrayElements(key_, NULL);
    jsize cipherLen_ = env->GetArrayLength(cipher_);
    jbyte *cipherVal_ = env->GetByteArrayElements(cipher_, NULL);

    //key
    uint8_t *realKey = (uint8_t *)malloc(16*sizeof(uint8_t));
    MD5Context ctx;
    MD5Init(&ctx);
    MD5Update(&ctx, reinterpret_cast<const unsigned char *>(keyVal_), keyLen_);
    MD5Final(&ctx,realKey);

    //cipher
    size_t realCipherLen = 0;
    uint8_t *realCipher = b64_decode_ex(reinterpret_cast<const char *>(cipherVal_), cipherLen_,&realCipherLen);
    //
    unsigned char iv[16] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    uint8_t *plain =(uint8_t *)malloc(realCipherLen);
    unsigned long plainLen;
    int ret =  aesDecrypt(2,iv,realCipher,realCipherLen,plain,&plainLen,realKey,16);

    env->ReleaseByteArrayElements(key_, keyVal_, 0);
    env->ReleaseByteArrayElements(cipher_, cipherVal_, 0);

    if(ret == 0){
        jbyteArray result = env->NewByteArray(plainLen);
        env->SetByteArrayRegion(result, 0, plainLen, reinterpret_cast<const jbyte *>(plain));
        return result;
    }
    return NULL;
}