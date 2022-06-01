package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterGetData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class VirtualRouter extends CloudClient {

    public VirtualRouterCreateData create(VirtualRouterCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualRouterCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_ROUTER_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualRouterCreateData>>() {}, true);
        var virtualRouterData =  result.getData();
        var externalGatewayInfo = virtualRouterData.getExternalGatewayInfo();
        if (externalGatewayInfo != null) {
            var networks = virtualRouterData.getNetworks();
            networks = networks == null ? new ArrayList<>() : networks;
            var externalNetwork = new VirtualNic();
            externalNetwork.setNetworkId(externalGatewayInfo.getNetworkId());
            externalNetwork.setFixedIp(externalGatewayInfo.getFixedIp());
            networks.add(externalNetwork);
            virtualRouterData.setNetworks(networks);
        }
        return virtualRouterData;
    }

    public boolean delete(VirtualRouterDeleteData deleteData) {
        heavyConcurrentWait();

        try {
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_ROUTER_DELETE_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {}, true);
        } catch (Exception e) {
            if (super.excludeDeleteException(e)) {
                return true;
            }
            throw e;
        }
        return true;
    }

    public List<VirtualRouterGetData> getAllRouters(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<CloudListData<VirtualRouterGetData>> result = cloudGet(CloudUrlConstant.GET_ALL_ROUTER_URL, param, new TypeReference<>() {
        }, true);
        if (result != null && result.getData() != null && result.getData().getList() != null && !result.getData().getList().isEmpty()) {
            return result.getData().getList();
        }
        return null;
    }

    public void deleteRouterById(String routerId) {
        heavyConcurrentWait();

        VirtualRouterDeleteData deleteData = new VirtualRouterDeleteData();
        deleteData.setIds(List.of(routerId));
        cloudDelete(CloudUrlConstant.VIRTUAL_ROUTER_DELETE_URL, deleteData, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }
}
