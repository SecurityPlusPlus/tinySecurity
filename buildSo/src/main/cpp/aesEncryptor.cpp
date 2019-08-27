#include <cstdlib>
#include "include/aesEncryptor.h"
#include "include/logger.h"
#include "include/extern-keys.h"
#include "include/aes.h"
#include "include/md5.h"
#include "include/base64.h"

Encryptor::Encryptor(JNIEnv *jniEnv) {
    this->jniEnv = jniEnv;
}

const char *Encryptor::decrypt(const char *key, const char *cipher_message) {

    int ulRet = 0;
    uint32_t cipherLen = strlen(cipher_message);
    char *resultChars = new char[cipherLen + 1];
    unsigned long resultLen = 0;

    //key
    uint8_t *realKey = (uint8_t *) malloc(16 * sizeof(uint8_t));
    MD5Context ctx;
    MD5Init(&ctx);
    MD5Update(&ctx, reinterpret_cast<const unsigned char *>(key), strlen(key));
    MD5Final(&ctx, realKey);

    //cipher
    size_t realCipherLen = 0;
    uint8_t *realCipher = b64_decode_ex(cipher_message, strlen(cipher_message), &realCipherLen);

#if defined(ENCRYPT_MODE)
#ifdef ENCRYPT_IV
    size_t realIVLen = 0;
    uint8_t *realIV = b64_decode_ex(ENCRYPT_IV, strlen(ENCRYPT_IV), &realIVLen);
//    LOGE("realIV len = %d",realIVLen);
//    uint8_t *realIV = (uint8_t *)malloc(sizeof(uint8_t) * realIVLen + 1);
//    memcpy(realIV,desIV,16);
//    LOG_DATA(realIV,16);

    ulRet = aesDecrypt(ENCRYPT_MODE, realIV, realCipher, realCipherLen,
                       reinterpret_cast<unsigned char *>(resultChars), &resultLen,
                       realKey, 16);
#else
    uint8_t realIV[16];
    memset(realIV,0x00,16);
    ulRet = aesDecrypt(ENCRYPT_MODE, realIV, realCipher, realCipherLen,
                       reinterpret_cast<unsigned char *>(resultChars), &resultLen,
                       realKey, 16);
#endif
#else //默认ECB模式
    ulRet = aesDecrypt(1, NULL, realCipher, realCipherLen,
                       reinterpret_cast<unsigned char *>(resultChars), &resultLen,
                       realKey, 16);
#endif
    if (ulRet == 0) {
        char *result = (char *) malloc(resultLen * sizeof(char) + 1);
        memset(result, 0, resultLen * sizeof(char) + 1);
        memcpy(result, resultChars, resultLen);
        return result;
    } else {
        return NULL;
    }
}
