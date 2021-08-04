package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.RouterCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.RouterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-路由
 * @author wanggang
 */
@Service
public class RouterServiceImpl extends BaseOpenStackServiceImpl implements RouterService {

    public RouterServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public RouterCloudPlatformModel page(String getParams) {
        return super.sendGet(URL_VERSION + "/routers" + getParams, RouterCloudPlatformModel.class);
    }

}
