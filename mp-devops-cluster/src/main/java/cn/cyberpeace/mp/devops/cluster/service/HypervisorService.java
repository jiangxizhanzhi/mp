package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorComputeStatusOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorWeightAndMaxInstanceOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorComputeStatusRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorWeightAndMaxInstanceRequest;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorServiceStateResponse;

import java.util.List;

/**
 * 云平台监控
 */
public interface HypervisorService extends BaseOpenStackService{

    String[] DEFAULT_PAGE_IGNORE_FIELD = BeanUtils.getBeanGetterFieldName(HypervisorPageRequest::getPage,HypervisorPageRequest::getPerPage);

    String[] TOTAL_PAGE_IGNORE_FIELD = BeanUtils.getBeanGetterFieldName(HypervisorPageRequest::getLimit,HypervisorPageRequest::getOffset,
            HypervisorPageRequest::getPage,HypervisorPageRequest::getPerPage);

    /**
     * 资源统计
     * @return
     */
    HypervisorStatisticsOpenStackModel statistics();

    /**
     * 查询节点管理信息
     * @return
     */
    HypervisorOpenStackModel pageHypervisors(HypervisorPageRequest request,String... ignoreFields);

    /**
     * 获取虚拟机数量限制
     * @return
     */
    HypervisorWeightAndMaxInstanceOpenStackModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceRequest request);

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
    HypervisorComputeStatusOpenStackModel computeStatus(HypervisorComputeStatusRequest request);

    /**
     * 获取所有节点的磁盘信息、网络IO、内存信息、网络IO、CPU信息
     * @return
     */
    List<HypervisorComputeStatusOpenStackModel> computeStatus();
}
