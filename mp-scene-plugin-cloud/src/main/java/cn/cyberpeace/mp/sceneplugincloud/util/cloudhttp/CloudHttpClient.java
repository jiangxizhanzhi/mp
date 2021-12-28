package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class CloudHttpClient {

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

    public CloudHttpClient() {
        this(CloudSingleton.cloudAppConfig.getCpcloudServer(),
                CloudSingleton.cloudAppConfig.getCpcloudServerTimeout(),
                CloudSingleton.cloudAppConfig.getCpcloudHttpPoolSize());
    }

    private CloudHttpClient(String server, Integer timeout, Integer poolSize) {
        httpClient = new HttpClient(server, timeout, poolSize, poolSize);
        httpClient.setLogRequest(true);
    }

    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return cloudResultBean(httpClient.get(url, param, null, null), valueTypeRef, "get", url, null, param);
    }

    public <T> CloudResponse<T> cloudPost(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return cloudResultBean(httpClient.post(url, data, param, null, null), valueTypeRef, "post", url, data, param);
    }

    public <T> CloudResponse<T> cloudPatch(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return cloudResultBean(httpClient.patch(url, data, param, null, null), valueTypeRef, "patch", url, data, param);
    }

    public <T> CloudResponse<T> cloudDelete(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return cloudResultBean(httpClient.delete(url, data, param, null, null), valueTypeRef, "delete", url, data, param);
    }

    public <T> T get(String url, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.get(url, param, null, null), valueType, "get", url, null, param);
    }

    public <T> T post(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.post(url, data, param, null, null), valueType, "post", url, data, param);
    }

    public <T> T put(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.put(url, data, param, null, null), valueType, "put", url, data, param);
    }

    public <T> T patch(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.patch(url, data, param, null, null), valueType, "patch", url, data, param);
    }

    public <T> T delete(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.delete(url, data, param, null, null), valueType, "delete", url, data, param);
    }

    public <T> T get(String url, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.get(url, param, null, null), valueTypeRef, "get", url, null, param);
    }

    public <T> T post(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.post(url, data, param, null, null), valueTypeRef, "post", url, data, param);
    }

    public <T> T patch(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.patch(url, data, param, null, null), valueTypeRef, "patch", url, data, param);
    }

    public <T> T delete(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.delete(url, data, param, null, null), valueTypeRef, "delete", url, data, param);
    }

}
