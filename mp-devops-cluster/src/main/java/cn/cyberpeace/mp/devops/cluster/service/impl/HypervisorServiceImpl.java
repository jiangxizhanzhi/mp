package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.service.HypervisorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * 云平台监控
 */
@Service
public class HypervisorServiceImpl extends BaseOpenStackServiceImpl implements HypervisorService {


    public HypervisorServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public HypervisorStatisticsCloudPlatformModel statistics() {
        return super.sendGet(URL_VERSION + "/hypervisors/statistics", HypervisorStatisticsCloudPlatformModel.class);
    }

    @Override
    public HypervisorCloudPlatformModel page(String getParams) {
        return super.sendGet(URL_VERSION + "/hypervisors" + getParams, HypervisorCloudPlatformModel.class);
    }

    @Override
    public HypervisorWeightAndMaxInstanceCloudPlatformModel getWeightAndMaxInstances(String getParams) {
        return super.sendGet(CPCLOUD_INFO_URL + URL_VERSION + "/vm_status" + getParams, HypervisorWeightAndMaxInstanceCloudPlatformModel.class);
    }

    @Override
    public void rebootOrPowerOff(String json) {
        super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/compute_control", json);
    }

    /**
     * 
     * @return
     */
    public InstanceOsServiceCloudPlatformModel osServicesStateFromInstances(){
       return super.sendGet(URL_VERSION + "/instances/os-services", InstanceOsServiceCloudPlatformModel.class);
    }

    /**
     *
     * @return
     */
    public NetworkAgentCloudPlatformModel agentsStateFromNetworks(){
       return super.sendGet(URL_VERSION + "/networks/agents", NetworkAgentCloudPlatformModel.class);
    }


    @Override
    public VirtualRealSwitchCloudPlatformModel listVirtualRealSwitch() {
        return super.sendGet(CPCLOUD_SWITCH_URL + URL_VERSION + "/switchs" , VirtualRealSwitchCloudPlatformModel.class);
    }

}
