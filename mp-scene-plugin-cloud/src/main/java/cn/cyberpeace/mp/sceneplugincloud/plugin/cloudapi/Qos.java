 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name Qos.java.
 // File size 1003.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.QosCreateData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Qos
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
@Slf4j
public class Qos extends CloudClient {
    /**
     * create
     *
     * @param createData
     * @return boolean
     */
    public boolean create(QosCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudPost(CloudUrlConstant.QOS_CREATE_URL, createData, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
        return true;
    }
}
