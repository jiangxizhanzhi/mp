package cn.cyberpeace.mp.aggregation.utils;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.base.util.http.HttpResult;
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

/**
 * @Description
 * @Author zhanglin
 * @Date 2023/3/22
 */
@Slf4j
public class CpcloudHttpClient {

    private final HttpClient httpClient;

    public CpcloudHttpClient(String server, Integer timeout, Integer poolSize) {
        httpClient = HttpClient.builder()
                .configServer(server)
                .configTimeout(timeout)
                .configMaxConnTotal(poolSize)
                .configMaxConnPerRoute(poolSize)
                .build();
        httpClient.setLogRequest(true);
    }

    public <T> CplcoudResponse<T> Get(String url, HttpParam param, TypeReference<CplcoudResponse<T>> valueTypeRef) {

        return cpcloudResultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, null, param);
    }

    public <T> CplcoudResponse<T> Post(String url, HttpData data, HttpParam param, TypeReference<CplcoudResponse<T>> valueTypeRef) {

        return cpcloudResultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
    }

    public <T> CplcoudResponse<T> Delete(String url, HttpData data, HttpParam param, TypeReference<CplcoudResponse<T>> valueTypeRef) {
        return cpcloudResultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
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

    public <T> T delete(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueType, "delete", url, data, param);
    }

    public <T> T get(String url, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, null, param);
    }

    public <T> T post(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
    }

    public <T> T delete(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
    }

    private <T> CplcoudResponse<T> cpcloudResultBean(HttpResult result, TypeReference<CplcoudResponse<T>> valueTypeRef,
                                                  String method, String url, HttpData data, HttpParam param) {
        CplcoudResponse<T> response = new CplcoudResponse<>();
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Cplcoud {}[url - {}?{}, data - {}] empty result: ", method, url,
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
            log.error("Cplcoud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            response.setParseError();
            return response;
        }

        if (!response.isSuccess()) {
            log.error("Cplcoud {}[url - {}?{}, data - {}] status error: {}, {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), response.getMessage(), response.getDetail());
        }

        return response;
    }

    private <T> T resultBean(HttpResult result, Class<T> valueType, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Cplcoud {}[url - {}?{}, data - {}] empty result: ", method, url,
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
            log.error("Cplcoud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    private <T> T resultBean(HttpResult result, TypeReference<T> valueTypeRef, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Cplcoud {}[url - {}?{}, data - {}] empty result: ", method, url,
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
            log.error("Cplcoud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
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
        return headers;
    }
}

