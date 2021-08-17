package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.NetworkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
public class NetworkServiceImpl extends BaseOpenStackServiceImpl implements NetworkService {

    public NetworkServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public NetworkCloudPlatformModel page(String getParams) {
        return super.sendGet(URL_VERSION + "/networks" + getParams, NetworkCloudPlatformModel.class);
    }


}
