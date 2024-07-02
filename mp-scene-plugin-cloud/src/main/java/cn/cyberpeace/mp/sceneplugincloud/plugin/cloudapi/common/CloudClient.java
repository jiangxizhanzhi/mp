package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static cn.cyberpeace.mp.base.constant.BaseErrorCode.CUSTOM_ERROR;

/**
 * @ClassName CloudClient
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@Data
public abstract class CloudClient {

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
        if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getCloudHeavyConcurrentDelayEnable())) {
            synchronized (syncObj) {
                try {
                    syncObj.wait(CloudSingleton.cloudAppConfig.getCloudHeavyConcurrentDelay());
                } catch (InterruptedException e) {
                    log.error("Cloud heavy concurrent wait interrupted: ", e);
                }
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
     * cloudGetWithoutResponseWrapper
     *
     * @param url
     * @param param
     * @param valueTypeRef
     * @param throwException
     * @return
     */
    public <T> T cloudGetWithoutResponseWrapper(String url, HttpParam param, TypeReference<T> valueTypeRef, boolean throwException) {
        try {
            return httpClient.get(url, param, valueTypeRef);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (throwException) {
                throw new MpRuntimeException(CUSTOM_ERROR.format("请求平台接口失败"), e.getMessage());
            }
            return null;
        }
    }

    /**
     * cloudGet
     *
     * @param url
     * @param param
     * @param valueTypeRef
     * @param throwException
     * @param timeout
     * @return
     */
    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef,
                                         boolean throwException, Integer timeout) {
        return checkResponse(httpClient.cloudGet(url, param, valueTypeRef, timeout), throwException);
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

    /**
     * cloudDownload
     *
     * @param url
     * @param param
     * @return
     */
    public HttpResult cloudDownload(String url, HttpParam param) {
        return httpClient.cloudDownload(url, param);
    }

    /**
     * excludeDeleteException
     *
     * @param runtimeException
     * @return boolean
     */
    protected boolean excludeDeleteException(Exception runtimeException) {
        Boolean ignoreDeleteException = BeanCommon.getBean(CloudAppConfig.class).getIgnoreDeleteException();
        if (Boolean.TRUE.equals(ignoreDeleteException)) {
            return true;
        }
        if (StringUtils.isNotEmpty(runtimeException.getMessage()) && runtimeException.getMessage().contains("could not be found")) {
            // 删除时已不存在的错误可以视为删除成功
            return true;
        }
        return false;
    }
}
