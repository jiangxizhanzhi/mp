 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CommonUtil.java.
 // File size 1481.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description: 通用工具类
 * @Author qiaokang
 * @Date：2021/8/25 10:08
 */
@Slf4j
public class CommonUtil {

    /**
     * 获取流量发生器执行地址
     * @param host
     * @return
     */
    public static String getHostUrl(String host) {
        return "http://" + host + "/api/v1/task";
    }


}
