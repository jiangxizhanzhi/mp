package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.ElasticIpModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkModel;
import cn.cyberpeace.mp.devopscluster.model.common.VlanIpStockModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 运维管控-系统运维-网络实例管理-网络
 * @author wanggang
 */
@Service
@Slf4j
public class NetworkService extends BaseCpService {

    private static final String NETWORKS = "/networks";


    public NetworkService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    /**
     * 分页查询网络信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<NetworkModel> page(String getParams){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + getParams, NetworkModel.class);
    }


    /**
     * 查询网络vlan ip信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<VlanIpStockModel> vlanIpStock(String getParams){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/vlan_ip_stock" + getParams, VlanIpStockModel.class);
    }

    /**
     * 分页查询弹性IP
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<ElasticIpModel> pageElasticIp(String getParams){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/floatingips" + getParams, ElasticIpModel.class);
    }

    /**
     *
     * @return
     */
    public CloudPlatformPageResult<NetworkAgentModel> agentsStateFromNetworks(){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/agents", NetworkAgentModel.class);
    }
}
