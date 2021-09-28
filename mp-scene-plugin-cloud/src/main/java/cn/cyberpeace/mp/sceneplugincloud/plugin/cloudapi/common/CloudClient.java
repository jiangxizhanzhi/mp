package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;

@Data
public abstract class CloudClient {

    private final CloudHttpClient httpClient = new CloudHttpClient();

    private <T> CloudResponse<T> checkResponse(CloudResponse<T> response, boolean throwException) {
        if (throwException && !response.isSuccess()) {
            throw new MpRuntimeException(response.getMessage());
        }

        return response;
    }

    public <T> CloudResponse<T> get(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef,
                                    boolean throwException) {
        return checkResponse(httpClient.get(url, param, valueTypeRef), throwException);
    }

    public <T> CloudResponse<T> post(String url, HttpData data, HttpParam param,
                                     TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.post(url, data, param, valueTypeRef), throwException);
    }

    public <T> CloudResponse<T> patch(String url, HttpData data, HttpParam param,
                                      TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.patch(url, data, param, valueTypeRef), throwException);
    }

    public <T> CloudResponse<T> delete(String url, HttpData data, HttpParam param,
                                       TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.delete(url, data, param, valueTypeRef), throwException);
    }

}
