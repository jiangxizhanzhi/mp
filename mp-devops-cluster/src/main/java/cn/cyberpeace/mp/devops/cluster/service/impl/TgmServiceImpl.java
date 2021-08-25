package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.TgmService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;


/**
 * TGM管理接口
 */
@Service
public class TgmServiceImpl extends BaseOpenStackServiceImpl implements TgmService {

    public TgmServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public DeviceCloudPlatformModel page(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/tgm/lists" + getParams, DeviceCloudPlatformModel.class);
    }

    @Override
    public Device add(Device device) {
        try {
            return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/tgm/lists", JsonUtils.toUnderlineJSONString(device),Device.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Device update(String uuid,Device device) {
        try {
            return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/tgm/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
