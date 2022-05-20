package cn.cyberpeace.mp.devops.cluster.cloud.platform.service;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.HypervisorStatisticsResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorWeightAndMaxInstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.Hypervisor;
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
    public HypervisorStatisticsCloudPlatformModel statistics(){
        HypervisorStatisticsResult result = super.sendGet(URL_VERSION + HYPERVISORS + "/statistics", HypervisorStatisticsResult.class);
        super.checkResult(result);
        HypervisorStatisticsCloudPlatformModel model = new HypervisorStatisticsCloudPlatformModel();
        model.setHypervisorStatistics(result.getData());
        model.setCode(result.getCode());
        return model;
    }

    /**
     * 分页查询节点管理信息
     * @param getParams
     * @return
     */
    public CloudPlatformPageModel<Hypervisor> page(String getParams){
        return super.page(URL_VERSION + HYPERVISORS + getParams, Hypervisor.class);
    }

    /**
     * 获取虚拟机数量限制
     * @return
     */
    public HypervisorWeightAndMaxInstanceCloudPlatformModel getWeightAndMaxInstances(String getParams){
        return super.sendGet(CPCLOUD_INFO_URL + URL_VERSION + "/vm_status" + getParams, HypervisorWeightAndMaxInstanceCloudPlatformModel.class);
    }

    /**
     * 节点管理-重启/关机
     */
    public void rebootOrPowerOff(String json){
        super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/compute_control", json);
    }

}
