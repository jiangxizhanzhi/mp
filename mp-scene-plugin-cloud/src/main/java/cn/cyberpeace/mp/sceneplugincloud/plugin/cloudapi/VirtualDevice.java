 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualDevice.java.
 // File size 1213.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetTemplateIdParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDeviceData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @ClassName VirtualDevice
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
public class VirtualDevice extends CloudClient {
    /**
     * getTemplateId
     *
     * @param name
     * @return
     */
    public VirtualDeviceData getTemplateId(String name) {
        GetTemplateIdParam param = new GetTemplateIdParam();
        param.setName(name);
        param.setDefaultInit(true);
        CloudResponse<VirtualDeviceData> result = cloudGet(CloudUrlConstant.VIRTUAL_DEVICE_GET, param,
                new TypeReference<CloudResponse<VirtualDeviceData>>() {
                }, true);
        return result.getData();
    }
}

