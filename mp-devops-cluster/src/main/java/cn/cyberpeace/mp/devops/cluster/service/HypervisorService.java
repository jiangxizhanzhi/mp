package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.HypervisorOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsOpenStackModel;

/**
 * 资源统计
 */
public interface HypervisorService extends BaseOpenStackService{

    /**
     * 资源统计
     * @return
     */
    HypervisorStatisticsOpenStackModel statistics();

    /**
     * 查询节点管理信息
     * @return
     */
    HypervisorOpenStackModel page();
}
