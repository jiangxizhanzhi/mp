package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.SwitchesCloudPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_SWITCH_URL;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
@Service
@Slf4j
public class SwitchService extends BaseOpenStackService {


    public SwitchService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 查询交换机信息
     * @return
     */
    public SwitchesCloudPlatform list() {
        return super.sendGet(CPCLOUD_SWITCH_URL + URL_VERSION + "/switchs" , SwitchesCloudPlatform.class);
    }

}
