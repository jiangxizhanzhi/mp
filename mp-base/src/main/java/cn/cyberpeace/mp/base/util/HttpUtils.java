package cn.cyberpeace.mp.base.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * http工具类
 */
@Slf4j
public class HttpUtils {

    private static final HttpUtils INSTANCE = new HttpUtils();
    private HttpClient httpClient;
    private RequestConfig requestConfig;

    private HttpUtils() {
        httpClient = HttpClients.custom().disableAutomaticRetries().build();
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(30000)
                .setSocketTimeout(30000).setConnectTimeout(30000).build();// 设置超时时间
    }

    public synchronized static HttpUtils getInstance() {
        return INSTANCE;
    }

    /**
     * 发送一个Post请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String sendPost(String url, String content) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        return doPost(url, content, client);
    }

    /**
     * 对接需要授权
     *
     * @param url
     * @param content
     * @param password
     * @param username
     * @return
     * @throws IOException
     */
    public static String sendPost(String url, String content, String password, String username) throws IOException {
        HttpClient client = withDigestAuth(password, username);
        return doPost(url, content, client);
    }

    /**
     * form形式post
     *
     * @param url
     * @param jsonObject
     * @return
     * @throws IOException
     */
    public static String sendPost(String url, JSONObject jsonObject) throws IOException {
        log.info("请求地址================>{}", url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        List<NameValuePair> params = new ArrayList<>();
        if (Objects.nonNull(jsonObject)) {
            log.info("请求参数================>{}", jsonObject.toJSONString());
            jsonObject.keySet().forEach(key -> params.add(new BasicNameValuePair(key, jsonObject.getString(key))));
        } else {
            log.info("请求参数================>{}", jsonObject);
        }

        httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        HttpResponse resp = client.execute(httpPost);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpPost.setConfig(requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
    }

    private static String doPost(String url, String content, HttpClient client) throws IOException {
        log.info("请求地址================>{}", url);
        log.info("请求参数================>{}", content);
        HttpPost httpPost = new HttpPost(url);
        // 解决中文乱码问题
        StringEntity entity = new StringEntity(content, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setEntity(entity);

        HttpResponse resp = client.execute(httpPost);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpPost.setConfig(requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
    }

    /**
     * patch 请求
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String sendPatch(String url, String content) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        log.info("请求地址================>{}", url);
        log.info("请求参数================>{}", content);
        HttpPatch httpPatch = new HttpPatch(url);
        // 解决中文乱码问题
        StringEntity entity = new StringEntity(content, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPatch.setEntity(entity);

        HttpResponse resp = client.execute(httpPatch);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpPatch.setConfig(requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
    }


    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGet(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        return doGetRequest(url, Objects.nonNull(INSTANCE.httpClient) ? INSTANCE.httpClient : client);
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static HttpResponse sendGetForDownLoad(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        return doGetRequestForDownload(url, Objects.nonNull(INSTANCE.httpClient) ? INSTANCE.httpClient : client);
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGet(String url,Map<String,String> headerMap) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        return doGetRequest(url, headerMap, Objects.nonNull(INSTANCE.httpClient) ? INSTANCE.httpClient : client);
    }


    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGet(String url, String userName, String password) throws IOException {
        HttpClient client = withDigestAuth(userName, password);
        return doGetRequest(url, client);
    }

    private static String doGetRequest(String url, HttpClient client) throws IOException {
        // 替换url中的空格
        url = url.replace(" ", "%20");
        HttpGet httpget = new HttpGet(url);
        HttpResponse resp = client.execute(httpget);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpget.setConfig(Objects.nonNull(INSTANCE.requestConfig) ? INSTANCE.requestConfig : requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
    }

    private static String doGetRequest(String url, Map<String, String> headerMap, HttpClient client) throws IOException {
        // 替换url中的空格
        url = url.replace(" ", "%20");
        HttpGet httpget = new HttpGet(url);
        if (MapUtils.isNotEmpty(headerMap)) {
            Header[] headers = headerMap.entrySet().stream().map(v ->
                    new BasicHeader(v.getKey(), v.getValue())
            ).toArray(Header[]::new);
            httpget.setHeaders(headers);
        }
        HttpResponse resp = client.execute(httpget);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpget.setConfig(Objects.nonNull(INSTANCE.requestConfig) ? INSTANCE.requestConfig : requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
    }

    /**
     * 删除请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendDelete(String url) throws IOException {
        log.info("请求地址================>{}", url);
        HttpClient client = HttpClientBuilder.create().build();
        var httpDelete = new HttpDelete(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        HttpResponse response = client.execute(httpDelete);
        httpDelete.setConfig(requestConfig);
        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

    /**
     * get请求下载文件
     *
     * @param url
     * @return HttpResponse
     * @throws IOException
     */
    public static HttpResponse sendGetDownLoadWithDigestAuth(String url, String userName,
                                                             String password) throws IOException {
        var credentialsProvider = new BasicCredentialsProvider();
        var usernamePasswordCredentials = new UsernamePasswordCredentials(userName, password);
        credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
        CloseableHttpClient client =
                HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        var httpGet = new HttpGet(url);

        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept", "*/*");
        return client.execute(httpGet);
    }

    private static CloseableHttpClient withDigestAuth(String userName, String password) {
        var credentialsProvider = new BasicCredentialsProvider();
        var usernamePasswordCredentials = new UsernamePasswordCredentials(userName, password);
        credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
        CloseableHttpClient client =
                HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        return client;
    }

    /**
     * 发送文件
     *
     * @param url
     * @param fileByte
     * @param jsonObj
     * @param fileField
     * @return
     */
    public static String postFile(String url, byte[] fileByte, String fileField, JSONObject jsonObj) {
        log.info("请求地址================>{}", url);
        String result = StringUtils.EMPTY;
        // 向流量发生平台导入pcap包
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            String fileName = "";

            // 请求参数
            if (Objects.nonNull(jsonObj)) {
                log.info("请求参数================>{}", jsonObj.toJSONString());
                fileName = jsonObj.getString("fileName");
                for (Object key : jsonObj.keySet()) {
                    builder.addFormDataPart(key.toString(), jsonObj.getString(key.toString()));
                }
            } else {
                log.info("请求参数================>{}", jsonObj);
            }

            // 文件对象
            RequestBody body = builder.setType(MultipartBody.FORM)
                    .addFormDataPart(fileField, fileName,
                            RequestBody.create(MediaType.parse(ContentType.APPLICATION_OCTET_STREAM.getMimeType()),
                                    fileByte))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", ContentType.MULTIPART_FORM_DATA.getMimeType())
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                log.error("post file error:{}", response.message());
                result = new JSONObject().toJSONString();
            }
        } catch (Exception e) {
            log.error("post file error", e);
        }

        return result;
    }

    public static String sendPut(String url,String content) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut httpPut = new HttpPut(url);
        // 解决中文乱码问题
        StringEntity entity = new StringEntity(content, "utf-8");
        entity.setContentType("application/json");
        httpPut.setEntity(entity);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
        HttpResponse response = client.execute(httpPut);
        httpPut.setConfig(requestConfig);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    public static String doFormDataPost(String url, HttpEntity entity) throws IOException {
        HttpClient client = INSTANCE.httpClient;
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
        httpPost.setConfig(requestConfig);
        HttpResponse response = client.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    public static String doFormDataPut(String url, HttpEntity entity) throws IOException {
        HttpClient client = INSTANCE.httpClient;
        HttpPut httpPost = new HttpPut(url);
        httpPost.setEntity(entity);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
        httpPost.setConfig(requestConfig);
        HttpResponse response = client.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    private static HttpResponse doGetRequestForDownload(String url, HttpClient client) throws IOException {
        // 替换url中的空格
        HttpGet httpget = new HttpGet(url);
        HttpResponse resp = client.execute(httpget);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpget.setConfig(Objects.nonNull(INSTANCE.requestConfig) ? INSTANCE.requestConfig : requestConfig);
        // 获得返回值
        return resp;
    }

}





