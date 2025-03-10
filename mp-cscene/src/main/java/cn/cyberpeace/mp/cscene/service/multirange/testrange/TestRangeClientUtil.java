package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.http.*;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.Map;

public class TestRangeClientUtil {

    private static HttpClient httpClient;
    static {
        CsceneAppConfig csceneAppConfig = BeanCommon.getBean(CsceneAppConfig.class);
        String ip = csceneAppConfig.getHfdyTestRangeIp();
        String port = csceneAppConfig.getHfdyTestRangePort();
        var httpClientBuilder = HttpClient.builder().configServer("http://" + ip + ":" + port);
        httpClientBuilder.configTimeout(10000);
        httpClientBuilder.configMaxConnPerRoute(10);
        httpClientBuilder.configMaxConnTotal(10);
        httpClient = httpClientBuilder.build();
    }

    public static HttpResult get(String url, HttpParam param, Map<String, String> headers, Integer timeout) {
        return httpClient.request(HttpGet.class, url, null, param, headers, timeout, true);
    }

    public static HttpResult post(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return httpClient.request(HttpPost.class, url, data, param, headers, timeout, true);
    }

    public static HttpResult put(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return httpClient.request(HttpPut.class, url, data, param, headers, timeout, true);
    }

    public static HttpResult patch(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return httpClient.request(HttpPatch.class, url, data, param, headers, timeout, true);
    }

    public static HttpResult delete(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return httpClient.request(HttpDeleteWithBody.class, url, data, param, headers, timeout, true);
    }


}
