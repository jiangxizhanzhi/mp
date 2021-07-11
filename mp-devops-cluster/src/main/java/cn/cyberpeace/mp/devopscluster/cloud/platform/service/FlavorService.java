package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class FlavorService extends BaseCpService {

    public FlavorService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<Flavor> page(String getParams) {
        return super.page(BaseCloudPlatformConstant.URL_VERSION + "/flavors" + getParams, Flavor.class);
    }
}
