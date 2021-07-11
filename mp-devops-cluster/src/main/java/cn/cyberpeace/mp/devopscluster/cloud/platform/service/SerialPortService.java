package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.SerialPortCloudPlatform;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class SerialPortService extends BaseCpService {

    public SerialPortService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public SerialPortCloudPlatform list() {
        SerialPortCloudPlatform result = super.sendGet(BaseCloudPlatformConstant.CPCLOUD_CCM + BaseCloudPlatformConstant.URL_VERSION + "/list" , SerialPortCloudPlatform.class);
        return result;
    }
}
