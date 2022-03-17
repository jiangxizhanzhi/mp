package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

@Slf4j
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
            return true;
        }catch (Exception e) {
            log.error("failed to delete node,", e);
            return true;
        }
    }
}
