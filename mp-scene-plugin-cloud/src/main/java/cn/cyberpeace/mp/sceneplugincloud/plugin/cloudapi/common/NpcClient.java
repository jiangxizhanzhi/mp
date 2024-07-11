package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NpcClient
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Data
public abstract class NpcClient {

    private final CloudHttpClient httpClient = new CloudHttpClient();

    private static final Object syncObj = new Object();

    /**
     * checkResponse
     *
     * @param response
     * @param throwException
     * @return
     */
    private <T> CloudResponse<T> checkResponse(CloudResponse<T> response, boolean throwException) {
        if (throwException && !response.isSuccess()) {
            throw new MpRuntimeException(response.getMessage());
        }

        return response;
    }

    /**
     * heavyConcurrentWait
     */
    protected void heavyConcurrentWait() {
        synchronized (syncObj) {
            try {
                wait(CloudSingleton.cloudAppConfig.getCloudHeavyConcurrentDelay());
            } catch (InterruptedException e) {
                log.error("Cloud heavy concurrent wait interrupted: ", e);
                Thread.currentThread().interrupt();
                Thread.interrupted();
            }
        }
    }

    /**
     * cloudGet
     *
     * @param url
     * @param param
     * @param valueTypeRef
     * @param throwException
     * @return
     */
    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef,
                                         boolean throwException) {
        return checkResponse(httpClient.cloudGet(url, param, valueTypeRef), throwException);
    }

    /**
     * cloudPost
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @param throwException
     * @return
     */
    public <T> CloudResponse<T> cloudPost(String url, HttpData data, HttpParam param,
                                          TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.cloudPost(url, data, param, valueTypeRef), throwException);
    }

    /**
     * cloudPatch
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @param throwException
     * @return
     */
    public <T> CloudResponse<T> cloudPatch(String url, HttpData data, HttpParam param,
                                           TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.cloudPatch(url, data, param, valueTypeRef), throwException);
    }

    /**
     * cloudDelete
     *
     * @param url
     * @param data
     * @param param
     * @param valueTypeRef
     * @param throwException
     * @return
     */
    public <T> CloudResponse<T> cloudDelete(String url, HttpData data, HttpParam param,
                                            TypeReference<CloudResponse<T>> valueTypeRef, boolean throwException) {
        return checkResponse(httpClient.cloudDelete(url, data, param, valueTypeRef), throwException);
    }

}
