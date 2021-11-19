package cn.cyberpeace.mp.front.admin.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Slf4j
public class EncryptUtils {

    private static final String DES_KEY = "B13H089Y";

    private static DES DES = SecureUtil.des(DES_KEY.getBytes());

    public static String desEncrypt(String password) {
       return DES.encryptHex(password);
    }

    public static String desDecrypt(String encryptHex) {
        return DES.decryptStr(encryptHex);
    }


}
