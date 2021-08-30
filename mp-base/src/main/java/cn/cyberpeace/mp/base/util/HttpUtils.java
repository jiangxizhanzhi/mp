package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * http工具类
 */
@Slf4j
public class HttpUtils {

    private HttpClient httpClient;

    private RequestConfig requestConfig;

    private static final HttpUtils INSTANCE = new HttpUtils();

    private HttpUtils() {
        httpClient = HttpClients.custom().disableAutomaticRetries().build();;
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

    private static String doPost(String url, String content, HttpClient client) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        // 解决中文乱码问题
        StringEntity entity = new StringEntity(content, "UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        HttpResponse resp = client.execute(httpPost);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpPost.setConfig(requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), "UTF-8");
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
        return doGetRequest(url,headerMap, Objects.nonNull(INSTANCE.httpClient) ? INSTANCE.httpClient : client);
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
        return EntityUtils.toString(resp.getEntity(), "UTF-8");
    }

    private static String doGetRequest(String url, Map<String,String> headerMap, HttpClient client) throws IOException {
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
        return EntityUtils.toString(resp.getEntity(), "UTF-8");
    }

    /**
     * 删除请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendDelete(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpDelete httpDelete = new HttpDelete(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        HttpResponse response = client.execute(httpDelete);
        httpDelete.setConfig(requestConfig);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
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
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        var httpGet = new HttpGet(url);

        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept", "*/*");
        return client.execute(httpGet);
    }

    private static CloseableHttpClient withDigestAuth(String userName, String password) {
        var credentialsProvider = new BasicCredentialsProvider();
        var usernamePasswordCredentials = new UsernamePasswordCredentials(userName, password);
        credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialsProvider).build();
        return client;
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


    public static String doFormDataPost(String url,final File file) throws IOException {
        HttpClient client = INSTANCE.httpClient;
        HttpPost httpPost = new HttpPost(url);

        //创建 MultipartEntityBuilder,以此来构建我们的参数
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//        ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
//        entityBuilder.addPart("name", new StringBody("value",contentType));
//        entityBuilder.addPart("name1", new StringBody("value1",contentType));
//        entityBuilder.addPart("name2", new StringBody("value2",contentType));
        entityBuilder.addBinaryBody("image", file);

        httpPost.setEntity(entityBuilder.build());
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


