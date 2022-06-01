package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class Network extends CloudClient {

    public NetworkData get(String id) {
        var url = String.format(CloudUrlConstant.NETWORK_GET_URL, id);
        CloudResponse<NetworkData> result = cloudGet(url, null, new TypeReference<>() {}, true);
        return result.getData();
    }

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

    public List<NetworkData> getAll(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<CloudListData<NetworkData>> result = cloudGet(CloudUrlConstant.NETWORK_QUERY_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return null;
    }

    public void deleteNetworkById(String networkId) {
        heavyConcurrentWait();
        cloudDelete(CloudUrlConstant.DELETE_NETWORK_URL.replace("{network_id}", networkId), null, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }
}
