package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.FlavorCloudPlatformModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * Flavor
 * @author wanggang
 */
@Service
@Slf4j
public class FlavorService extends BaseOpenStackServiceImpl {

    public FlavorService(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    /**
     * 分页查询
     * @param getParams
     * @return
     */
    public FlavorCloudPlatformModel page(String getParams) {
        return super.sendGet(  URL_VERSION + "/flavors" + getParams, FlavorCloudPlatformModel.class);
    }
}
