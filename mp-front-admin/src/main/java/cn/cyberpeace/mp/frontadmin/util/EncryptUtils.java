 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EncryptUtils.java.
 // File size 688.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.util;

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
