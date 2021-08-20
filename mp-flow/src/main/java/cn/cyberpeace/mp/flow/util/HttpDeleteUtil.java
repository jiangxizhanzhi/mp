package cn.cyberpeace.mp.flow.util;

import cn.cyberpeace.mp.flow.config.HttpDeleteWithBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Description: http delete工具类
 * @Autho qiaokang
 * @Date：2021/8/11 17:17
 */
@Slf4j
public class HttpDeleteUtil {

    /**
     * 带body的delete
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String sendDelete(String url, String content) throws IOException {
        log.info("请求地址================>{}", url);
        log.info("请求参数================>{}", content);
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);
        //设置Content-Type
        httpDeleteWithBody.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        //写入JSON数据参数
        httpDeleteWithBody.setEntity(new StringEntity(content, StandardCharsets.UTF_8));

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpDeleteWithBody.setConfig(requestConfig);

        HttpResponse response = httpClient.execute(httpDeleteWithBody);
        return EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
    }

}
