package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备
 * @author wanggang
 */
@Service
public class DeviceServiceImpl extends BaseOpenStackServiceImpl implements DeviceService {

    public DeviceServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public DeviceCloudPlatformModel page(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/device/lists" + getParams, DeviceCloudPlatformModel.class);
    }

    @Override
    public Device findById(String id) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/device/" + id, Device.class);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", ids);
        super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + "/device/lists",JSONObject.toJSONString(jsonObject));
    }
}
