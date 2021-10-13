package cn.cyberpeace.mp.devops.cluster.service;

import cn.cyberpeace.mp.devops.cluster.model.ElasticIpCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.NetworkAgentCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.NetworkCloudPlatformModel;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
public interface NetworkService extends BaseCloudPlatformService {

    String NETWORKS = "/networks";

    /**
     * 分页查询网络信息
     * @param getParams
     * @return
     */
    NetworkCloudPlatformModel page(String getParams);

    /**
     * 分页查询弹性IP
     * @param getParams
     * @return
     */
    ElasticIpCloudPlatformModel pageElasticIp(String getParams);

    /**
     *
     * @return
     */
    NetworkAgentCloudPlatformModel agentsStateFromNetworks();
}
