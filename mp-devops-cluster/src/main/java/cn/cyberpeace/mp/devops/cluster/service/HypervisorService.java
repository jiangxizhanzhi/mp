package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;

/**
 * 云平台监控
 */
public interface HypervisorService extends BaseCloudPlatformService {

    /**
     * 资源统计
     * @return
     */
    HypervisorStatisticsCloudPlatformModel statistics();

    /**
     * 分页查询节点管理信息
     * @param getParams
     * @return
     */
    HypervisorCloudPlatformModel page(String getParams);

    /**
     * 获取虚拟机数量限制
     * @return
     */
    HypervisorWeightAndMaxInstanceCloudPlatformModel getWeightAndMaxInstances(String getParams);

    /**
     * 节点管理-重启/关机
     */
    void rebootOrPowerOff(String json);

    /**
     *
     * @return
     */
    InstanceOsServiceCloudPlatformModel osServicesStateFromInstances();

    /**
     *
     * @return
     */
    NetworkAgentCloudPlatformModel agentsStateFromNetworks();
}
