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

    private <T> CloudResponse<T> resultBean(HttpResult result, TypeReference<CloudResponse<T>> valueTypeRef, String method,
                                            String url, HttpData data, HttpParam param) {
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

    public CloudHttpClient() {
        this(CloudSingleton.cloudAppConfig.getCpcloudServer(), CloudSingleton.cloudAppConfig.getCpcloudServerTimeout());
    }

    private CloudHttpClient(String server, int timeout) {
        httpClient = new HttpClient(server, timeout);
        httpClient.setLogRequest(true);
    }

    public <T> CloudResponse<T> get(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.get(url, param, null, null), valueTypeRef, "get", url, null, param);
    }

    public <T> CloudResponse<T> post(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.post(url, data, param, null, null), valueTypeRef, "post", url, data, param);
    }

    public <T> CloudResponse<T> patch(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.patch(url, data, param, null, null), valueTypeRef, "patch", url, data, param);
    }

    public <T> CloudResponse<T> delete(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.delete(url, data, param, null, null), valueTypeRef, "delete", url, data, param);
    }

}
