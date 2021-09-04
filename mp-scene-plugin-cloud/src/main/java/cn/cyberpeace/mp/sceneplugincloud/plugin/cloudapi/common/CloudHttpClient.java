package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;

public class CloudHttpClient {

    private final HttpClient httpClient;

    public CloudHttpClient() {
        this(CloudSingleton.cloudAppConfig.getCpcloudServer(), CloudSingleton.cloudAppConfig.getCpcloudServerTimeout());
    }

    private CloudHttpClient(String server, int timeout) {
        httpClient = new HttpClient(server, timeout);
    }

    public String successGet(String url, HttpParam param) {
        return httpClient.successGet(url, param, null, null);
    }

    public String successPost(String url, HttpData data, HttpParam param) {
        return httpClient.successPost(url, data, param, null, null);
    }

    public String successPatch(String url, HttpData data, HttpParam param) {
        return httpClient.successPatch(url, data, param, null, null);
    }

    public String successDelete(String url, HttpData data, HttpParam param) {
        return httpClient.successDelete(url, data, param, null, null);
    }

}
