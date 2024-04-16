package com.lunar.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.security.Security;

/**
 * @author szx
 * @date 2022/11/25 18:40
 */
@Slf4j
public class AESUtil {

    private static final String CHARSET_NAME = "UTF-8";
    private static final String AES_NAME = "AES";

    /**
     * 加密模式 PKCS5Padding是限制块大小的PKCS7Padding
     */
    public static final String ALGORITHM_PKCS5Padding = "AES/CBC/PKCS5Padding";
    /**
     * 加密模式
     */
    public static final String ALGORITHM_PKCS7Padding = "AES/ECB/PKCS7Padding";

    //    // 密钥
//    public static final String KEY = "";
//    // 偏移量
//    public static final String IV = "qwe1231231231231";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密
     * <p>
     * AES/ECB/PKCS5Padding
     *
     * @param content
     * @return
     */
    public static String encryptPKCS5Padding(@NotNull String key, @NotNull String content, @NotNull String iv) {
        byte[] result = null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5Padding);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            result = cipher.doFinal(content.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            log.error("aes加密失败", e);
        }
        return Base64.encodeBase64String(result);
    }

    /**
     * 解密
     * <p>
     * AES/ECB/PKCS5Padding
     *
     * @param key
     * @param content
     * @param iv
     * @return
     * @throws Exception
     */
    public static String decryptPKCS5Padding(@NotNull String key, @NotNull String content,
                                             @NotNull String iv) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
        Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5Padding);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
        return new String(cipher.doFinal(java.util.Base64.getDecoder().decode(content.getBytes())));
    }

    /**
     * 加密
     * <p>
     * AES/ECB/PKCS7Padding
     *
     * @param content
     * @return
     */
    public static String encryptPKCS7Padding(@NotNull String key, @NotNull String content) {
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS7Padding);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
//            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            result = cipher.doFinal(content.getBytes(CHARSET_NAME));
        } catch (Exception e) {
            log.error("aes加密失败", e);
        }
        return Base64.encodeBase64String(result);
    }

    /**
     * 解密
     * <p>
     * AES/ECB/PKCS7Padding
     *
     * @param content
     * @return
     */
    public static String decryptPKCS7Padding(@NotNull String key, @NotNull String content) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS7Padding);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(CHARSET_NAME), AES_NAME);
//            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
//            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return new String(cipher.doFinal(Base64.decodeBase64(content)), CHARSET_NAME);
        } catch (Exception e) {
            log.error("aes解密失败", e);
        }
        return StringUtils.EMPTY;
    }

    public static void main(String[] args) throws Exception {
        String key = "g98uezzumgv2wsbs";
        String iv = "qwe1231231231231";
//        String encrypt = "YID5bIAxvM7ZJDIGFmPZFQQOD3Bhbm0Xh4fxc4m/hymNSZSi7NeWKKUOlcejigbo";
//
////        String decrypt =
////                AESUtil.decryptPKCS5Padding("YID5bIAxvM7ZJDIGFmPZFQQOD3Bhbm0Xh4fxc4m/hymNSZSi7NeWKKUOlcejigbo".getBytes(), key.getBytes(), iv.getBytes());
//        String decrypt = AESUtil.decryptPKCS5Padding(key, encrypt, iv);
//        System.out.println(decrypt);

        String decrypt = "{\"username\":\"admin\",\"password\":\"abcd123456\"}";
        String s1 = AESUtil.encryptPKCS5Padding(key, decrypt, iv);
        System.out.println(s1);

        String s2 = AESUtil.decryptPKCS5Padding(key, s1, iv);
//        String s2 = AESUtil.decryptPKCS5Padding(contents.getBytes(), key.getBytes(), iv.getBytes());
        System.out.println(s2);

    }

}
