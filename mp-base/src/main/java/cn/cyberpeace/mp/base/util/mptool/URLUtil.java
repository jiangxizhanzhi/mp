package cn.cyberpeace.mp.base.util.mptool;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

public class URLUtil {

    /**
     * 将Map形式的Form表单数据转换为Url参数形式
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
        }
        return queryBuilder.toString();
    }

    public static URL url(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
        }
        return null;
    }

    public static String getPath(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            return path;
        } catch (MalformedURLException e) {
        }
        return null;
    }
}
