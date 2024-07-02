package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import cn.cyberpeace.mp.base.context.SysContextHolder;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
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
public class CloudHttpClient {

    private final HttpClient httpClient;

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
            log.error("Cloud {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            response.setRequestError();
            return response;
        }


        ObjectMapper objectMapper = new ObjectMapper();
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
            log.error("Cloud {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            return null;
        }

        T response;
        ObjectMapper objectMapper = new ObjectMapper();
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
            log.error("Cloud {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            return null;
        }

        T response;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            response = objectMapper.readValue(result.getResult(), valueTypeRef);
        } catch (Exception e) {
            log.error("Cloud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    /**
     * CloudHttpClient
     */
    public CloudHttpClient() {
        this(CloudSingleton.cloudAppConfig.getCpcloudServer(),
                CloudSingleton.cloudAppConfig.getCpcloudServerTimeout(),
                CloudSingleton.cloudAppConfig.getCpcloudHttpPoolSize());
    }

    /**
     * CloudHttpClient
     *
     * @param server
     * @param timeout
     * @param poolSize
     */
    private CloudHttpClient(String server, Integer timeout, Integer poolSize) {
        httpClient = HttpClient.builder()
                .configServer(server)
                .configTimeout(timeout)
                .configMaxConnTotal(poolSize)
                .configMaxConnPerRoute(poolSize)
                .build();
        httpClient.setLogRequest(true);
    }

    /**
     * cloudGet
     *
     * @param url
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {

        return cloudResultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, null, param);
    }

    /**
     * cloudGet
     *
     * @param url
     * @param param
     * @param valueTypeRef
     * @param timeout
     * @return
     */
    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef, Integer timeout) {

        return cloudResultBean(httpClient.get(url, param, this.getHeaders(), timeout), valueTypeRef, "get", url, null, param);
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
     * cloudPatch
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> CloudResponse<T> cloudPatch(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {

        return cloudResultBean(httpClient.patch(url, data, param, this.getHeaders(), null), valueTypeRef, "patch", url, data, param);
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
        return cloudResultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
    }

    /**
     * cloudDownload
     *
     * @param url
     * @param param
     * @return
     */
    public HttpResult cloudDownload(String url, HttpParam param) {
        return httpClient.streamGet(url, param, this.getHeaders(), null);
    }

    /**
     * get
     *
     * @param url
     * @param param
     * @param valueType
     * @return
     */
    public <T> T get(String url, HttpParam param, Class<T> valueType) {

        return resultBean(httpClient.get(url, param, this.getHeaders(), null), valueType, "get", url, null, param);
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
     * put
     *
     * @param url
     * @param data
     * @param param
     * @param valueType
     * @return
     */
    public <T> T put(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.put(url, data, param, this.getHeaders(), null), valueType, "put", url, data, param);
    }

    /**
     * patch
     *
     * @param url
     * @param data
     * @param param
     * @param valueType
     * @return
     */
    public <T> T patch(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.patch(url, data, param, this.getHeaders(), null), valueType, "patch", url, data, param);
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
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueType, "delete", url, data, param);
    }

    /**
     * get
     *
     * @param url
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> T get(String url, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.get(url, param, this.getHeaders(), null), valueTypeRef, "get", url, null, param);
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
     * patch
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @return
     */
    public <T> T patch(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.patch(url, data, param, this.getHeaders(), null), valueTypeRef, "patch", url, data, param);
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
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
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
