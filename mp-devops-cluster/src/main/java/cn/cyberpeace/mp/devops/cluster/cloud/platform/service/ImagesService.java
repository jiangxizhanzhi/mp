package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.DeviceQuickCreateCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 镜像管理底层接口
 * @author wanggang
 */
@Service
@Slf4j
public class ImagesService extends BaseCpService {

    private static final String NORMAL = "/normal";

    public ImagesService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 新增
     */
    public Device add(Device device){
        Device result = super.sendPost(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/lists", JsonUtils.toUnderlineJSONString(device), Device.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 更新
     */
    public Device update(String uuid,Device device){
        Device result = super.sendPut(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
        super.checkResult(result);
        return result;
    }

    /**
     * 导出
     */
    public String export(String uuid, String imageName){
        BaseCloudPlatformResult result = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + "/image/export?device_uuid=" + uuid + "&&image_name=" + imageName,10 * 60 * 1000, BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result.getUrl();
    }

    /**
     * 快速构建
     * @param model
     * @return
     */
    public Device quickCreate(DeviceQuickCreateCloudPlatformModel model){
        Device device = super.sendPost(CPCLOUD_DEVICE + URL_VERSION + "/quick_create", JsonUtils.toUnderlineJSONString(model), Device.class);
        super.checkResult(device);
        return device;
    }

    /**
     * 上传文件
     * @param fileList
     * @param type :  image or installer
     * @return
     */
    public BaseCloudPlatformResult upload(List<File> fileList, String type){
        if (CollectionUtils.isEmpty(fileList)){
            return null;
        }
        try {
            BaseCloudPlatformResult result = super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + "/file_upload?type=" + type, fileList, BaseCloudPlatformResult.class);
            super.checkResult(result);
            return result;
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
    public CloudPlatformPageResult<TargetConnection> connection(String deviceUuid){
        CloudPlatformItemsPageResult pageResult =  super.sendGet(CPCLOUD_DEVICE + URL_VERSION + NORMAL + "/connection?device_uuid=" + deviceUuid + "&user_id=" + (Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : ""),CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TargetConnection> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()),TargetConnection.class);
        CloudPlatformPageResult<TargetConnection> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }
}
