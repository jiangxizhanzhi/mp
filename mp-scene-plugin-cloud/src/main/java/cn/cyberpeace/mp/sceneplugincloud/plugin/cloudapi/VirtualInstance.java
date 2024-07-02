package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.InstanceModifyNetworkData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.InstanceModifyNetworkResult;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VirtualInstance extends CloudClient {
    /**
     * modifyNetwork
     *
     * @param modifyNetworkData
     * @return
     */
    public InstanceModifyNetworkResult modifyNetwork(InstanceModifyNetworkData modifyNetworkData) {
        heavyConcurrentWait();

        CloudResponse<InstanceModifyNetworkResult> result = cloudPost(CloudUrlConstant.INSTANCE_MODIFY_NETWORK,
                modifyNetworkData, null, new TypeReference<CloudResponse<InstanceModifyNetworkResult>>() {
                }, true);
        return result.getData();
    }
}