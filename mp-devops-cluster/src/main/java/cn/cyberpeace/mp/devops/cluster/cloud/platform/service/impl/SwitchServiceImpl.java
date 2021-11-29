package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.SwitchService;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.SwitchesCloudPlatform;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
@Service
public class SwitchServiceImpl extends BaseOpenStackServiceImpl implements SwitchService {

    public SwitchServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public SwitchesCloudPlatform list() {
        return super.sendGet(CPCLOUD_SWITCH_URL + URL_VERSION + "/switchs" , SwitchesCloudPlatform.class);
    }


}
