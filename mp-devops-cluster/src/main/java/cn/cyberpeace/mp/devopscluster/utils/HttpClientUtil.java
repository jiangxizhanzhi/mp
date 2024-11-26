package cn.cyberpeace.mp.devopscluster.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class HttpClientUtil {
    private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    private final static String DEFAULT_ENCODE = "UTF-8";
    /**
     * 默认 10s 超时
     */
    private static final int TIME_OUT = 10 * 1000;

    private HttpClientUtil() {
    }


    /**
     * 忽略 ssl
     *
     * @return
     */
    private static SSLContext buildIgnoreContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().setProtocol("TLSv1.2").build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error(e.getMessage(), e);
        }

        return sslContext;
    }

    private static CloseableHttpClient getClient(int timeOut) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .setSocketTimeout(timeOut)
                .build();

        SSLContext sslContext = buildIgnoreContext();
        // 注册
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(registry);
        return HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    /**
     * POST application/json请求
     *
     * @param url     请求地址
     * @param jsonStr 请求数据json字符串
     * @param headers 请求头
     * @param timeOut 超时时间
     * @return
     */
    public static String sendPostJson(String url, String jsonStr, Map<String, String> headers, int timeOut) {
        HttpPost post = new HttpPost(url);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                post.setHeader(entry.getKey(), entry.getValue());
            }
        }
        StringEntity entity = new StringEntity(jsonStr, DEFAULT_ENCODE);
        entity.setContentEncoding(DEFAULT_ENCODE);
        entity.setContentType("application/json;charset=" + DEFAULT_ENCODE);
        post.setEntity(entity);
        return execute(post, timeOut);
    }

    public static String sendDelete(String url, Map<String, String> headers, int timeOut) {

        HttpDelete httpDelete = new HttpDelete(url);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpDelete.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(httpDelete, timeOut);
    }

    public static String sendDelete(String url, Map<String, String> headers) {

        return sendDelete(url, headers, TIME_OUT);

    }

    /**
     * POST application/json请求
     *
     * @param url     请求地址
     * @param jsonStr 请求数据json字符串
     * @param headers 请求头
     * @return
     */
    public static String sendPostJson(String url, String jsonStr, Map<String, String> headers) {
        return sendPostJson(url, jsonStr, headers, TIME_OUT);
    }

    /**
     * POST application/json请求
     *
     * @param url     请求地址
     * @param jsonStr 请求数据json字符串
     * @param timeOut 超时时间
     * @return
     */
    public static String sendPostJson(String url, String jsonStr, int timeOut) {
        return sendPostJson(url, jsonStr, new HashMap<>(0), timeOut);
    }

    /**
     * POST application/json请求
     *
     * @param url     请求地址
     * @param jsonStr 请求数据json字符串
     * @return
     */
    public static String sendPostJson(String url, String jsonStr) {
        return sendPostJson(url, jsonStr, TIME_OUT);
    }

    /**
     * POST application/json请求
     *
     * @param url  请求地址
     * @param data 请求数据
     * @return
     */
    public static String sendPostJson(String url, Object data) {
        if (data == null) {
            data = new HashMap(0);
        }
        return sendPostJson(url, JSON.toJSONString(data));
    }

    /**
     * POST application/json请求
     *
     * @param url          请求地址
     * @param jsonStr      请求数据json字符串
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T postJsonForObject(String url, String jsonStr, Class<T> responseType) {
        String result = sendPostJson(url, jsonStr);
        if (StringUtils.isNotBlank(result)) {
            return JSONObject.parseObject(result, responseType);
        } else {
            return null;
        }
    }

    /**
     * POST application/json请求
     *
     * @param url          请求地址
     * @param jsonStr      请求数据json字符串
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T postJsonForObject(String url, String jsonStr, TypeReference<T> responseType) {
        String result = sendPostJson(url, jsonStr);
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, responseType);
        } else {
            return null;
        }
    }

    /**
     * POST application/json请求
     *
     * @param url          请求地址
     * @param data         请求数据
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T postJsonForObject(String url, Object data, Class<T> responseType) {
        if (data == null) {
            data = new HashMap(0);
        }
        return postJsonForObject(url, JSON.toJSONString(data), responseType);
    }

    /**
     * POST application/json请求
     *
     * @param url          请求地址
     * @param data         请求数据
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T postJsonForObject(String url, Object data, TypeReference<T> responseType) {
        if (data == null) {
            data = new HashMap(0);
        }
        return postJsonForObject(url, JSON.toJSONString(data), responseType);
    }


    /**
     * POST application/x-www-form-urlencoded 请求
     *
     * @param url    请求地址
     * @param params 请求数据map
     * @return
     */
    public static String sendPostForm(String url, Map<String, String> params) {
        return sendPostForm(url, params, new HashMap<>(0));
    }

    /**
     * POST application/x-www-form-urlencoded 请求
     *
     * @param url     请求地址
     * @param params  请求数据map
     * @param timeOut 超时时间
     * @return
     */
    public static String sendPostForm(String url, Map<String, String> params, int timeOut) {
        Map<String, String> headers = new HashMap<>(1);
        return sendPostForm(url, params, headers, timeOut);
    }

    /**
     * POST application/x-www-form-urlencoded 请求
     *
     * @param url     请求地址
     * @param params  请求数据map
     * @param headers 请求头
     * @return
     */
    public static String sendPostForm(String url, Map<String, String> params, Map<String, String> headers) {
        return sendPostForm(url, params, headers, TIME_OUT);
    }

    /**
     * POST application/x-www-form-urlencoded 请求
     *
     * @param url     请求地址
     * @param params  请求数据map
     * @param headers 请求头
     * @param timeOut 超时时间
     * @return
     */
    public static String sendPostForm(String url, Map<String, String> params, Map<String, String> headers, int timeOut) {
        UrlEncodedFormEntity reqEntity = createFormEntity(params);
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (StringUtils.equalsAnyIgnoreCase
                        (entry.getKey(), HttpHeaders.CONTENT_LENGTH, HttpHeaders.CONTENT_TYPE)) {
                    continue;
                }
                httppost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        httppost.setEntity(reqEntity);
        return execute(httppost, timeOut);
    }

    /**
     * POST application/x-www-form-urlencoded 请求
     *
     * @param url          请求地址
     * @param params       请求数据map
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T postFormForObject(String url, Map<String, String> params, Class<T> responseType) {
        String result = sendPostForm(url, params);
        if (StringUtils.isNotBlank(result)) {
            return JSONObject.parseObject(result, responseType);
        } else {
            return null;
        }
    }

    /**
     * POST application/x-www-form-urlencoded 请求
     *
     * @param url          请求地址
     * @param params       请求数据map
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T postFormForObject(String url, Map<String, String> params, TypeReference<T> responseType) {
        String result = sendPostForm(url, params);
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, responseType);
        } else {
            return null;
        }
    }

    /**
     * GET 请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param timeOut 超时时间
     * @return
     */
    public static String sendGet(String url, Map<String, String> params, int timeOut) {
        return sendGet(url, params, new HashMap<>(1), timeOut);
    }

    /**
     * GET 请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return
     */
    public static String sendGet(String url, Map<String, String> params) {
        return sendGet(url, params, new HashMap<>(1));
    }

    /**
     * GET 请求
     *
     * @param url     url
     * @param params  请求参数
     * @param headers 请求头
     * @param timeOut 超时时间
     * @return
     */
    public static String sendGet(String url, Map<String, String> params, Map<String, String> headers, int timeOut) {
        if (url == null) {
            return null;
        }
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            if (null != params) {
                uriBuilder.setParameters(getNameValuePairList(params));
            }
            URI uri = uriBuilder.build();
            String rawQueryString = uri.getRawQuery();
            //拼接url
            if (StringUtils.isNotBlank(rawQueryString)) {
                // 防止原本url里面就有参数
                if (!url.contains("?")) {
                    url = url + "?";
                }
                if (url.endsWith("?")) {
                    url = url + rawQueryString;
                } else {
                    url = url + "&" + rawQueryString;
                }
            }
            HttpGet httpGet = new HttpGet(url);
            if (headers != null) {
                Set<Map.Entry<String, String>> entrySet = headers.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            return execute(httpGet, timeOut);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * GET 请求
     *
     * @param url     请求地址
     * @param params  请求参数
     * @param headers 请求头
     * @return
     */
    public static String sendGet(String url, Map<String, String> params, Map<String, String> headers) {
        return sendGet(url, params, headers, TIME_OUT);
    }

    /**
     * GET 请求
     *
     * @param url          请求地址
     * @param params       请求参数
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T getForObject(String url, Map<String, String> params, Class<T> responseType) {
        String result = sendGet(url, params);
        if (StringUtils.isNotBlank(result)) {
            return JSONObject.parseObject(result, responseType);
        } else {
            return null;
        }
    }

    /**
     * GET 请求
     *
     * @param url          请求地址
     * @param params       请求参数
     * @param responseType 返回值类型
     * @return
     * @author lizhenjiang
     * @date 2020/05/30
     */
    public static <T> T getForObject(String url, Map<String, String> params, TypeReference<T> responseType) {
        String result = sendGet(url, params);
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseObject(result, responseType);
        } else {
            return null;
        }
    }

    private static List<NameValuePair> getNameValuePairList(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<>();
        try {
            if (params != null && !params.isEmpty()) {
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    if (value != null) {
                        list.add(new BasicNameValuePair(key, value));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    private static UrlEncodedFormEntity createFormEntity(Map<String, String> pram) {
        try {
            List<NameValuePair> formParams = getNameValuePairList(pram);
            return new UrlEncodedFormEntity(formParams, DEFAULT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private static String execute(HttpRequestBase requestBase, int timeOut) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = getClient(timeOut);
        try {

            // 执行
            long start = System.currentTimeMillis();
            response = httpClient.execute(requestBase);
            log.debug("请求id: {} , url: {} , 耗时: {} ", requestBase.getURI().toString(), (System.currentTimeMillis() - start));
            HttpEntity entity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(entity.getContent(), DEFAULT_ENCODE));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            EntityUtils.consume(entity);

        } catch (Exception e) {
            log.error("远程调用异常", e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                log.error("", e);
            }

        }
        return sb.toString();
    }


    /**
     * POST   multipart/form-data  请求
     *
     * @param requestUrl
     * @param body
     * @return
     */
    public static String sendPostWithFile(String requestUrl, RequestBody body) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(requestUrl)
                    .method("POST", body)
                    .addHeader("Content-Type", "multipart/form-data")
                    .build();
            Response response = client.newCall(request).execute();
            if (response.body() == null) {
                log.info("短信发送httpClent获取数据为空");
                return null;
            }
            log.info("from-data:" + response.body().toString());
            return response.body().string();
        } catch (Exception e) {
            log.info("******短信发送httpClent  请求出错****" + e.getMessage());
        } finally {

        }
        return null;
    }
}
