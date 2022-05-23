package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.*;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.enums.HypervisorActionEnum;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorServiceStateResponse;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorStatisticsResponse;
import cn.cyberpeace.mp.devops.cluster.response.VirtualRealSwitchStatusResponse;
import cn.cyberpeace.mp.devops.cluster.response.base.PageResult;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 云平台监控
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor")
@Api(tags = "云平台监控")
@RequiredArgsConstructor
public class CloudPlatformMonitorController {

    private final VmConverter vmConverter;

    private final ContainerConverter containerConverter;

    private final RouterConverter routerConverter;

    private final RealEquipmentConverter realEquipmentConverter;

    private final HypervisorConverter hypervisorConverter;

    private final ImagesConverter imagesConverter;

    private final NpcConverter npcConverter;

    /**
     * 资源统计
     * @return
     */
    @ApiOperation("资源统计")
    @GetMapping("/resources/statistics")
    public Result<HypervisorStatisticsResponse> statistics(){

        //虚拟机个数
        InstancePageRequest instancePageRequest = new InstancePageRequest();
        instancePageRequest.setPerPage(NumberUtils.INTEGER_ONE);
        Integer totalInstances = vmConverter.getTotalInstances(instancePageRequest);

        //容器个数
        ContainerPageRequest containerPageRequest = new ContainerPageRequest();
        containerPageRequest.setPerPage(NumberUtils.INTEGER_ONE);
        Integer totalContainers =  containerConverter.getTotalContainers(containerPageRequest);

        //实装设备个数
        RealEquipmentPageRequest realEquipmentPageRequest = new RealEquipmentPageRequest();
        realEquipmentPageRequest.setPerPage(NumberUtils.INTEGER_ONE);
        Integer totalRealEquipments = realEquipmentConverter.getTotalRealEquipment(realEquipmentPageRequest);

        //路由个数
        RouterPageRequest routerPageRequest = new RouterPageRequest();
        routerPageRequest.setPerPage(NumberUtils.INTEGER_ONE);
        Integer totalRouters = routerConverter.getTotalRouters(routerPageRequest);

        //NPC个数
        NpcPageRequest npcPageRequest = new NpcPageRequest();
        npcPageRequest.setPerPage(NumberUtils.INTEGER_ONE);
        Integer totalNpcs = npcConverter.getTotalNpcs(npcPageRequest);

        HypervisorStatisticsConvertModel hypervisorStatisticsConvertModel = hypervisorConverter.statistics();

        HypervisorStatisticsResponse response = HypervisorStatisticsResponse.builder().diskIo(BigDecimal.ZERO)
                .netIo(BigDecimal.ZERO)
                .totalNpc(totalNpcs).totalInstances(totalInstances)
                .totalContainers(totalContainers).totalRealEquipments(totalRealEquipments).totalRouters(totalRouters).build();
        if(Objects.isNull(hypervisorStatisticsConvertModel)){
            return new Result<>(response);
        }

        HypervisorStatisticsConvertModel hypervisorStatistics = hypervisorStatisticsConvertModel;

        response = response.HypervisorStatisticsResponse(hypervisorStatistics.getCpus(), hypervisorStatistics.getMemoryMb()
                ,hypervisorStatistics.getLocalGb(),hypervisorStatistics.getCpusUsed(),hypervisorStatistics.getMemoryMbUsed()
                ,hypervisorStatistics.getLocalGbUsed(),hypervisorStatistics.getFreeRamMb(),hypervisorStatistics.getFreeDiskGb());
        return new Result<>(response);
    }

    /**
     * 节点管理
     * @return
     */
    @ApiOperation("节点管理")
    @GetMapping("/hypervisor/page")
    public PageResult<HypervisorPageResponse> page(HypervisorPageRequest request){
        HypervisorPageConvertModel model = hypervisorConverter.page(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<HypervisorPageResponse> hypervisorPageResponses =  model.getModelList().stream().map(hypervisor ->
                HypervisorPageResponse.builder().name(hypervisor.getName())
                        .ip(hypervisor.getIp()).status(hypervisor.getStatus()).state(hypervisor.getState())
                        .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                        .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed()).totalInstances(hypervisor.getTotalInstances())
                        .diskUsed(hypervisor.getDiskUsed()).build()
        ).collect(Collectors.toList());

        // 获取所有物理主机服务状态
        CloudPlatformPageModel<ServiceStatus> serviceStatusModel = imagesConverter.listServiceStatus();

        hypervisorPageResponses.forEach(v -> {
            String name = v.getName();
            Optional<ServiceStatus> serviceStatus =
                    serviceStatusModel.getList().stream().filter(statusModel -> name.equals(statusModel.getHost())).findFirst();
            // 设置物理主机服务状态
            v.setServiceStatus(serviceStatus.isPresent() && serviceStatus.get().isStatus());

            HypervisorWeightAndMaxInstanceConvertModel weightAndMaxInstances = hypervisorConverter.getWeightAndMaxInstances(new HypervisorWeightAndMaxInstanceRequest(name));
            Integer totalContainers = containerConverter.getTotalContainers(new ContainerPageRequest(name));
            v.setTotalContainers(totalContainers);
            if (Objects.nonNull(weightAndMaxInstances)) {
                v.setWeight(weightAndMaxInstances.getWeight());
                v.setMaxInstances(weightAndMaxInstances.getMaxInstances());
            }
        });

        return new PageResult<>(hypervisorPageResponses, model.getTotal());
    }

    /**
     * 节点管理-关机/重启
     * @param request
     * @return
     */
    @ApiOperation("节点管理-关机/重启")
    @GetMapping("/hypervisor/reboot-poweroff")
    public ServiceResult rebootOrPowerOff(HypervisorRequest request){
        if (StringUtils.isBlank(request.getAction()) || StringUtils.isBlank(request.getHostname())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if(request.getAction().equals(HypervisorActionEnum.REBOOT.getAction())
                || request.getAction().equals(HypervisorActionEnum.POWEROFF.getAction())) {
            hypervisorConverter.rebootOrPowerOff(request);
        }
        return ServiceResult.successful();
    }

    /**
     * 服务状态监控
     * @return
     */
    @ApiOperation("服务状态监控")
    @GetMapping("/service-state")
    public Result<List<HypervisorServiceStateResponse>> serviceState(){
        List<HypervisorServiceStateConvertModel> modelList = hypervisorConverter.serviceState();
        List<HypervisorServiceStateResponse> result = modelList.stream().map(m ->
                new HypervisorServiceStateResponse(m.getName(),m.getAlive(),m.getState())).collect(Collectors.toList());
        return new Result<>(result);
    }

    /**
     * 虚实结合交换机状态
     * @return
     */
    @ApiOperation("虚实结合交换机状态")
    @GetMapping("/virtual-real-switch-state")
    public Result<List<VirtualRealSwitchStatusResponse>> virtualRealSwitchStatus(){
        VirtualRealSwitchConvertModel model = hypervisorConverter.listVirtualRealSwitch();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getModelList())){
            return new Result<>();
        }
        List<VirtualRealSwitchStatusResponse> list = model.getModelList().stream().map(v ->
                VirtualRealSwitchStatusResponse.builder().name(v.getName()).managementIp(v.getManagementIp()).state(v.getState())
                        .usedPorts(v.getUsedPorts()).ports(v.getPorts()).build()
        ).collect(Collectors.toList());
        return new Result<>(list);
    }


}
