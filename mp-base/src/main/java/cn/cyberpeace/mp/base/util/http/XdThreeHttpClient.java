package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.config.XdAppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class XdThreeHttpClient {

    private HttpClient httpClient;

    @Autowired
    private XdAppConfig xdAppConfig;

    public void init() {
        httpClient = HttpClient.builder()
                .configServer(xdAppConfig.getXdThreeServer())
                .configTimeout(60000)
                .configMaxConnTotal(20)
                .configMaxConnPerRoute(20)
                .build();
        httpClient.setLogRequest(true);
    }

    public HttpResult xdGet(String url, HttpParam param, String cookie) {
        return httpClient.get(url, param, this.getHeaders(cookie), null);
    }

    public HttpResult xdPost(String url, HttpParam param, HttpData data, String cookie) {
        return httpClient.post(url, data, param, this.getHeaders(cookie), null);
    }

    private Map<String, String> getHeaders(String cookie) {
        Map<String, String> headers = new HashMap<>();
        // 国际化header的处理
        Locale locale = LocaleContextHolder.getLocale();
        if (locale != null) {
            headers.put("Accept-Language", locale.getLanguage());
        }
        // 调用链id
        String traceId = SysContextHolder.getCallChainId();
        headers.put("X-Request-Id", traceId);

        if (StringUtils.isNotEmpty(cookie)) {
            headers.put("Cookie", cookie);
        }

        return headers;
    }

}
