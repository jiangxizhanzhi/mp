package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Flavor
 * @author wanggang
 */
@Service
@Slf4j
public class FlavorService extends BaseCpService {

    private static final String LIST_FLAVOR = "/cloud/v1/flavor";

    /**
     * FlavorService
     *
     * @param devopsClusterConfig
     */
    public FlavorService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Flavor> page(String getParams) {
        return super.page(LIST_FLAVOR + getParams, Flavor.class);
    }
}
