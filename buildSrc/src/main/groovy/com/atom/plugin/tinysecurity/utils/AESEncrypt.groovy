package com.atom.plugin.tinysecurity.utils

import org.apache.commons.codec.binary.Base64

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

class AESEncrypt {
    private static final String CHARSET = "UTF-8"

    private static final String HASH_ALGORITHM = "MD5"
    private static final String AES_MODE_ECB = "AES/ECB/PKCS5Padding"
    private static final String AES_MODE_CBC = "AES/CBC/PKCS5Padding"
    private static final byte[] AES_CBC_IV_DEFAULT = [0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]

    static String encrypt(String mode, String key, String message, byte[] iv) {
        SecretKeySpec keySpec = generateKeySpec(key)
        byte[] result = encrypt(mode, keySpec, iv, message.getBytes(CHARSET))
        return Base64.encodeBase64String(result)
    }
    static byte[] encrypt(String mode, SecretKeySpec keySpec, byte[] iv, byte[] message){
        if (mode != null) {
            if ("ECB" == mode) {
                Cipher cipher = Cipher.getInstance(AES_MODE_ECB)
                cipher.init(Cipher.ENCRYPT_MODE, keySpec)
                return cipher.doFinal(message)
            }
        }
        Cipher cipher = Cipher.getInstance(AES_MODE_CBC)
        IvParameterSpec ivParameterSpec
        if (iv != null) {
            LogUtils.Log("encrypt iv = ${Arrays.toString(iv)}")
            ivParameterSpec = new IvParameterSpec(iv)
        } else {
            LogUtils.Log("encrypt iv = ${Arrays.toString(AES_CBC_IV_DEFAULT)}")
            ivParameterSpec = new IvParameterSpec(AES_CBC_IV_DEFAULT)
        }
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
        return cipher.doFinal(message)
    }


    static SecretKeySpec generateKeySpec(final String key) {
        final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM)
        digest.update(key.getBytes(CHARSET))
        byte[] keyBytes = digest.digest()
        return new SecretKeySpec(keyBytes, "AES")
    }
}