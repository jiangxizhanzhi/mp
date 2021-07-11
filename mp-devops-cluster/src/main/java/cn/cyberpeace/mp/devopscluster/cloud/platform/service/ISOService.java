package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Service
@Slf4j
public class ISOService extends BaseCpService {

    public ISOService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public BaseCloudPlatformResult upload(List<File> files) {
        BaseCloudPlatformResult result = null;
        try {
            result = super.sendFormDataPost(BaseCloudPlatformConstant.CPCLOUD_DEVICE +
                    BaseCloudPlatformConstant.URL_VERSION + "/iso", files, BaseCloudPlatformResult.class);
        } catch (IOException e) {
            log.error("upload file error", e);
        } finally {
            files.forEach(File::delete);
        }
        super.checkResult(result);
        return result;
    }

    public BaseCloudPlatformResult download(String tmpdirUuid, String fileName) {
        var result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE +
                BaseCloudPlatformConstant.URL_VERSION + "/iso/" + tmpdirUuid + "/" + fileName, BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    public void delete(String tmpdirUuid, String fileName) {
        var result = super.sendDelete(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/iso/" +
                tmpdirUuid + "/" + fileName, null, BaseCloudPlatformResult.class);
        super.checkResult(result);
    }
}
