package cn.cyberpeace.mp.devops.cluster.cloud.platform.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.SerialPortService;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.SerialPortCloudPlatform;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
@Service
public class SerialPortServiceImpl extends BaseOpenStackServiceImpl implements SerialPortService {

    public SerialPortServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public SerialPortCloudPlatform list() {
        return super.sendGet(CPCLOUD_CCM + URL_VERSION + "/list" , SerialPortCloudPlatform.class);
    }


}
