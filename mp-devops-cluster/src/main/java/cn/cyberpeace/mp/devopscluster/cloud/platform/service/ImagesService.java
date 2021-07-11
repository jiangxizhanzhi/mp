package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtDocker;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.DeviceQuickCreateCloudPlatformModel;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;


@Service
@Slf4j
public class ImagesService extends BaseCpService {

    private static final String NORMAL = "/normal";

    public ImagesService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public Device add(Device device){
        Device result = super.sendPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + NORMAL + "/lists", JsonUtils.toUnderlineJSONString(device), Device.class);
        super.checkResult(result);
        return result;
    }

    
    public Device update(String uuid,Device device){
        final ExtVm extVm = device.getExtVm();
        
        if (Objects.nonNull(extVm)){
            extVm.setInstanceState(null);
        }
        final ExtDocker extDocker = device.getExtDocker();
        
        if (Objects.nonNull(extDocker)){
            extDocker.setInstanceState(null);
        }
        final ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo)){
            extImageInfo.setDeviceActions(null);
        }
        Device result = super.sendPut(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + NORMAL + "/" + uuid,JsonUtils.toUnderlineJSONString(device),Device.class);
        super.checkResult(result);
        return result;
    }

    
    public String export(String uuid, String imageName){
        BaseCloudPlatformResult result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/image/export?device_uuid=" + uuid + "&&image_name=" + imageName,10 * 60 * 1000, BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result.getUrl();
    }

    
    public Device quickCreate(DeviceQuickCreateCloudPlatformModel model){
        Device device = super.sendPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/quick_create", JsonUtils.toUnderlineJSONString(model), Device.class);
        super.checkResult(device);
        return device;
    }

    
    public BaseCloudPlatformResult upload(List<File> fileList, String type){
        if (CollectionUtils.isEmpty(fileList)){
            return null;
        }
        try {
            BaseCloudPlatformResult result = super.sendFormDataPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/file_upload?type=" + type, fileList, BaseCloudPlatformResult.class);
            super.checkResult(result);
            return result;
        } catch (IOException e) {
            log.error("upload file error",e);
        } finally {
            fileList.forEach(File::delete);
        }
        return null;
    }

    
    public CloudPlatformPageResult<TargetConnection> connection(String deviceUuid){
        CloudPlatformItemsPageResult pageResult =  super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + NORMAL + "/connection?device_uuid=" + deviceUuid + "&user_id=" + (Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : ""),CloudPlatformItemsPageResult.class);
        super.checkResult(pageResult);
        List<TargetConnection> list = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), TargetConnection.class);
        DevopsClusterConfig config = BeanCommon.getBean(DevopsClusterConfig.class);
        if (config.getPublicNetProxy() && config.getPublicNetProxyServer()) {
            list.forEach(l -> {
                if (StringUtils.isEmpty(config.getPublicNetProxyIp())) {
                    return;
                }
                var u = URLUtil.url(l.getUrl());
                l.setUrl(StringUtils.replaceOnce(l.getUrl(), u.getHost(), config.getPublicNetProxyIp()));
                if (config.getPublicNetProxyExcludeProtocols().contains(l.getProtocol())) {
                    return;
                }
                l.setIpAddress(config.getPublicNetProxyIp());
                if (config.getPublicNetProxyPort() != null) {
                    l.setPort(config.getPublicNetProxyPort());
                }
            });
        }
        CloudPlatformPageResult<TargetConnection> model = new CloudPlatformPageResult<>();
        model.setList(list);
        return model;
    }
}
