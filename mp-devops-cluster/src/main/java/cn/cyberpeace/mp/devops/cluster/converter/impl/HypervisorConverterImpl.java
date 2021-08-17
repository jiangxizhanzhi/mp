package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorWeightAndMaxInstanceRequest;
import cn.cyberpeace.mp.devops.cluster.service.HypervisorService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HypervisorConverterImpl implements HypervisorConverter {

    private HypervisorService hypervisorService;

    @Autowired
    public HypervisorConverterImpl(HypervisorService hypervisorService) {
        this.hypervisorService = hypervisorService;
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
    public HypervisorStatisticsConvertModel statistics() {
        HypervisorStatisticsCloudPlatformModel statistics = hypervisorService.statistics();
        HypervisorStatisticsCloudPlatformModel.HypervisorStatistics hypervisorStatistics = statistics.getHypervisor_statistics();
        return new HypervisorStatisticsConvertModel(hypervisorStatistics.getVcpus(),hypervisorStatistics.getMemory_mb()
                ,hypervisorStatistics.getLocal_gb(),hypervisorStatistics.getVcpus_used(),hypervisorStatistics.getMemory_mb_used()
                ,hypervisorStatistics.getLocal_gb_used(),hypervisorStatistics.getFree_ram_mb(),hypervisorStatistics.getFree_disk_gb());
    }

    @Override
    public HypervisorPageConvertModel page(HypervisorPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(request,ignoreFields);
        final HypervisorCloudPlatformModel model = hypervisorService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getHypervisors())){
            return null;
        }
        List<HypervisorPageConvertModel.Model> list = model.getHypervisors().stream().map(hypervisor ->
                HypervisorPageConvertModel.Model.builder().name(hypervisor.getHostname())
                        .ip(hypervisor.getHost_ip()).status(hypervisor.getStatus()).state(hypervisor.getState())
                        .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                        .cpuUsed(hypervisor.getCpu_used()).ramUsed(hypervisor.getRam_used())
                        .diskUsed(hypervisor.getDisk_used()).build()
        ).collect(Collectors.toList());

        HypervisorPageConvertModel convertModel = new HypervisorPageConvertModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    @Override
    public HypervisorWeightAndMaxInstanceConvertModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceRequest request) {
        HypervisorWeightAndMaxInstanceCloudPlatformModel model = hypervisorService.getWeightAndMaxInstances(BeanUtils.wrapperGetParamsByObject(request));
        if (Objects.isNull(model)){
            return null;
        }
        return new HypervisorWeightAndMaxInstanceConvertModel(model.getWeight(),model.getMax_instances());
    }

    @Override
    public void rebootOrPowerOff(HypervisorRequest request) {
        hypervisorService.rebootOrPowerOff(JSONObject.toJSONString(request));
    }

    @Override
    public List<HypervisorServiceStateConvertModel> serviceState() {
        InstanceOsServiceCloudPlatformModel osServiceOpenStackModel = hypervisorService.osServicesStateFromInstances();
        NetworkAgentCloudPlatformModel networkAgentOpenStackModel = hypervisorService.agentsStateFromNetworks();

        List<HypervisorServiceStateConvertModel> resultList = new ArrayList<>();

        if (Objects.nonNull(osServiceOpenStackModel)){
            resultList.addAll(osServiceOpenStackModel.getServices().stream().map(instanceOsServiceModel ->
                    new HypervisorServiceStateConvertModel(instanceOsServiceModel.getBinary(),instanceOsServiceModel.getState())
            ).collect(Collectors.toList()));
        }

        if (Objects.nonNull(networkAgentOpenStackModel)){
            resultList.addAll(networkAgentOpenStackModel.getAgents().stream().map(networkAgentModel ->
                    new HypervisorServiceStateConvertModel(networkAgentModel.getBinary(),networkAgentModel.getAlive())).collect(Collectors.toList())
            );
        }
        return resultList;
    }

    @Override
    public VirtualRealSwitchConvertModel listVirtualRealSwitch() {
        VirtualRealSwitchCloudPlatformModel model = hypervisorService.listVirtualRealSwitch();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getSwitchs())){
            return null;
        }
        List<VirtualRealSwitchConvertModel.Model> list = model.getSwitchs().stream().map(v ->
                VirtualRealSwitchConvertModel.Model.builder().name(v.getName()).managementIp(v.getManagement_ip()).state(v.getStatus())
                        .usedPorts(Integer.valueOf(v.getInterfaces_used())).ports(Integer.valueOf(v.getInterfaces_summary())).build()
        ).collect(Collectors.toList());

        VirtualRealSwitchConvertModel convertModel = new VirtualRealSwitchConvertModel();
        convertModel.setModelList(list);
        return convertModel;
    }
}
