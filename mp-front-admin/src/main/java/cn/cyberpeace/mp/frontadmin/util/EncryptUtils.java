package cn.cyberpeace.mp.frontadmin.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


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
