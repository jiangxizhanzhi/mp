package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorComputeStatusRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorWeightAndMaxInstanceRequest;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorServiceStateResponse;
import cn.cyberpeace.mp.devops.cluster.service.HypervisorService;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public HypervisorCloudPlatformModel pageHypervisors(HypervisorPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        return super.sendGet(URL_VERSION + "/hypervisors" + BeanUtils.wrapperGetParamsByObject(request,ignoreFields), HypervisorCloudPlatformModel.class);
    }

    @Override
    public HypervisorWeightAndMaxInstanceCloudPlatformModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceRequest request) {
        return super.sendGet(CPCLOUD_INFO_URL + URL_VERSION + "/vm_status" + BeanUtils.wrapperGetParamsByObject(request), HypervisorWeightAndMaxInstanceCloudPlatformModel.class);
    }

    @Override
    public void rebootOrPowerOff(HypervisorRequest request) {
        super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/compute_control", JSONObject.toJSONString(request));
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
    public List<HypervisorServiceStateResponse> serviceState() {
        InstanceOsServiceCloudPlatformModel osServiceOpenStackModel = this.osServicesStateFromInstances();
        NetworkAgentCloudPlatformModel networkAgentOpenStackModel = this.agentsStateFromNetworks();

        List<HypervisorServiceStateResponse> resultList = new ArrayList<>();

        if (Objects.nonNull(osServiceOpenStackModel)){
            resultList.addAll(osServiceOpenStackModel.getServices().stream().map(instanceOsServiceModel ->
                new HypervisorServiceStateResponse(instanceOsServiceModel.getBinary(),instanceOsServiceModel.getState())
            ).collect(Collectors.toList()));
        }

        if (Objects.nonNull(networkAgentOpenStackModel)){
            resultList.addAll(networkAgentOpenStackModel.getAgents().stream().map(networkAgentModel ->
                    new HypervisorServiceStateResponse(networkAgentModel.getBinary(),networkAgentModel.getAlive())).collect(Collectors.toList())
            );
        }
        return resultList;
    }

    @Override
    public HypervisorComputeStatusCloudPlatformModel computeStatus(HypervisorComputeStatusRequest request) {
        return super.sendGet(CPCLOUD_INFO_URL + URL_VERSION + "/compute_status" + BeanUtils.wrapperGetParamsByObject(request), HypervisorComputeStatusCloudPlatformModel.class);
    }

    /**
     * 获取所有节点的磁盘信息、网络IO、内存信息、网络IO、CPU信息
     * @return
     */
    @Override
    public List<HypervisorComputeStatusCloudPlatformModel> computeStatus(){
        HypervisorCloudPlatformModel model = this.pageHypervisors(new HypervisorPageRequest(),this.getTotalPageIgnoreField());
        List<HypervisorCloudPlatformModel.Hypervisor> hypervisorList = model.getHypervisors();
        if (CollectionUtils.isEmpty(hypervisorList)){
            return new ArrayList<>();
        }

        return hypervisorList.stream().map(hypervisor ->
            this.computeStatus(new HypervisorComputeStatusRequest(hypervisor.getHostname()))
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(HypervisorPageRequest::getPage,HypervisorPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(HypervisorPageRequest::getLimit,HypervisorPageRequest::getOffset,
                HypervisorPageRequest::getPage,HypervisorPageRequest::getPerPage);
    }

    @Override
    public VirtualRealSwitchCloudPlatformModel listVirtualRealSwitch() {
        return super.sendGet(CPCLOUD_SWITCH_URL + URL_VERSION + "/switchs" , VirtualRealSwitchCloudPlatformModel.class);
    }

}
