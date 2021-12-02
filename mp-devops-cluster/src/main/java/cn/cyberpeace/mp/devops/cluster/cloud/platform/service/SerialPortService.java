package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.model.SerialPortCloudPlatform;

/**
 * 运维管控-系统运维-网络实例管理-串口
 * @author wanggang
 */
public interface SerialPortService extends BaseCloudPlatformService {

    /**
     * 查询串口信息
     * @return
     */
    SerialPortCloudPlatform list();
}
