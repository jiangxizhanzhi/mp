package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.SwitchesCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SwitchService extends BaseCpService {

    public SwitchService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public SwitchesCloudPlatform list() {
        return super.sendGet(BaseCloudPlatformConstant.CPCLOUD_SWITCH_URL + BaseCloudPlatformConstant.URL_VERSION + "/switchs" , SwitchesCloudPlatform.class);
    }

}
