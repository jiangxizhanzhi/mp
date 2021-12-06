//package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;
//
//import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ImagesService;
//import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
//import cn.cyberpeace.mp.devops.cluster.model.*;
//import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.MapUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//
///**
// * 镜像管理
// * @author wanggang
// */
//@Slf4j
//@Service
//public class ImagesServiceImpl extends BaseOpenStackServiceImpl implements ImagesService {
//
//    public ImagesServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
//        super(devopsConfig, objectMapper);
//    }
//
//    @Override
//    public DeviceCloudPlatformModel page(String getParams) {
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/lists" + getParams, DeviceCloudPlatformModel.class);
//    }
//
//    @Override
//    public Device add(Device device) {
//        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/lists", JsonUtils.toUnderlineJSONString(device), Device.class);
//    }
//
//
//    @Override
//    public Device update(String uuid,Device device) {
//        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
//    }
//
//    @Override
//    public HttpResponse export(String uuid,String imageName) {
//        return super.downLoad(CPCLOUD_DEVICE + URL_VERSION + "/image/export?device_uuid=" + uuid + "&&image_name=" + imageName);
//    }
//
//    @Override
//    public Device quickCreate(DeviceQuickCreateCloudPlatformModel model) {
//        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/quick_create", JsonUtils.toUnderlineJSONString(model), Device.class);
//    }
//
//
//    @Override
//    public BaseCloudPlatformModel upload(Map<String,File> fileList, String type){
//        if (MapUtils.isEmpty(fileList)){
//            return null;
//        }
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        fileList.entrySet().stream().forEach(entry -> builder.addBinaryBody(entry.getKey(), entry.getValue()));
//        try {
//            return super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + "/file_upload?type=" + type, builder.build(), BaseCloudPlatformModel.class);
//        } catch (IOException e) {
//            log.error("upload file error",e);
//        }
//        return null;
//    }
//
//    @Override
//    public ImageConnectionCloudPlatform connection(String deviceUuid){
//        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/connection?device_uuid=" + deviceUuid,ImageConnectionCloudPlatform.class);
//    }
//}
