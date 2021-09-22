package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.HypervisorPageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.HypervisorStatisticsResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.InstanceOsServicePageResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.NetworkAgentPageResult;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.model.common.Hypervisor;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceOsServiceModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devops.cluster.service.HypervisorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        HypervisorStatisticsResult result = super.sendGet(URL_VERSION + "/hypervisors/statistics", HypervisorStatisticsResult.class);
        if (Objects.isNull(result)){
            return null;
        }
        HypervisorStatisticsCloudPlatformModel model = new HypervisorStatisticsCloudPlatformModel();
        model.setHypervisorStatistics(result.getData());
        model.setCode(result.getCode());
        return model;
    }

    @Override
    public HypervisorCloudPlatformModel page(String getParams) {
        HypervisorPageResult pageResult =  super.sendGet(URL_VERSION + "/hypervisors" + getParams, HypervisorPageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<Hypervisor> data = pageResult.getData();
        List<Hypervisor> hypervisors = data.getList();
        HypervisorCloudPlatformModel model = new HypervisorCloudPlatformModel();
        model.setHypervisors(CollectionUtils.isEmpty(hypervisors) ? Lists.newArrayList() : hypervisors);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
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
        InstanceOsServicePageResult pageResult = super.sendGet(URL_VERSION + "/instances/os-services", InstanceOsServicePageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<InstanceOsServiceModel> data = pageResult.getData();
        List<InstanceOsServiceModel> list = data.getList();
        InstanceOsServiceCloudPlatformModel model = new InstanceOsServiceCloudPlatformModel();
        model.setServices(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }

    /**
     *
     * @return
     */
    public NetworkAgentCloudPlatformModel agentsStateFromNetworks(){
        NetworkAgentPageResult pageResult = super.sendGet(URL_VERSION + "/networks/agents", NetworkAgentPageResult.class);
        if (Objects.isNull(pageResult) || Objects.isNull(pageResult.getData())){
            return null;
        }
        CloudPlatformPageResult<NetworkAgentModel> data = pageResult.getData();
        List<NetworkAgentModel> list = data.getList();
        NetworkAgentCloudPlatformModel model = new NetworkAgentCloudPlatformModel();
        model.setAgents(CollectionUtils.isEmpty(list) ? Lists.newArrayList() : list);
        model.setTotal(data.getTotal());
        model.setCode(pageResult.getCode());
        return model;
    }


    @Override
    public VirtualRealSwitchCloudPlatformModel listVirtualRealSwitch() {
        return super.sendGet(CPCLOUD_SWITCH_URL + URL_VERSION + "/switchs" , VirtualRealSwitchCloudPlatformModel.class);
    }

}
