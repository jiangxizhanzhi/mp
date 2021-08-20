package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ImagesServiceImpl extends BaseOpenStackServiceImpl implements ImagesService {

    public ImagesServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public DeviceCloudPlatformModel page(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/normal/lists" + getParams, DeviceCloudPlatformModel.class);
    }

    @Override
    public Device add(Device device) {
        try {
            return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/normal/lists", JsonUtils.toUnderlineJSONString(device), Device.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }


    @Override
    public Device update(String uuid,Device device) {
        try {
            return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/normal/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public void build() {

    }

    @Override
    public void export() {

    }
}
