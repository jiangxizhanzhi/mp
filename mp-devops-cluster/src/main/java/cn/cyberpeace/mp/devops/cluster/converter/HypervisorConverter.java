package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.HypervisorService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.NetworkService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.SwitchService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.VmService;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorWeightAndMaxInstanceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.SwitchesCloudPlatform;
import cn.cyberpeace.mp.devops.cluster.model.common.Hypervisor;
import cn.cyberpeace.mp.devops.cluster.model.common.HypervisorStatistics;
import cn.cyberpeace.mp.devops.cluster.model.common.InstanceOsServiceModel;
import cn.cyberpeace.mp.devops.cluster.model.common.NetworkAgentModel;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorRequest;
import cn.cyberpeace.mp.devops.cluster.request.HypervisorWeightAndMaxInstanceRequest;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Service
public class HypervisorConverter implements BaseConverter {

    private HypervisorService hypervisorService;

    private SwitchService switchService;

    private NetworkService networkService;

    private VmService vmService;

    public HypervisorConverter(HypervisorService hypervisorService, SwitchService switchService,
                                   NetworkService networkService,VmService vmService) {
        this.hypervisorService = hypervisorService;
        this.switchService = switchService;
        this.networkService = networkService;
        this.vmService = vmService;
    }

    /**
     * 资源统计
     * @return
     */
    public HypervisorStatisticsConvertModel statistics() {
        HypervisorStatisticsCloudPlatformModel statistics = hypervisorService.statistics();
        HypervisorStatistics hypervisorStatistics = statistics.getHypervisorStatistics();
        return new HypervisorStatisticsConvertModel(hypervisorStatistics.getVcpus(),hypervisorStatistics.getMemoryMb()
                ,hypervisorStatistics.getLocalGb(),hypervisorStatistics.getVcpusUsed(),hypervisorStatistics.getMemoryMbUsed()
                ,hypervisorStatistics.getLocalGbUsed(),hypervisorStatistics.getFreeRamMb(),hypervisorStatistics.getFreeDiskGb());
    }

    /**
     * 分页查询节点管理信息
     * @param request
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public HypervisorPageConvertModel page(HypervisorPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(request,ignoreFields);
        final CloudPlatformPageModel<Hypervisor> model = hypervisorService.page(getParams);
        List<HypervisorPageConvertModel.Model> list = model.getList().stream().map(hypervisor ->
                HypervisorPageConvertModel.Model.builder().name(hypervisor.getHostname())
                        .ip(hypervisor.getHostIp()).status(hypervisor.getStatus()).state(hypervisor.getState())
                        .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                        .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed()).totalInstances(hypervisor.getRunningVms())
                        .diskUsed(hypervisor.getDiskUsed()).build()
        ).collect(Collectors.toList());

        HypervisorPageConvertModel convertModel = new HypervisorPageConvertModel();
        convertModel.setModelList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取虚拟机数量限制与权重
     * @param request
     * @return
     */
    public HypervisorWeightAndMaxInstanceConvertModel getWeightAndMaxInstances(HypervisorWeightAndMaxInstanceRequest request) {
        HypervisorWeightAndMaxInstanceCloudPlatformModel model = hypervisorService.getWeightAndMaxInstances(ParamUtils.wrapperGetParamsByObject(request));
        if (Objects.isNull(model)){
            return null;
        }
        return new HypervisorWeightAndMaxInstanceConvertModel(model.getWeight(),model.getMaxInstances());
    }

    /**
     * 节点管理-重启/关机
     * @param request
     */
    public void rebootOrPowerOff(HypervisorRequest request) {
        hypervisorService.rebootOrPowerOff(JSONObject.toJSONString(request));
    }

    /**
     * 服务状态监控
     * @return
     */
    public List<HypervisorServiceStateConvertModel> serviceState() {
        CloudPlatformPageModel<InstanceOsServiceModel> osServiceOpenStackModel = vmService.osServicesStateFromInstances();
        CloudPlatformPageModel<NetworkAgentModel> networkAgentOpenStackModel = networkService.agentsStateFromNetworks();

        List<HypervisorServiceStateConvertModel> resultList = new ArrayList<>();

        if (Objects.nonNull(osServiceOpenStackModel) && CollectionUtils.isNotEmpty(osServiceOpenStackModel.getList())){
            resultList.addAll(osServiceOpenStackModel.getList().stream().map(instanceOsServiceModel ->
                    new HypervisorServiceStateConvertModel(instanceOsServiceModel.getBinary(),instanceOsServiceModel.getState())
            ).collect(Collectors.toList()));
        }

        if (Objects.nonNull(networkAgentOpenStackModel) && CollectionUtils.isNotEmpty(networkAgentOpenStackModel.getList())){
            resultList.addAll(networkAgentOpenStackModel.getList().stream().map(networkAgentModel ->
                    new HypervisorServiceStateConvertModel(networkAgentModel.getBinary(),networkAgentModel.getAlive())).collect(Collectors.toList())
            );
        }
        return resultList;
    }

    /**
     * 查询虚拟结合交换机状态列表
     * @return
     */
    public VirtualRealSwitchConvertModel listVirtualRealSwitch() {
        SwitchesCloudPlatform model = switchService.list();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getSwitchs())){
            return null;
        }
        List<VirtualRealSwitchConvertModel.Model> list = model.getSwitchs().stream().map(v ->
                VirtualRealSwitchConvertModel.Model.builder().name(v.getName()).managementIp(v.getManagementIp()).state(v.getStatus())
                        .usedPorts(v.getInterfaceUpNums()).ports(v.getInterfaceNums()).build()
        ).collect(Collectors.toList());

        VirtualRealSwitchConvertModel convertModel = new VirtualRealSwitchConvertModel();
        convertModel.setModelList(list);
        return convertModel;
    }
}
