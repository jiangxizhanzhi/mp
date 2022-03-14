package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class NpcClient {

    private final CloudHttpClient httpClient = new CloudHttpClient();

    private static final Object syncObj = new Object();

    private <T> CloudResponse<T> checkResponse(CloudResponse<T> response, boolean throwException) {
        if (throwException && !response.isSuccess()) {
            throw new MpRuntimeException(response.getMessage());
        }

        return response;
    }

    protected void heavyConcurrentWait() {
        synchronized (syncObj) {
            try {
                Thread.sleep(CloudSingleton.cloudAppConfig.getCloudHeavyConcurrentDelay());
            } catch (InterruptedException e) {
                log.error("Cloud heavy concurrent wait interrupted: ", e);
            }
        }
    }

    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef,
                                         boolean throwException) {
        return checkResponse(httpClient.cloudGet(url, param, valueTypeRef), throwException);
    }

    public <T> CloudResponse<T> cloudPost(String url, HttpData data, HttpParam param,
                                          TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.cloudPost(url, data, param, valueTypeRef), throwException);
    }

    public <T> CloudResponse<T> cloudPatch(String url, HttpData data, HttpParam param,
                                           TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.cloudPatch(url, data, param, valueTypeRef), throwException);
    }

    public <T> CloudResponse<T> cloudDelete(String url, HttpData data, HttpParam param,
                                            TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.cloudDelete(url, data, param, valueTypeRef), throwException);
    }

}
