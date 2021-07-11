package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.HypervisorService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.SwitchService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.VmService;
import cn.cyberpeace.mp.devopscluster.config.DevopsCacheConfig;
import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.VirtualRealSwitchBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorServiceStateConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorStatisticsConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorWeightAndMaxInstanceConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.dto.HypervisorActionDTO;
import cn.cyberpeace.mp.devopscluster.dto.HypervisorWeightAndMaxInstanceDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.HypervisorStatisticsCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.model.HypervisorWeightAndMaxInstanceCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.model.SwitchesCloudPlatform;
import cn.cyberpeace.mp.devopscluster.model.common.Hypervisor;
import cn.cyberpeace.mp.devopscluster.model.common.HypervisorStatistics;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceOsServiceModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = DevopsCacheConfig.CACHE_MANAGER_20S)
public class HypervisorConverter implements BaseConverter {

    private final HypervisorService hypervisorService;

    private final SwitchService switchService;

    private final NetworkService networkService;

    private final VmService vmService;

    public static final String CACHE_VALUE_HYPERVISOR_PAGE = "HYPERVISOR_PAGE";


    
    public HypervisorStatisticsConvertModel statistics() {
        HypervisorStatisticsCloudPlatformResult statistics = hypervisorService.statistics();
        HypervisorStatistics hypervisorStatistics = statistics.getHypervisorStatistics();
        return new HypervisorStatisticsConvertModel(hypervisorStatistics.getVcpus(),hypervisorStatistics.getMemoryMb()
                ,hypervisorStatistics.getLocalGb(),hypervisorStatistics.getVcpusUsed(),hypervisorStatistics.getMemoryMbUsed()
                ,hypervisorStatistics.getLocalGbUsed(),hypervisorStatistics.getFreeRamMb(),hypervisorStatistics.getFreeDiskGb());
    }

    
    @Cacheable(value = CACHE_VALUE_HYPERVISOR_PAGE)
    public BasePageBO<HypervisorBO> page(BasePageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
        final CloudPlatformPageResult<Hypervisor> model = hypervisorService.page(getParams);
        List<HypervisorBO> list = model.getList().stream().map(hypervisor ->
                HypervisorBO.builder().name(hypervisor.getHostname())
                        .ip(hypervisor.getHostIp()).status(hypervisor.getStatus()).state(hypervisor.getState())
                        .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                        .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed()).totalInstances(hypervisor.getRunningVms())
                        .serviceStatus(false)
                        .diskUsed(hypervisor.getDiskUsed()).build()
        ).collect(Collectors.toList());

        BasePageBO<HypervisorBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    
    public HypervisorWeightAndMaxInstanceConvertModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceDTO dto) {
        HypervisorWeightAndMaxInstanceCloudPlatformResult model = hypervisorService.getWeightAndMaxInstances(ParamUtils.wrapperGetParamsByObject(dto));
        if (Objects.isNull(model)){
            return null;
        }
        return new HypervisorWeightAndMaxInstanceConvertModel(model.getWeight(),model.getMaxInstances());
    }

    
    public void rebootOrPowerOff(HypervisorActionDTO dto) {
        hypervisorService.rebootOrPowerOff(JSONObject.toJSONString(dto));
    }

    
    public List<HypervisorServiceStateConvertModel> serviceState() {
        CloudPlatformPageResult<InstanceOsServiceModel> modelCloudPlatformPageResult = vmService.osServicesStateFromInstances();
        CloudPlatformPageResult<NetworkAgentModel> networks = networkService.agentsStateFromNetworks();

        List<HypervisorServiceStateConvertModel> resultList = new ArrayList<>();

        if (Objects.nonNull(modelCloudPlatformPageResult) && CollectionUtils.isNotEmpty(modelCloudPlatformPageResult.getList())){
            resultList.addAll(modelCloudPlatformPageResult.getList().stream().map(instanceOsServiceModel ->
                    new HypervisorServiceStateConvertModel(instanceOsServiceModel.getBinary(),instanceOsServiceModel.getState())
            ).collect(Collectors.toList()));
        }

        if (Objects.nonNull(networks) && CollectionUtils.isNotEmpty(networks.getList())){
            resultList.addAll(networks.getList().stream().map(networkAgentModel ->
                    new HypervisorServiceStateConvertModel(networkAgentModel.getBinary(),networkAgentModel.getAlive())).collect(Collectors.toList())
            );
        }
        return resultList;
    }

    
    public ListResult<VirtualRealSwitchBO> listVirtualRealSwitch() {
        SwitchesCloudPlatform model = switchService.list();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getSwitchs())){
            return null;
        }
        List<VirtualRealSwitchBO> list = model.getSwitchs().stream().map(v ->
                VirtualRealSwitchBO.builder().name(v.getName()).managementIp(v.getManagementIp()).state(v.getStatus())
                        .usedPorts(v.getInterfaceUpNums()).ports(v.getInterfaceNums()).build()
        ).collect(Collectors.toList());

        ListResult<VirtualRealSwitchBO> convertModel = new ListResult<>();
        convertModel.setList(list);
        return convertModel;
    }
}
