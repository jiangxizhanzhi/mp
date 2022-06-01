package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.ElasticIpModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
@Slf4j
public class NetworkService extends BaseOpenStackService {

    private static final String NETWORKS = "/networks";

    public NetworkService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 分页查询网络信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<NetworkModel> page(String getParams){
        return super.page(URL_VERSION + NETWORKS + getParams, NetworkModel.class);
    }

    /**
     * 分页查询弹性IP
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<ElasticIpModel> pageElasticIp(String getParams){
        return super.page(URL_VERSION + NETWORKS + "/floatingips" + getParams, ElasticIpModel.class);
    }

    /**
     *
     * @return
     */
    public CloudPlatformPageResult<NetworkAgentModel> agentsStateFromNetworks(){
        return super.page(URL_VERSION + NETWORKS + "/agents", NetworkAgentModel.class);
    }
}
