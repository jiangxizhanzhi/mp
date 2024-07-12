 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ISOService.java.
 // File size 2536.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName ISOService
 * @Description 镜像管理底层接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class ISOService extends BaseCpService {

    private static final String UPLOAD_ISO_FILE = "/cloud/v1/device/upload?type=iso";

    /**
     * ISOService
     *
     * @param devopsClusterConfig
     */
    public ISOService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 上传iso文件
     * @param files
     * @return
     */
    public BaseCloudPlatformResult upload(List<File> files) {
        BaseCloudPlatformResult result = null;
        try {
            result = super.sendFormDataPost(UPLOAD_ISO_FILE, files, BaseCloudPlatformResult.class);
        } catch (IOException e) {
            log.error("upload file error", e);
        } finally {
            for (File file : files) {
                file.delete();
            }
        }
        super.checkResult(result);
        return result;
    }

    /**
     * download
     *
     * @param tmpdirUuid
     * @param fileName
     * @return 基础模型，对应云平台接口返回信息
     */
    public BaseCloudPlatformResult download(String tmpdirUuid, String fileName) {
        BaseCloudPlatformResult result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE +
                BaseCloudPlatformConstant.URL_VERSION + "/iso/" + tmpdirUuid + "/" + fileName, BaseCloudPlatformResult.class);
        super.checkResult(result);
        return result;
    }

    /**
     * delete
     *
     * @param tmpdirUuid
     * @param fileName
     */
    public void delete(String tmpdirUuid, String fileName) {
        BaseCloudPlatformResult result = super.sendDelete(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + "/iso/" +
                tmpdirUuid + "/" + fileName, null, BaseCloudPlatformResult.class);
        super.checkResult(result);
    }
}
