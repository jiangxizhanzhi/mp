package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import com.alibaba.fastjson.JSONObject;
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
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/normal/lists",JSONObject.toJSONString(device), Device.class);
    }


    @Override
    public String update(String uuid,Device device) {
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/normal/" + uuid,JSONObject.toJSONString(device));
    }

    @Override
    public void build() {

    }

    @Override
    public void export() {

    }
}
