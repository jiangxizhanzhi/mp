 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SerialPortService.java.
 // File size 1186.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.SerialPortCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName SerialPortService
 * @Description 运维管控-系统运维-网络实例管理-串口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
@Deprecated
public class SerialPortService extends BaseCpService {

    /**
     * SerialPortService
     *
     * @param devopsClusterConfig
     */
    public SerialPortService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 查询串口信息
     * @return
     */
    public SerialPortCloudPlatform list() {
        SerialPortCloudPlatform result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_CCM + BaseCloudPlatformConstant.URL_VERSION + "/list" , SerialPortCloudPlatform.class);
        return result;
    }
}
