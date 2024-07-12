 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImportService.java.
 // File size 1561.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.ImportDiskSpaceCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName ImportService
 * @Description 靶标导入相关服务
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class ImportService extends BaseCpService {

    private static final String IMPORT = "/import";

    /**
     * ImportService
     *
     * @param devopsClusterConfig
     */
    public ImportService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 获取磁盘剩余空间
     *
     * @return
     */
    public ImportDiskSpaceCloudPlatformResult getDiskSpace(){
        ImportDiskSpaceCloudPlatformResult diskSpaceModel = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_DEVICE + BaseCloudPlatformConstant.URL_VERSION + IMPORT + "/disk_free", ImportDiskSpaceCloudPlatformResult.class);
        if (Objects.isNull(diskSpaceModel) || Objects.isNull(diskSpaceModel.getData()) || !diskSpaceModel.isSuccess()){
            return new ImportDiskSpaceCloudPlatformResult(new ImportDiskSpaceCloudPlatformResult.DiskSpace(0L));
        }

        return diskSpaceModel;
    }

}
