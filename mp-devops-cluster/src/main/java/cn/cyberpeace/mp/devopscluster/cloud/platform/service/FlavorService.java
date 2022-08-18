package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devopscluster.config.DevopsConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Flavor
 * @author wanggang
 */
@Service
@Slf4j
public class FlavorService extends BaseCpService {

    public FlavorService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Flavor> page(String getParams) {
        return super.page(BaseCloudPlatformConstant.URL_VERSION + "/flavors" + getParams, Flavor.class);
    }
}
