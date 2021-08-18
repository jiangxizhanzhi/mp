package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
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
        try {
            return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/entity/lists", JsonUtils.toUnderlineJSONString(device),Device.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Device update(String uuid,Device device) {
        try {
            return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/entity/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
