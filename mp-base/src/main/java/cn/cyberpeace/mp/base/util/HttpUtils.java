package cn.cyberpeace.mp.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * http工具类
 */
@Slf4j
public class HttpUtils {

    private HttpUtils() {
        throw new IllegalStateException("Utility class");
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
        StringEntity entity = new StringEntity(content, "utf-8");
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

    private static String doGetRequest(String url, HttpClient client) throws IOException {
        HttpGet httpget = new HttpGet(url);
        HttpResponse resp = client.execute(httpget);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpget.setConfig(requestConfig);
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



}


