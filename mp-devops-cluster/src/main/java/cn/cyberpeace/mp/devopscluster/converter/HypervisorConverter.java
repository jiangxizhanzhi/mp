package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Switches;
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
import cn.cyberpeace.mp.cscene.response.HypervisorStatistics;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceOsServiceModel;
import cn.cyberpeace.mp.devopscluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.CloudHypervisor;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.HypervisorData;
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

/**
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheManager = DevopsCacheConfig.CACHE_MANAGER_20S)
public class HypervisorConverter implements BaseConverter {

    private final HypervisorService hypervisorService;

    private final SwitchService switchService;

    private final NetworkService networkService;

    private final VmService vmService;

    public static final String CACHE_VALUE_HYPERVISOR_PAGE = "HYPERVISOR_PAGE";


    /**
     * 资源统计
     * @return
     */
    public HypervisorStatisticsConvertModel statistics() {
        HypervisorStatisticsCloudPlatformResult statistics = hypervisorService.statistics();
        HypervisorStatistics hypervisorStatistics = statistics.getHypervisorStatistics();
        return new HypervisorStatisticsConvertModel(hypervisorStatistics.getVcpus(),hypervisorStatistics.getMemoryMb()
                ,hypervisorStatistics.getLocalGb(),hypervisorStatistics.getVcpusUsed(),hypervisorStatistics.getMemoryMbUsed()
                ,hypervisorStatistics.getLocalGbUsed(),hypervisorStatistics.getFreeRamMb(),hypervisorStatistics.getFreeDiskGb());
    }

    /**
     * 分页查询节点管理信息
     * @param dto
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<HypervisorBO> page(BasePageDTO dto, String... ignoreFields) {
        var hypervisors = new CloudHypervisor().getCachedHypervisors();
        List<HypervisorBO> list = new ArrayList<>();
        for (HypervisorData hypervisor : hypervisors) {
            HypervisorBO build = HypervisorBO.builder().name(hypervisor.getHostname())
                    .ip(hypervisor.getHostIp()).status(hypervisor.getStatus()).state(hypervisor.getState())
                    .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                    .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed()).totalInstances(hypervisor.getRunningVms())
                    .serviceStatus(false)
                    .diskUsed(hypervisor.getDiskUsed()).build();
            list.add(build);
        }

        BasePageBO<HypervisorBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(hypervisors.size());
        return convertModel;
    }

    /**
     * 获取虚拟机数量限制与权重
     * @param dto
     * @return
     */
    public HypervisorWeightAndMaxInstanceConvertModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceDTO dto) {
        HypervisorWeightAndMaxInstanceCloudPlatformResult model = hypervisorService.getWeightAndMaxInstances(ParamUtils.wrapperGetParamsByObject(dto));
        if (Objects.isNull(model)){
            return null;
        }
        return new HypervisorWeightAndMaxInstanceConvertModel(model.getWeight(),model.getMaxInstances());
    }

    /**
     * 节点管理-重启/关机
     * @param dto
     */
    public void rebootOrPowerOff(HypervisorActionDTO dto) {
        hypervisorService.rebootOrPowerOff(JSONObject.toJSONString(dto));
    }

    /**
     * 服务状态监控
     * @return
     */
    public List<HypervisorServiceStateConvertModel> serviceState() {
        CloudPlatformPageResult<InstanceOsServiceModel> modelCloudPlatformPageResult = vmService.osServicesStateFromInstances();
        CloudPlatformPageResult<NetworkAgentModel> networks = networkService.agentsStateFromNetworks();

        List<HypervisorServiceStateConvertModel> resultList = new ArrayList<>();

        if (Objects.nonNull(modelCloudPlatformPageResult) && CollectionUtils.isNotEmpty(modelCloudPlatformPageResult.getList())){
            List<HypervisorServiceStateConvertModel> list = new ArrayList<>();
            for (InstanceOsServiceModel instanceOsServiceModel : modelCloudPlatformPageResult.getList()) {
                HypervisorServiceStateConvertModel hypervisorServiceStateConvertModel = new HypervisorServiceStateConvertModel(instanceOsServiceModel.getBinary(), instanceOsServiceModel.getState());
                list.add(hypervisorServiceStateConvertModel);
            }
            resultList.addAll(list);
        }

        if (Objects.nonNull(networks) && CollectionUtils.isNotEmpty(networks.getList())){
            List<HypervisorServiceStateConvertModel> list = new ArrayList<>();
            for (NetworkAgentModel networkAgentModel : networks.getList()) {
                HypervisorServiceStateConvertModel hypervisorServiceStateConvertModel = new HypervisorServiceStateConvertModel(networkAgentModel.getBinary(), networkAgentModel.getAlive());
                list.add(hypervisorServiceStateConvertModel);
            }
            resultList.addAll(list
            );
        }
        return resultList;
    }

    /**
     * 查询虚拟结合交换机状态列表
     * @return
     */
    public ListResult<VirtualRealSwitchBO> listVirtualRealSwitch() {
        SwitchesCloudPlatform model = switchService.list();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getSwitchs())){
            return null;
        }
        List<VirtualRealSwitchBO> list = new ArrayList<>();
        for (Switches v : model.getSwitchs()) {
            VirtualRealSwitchBO build = VirtualRealSwitchBO.builder().name(v.getName()).managementIp(v.getManagementIp()).state(v.getStatus())
                    .usedPorts(v.getInterfaceUpNums()).ports(v.getInterfaceNums()).build();
            list.add(build);
        }

        ListResult<VirtualRealSwitchBO> convertModel = new ListResult<>();
        convertModel.setList(list);
        return convertModel;
    }
}
