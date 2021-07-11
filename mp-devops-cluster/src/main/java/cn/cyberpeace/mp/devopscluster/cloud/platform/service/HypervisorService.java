package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.HypervisorStatisticsCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.model.HypervisorWeightAndMaxInstanceCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.model.common.Hypervisor;
import cn.cyberpeace.mp.devopscluster.model.common.HypervisorStatistics;
import cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class HypervisorService extends BaseCpService {

    public static final String HYPERVISORS = "/hypervisors";

    public HypervisorService(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    
    public HypervisorStatisticsCloudPlatformResult statistics(){
        CloudPlatformResult result = super.sendGet(BaseCloudPlatformConstant.URL_VERSION + HYPERVISORS + "/statistics", CloudPlatformResult.class);
        super.checkResult(result);
        HypervisorStatistics data = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), HypervisorStatistics.class);
        HypervisorStatisticsCloudPlatformResult model = new HypervisorStatisticsCloudPlatformResult();
        model.setHypervisorStatistics(data);
        model.setCode(result.getCode());
        return model;
    }

    
    public CloudPlatformPageResult<Hypervisor> page(String getParams){
        return super.page(BaseCloudPlatformConstant.URL_VERSION + HYPERVISORS + getParams, Hypervisor.class);
    }

    
    public HypervisorWeightAndMaxInstanceCloudPlatformResult getWeightAndMaxInstances(String getParams){
        return super.sendGet(BaseCloudPlatformConstant.CPCLOUD_INFO_URL + BaseCloudPlatformConstant.URL_VERSION + "/vm_status" + getParams, HypervisorWeightAndMaxInstanceCloudPlatformResult.class);
    }

    
    public void rebootOrPowerOff(String json){
        super.sendPost(BaseCloudPlatformConstant.CPCLOUD_INFO_URL + BaseCloudPlatformConstant.URL_VERSION + "/compute_control", json);
    }

}
