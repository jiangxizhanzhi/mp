package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.ElasticIpModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkModel;
import cn.cyberpeace.mp.devopscluster.model.common.VlanIpStockModel;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class NetworkService extends BaseCpService {

    private static final String NETWORKS = "/networks";


    public NetworkService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public CloudPlatformPageResult<NetworkModel> page(String getParams){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + getParams, NetworkModel.class);
    }


    
    public CloudPlatformResult<VlanIpStockModel> vlanIpStock(String getParams){
        CloudPlatformResult result = super.sendGet(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/vlan_ip_stock" + getParams, CloudPlatformResult.class);
        super.checkResult(result);
        VlanIpStockModel data = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), VlanIpStockModel.class);
        CloudPlatformResult cpresult = new CloudPlatformResult();
        cpresult.setData(data);
        cpresult.setCode(result.getCode());
        return cpresult;
    }

    
    public CloudPlatformPageResult<ElasticIpModel> pageElasticIp(String getParams){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/floatingips" + getParams, ElasticIpModel.class);
    }

    
    public CloudPlatformPageResult<NetworkAgentModel> agentsStateFromNetworks(){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + NETWORKS + "/agents", NetworkAgentModel.class);
    }
}
