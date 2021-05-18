package cn.cyberpeace.mp.base.util;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * http工具类
 */
@Slf4j
public class HttpUtils {
    /**
     * 发送一个Post请求
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String sendPost(String url, String content) throws IOException {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(url);
            // 解决中文乱码问题
            StringEntity entity = new StringEntity(content,"utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);

            HttpResponse resp = client.execute(httpPost);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
            httpPost.setConfig(requestConfig);
            // 获得返回值
            return EntityUtils.toString(resp.getEntity(),"UTF-8");
        } catch (Exception e) {
            log.error("HttpUtils-sendPost is error.request url is {},content is {},error is",url,content,e);
            throw e;
        }
    }

    /**
     * get请求
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGet(String url) throws IOException {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httpget = new HttpGet(url);
            HttpResponse resp = client.execute(httpget);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
            httpget.setConfig(requestConfig);
            // 获得返回值
            return EntityUtils.toString(resp.getEntity(),"UTF-8");
        } catch (Exception e) {
            log.error("HttpUtils-sendPost is error.request url is {},error is",url,e);
            throw e;
        }
    }
}
