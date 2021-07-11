package cn.cyberpeace.mp.sceneplugingns3.util.gns3http;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3Singleton;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class Gns3HttpClient {

    private final HttpClient httpClient;

    private <T> CloudResponse<T> cloudResultBean(HttpResult result, TypeReference<CloudResponse<T>> valueTypeRef,
                                                 String method, String url, HttpData data, HttpParam param) {
        CloudResponse<T> response = new CloudResponse<>();
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Cloud {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            response.setRequestError();
            return response;
        }


        var objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            response = objectMapper.readValue(result.getResult(), valueTypeRef);
        } catch (Exception e) {
            log.error("Cloud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            response.setParseError();
            return response;
        }

        if (!response.isSuccess()) {
            log.error("Cloud {}[url - {}?{}, data - {}] status error: {}, {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), response.getMessage(), response.getDetail());
        }

        return response;
    }

    private <T> T resultBean(HttpResult result, Class<T> valueType, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Cloud {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            return null;
        }

        T response;
        var objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            response = objectMapper.readValue(result.getResult(), valueType);
        } catch (Exception e) {
            log.error("Cloud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    private <T> T resultBean(HttpResult result, TypeReference<T> valueTypeRef, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Cloud {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            return null;
        }

        T response;
        var objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            response = objectMapper.readValue(result.getResult(), valueTypeRef);
        } catch (Exception e) {
            log.error("Cloud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    public Gns3HttpClient() {
        this(Gns3Singleton.gns3AppConfig.getGns3Server(), 3000, 100);
    }

    private Gns3HttpClient(String server, Integer timeout, Integer poolSize) {
        httpClient = HttpClient.builder()
                .configServer(server)
                .configTimeout(timeout)
                .configMaxConnTotal(poolSize)
                .configMaxConnPerRoute(poolSize)
                .build();
        httpClient.setLogRequest(true);
    }

    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {

        return cloudResultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, null, param);
    }

    public <T> CloudResponse<T> cloudPost(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {

        return cloudResultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
    }

    public <T> CloudResponse<T> cloudPatch(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {

        return cloudResultBean(httpClient.patch(url, data, param, this.getHeaders(), null), valueTypeRef, "patch", url, data, param);
    }

    public <T> CloudResponse<T> cloudDelete(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return cloudResultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
    }

    public <T> T get(String url, HttpParam param, Class<T> valueType) {

        return resultBean(httpClient.get(url, param, this.getHeaders(), null), valueType, "get", url, null, param);
    }

    public <T> T post(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueType, "post", url, data, param);
    }

    public <T> T put(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.put(url, data, param, this.getHeaders(), null), valueType, "put", url, data, param);
    }

    public <T> T patch(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.patch(url, data, param, this.getHeaders(), null), valueType, "patch", url, data, param);
    }

    public <T> T delete(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueType, "delete", url, data, param);
    }

    public <T> T get(String url, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, null, param);
    }

    public <T> T post(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
    }

    public <T> T patch(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.patch(url, data, param, this.getHeaders(), null), valueTypeRef, "patch", url, data, param);
    }

    public <T> T delete(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        
        Locale locale = LocaleContextHolder.getLocale();
        if (locale != null) {
            headers.put("Accept-Language", locale.getLanguage());
        }
        
        String traceId = SysContextHolder.getCallChainId();
        headers.put("X-Request-Id", traceId);
        return headers;
    }
}
