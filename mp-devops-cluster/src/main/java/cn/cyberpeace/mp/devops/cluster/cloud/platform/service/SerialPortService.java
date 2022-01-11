package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.SerialPortCloudPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_CCM;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-串口
 * @author wanggang
 */
@Service
@Slf4j
public class SerialPortService extends BaseOpenStackService {

    public SerialPortService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 查询串口信息
     * @return
     */
    public SerialPortCloudPlatform list() {
        return super.sendGet(CPCLOUD_CCM + URL_VERSION + "/list" , SerialPortCloudPlatform.class);
    }
}
