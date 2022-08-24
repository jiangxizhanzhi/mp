package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsConfig;
import cn.cyberpeace.mp.devopscluster.model.SwitchesCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
@Service
@Slf4j
public class SwitchService extends BaseCpService {

    public SwitchService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 查询交换机信息
     * @return
     */
    public SwitchesCloudPlatform list() {
        return super.sendGet(BaseCloudPlatformConstant.CPCLOUD_SWITCH_URL + BaseCloudPlatformConstant.URL_VERSION + "/switchs" , SwitchesCloudPlatform.class);
    }

}
