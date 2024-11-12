 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name URLUtil.java.
 // File size 9184.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util.mptool;

 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.codec.binary.StringUtils;
 import org.apache.commons.codec.net.URLCodec;

 import java.io.File;
 import java.lang.reflect.Array;
 import java.net.MalformedURLException;
 import java.net.URI;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.net.URLStreamHandler;
 import java.nio.charset.Charset;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.BitSet;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

/**
 * @ClassName URLUtil
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class URLUtil {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";
    public static final String FILE_URL_PREFIX = "file:";

    /**
     * gen-delims = ":" / "/" / "?" / "#" / "[" / "]" / "@"
     */
    public static final String GEN_DELIMS = ":/?#[]@";

    /**
     * sub-delims = "!" / "$" / "{@code &}" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     */
    public static final String SUB_DELIMS = "!$&'()*+,;=";

    /**
     * reserved = gen-delims / sub-delims<br>
     * see：<a href="https://www.ietf.org/rfc/rfc3986.html#section-2.2">https://www.ietf.org/rfc/rfc3986.html#section-2.2</a>
     */
    public static final String RESERVED = GEN_DELIMS +SUB_DELIMS;
    /**
     * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"<br>
     * see: <a href="https://www.ietf.org/rfc/rfc3986.html#section-2.3">https://www.ietf.org/rfc/rfc3986.html#section-2.3</a>
     */
    public static final String UNRESERVED = unreservedChars().toString();
    /**
     * pchar = unreserved / pct-encoded / sub-delims / ":" / "@"
     */
    public static final String PCHAR = UNRESERVED + SUB_DELIMS + ":@";

    /**
     * query = pchar / "/" / "?"
     */
    public static final String QUERY = PCHAR +"/?";

    /**
     * query中的value<br>
     * value不能包含"{@code &}"，可以包含 "="
     */
    public static final String QUERY_PARAM_VALUE = QUERY.replace("&","");

    /**
     * query中的key<br>
     * key不能包含"{@code &}" 和 "="
     */
    public static final String QUERY_PARAM_NAME = QUERY.replace("=","");


    public static final BitSet QUERY_PARAM_VALUE_SET = buildBitSet(QUERY_PARAM_VALUE);
    public static final BitSet QUERY_PARAM_NAME_SET = buildBitSet(QUERY_PARAM_NAME);

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
                String name = encodeKey(entry.getKey(), charset);
                String value = encodeValue(toStr(entry.getValue()), charset);
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

    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    /**
     * encodeKey
     *
     * @param key
     * @param charset
     * @return
     */
    private static String encodeKey(String key, Charset charset) {
        return StringUtils.newStringUsAscii(URLCodec.encodeUrl(QUERY_PARAM_NAME_SET, key.getBytes(charset)));
    }

    /**
     * encodeValue
     *
     * @param value
     * @param charset
     * @return
     */
    private static String encodeValue(String value, Charset charset) {
        return StringUtils.newStringUsAscii(URLCodec.encodeUrl(QUERY_PARAM_VALUE_SET, value.getBytes(charset)));
    }

    /**
     * toStr
     *
     * @param value
     * @return
     */
    private static  String toStr(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof CharSequence) {
            return value.toString();
        }

        if (value instanceof Collection) {
            Collection<?> items = (Collection<?>) value;
            List<String> values = new ArrayList<>();
            for (Object item : items) {
                String str = toStr(item);
                values.add(str);
            }
            return org.apache.commons.lang3.StringUtils.join(values, ",");
        }

        if (value.getClass().isArray()) {
            int size = Array.getLength(value);
            Object[] objects = new Object[size];
            for(int i=0;i<size;i++){
                objects[i] = Array.get(value, i);
            }
            List<String> values = new ArrayList<>();
            for (Object object : objects) {
                String str = toStr(object);
                values.add(str);
            }
            return org.apache.commons.lang3.StringUtils.join(values, ",");
        }
        return value.toString();
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

    /**
     * getDecodedPath
     *
     * @param url
     * @return
     */
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

    /**
     * toURI
     *
     * @param url
     * @return
     */
    public static URI toURI(URL url) throws Exception {
        return toURI(url, false);
    }

    /**
     * toURI
     *
     * @param url
     * @param isEncode
     * @return
     */
    public static URI toURI(URL url, boolean isEncode) throws Exception {
        if (null == url) {
            return null;
        }

        return toURI(url.toString(), isEncode);
    }

    /**
     * toURI
     *
     * @param location
     * @param isEncode
     * @return
     */
    public static URI toURI(String location, boolean isEncode) throws Exception {
        try {
            return new URI(StrUtil.trim(location));
        } catch (URISyntaxException e) {
            log.error(e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * unreservedChars
     *
     * @return
     */
    private static StringBuilder unreservedChars() {
        StringBuilder sb = new StringBuilder();

        // ALPHA
        for (char c = 'A'; c <= 'Z'; c++) {
            sb.append(c);
        }
        for (char c = 'a'; c <= 'z'; c++) {
            sb.append(c);
        }

        // DIGIT
        for (char c = '0'; c <= '9'; c++) {
            sb.append(c);
        }

        // "-" / "." / "_" / "~"
        sb.append("_.-~");

        return sb;
    }

    /**
     * buildBitSet
     *
     * @param value
     * @return
     */
    private static BitSet buildBitSet(String value){
        BitSet set = new BitSet(value.length());
        for(int i=0;i<value.length();i++){
            set.set(value.charAt(i));
        }
        return set;
    }

    /**
     * main
     *
     * @param args
     */
    public static void main(String[] args) {
        Map<String,Object> params=new HashMap<>();
        params.put("name",12345);
        String[] ports = new String[]{"0acade7c-9c4b-4ad6-96e4-aa587494d67f","1e12906c-8393-42cb-9776-1f800b3231a4","db460449-085f-4620-8e28-a5bbdf4b703b"};
//        List<String> portList = Arrays.asList(ports);
        params.put("ports", ports);
        System.out.println(URLUtil.buildQuery(params, StandardCharsets.UTF_8));;
    }
}
