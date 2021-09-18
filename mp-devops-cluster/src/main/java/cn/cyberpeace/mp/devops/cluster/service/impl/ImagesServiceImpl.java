package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
    public BaseCloudPlatformModel upload(Map<String,File> fileList, String type){
        if (MapUtils.isEmpty(fileList)){
            return null;
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        fileList.entrySet().stream().forEach(entry -> builder.addBinaryBody(entry.getKey(), entry.getValue()));
        try {
            return super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + "/file_upload?type=" + type, builder.build(), BaseCloudPlatformModel.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ImageConnectionCloudPlatform connection(String deviceUuid){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/normal/connection?device_uuid=" + deviceUuid,ImageConnectionCloudPlatform.class);
    }
}
