package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.DeviceQuickCreateCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.ImageConnectionCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 镜像管理底层接口
 * @author wanggang
 */
@Service
@Slf4j
public class ImagesService extends BaseOpenStackService {

    private static final String NORMAL = "/normal";

    public ImagesService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询
     */
    public DeviceCloudPlatformModel page(String getParams){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/lists" + getParams, DeviceCloudPlatformModel.class);
    }

    /**
     * 新增
     */
    public Device add(Device device){
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/lists", JsonUtils.toUnderlineJSONString(device), Device.class);
    }

    /**
     * 更新
     */
    public Device update(String uuid,Device device){
        return super.sendPut(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
    }

    /**
     * 导出
     */
    public BaseCloudPlatformModel export(String uuid,String imageName){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/image/export?device_uuid=" + uuid + "&&image_name=" + imageName,BaseCloudPlatformModel.class);
    }

    /**
     * 快速构建
     * @param model
     * @return
     */
    public Device quickCreate(DeviceQuickCreateCloudPlatformModel model){
        return super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/quick_create", JsonUtils.toUnderlineJSONString(model), Device.class);
    }


    /**
     * 上传文件
     * @param fileMap
     * @param type :  image or installer
     * @return
     */
    public BaseCloudPlatformModel upload(Map<String,File> fileMap, String type){
        if (MapUtils.isEmpty(fileMap)){
            return null;
        }
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        fileMap.entrySet().stream().forEach(entry -> builder.addBinaryBody(entry.getKey(), entry.getValue()));
        try {
            return super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + "/file_upload?type=" + type, builder.build(), BaseCloudPlatformModel.class);
        } catch (IOException e) {
            log.error("upload file error",e);
        }
        return null;
    }

    /**
     * 根据靶标ID查询连接信息
     * @param deviceUuid
     * @return
     */
    public ImageConnectionCloudPlatform connection(String deviceUuid){
        return super.sendGet(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/connection?device_uuid=" + deviceUuid,ImageConnectionCloudPlatform.class);
    }
}
