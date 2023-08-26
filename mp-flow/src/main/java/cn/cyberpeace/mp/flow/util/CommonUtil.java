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


    /**
     * 获取资源服务url
     * @param host
     * @return
     */
    public static String getResourcetUrl(String host) {
        return "http://" + host + "/api/v1/resource";
    }

    /**
     * 获取md5值
     * @param input
     * @return
     */
    public static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            log.error("calculate MD5 error:", e);
        }
        return null;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
