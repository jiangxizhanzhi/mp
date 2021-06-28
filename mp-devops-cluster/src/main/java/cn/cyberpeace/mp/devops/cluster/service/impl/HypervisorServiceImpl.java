package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.config.OpenStackConfig;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 云平台监控
 */
@Service
public class HypervisorServiceImpl extends BaseOpenStackServiceImpl implements HypervisorService {


    public HypervisorServiceImpl(OpenStackConfig openStackConfig, ObjectMapper objectMapper) {
        super(openStackConfig, objectMapper);
    }

    @Override
    public HypervisorStatisticsOpenStackModel statistics() {
        return super.sendGet(URL_VERSION + "/hypervisors/statistics",HypervisorStatisticsOpenStackModel.class);
    }

    @Override
    public HypervisorOpenStackModel pageHypervisors(HypervisorPageRequest request) {
        return super.sendGet(URL_VERSION + "/hypervisors" + BeanUtils.wrapperGetParamsByObject(request),HypervisorOpenStackModel.class);
    }

    @Override
    public HypervisorWeightAndMaxInstanceOpenStackModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceRequest request) {
        return super.sendGet(CPCLOUD_INFO_URL + URL_VERSION + "/vm_status" + BeanUtils.wrapperGetParamsByObject(request),HypervisorWeightAndMaxInstanceOpenStackModel.class);
    }

    @Override
    public void rebootOrPowerOff(HypervisorRequest request) {
        super.sendPost(CPCLOUD_INFO_URL + URL_VERSION + "/compute_control", JSONObject.toJSONString(request));
    }

    /**
     * 
     * @return
     */
    public InstanceOsServiceOpenStackModel osServicesStateFromInstances(){
       return super.sendGet(URL_VERSION + "/instances/os-services", InstanceOsServiceOpenStackModel.class);
    }

    /**
     *
     * @return
     */
    public NetworkAgentOpenStackModel agentsStateFromNetworks(){
       return super.sendGet(URL_VERSION + "/networks/agents", NetworkAgentOpenStackModel.class);
    }

    @Override
    public List<HypervisorServiceStateResponse> serviceState() {
        InstanceOsServiceOpenStackModel osServiceOpenStackModel = this.osServicesStateFromInstances();
        NetworkAgentOpenStackModel networkAgentOpenStackModel = this.agentsStateFromNetworks();

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
    public HypervisorComputeStatusOpenStackModel computeStatus(HypervisorComputeStatusRequest request) {
        return super.sendGet(CPCLOUD_INFO_URL + URL_VERSION + "/compute_status" + BeanUtils.wrapperGetParamsByObject(request), HypervisorComputeStatusOpenStackModel.class);
    }

    /**
     * 获取所有节点的磁盘信息、网络IO、内存信息、网络IO、CPU信息
     * @return
     */
    @Override
    public List<HypervisorComputeStatusOpenStackModel> computeStatus(){
        HypervisorOpenStackModel model = this.pageHypervisors(new HypervisorPageRequest());
        List<HypervisorOpenStackModel.Hypervisor> hypervisorList = model.getHypervisors();
        if (CollectionUtils.isEmpty(hypervisorList)){
            return new ArrayList<>();
        }

        return hypervisorList.stream().map(hypervisor ->
            this.computeStatus(new HypervisorComputeStatusRequest(hypervisor.getHostname()))
        ).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
