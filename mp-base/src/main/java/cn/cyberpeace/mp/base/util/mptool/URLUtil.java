package cn.cyberpeace.mp.base.util.mptool;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
public class URLUtil {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String FILE_URL_PREFIX = "file:";

    /**
     * 将Map形式的Form表单数据转换为Url参数形式
     *
     * @param params
     * @param charset
     * @return
     */
    public static String buildQuery(Map<String, Object> params, Charset charset) {
        StringBuilder queryBuilder = new StringBuilder();
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String name = URLEncoder.encode(entry.getKey(), charset);
                String value = URLEncoder.encode(String.valueOf(entry.getValue()), charset);
                queryBuilder.append(name).append('=').append(value).append('&');
            }
            if (queryBuilder.length() > 0) {
                queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            }
        } catch (Exception e) {
            // 处理异常
            log.error(e.getMessage());
        }
        return queryBuilder.toString();
    }


    /**
     * 通过一个字符串形式的URL地址创建URL对象
     * Params:
     * url – URL
     */
    public static URL url(String url) {
        return url(url, null);
    }

    public static URL url(String url, URLStreamHandler handler) {
        // 兼容Spring的ClassPath路径
        if (url.startsWith(CLASSPATH_URL_PREFIX)) {
            url = url.substring(CLASSPATH_URL_PREFIX.length());
            return ResourceUtil.getClassLoader().getResource(url);
        }

        try {
            return new URL(null, url, handler);
        } catch (MalformedURLException e) {
            // 尝试文件路径
            try {
                return new File(url).toURI().toURL();
            } catch (MalformedURLException ex2) {
                log.error(ex2.getMessage());
                return null;
            }
        }
    }

    /**
     * 获得path部分
     * Params:
     * uriStr – URI路径
     */
    public static String getPath(String urlString) {
        try {
            return toURI(urlString, false).getPath();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    public static String getDecodedPath(URL url) {
        if (null == url) {
            return null;
        }

        String path = null;
        try {
            // URL对象的getPath方法对于包含中文或空格的问题
            path = toURI(url).getPath();
        } catch (Exception e) {
            // ignore
            log.error(e.getMessage());
        }
        return (null != path) ? path : url.getPath();
    }

    public static URI toURI(URL url) throws Exception {
        return toURI(url, false);
    }

    public static URI toURI(URL url, boolean isEncode) throws Exception {
        if (null == url) {
            return null;
        }

        return toURI(url.toString(), isEncode);
    }

    public static URI toURI(String location, boolean isEncode) throws Exception {
        try {
            return new URI(StrUtil.trim(location));
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            throw new Exception(e);
        }
    }


}
