package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorWeightAndMaxInstanceCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.model.common.Hypervisor;
import cn.cyberpeace.mp.devops.cluster.model.common.HypervisorStatistics;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_INFO_URL;
import static cn.cyberpeace.mp.devops.cluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;

/**
 * 云平台监控
 * @author wanggang
 */
@Service
@Slf4j
public class HypervisorService extends BaseOpenStackService {

    public static final String HYPERVISORS = "/hypervisors";

    public HypervisorService(DevopsConfig devopsConfig) {
        super(devopsConfig);
    }

    /**
     * 资源统计
     * @return
     */
    public HypervisorStatisticsCloudPlatformResult statistics(){
        CloudPlatformResult result = super.sendGet(URL_VERSION + HYPERVISORS + "/statistics", CloudPlatformResult.class);
        super.checkResult(result);
        HypervisorStatistics data = JSONObject.parseObject(JSONObject.toJSONString(result.getData()), HypervisorStatistics.class);
        HypervisorStatisticsCloudPlatformResult model = new HypervisorStatisticsCloudPlatformResult();
        model.setHypervisorStatistics(data);
        model.setCode(result.getCode());
        return model;
    }

    /**
     * 分页查询节点管理信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageResult<Hypervisor> page(String getParams){
        return super.page(URL_VERSION + HYPERVISORS + getParams, Hypervisor.class);
    }

    /**
     * 获取虚拟机数量限制
     * @return
     */
    public HypervisorWeightAndMaxInstanceCloudPlatformResult getWeightAndMaxInstances(String getParams){
        return super.sendGet(CPCLOUD_INFO_URL + URL_VERSION + "/vm_status" + getParams, HypervisorWeightAndMaxInstanceCloudPlatformResult.class);
    }

    /**
     * 节点管理-重启/关机
     */
    public void rebootOrPowerOff(String json){
        super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/compute_control", json);
    }

}
