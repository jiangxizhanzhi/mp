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

@Slf4j
@Data
public abstract class CloudClient {

    private final CloudHttpClient httpClient = new CloudHttpClient();

    private static final Object syncObj = new Object();

    private <T> CloudResponse<T> checkResponse(CloudResponse<T> response, boolean throwException) {
        if (throwException && !response.isSuccess()) {
            throw new MpRuntimeException(response.getMessage());
        }

        return response;
    }

    protected void heavyConcurrentWait() {
        if (Boolean.TRUE.equals(CloudSingleton.cloudAppConfig.getCloudHeavyConcurrentDelayEnable())) {
            synchronized (syncObj) {
                try {
                    Thread.sleep(CloudSingleton.cloudAppConfig.getCloudHeavyConcurrentDelay());
                } catch (InterruptedException e) {
                    log.error("Cloud heavy concurrent wait interrupted: ", e);
                }
            }
        }
    }

    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef,
                                         boolean throwException) {
        return checkResponse(httpClient.cloudGet(url, param, valueTypeRef), throwException);
    }

    public <T> CloudResponse<T> cloudGet(String url, HttpParam param, TypeReference<CloudResponse<T>> valueTypeRef,
                                         boolean throwException, Integer timeout) {
        return checkResponse(httpClient.cloudGet(url, param, valueTypeRef, timeout), throwException);
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

    public HttpResult cloudDownload(String url, HttpParam param) {
        return httpClient.cloudDownload(url, param);
    }

    protected boolean excludeDeleteException(Exception runtimeException) {
        Boolean ignoreDeleteException = BeanCommon.getBean(CloudAppConfig.class).getIgnoreDeleteException();
        if (Boolean.TRUE.equals(ignoreDeleteException)) {
            return true;
        }
        if (StringUtils.isNotEmpty(runtimeException.getMessage()) && runtimeException.getMessage().contains("could not be found")) {
            
            return true;
        }
        return false;
    }
}
