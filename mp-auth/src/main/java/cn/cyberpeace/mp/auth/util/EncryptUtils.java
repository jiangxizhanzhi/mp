package cn.cyberpeace.mp.auth.util;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Slf4j
public class EncryptUtils {

    private static final String DES_KEY = AuthConstant.PASSWORD_DES_KEY;

    /**
     * desEncrypt
     *
     * @param password
     * @return
     */
    public static String desEncrypt(String password) {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            DESKeySpec desKeySpec = new DESKeySpec(DES_KEY.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(desKeySpec);

            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encryptedBytes);
        } catch (Exception e) {
            log.error("Error during DES encryption", e);
            return null;
        }
    }

    /**
     * desDecrypt
     *
     * @param encryptHex
     * @return
     */
    public static String desDecrypt(String encryptHex) {
        try {
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            DESKeySpec desKeySpec = new DESKeySpec(DES_KEY.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(desKeySpec);

            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(hexToBytes(encryptHex));

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error during DES decryption", e);
            return null;
        }
    }

    /**
     * MD5盐值加密
     * @param salt
     * @param content
     * @return
     */
    public static String md5DigestHex(String salt, String content) {
        String md5Str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] contentBytes = (salt + content).getBytes(StandardCharsets.UTF_8);
            byte[] digestBytes = md.digest(contentBytes);
            md5Str = bytesToHex(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("md5DigestHex error:", e);
        }
        return md5Str;
    }

    /**
     * bytesToHex
     *
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString().toLowerCase();
    }

    /**
     * hexToBytes
     *
     * @param hex
     * @return
     */
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

}
