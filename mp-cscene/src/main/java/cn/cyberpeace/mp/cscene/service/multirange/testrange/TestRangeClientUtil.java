package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.http.*;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.Map;

@Slf4j
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
        var result =  httpClient.request(HttpGet.class, url, null, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult post(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = httpClient.request(HttpPost.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult put(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = httpClient.request(HttpPut.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult patch(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = httpClient.request(HttpPatch.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }

    public static HttpResult delete(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        var result = httpClient.request(HttpDeleteWithBody.class, url, data, param, headers, timeout, true);
        log.info("result = {}", result);
        return result;
    }


}
