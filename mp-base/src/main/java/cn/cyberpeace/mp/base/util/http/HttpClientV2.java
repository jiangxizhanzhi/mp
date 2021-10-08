package cn.cyberpeace.mp.base.util.http;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @Description HttpClient , http请求复用一个 HttpClient
 * @Author maojun
 * @Date 2021/9/8
 */
class HttpClientV2 {
    private static HttpClient HTTP_CLIENT;

    private interface DEFAULT {
        int CONNECTION_REQUEST_TIMEOUT = 30000;
        int CONNECTION_TIMEOUT = 30000;
        int SOCKET_TIMEOUT = 30000;
    }

    private static void init() {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(DEFAULT.CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(DEFAULT.CONNECTION_TIMEOUT)
                .setSocketTimeout(DEFAULT.SOCKET_TIMEOUT)
                .build();
        HTTP_CLIENT = HttpClientBuilder.create()
                .evictExpiredConnections()
                .evictIdleConnections(1L, TimeUnit.MINUTES)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setMaxConnPerRoute(10)
                .setMaxConnTotal(30)
                .build();
    }

    protected static HttpClient instance() {
        if (HTTP_CLIENT == null) {
            synchronized (HttpClientV2.class) {
                if (HTTP_CLIENT == null) {
                    init();
                }
            }
        }
        return HTTP_CLIENT;
    }
}
