package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.model.HypervisorCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorWeightAndMaxInstanceCloudPlatformModel;

/**
 * 云平台监控
 */
public interface HypervisorService extends BaseCloudPlatformService {

    String HYPERVISORS = "/hypervisors";

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

}
