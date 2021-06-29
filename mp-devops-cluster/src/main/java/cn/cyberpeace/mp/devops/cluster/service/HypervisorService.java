package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorComputeStatusRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorWeightAndMaxInstanceRequest;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorServiceStateResponse;

import java.util.List;

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
     * 查询节点管理信息
     * @return
     */
    HypervisorCloudPlatformModel pageHypervisors(HypervisorPageRequest request, String... ignoreFields);

    /**
     * 获取虚拟机数量限制
     * @return
     */
    HypervisorWeightAndMaxInstanceCloudPlatformModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceRequest request);

    /**
     * 节点管理-重启/关机
     */
    void rebootOrPowerOff(HypervisorRequest request);

    /**
     * 服务状态监控
     */
    List<HypervisorServiceStateResponse> serviceState();

    /**
     * 计算状态监控
     * @param request
     * @return
     */
    HypervisorComputeStatusCloudPlatformModel computeStatus(HypervisorComputeStatusRequest request);

    /**
     * 获取所有节点的磁盘信息、网络IO、内存信息、网络IO、CPU信息
     * @return
     */
    List<HypervisorComputeStatusCloudPlatformModel> computeStatus();

    /**
     * 查询虚拟结合交换机状态列表
     * @return
     */
    VirtualRealSwitchCloudPlatformModel listVirtualRealSwitch();
}
