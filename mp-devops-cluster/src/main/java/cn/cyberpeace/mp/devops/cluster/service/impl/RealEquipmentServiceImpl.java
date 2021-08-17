package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.RealEquipmentOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 实装管理接口
 */
@Service
public class RealEquipmentServiceImpl extends BaseOpenStackServiceImpl implements RealEquipmentService {

    public RealEquipmentServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public DeviceCloudPlatformModel page(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/entity/lists" + getParams, DeviceCloudPlatformModel.class);
    }

    @Override
    public Device add(Device device) {
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/entity/lists",JSONObject.toJSONString(device),Device.class);
    }

    @Override
    public String update(String uuid,Device device) {
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/entity/" + uuid,JSONObject.toJSONString(device));
    }
}
