package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.common.ComputeStatusModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
@Slf4j
public class ComputeStatusService extends BaseCpService {

    private static final String COMPUTE_STATUS = "/compute_status";


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
