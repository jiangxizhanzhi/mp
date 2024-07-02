package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetTemplateIdParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDeviceData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

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

