#ifndef AES_H
#define AES_H

struct aes_key {
    unsigned long eK[60], dK[60];
    int Nr;
};


#ifdef _MSC_VER
#define byte(x, n) ((unsigned char)((x) >> (8 * (n))))
#else
#define byte(x, n) (((x) >> (8 * (n))) & 255)
#endif

#define LOAD32H(x, y)                            \
     { x = ((unsigned long)((y)[0] & 255)<<24) | \
           ((unsigned long)((y)[1] & 255)<<16) | \
           ((unsigned long)((y)[2] & 255)<<8)  | \
           ((unsigned long)((y)[3] & 255)); }

#define STORE32H(x, y)                                                                     \
     { (y)[0] = (unsigned char)(((x)>>24)&255); (y)[1] = (unsigned char)(((x)>>16)&255);   \
       (y)[2] = (unsigned char)(((x)>>8)&255); (y)[3] = (unsigned char)((x)&255); }

#define ROLc(value, bits) (((value) << (bits)) | ((value) >> (32 - (bits))))
#define RORc(value, bits) (((value) >> (bits)) | ((value) << (32 - (bits))))
/* make aes an alias */


#ifdef __cplusplus
extern "C" {
#endif

#define ECB                    1
#define CBC                    2
#define CFB                    3
#define OFB                    4

#define SYM_ENC_FAILED            27
#define SYM_DEC_FAILED            28

#define PARAM_ERROR                3


int aesEncrypt(int mode, unsigned char *IV, unsigned char *plain, unsigned long plainlen,
               unsigned char *cipher,unsigned long *cipherlen,
               unsigned char *key, unsigned long keylen);

int aesDecrypt(int mode, unsigned char *IV, unsigned char *cipher, unsigned long cipherlen,
               unsigned char *plain, unsigned long *plainlen,
               unsigned char *key,unsigned long keylen);


#ifdef __cplusplus
}
#endif
#endif