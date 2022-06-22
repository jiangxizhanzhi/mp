package cn.cyberpeace.mp.base.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Deprecated
 *
 * http工具类
 */
@Slf4j
public class HttpUtils {

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
        return doGetRequest(url, client);
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static HttpResponse sendPostForDownLoad(String url,String body) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        return doPostRequestForDownload(url,body,  client);
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
        httpget.setConfig( requestConfig);
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

        return result;
    }

    public static String doFormDataPost(String url, HttpEntity entity,Map<String,Object> headerMap) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(headerMap)) {
            Header[] headers = headerMap.entrySet().stream().map(v ->
                    new BasicHeader(v.getKey(), String.valueOf(v.getValue()))
            ).toArray(Header[]::new);
            httpPost.setHeaders(headers);
        }
        httpPost.setEntity(entity);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
        httpPost.setConfig(requestConfig);
        HttpResponse response = client.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    public static String doFormDataPut(String url, HttpEntity entity,Map<String,Object> headerMap) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut httpPost = new HttpPut(url);
        if (MapUtils.isNotEmpty(headerMap)) {
            Header[] headers = headerMap.entrySet().stream().map(v ->
                    new BasicHeader(v.getKey(), String.valueOf(v.getValue()))
            ).toArray(Header[]::new);
            httpPost.setHeaders(headers);
        }
        httpPost.setEntity(entity);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60000).setConnectTimeout(60000).build();
        httpPost.setConfig(requestConfig);
        HttpResponse response = client.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    private static HttpResponse doPostRequestForDownload(String url, String body,HttpClient client) throws IOException {
        // 替换url中的空格
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setEntity(entity);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpPost.setConfig( requestConfig);
        HttpResponse resp = client.execute(httpPost);

        // 获得返回值
        return resp;
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







