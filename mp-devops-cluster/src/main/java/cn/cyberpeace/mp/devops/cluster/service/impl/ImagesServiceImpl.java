package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceQuickCreateCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

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
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/normal/lists", JsonUtils.toUnderlineJSONString(device), Device.class);
    }


    @Override
    public Device update(String uuid,Device device) {
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + "/normal/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
    }

    @Override
    public void build() {

    }

    @Override
    public HttpResponse export(String uuid) {
        return super.downLoad(CPCLOUD_DEVICE + URL_VERSION + "/image/export?image_uuid=" + uuid);
    }


    @Override
    public DeviceCloudPlatformModel pageForQuickCreate(String getParams) {
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/quick_create"+ getParams, DeviceCloudPlatformModel.class);
    }

    @Override
    public Device quickCreate(DeviceQuickCreateCloudPlatformModel model) {
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/quick_create", JsonUtils.toUnderlineJSONString(model), Device.class);
    }


    @Override
    public String upload(String params,File file) {
        try {
            return super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + "/upload/" + params,file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
