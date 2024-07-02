package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.RouterModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
@Service
@Slf4j
public class RouterService extends BaseCpService {
    private static final String LIST_ROUTER = "/cloud/v1/router";


    /**
     * RouterService
     *
     * @param devopsClusterConfig
     */
    public RouterService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询路由信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<RouterModel> page(String getParams) {
        return super.page(LIST_ROUTER + getParams, RouterModel.class);
    }
}
