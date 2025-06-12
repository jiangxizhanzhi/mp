 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HttpUtils.java.
 // File size 24032.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.util;

import cn.cyberpeace.mp.base.util.http.CustomPostRedirectStrategy;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName HttpUtils
 * @Description http工具类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class HttpUtils {

    /**
     * 默认超时时间
     */
    private static final Integer DEFAULT_TIME_OUT = 600 * 1000;

    /**
     * 发送一个Post请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String sendPost(String url, String content) throws Exception {
        // 信任所有证书
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();
        // 支持重定向
        HttpClient client = HttpClientBuilder.create().setRedirectStrategy(new CustomPostRedirectStrategy())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)).build();
        return doPost(url, content, client);
    }

    /**
     * post请求带header
     * @param url
     * @param formMap
     * @param headers
     * @return
     * @throws IOException
     */
    public static String sendHttpPost(String url, Map<String, Object> formMap, Map<String, String> headers) throws IOException {
        log.info("=======sendHttpPost url: {}", url);
        HttpPost httpPost = new HttpPost(url);
        // header
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
        // form参数
        List<NameValuePair> nvps = new ArrayList<>();
        for (Map.Entry<String, Object> entry : formMap.entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                nvps.add(new BasicNameValuePair(key, value));
            }
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        String result = send(httpPost);
        log.info("=======sendHttpPost response: {}", result);
        return result;
    }

    /**
     * http post请求
     * @param url
     * @param body
     * @param headers
     * @return
     * @throws IOException
     */
    public static String sendHttpPost(String url, String body, Map<String, String> headers) throws IOException {
        log.info("=======sendHttpPost url: {},  body: {}", url, body);
        long startTime = System.currentTimeMillis();

        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(DEFAULT_TIME_OUT * 1000);
            connection.setReadTimeout(DEFAULT_TIME_OUT * 1000);

            // Set request headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.setDoOutput(true);
            if (StringUtils.isNotBlank(body)){
                connection.setRequestProperty("Content-Type", "application/json");
            }

            try (OutputStream os = connection.getOutputStream()) {
                if (StringUtils.isNotBlank(body)){
                    os.write(body.getBytes());
                }
                os.flush();
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                long callTime = System.currentTimeMillis() - startTime;
                if (log.isDebugEnabled()) {
                    log.debug("=======sendHttpPost callTime [{}] response: {}", callTime, response);
                }
                if (log.isInfoEnabled()) {
                    log.info("=======sendHttpPost callTime [{}] response success", callTime);
                }
                return response.toString();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    /**
     * http put请求
     * @param url
     * @param formMap
     * @param headers
     * @return
     * @throws IOException
     */
    public static String sendHttpPut(String url, Map<String, Object> formMap, Map<String, String> headers) throws IOException {
        log.info("=======sendHttpPut url: {}", url);
        HttpPut httpPut = new HttpPut(url);
        // header
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPut.addHeader(entry.getKey(), entry.getValue());
        }
        // form参数
        List<NameValuePair> nvps = new ArrayList<>();
        for (Map.Entry<String, Object> entry : formMap.entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                nvps.add(new BasicNameValuePair(key, value));
            }
        }
        httpPut.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
        String result = send(httpPut);
        log.info("=======sendHttpPut response: {}", result);
        return result;
    }

    /**
     * http put请求
     * @param url
     * @param body
     * @param headers
     * @return
     * @throws IOException
     */
    public static String sendHttpPut(String url, String body, Map<String, String> headers) throws IOException {
        log.info("=======sendHttpPut url: {}", url);

        HttpPut httpPut = new HttpPut(url);
        // header
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPut.addHeader(entry.getKey(), entry.getValue());
        }
        StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPut.setEntity(entity);

        String result = send(httpPut);
        log.info("=======sendHttpPut response: {}", result);
        return result;
    }

    /**
     * http delete请求
     * @param url
     * @param body
     * @param headers
     * @return
     * @throws IOException
     */
    public static String sendHttpDelete(String url, String body, Map<String, String> headers) throws IOException {
        log.info("=======sendHttpDelete url: {} , body {}", url, body);

        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        // header
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpDelete.addHeader(entry.getKey(), entry.getValue());
        }
        // body参数
        StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpDelete.setEntity(entity);

        String result = send(httpDelete);
        log.info("=======sendHttpDelete response: {}", result);
        return result;
    }

    /**
     * http get请求
     * @param url
     * @param headers
     * @param timeout
     * @return
     * @throws IOException
     */
    public static String sendHttpGet(String url, Map<String, String> headers, Integer timeout) throws IOException {
        log.info("=======sendHttpGet url: {}", url);
        HttpURLConnection connection = null;
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setConnectTimeout(DEFAULT_TIME_OUT * 1000);
            connection.setReadTimeout(DEFAULT_TIME_OUT * 1000);
            connection.setRequestMethod("GET");

            // Set request headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            if (Objects.nonNull(timeout) && timeout > 0) {
                connection.setConnectTimeout(timeout);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                log.info("=======sendHttpGet response: {}", response);
                return response.toString();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * post请求带文件
     * @param url
     * @param headers
     * @param fileList
     * @return
     * @throws IOException
     */
    public static String sendHttpPostWithFile(String url, Map<String, String> headers, List<File> fileList) throws IOException {
        log.info("=======sendHttpPostWithFile url: {}", url);

        HttpPost httpPost = new HttpPost(url);
        // header
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
        // body参数
        var multipartEntityBuilder = MultipartEntityBuilder.create();
        for (File file : fileList) {
            multipartEntityBuilder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        }
        var entity = multipartEntityBuilder.build();
        httpPost.setEntity(entity);

        String result = send(httpPost);
        log.info("=======sendHttpPostWithFile response: {}", result);
        return result;
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

    public static String sendPatch(String url, String content, Map<String, String> headers) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        log.info("请求地址================>{}", url);
        log.info("请求参数================>{}", content);
        HttpPatch httpPatch = new HttpPatch(url);
        // 解决中文乱码问题
        StringEntity entity = new StringEntity(content, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPatch.setEntity(entity);

        if (Objects.nonNull(headers) && !headers.isEmpty()) {
            headers.forEach(httpPatch::addHeader);
        }

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
        return doGetRequest(url, client);
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

    public static byte[] downloadFileToByteArray(String fileUrl)  {
        // 创建 HttpClient 实例
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // 创建 HTTP GET 请求
            HttpGet httpGet = new HttpGet(fileUrl);

            // 发送请求并获取响应
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {

                // 获取响应实体
                HttpEntity entity = response.getEntity();

                // 检查响应状态
                if (response.getStatusLine().getStatusCode() == 200) {
                    // 使用 ByteArrayOutputStream 将文件内容转换为字节数组
                    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        entity.writeTo(outputStream);
                        return outputStream.toByteArray();
                    }
                } else {
                    throw new IOException("下载失败，HTTP 状态码: " + response.getStatusLine().getStatusCode());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        HttpDelete httpDelete = new HttpDelete(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        HttpResponse response = client.execute(httpDelete);
        httpDelete.setConfig(requestConfig);
        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

    /**
     * 删除请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendDelete(String url, String content) throws IOException {
        log.info("请求地址================>{}", url);
        log.info("请求参数================>{}", content);
        HttpClient client = HttpClientBuilder.create().build();
        StringEntity entity = new StringEntity(content, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        httpDelete.setEntity(entity);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        HttpResponse response = client.execute(httpDelete);
        httpDelete.setConfig(requestConfig);
        var res = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        log.info("结果================>{}", res);
        return res;
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
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(userName, password);
        credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
        CloseableHttpClient client =
                HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        HttpGet httpGet = new HttpGet(url);

        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept", "*/*");
        return client.execute(httpGet);
    }

    public static String postFileV2(String url, byte[] fileByte, String fileField, JSONObject jsonObj) {
        log.info("请求地址================>{}", url);
        String result = StringUtils.EMPTY;
        // 向流量发生平台导入pcap包
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60
                    , TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS).build();
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
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                log.error("post file error:{}", response.message());
                result = new JSONObject().toJSONString();
            }
        } catch (Exception e) {
            log.error("post file error", e);
        } finally {
            IOUtils.closeQuietly(response);
        }
        log.info("结果================>{}", result);
        return result;
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
    public static String postFile(String url, byte[] fileByte, String fileField, JSONObject jsonObj) throws IOException {
        log.info("请求地址================>{}", url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", ContentType.MULTIPART_FORM_DATA.getMimeType());

        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        String fileName = "";
        // 请求参数
        if (Objects.nonNull(jsonObj)) {
            log.info("请求参数================>{}", jsonObj.toJSONString());
            fileName = jsonObj.getString("fileName");
            for (Object key : jsonObj.keySet()) {
                entityBuilder.addTextBody(key.toString(), jsonObj.getString(key.toString()));
            }
        } else {
            log.info("请求参数================> jsonObj = null");
        }
        entityBuilder.addBinaryBody(fileField, fileByte, ContentType.MULTIPART_FORM_DATA, fileName);
        httpPost.setEntity(entityBuilder.build());
        String result = send(httpPost);
        log.info("=======postFile response: {}", result);
        return result;
    }

    private static String send(HttpRequestBase httpRequest) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(DEFAULT_TIME_OUT * 1000)
                .setConnectTimeout(DEFAULT_TIME_OUT * 1000)
                .setConnectionRequestTimeout(DEFAULT_TIME_OUT * 1000)
                .build();

        CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpResponse resp = null;
        resp = client.execute(httpRequest);
        String result = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
        return result;
    }

    private static String doGetRequest(String url, HttpClient client) throws IOException {
        // 替换url中的空格
        url = url.replace(" ", "%20");
        HttpGet httpget = new HttpGet(url);
        HttpResponse resp = client.execute(httpget);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpget.setConfig( requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
    }

    private static CloseableHttpClient withDigestAuth(String userName, String password) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(userName, password);
        credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
        CloseableHttpClient client =
                HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        return client;
    }

    private static String doPost(String url, String content, HttpClient client) throws IOException {
        log.info("请求地址================>{}", url);
        log.info("请求参数================>{}", content);
        HttpPost httpPost = new HttpPost(url);

        // 解决中文乱码问题
        StringEntity entity = new StringEntity(content, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setEntity(entity);

        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpPost.setConfig(requestConfig);

        // 执行请求并确保资源释放
        try {
            HttpResponse response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } finally {
            httpPost.releaseConnection(); // 释放连接资源
        }
    }

    static class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }

        public HttpDeleteWithBody(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        public HttpDeleteWithBody(final URI uri) {
            super();
            setURI(uri);
        }

        public HttpDeleteWithBody() {
            super();
        }
    }
}







