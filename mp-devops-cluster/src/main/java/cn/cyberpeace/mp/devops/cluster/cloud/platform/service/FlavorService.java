package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Flavor;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * Flavor
 * @author wanggang
 */
@Service
@Slf4j
public class FlavorService extends BaseOpenStackService {

    public FlavorService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<Flavor> page(String getParams) {
        return super.page(URL_VERSION + "/flavors" + getParams, Flavor.class);
    }
}
