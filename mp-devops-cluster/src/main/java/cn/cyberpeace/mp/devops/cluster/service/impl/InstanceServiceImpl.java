package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.InstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.service.InstanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-计算实例管理-虚拟机
 * @author wanggang
 */
@Slf4j
@Service
public class InstanceServiceImpl extends BaseOpenStackServiceImpl implements InstanceService {

    public InstanceServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig,objectMapper);
    }

    @Override
    public InstanceCloudPlatformModel page(String getParams) {
        return super.sendGet(URL_VERSION + "/instances" + getParams, InstanceCloudPlatformModel.class);
    }


}
