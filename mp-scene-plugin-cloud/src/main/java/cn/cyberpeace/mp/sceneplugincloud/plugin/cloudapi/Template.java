package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.res.TemplateIdByDeviceNameRes;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Template
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudClient
 */
@Slf4j
@Data
public class Template extends CloudClient {

    /**
     * getTemplateIdByDeviceName
     *
     * @param deviceName
     * @return
     */
    public String getTemplateIdByDeviceName(String deviceName) {
        String requestUrl = String.format(CloudUrlConstant.GET_TEMPLATE_ID_BY_DEVICE_NAME, deviceName);
        CloudResponse<TemplateIdByDeviceNameRes> response = cloudGet(requestUrl, null, new TypeReference<CloudResponse<TemplateIdByDeviceNameRes>>() {
        }, true);
        return response.getData().getTemplateId();
    }



}
