package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.FlavorCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.FlavorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class FlavorServiceImpl  extends BaseOpenStackServiceImpl implements FlavorService {

    public FlavorServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public FlavorCloudPlatformModel page(String getParams) {
        return super.sendGet(  URL_VERSION + "/flavors" + getParams, FlavorCloudPlatformModel.class);
    }


}
