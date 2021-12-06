package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl.BaseOpenStackServiceImpl;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.SerialPortCloudPlatform;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.CPCLOUD_CCM;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.service.BaseCloudPlatformService.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-串口
 * @author wanggang
 */
@Service
@Slf4j
public class SerialPortService extends BaseOpenStackServiceImpl {

    public SerialPortService(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    /**
     * 查询串口信息
     * @return
     */
    public SerialPortCloudPlatform list() {
        return super.sendGet(CPCLOUD_CCM + URL_VERSION + "/list" , SerialPortCloudPlatform.class);
    }
}
