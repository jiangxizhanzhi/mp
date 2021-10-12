package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class Network extends CloudClient {

    public NetworkData getByName(String name) {
        var queryParam = new NetworkQueryParam();
        queryParam.setName(name);
        CloudResponse<CloudListData<NetworkData>> result = cloudGet(CloudUrlConstant.NETWORK_QUERY_URL, queryParam,
                new TypeReference<>() {}, true);

        var listData = result.getData();
        if (listData != null && listData.getList() != null && !listData.getList().isEmpty()) {
            return listData.getList().get(0);
        } else {
            return null;
        }
    }
}
