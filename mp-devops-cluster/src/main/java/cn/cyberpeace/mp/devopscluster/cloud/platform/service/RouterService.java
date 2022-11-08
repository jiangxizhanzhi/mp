package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.RouterModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
@Service
@Slf4j
public class RouterService extends BaseCpService {

    public RouterService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询路由信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<RouterModel> page(String getParams) {
        return super.page(BaseCloudPlatformConstant.URL_VERSION + "/routers" + getParams, RouterModel.class);
    }
}
