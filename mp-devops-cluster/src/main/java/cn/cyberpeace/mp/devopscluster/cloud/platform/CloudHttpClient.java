package cn.cyberpeace.mp.devopscluster.cloud.platform;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
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
 * @ClassName CloudHttpClient
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class CloudHttpClient {
    private final HttpClient httpClient;

    private static final String METHOD_DELETE = "delete";
    private static final String EMPTY_RESULT_LOG = "cloud {}[url - {}?{}, data - {}] empty result: ";
    private static final String ERROR_RESULT_LOG = "cloud {}[url - {}?{}, data - {}] parse result[{}] error: {}";

    /**
     * CloudHttpClient
     *
     * @param server
     * @param timeout
     * @param poolSize
     */
    public CloudHttpClient(String server, Integer timeout, Integer poolSize) {
        httpClient = HttpClient.builder()
                .configServer(server)
                .configTimeout(timeout)
                .configMaxConnTotal(poolSize)
                .configMaxConnPerRoute(poolSize)
                .build();
        httpClient.setLogRequest(true);
        Map<String, String> headers = new HashMap<>();
        httpClient.setHeaders(headers);
    }

    /**
     * cloudPost
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> CloudResponse<T> cloudPost(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {

        return cloudResultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
    }

    /**
     * cloudGet
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> CloudResponse<T> cloudGet(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {

        return cloudResultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, data, param);
    }

    /**
     * cloudDelete
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> CloudResponse<T> cloudDelete(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return cloudResultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, METHOD_DELETE, url, data, param);
    }

    /**
     * get
     *
     * @param url
     * @param data
     * @param param
     * @param valueType
     * @return
     */
    public <T> T get(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.get(url, param, this.getHeaders(), null), valueType, "get", url, data, param);
    }

    /**
     * post
     *
     * @param url
     * @param data
     * @param param
     * @param valueType
     * @return
     */
    public <T> T post(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueType, "post", url, data, param);
    }

    /**
     * delete
     *
     * @param url
     * @param data
     * @param param
     * @param valueType
     * @return
     */
    public <T> T delete(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueType, METHOD_DELETE, url, data, param);
    }

    /**
     * get
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> T get(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, data, param);
    }

    /**
     * post
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> T post(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
    }

    /**
     * delete
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> T delete(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, METHOD_DELETE, url, data, param);
    }

    /**
     * cloudResultBean
     *
     * @param result
     * @param valueTypeRef
     * @param method
     * @param url
     * @param data
     * @param param
     * @return
     */
    private <T> CloudResponse<T> cloudResultBean(HttpResult result, TypeReference<CloudResponse<T>> valueTypeRef,
                                                 String method, String url, HttpData data, HttpParam param) {
        CloudResponse<T> response = new CloudResponse<>();
        if (StringUtils.isEmpty(result.getResult())) {
            log.error(EMPTY_RESULT_LOG, method, url,
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
            log.error(ERROR_RESULT_LOG, method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            response.setParseError();
            return response;
        }

        if (!response.isSuccess()) {
            log.error("cloud {}[url - {}?{}, data - {}] status error: {}, {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), response.getMessage(), response.getDetail());
        }

        return response;
    }

    /**
     * resultBean
     *
     * @param result
     * @param valueType
     * @param method
     * @param url
     * @param data
     * @param param
     * @return
     */
    private <T> T resultBean(HttpResult result, Class<T> valueType, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error(EMPTY_RESULT_LOG, method, url,
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
            log.error(ERROR_RESULT_LOG, method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    /**
     * resultBean
     *
     * @param result
     * @param valueTypeRef
     * @param method
     * @param url
     * @param data
     * @param param
     * @return
     */
    private <T> T resultBean(HttpResult result, TypeReference<T> valueTypeRef, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error(EMPTY_RESULT_LOG, method, url,
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
            log.error(ERROR_RESULT_LOG, method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    /**
     * getHeaders
     *
     * @return
     */
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
