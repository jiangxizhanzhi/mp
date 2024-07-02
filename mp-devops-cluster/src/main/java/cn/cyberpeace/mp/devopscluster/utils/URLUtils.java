package cn.cyberpeace.mp.devopscluster.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName URLUtils
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
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
