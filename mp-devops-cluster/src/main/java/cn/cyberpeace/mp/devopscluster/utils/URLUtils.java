package cn.cyberpeace.mp.devopscluster.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Description: URL工具类
 * @Author qiaokang
 * @Date：2023/8/17 15:33
 */
@Slf4j
public class URLUtils {

    public static URL url(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            log.error("generate url error:", e);
        }

        return url;
    }
}
