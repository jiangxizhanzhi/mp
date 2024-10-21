package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualInternetData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualInternet extends CloudClient {

    public VirtualInternet() {
    }

    public VirtualInternet(CloudHttpClient httpClient) {
        super(httpClient);
    }

    public VirtualInternetData create() {
        CloudResponse<VirtualInternetData> result = cloudPost(CloudUrlConstant.VIRTUAL_INTERNET_CREATE_URL, null,
                null, new TypeReference<>() {}, true);
        return result.getData();
    }
}
