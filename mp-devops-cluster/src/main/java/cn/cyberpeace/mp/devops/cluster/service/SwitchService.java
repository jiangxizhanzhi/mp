package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.SwitchesCloudPlatform;

/**
 * 运维管控-系统运维-网络实例管理-交换机
 * @author wanggang
 */
public interface SwitchService extends BaseCloudPlatformService {

    /**
     * 查询交换机信息
     * @return
     */
    SwitchesCloudPlatform list();
}
