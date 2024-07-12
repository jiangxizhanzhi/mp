 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualInstance.java.
 // File size 1277.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.InstanceModifyNetworkData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.InstanceModifyNetworkResult;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName VirtualInstance
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
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