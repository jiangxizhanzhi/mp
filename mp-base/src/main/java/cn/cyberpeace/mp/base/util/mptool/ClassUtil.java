package cn.cyberpeace.mp.base.util.mptool;

import lombok.extern.slf4j.Slf4j;

import java.net.URL;

/**
 * @ClassName ClassUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class ClassUtil {

    /**
     * getClassPath
     *
     * @return
     */
    public static String getClassPath() {
        final URL classPathURL = getClassPathURL();
        String url = URLUtil.getDecodedPath(classPathURL);
        return FileUtil.normalize(url);
    }

    /**
     * getClassPathURL
     *
     * @return
     */
    public static URL getClassPathURL() {
        try {
            return getResourceURL(StrUtil.EMPTY);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * getResourceURL
     *
     * @param resource
     * @return
     */
    public static URL getResourceURL(String resource) {
        return ResourceUtil.getResource(resource, null);
    }
}
