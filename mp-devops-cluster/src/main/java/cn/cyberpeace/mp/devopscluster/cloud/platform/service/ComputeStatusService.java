 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ComputeStatusService.java.
 // File size 1375.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.common.ComputeStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName ComputeStatusService
 * @Description 运维管控-系统运维-网络实例管理-网络
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCpService
 */
@Service
@Slf4j
public class ComputeStatusService extends BaseCpService {

    private static final String COMPUTE_STATUS = "/compute_status";


    /**
     * ComputeStatusService
     *
     * @param devopsClusterConfig
     */
    public ComputeStatusService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 查询
     * @param getParams
     * @return
     */
    public CloudPlatformResult<ComputeStatusModel> getComputeStatus(String getParams){
        return super.sendGet(BaseCloudPlatformConstant.CPCLOUD_INFO_URL + BaseCloudPlatformConstant.URL_VERSION + COMPUTE_STATUS + getParams, CloudPlatformResult.class);
    }

}
