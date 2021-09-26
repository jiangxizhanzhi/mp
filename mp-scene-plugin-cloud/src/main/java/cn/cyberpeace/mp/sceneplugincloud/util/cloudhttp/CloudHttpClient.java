package cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class CloudHttpClient {

    public static final String successCode = "CP000000";
    public static final String successCode0 = "0";

    private final HttpClient httpClient;

    private <T> CloudResponse<T> resultBean(String result, TypeReference<CloudResponse<T>> valueTypeRef, String method,
                                            String url, HttpData data, HttpParam param) {
        if (StringUtils.isEmpty(result)) {
            log.error("Cloud {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            return null;
        }

        CloudResponse<T> resultObj;
        var objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            resultObj = objectMapper.readValue(result, valueTypeRef);
        } catch (Exception e) {
            log.error("Cloud {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        String code = resultObj.getCode();
        if (!isSuccess(code)) {
            log.error("Cloud {}[url - {}?{}, data - {}] status error: {}, {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), resultObj.getMessage(), resultObj.getDetail());
        }

        return resultObj;
    }

    public CloudHttpClient() {
        this(CloudSingleton.cloudAppConfig.getCpcloudServer(), CloudSingleton.cloudAppConfig.getCpcloudServerTimeout());
    }

    private CloudHttpClient(String server, int timeout) {
        httpClient = new HttpClient(server, timeout);
    }

    public boolean isSuccess(String code) {
        return successCode.equals(code) || successCode0.equals(code);
    }

    public <T> CloudResponse<T> successGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.successGet(url, param, null, null), valueTypeRef, "get", url, null, param);
    }

    public <T> CloudResponse<T> successPost(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.successPost(url, data, param, null, null), valueTypeRef, "post", url, data, param);
    }

    public <T> CloudResponse<T> successPatch(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.successPatch(url, data, param, null, null), valueTypeRef, "patch", url, data, param);
    }

    public <T> CloudResponse<T> successDelete(String url, HttpData data, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef) {
        return resultBean(httpClient.successDelete(url, data, param, null, null), valueTypeRef, "delete", url, data, param);
    }

}
