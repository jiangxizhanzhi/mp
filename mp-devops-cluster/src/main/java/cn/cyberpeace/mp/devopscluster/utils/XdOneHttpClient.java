package cn.cyberpeace.mp.devopscluster.utils;

import cn.cyberpeace.mp.base.config.Swagger2Config;
import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.devopscluster.config.XdAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class XdOneHttpClient {

    private HttpClient httpClient;

    @Autowired
    private XdAppConfig xdAppConfig;

    public void init() {
        httpClient = HttpClient.builder()
                .configServer(xdAppConfig.getXdOneServer())
                .configTimeout(60000)
                .configMaxConnTotal(20)
                .configMaxConnPerRoute(20)
                .build();
        httpClient.setLogRequest(true);
    }

    public HttpResult xdGet(String url, HttpParam param) {
        return httpClient.get(url, param, this.getHeaders(), null);
    }

    public HttpResult xdPost(String url, HttpParam param, HttpData data) {
        return httpClient.post(url, data, param, this.getHeaders(), null);
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        // 国际化header的处理
        Locale locale = LocaleContextHolder.getLocale();
        if (locale != null) {
            headers.put("Accept-Language", locale.getLanguage());
        }
        // 调用链id
        String traceId = SysContextHolder.getCallChainId();
        headers.put("X-Request-Id", traceId);
        headers.put("x-mix-root-tenant-id", "2");
        headers.put("x-mix-tenant-id", "2");
        headers.put("x-mix-tenant-name", "admin");
        headers.put("x-mix-tenant-type", "tenant");
        headers.put("x-mix-user-id", "2");
        headers.put("x-mix-user-name", "admin");
        headers.put("content-type", "application/json");
        return headers;
    }

}
