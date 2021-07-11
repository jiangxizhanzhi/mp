package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.ContainerConverter;
import cn.cyberpeace.mp.devopscluster.converter.FirewallConverter;
import cn.cyberpeace.mp.devopscluster.converter.HypervisorConverter;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.converter.NetworkConverter;
import cn.cyberpeace.mp.devopscluster.converter.NpcConverter;
import cn.cyberpeace.mp.devopscluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devopscluster.converter.RouterConverter;
import cn.cyberpeace.mp.devopscluster.converter.SwitchConverter;
import cn.cyberpeace.mp.devopscluster.converter.VmConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.VirtualRealSwitchBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorServiceStateConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorStatisticsConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorWeightAndMaxInstanceConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.BasePageDTO;
import cn.cyberpeace.mp.devopscluster.dto.ContainerPageDTO;
import cn.cyberpeace.mp.devopscluster.dto.HypervisorActionDTO;
import cn.cyberpeace.mp.devopscluster.dto.HypervisorWeightAndMaxInstanceDTO;
import cn.cyberpeace.mp.devopscluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devopscluster.dto.NetworkPageDTO;
import cn.cyberpeace.mp.devopscluster.dto.RouterPageDTO;
import cn.cyberpeace.mp.devopscluster.enums.HypervisorActionEnum;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devopscluster.request.BasePageRequest;
import cn.cyberpeace.mp.devopscluster.request.CloudPlatformPageRequest;
import cn.cyberpeace.mp.devopscluster.request.HypervisorRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devopscluster.request.ServerRestartRequest;
import cn.cyberpeace.mp.devopscluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.devopscluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.devopscluster.vo.HypervisorPageVO;
import cn.cyberpeace.mp.devopscluster.vo.HypervisorServiceStateVO;
import cn.cyberpeace.mp.devopscluster.vo.HypervisorStatisticsVO;
import cn.cyberpeace.mp.devopscluster.vo.VirtualRealSwitchStatusVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


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

    private final FirewallConverter firewallConverter;

    private final SwitchConverter switchConverter;

    private final NetworkConverter networkConverter;

    private final ClearService clearService;

    private final ServiceMonitorService serviceMonitorService;

    private final MonitorScriptControlService monitorScriptControlService;

    private final HostStateService hostStateService;

    
    @ApiOperation("资源统计")
    @GetMapping("/resources/statistics")
    public Result<HypervisorStatisticsVO> statistics(){

        HypervisorStatisticsVO response = new HypervisorStatisticsVO();
        response.setDiskIo(BigDecimal.ZERO);
        response.setNetIo(BigDecimal.ZERO);

        
        HypervisorStatisticsVO finalResponse = response;
        CompletableFuture totalInstanceCf = CompletableFuture.runAsync(() -> {
            Integer totalInstances = vmConverter.getTotalInstances(new InstancePageDTO());
            finalResponse.setTotalInstances(totalInstances);
        });

        
        HypervisorStatisticsVO finalResponse1 = response;
        CompletableFuture totalContainersCf = CompletableFuture.runAsync(() -> {
            Integer totalContainers =  containerConverter.getTotalContainers(new ContainerPageDTO());
            finalResponse1.setTotalContainers(totalContainers);
        });

        
        RealEquipmentPageRequest realEquipmentPageRequest = new RealEquipmentPageRequest();
        realEquipmentPageRequest.setPerPage(NumberUtils.INTEGER_ONE);

        HypervisorStatisticsVO finalResponse2 = response;
        CompletableFuture totalRealEquipmentsCf = CompletableFuture.runAsync(() -> {
            Integer totalRealEquipments = realEquipmentConverter.getTotalRealEquipment(realEquipmentPageRequest);
            finalResponse2.setTotalRealEquipments(totalRealEquipments);
        });

        
        HypervisorStatisticsVO finalResponse3 = response;
        CompletableFuture totalRoutersCf = CompletableFuture.runAsync(() -> {
            Integer totalRouters = routerConverter.getTotalRouters(new RouterPageDTO());
            finalResponse3.setTotalRouters(totalRouters);
        });

        
        HypervisorStatisticsVO finalResponse4 = response;
        CompletableFuture totalNpcsCf = CompletableFuture.runAsync(() -> {
            Integer totalNpcs = npcConverter.getTotalNpcs(new BasePageDTO());
            finalResponse4.setTotalNpc(totalNpcs);
        });

        
        HypervisorStatisticsVO finalResponse5 = response;
        CompletableFuture totalFirewallsCf = CompletableFuture.runAsync(() -> {
            Integer totalFirewalls = firewallConverter.getTotalFirewall(new BasePageDTO());
            finalResponse5.setTotalFireWall(totalFirewalls);
        });

        
        HypervisorStatisticsVO finalResponse6 = response;
        CompletableFuture totalNetworksCf = CompletableFuture.runAsync(() -> {
            Integer totalNetworks = networkConverter.getTotalNetwork(new NetworkPageDTO());
            finalResponse6.setTotalSwitch(totalNetworks);
        });

        
        HypervisorStatisticsVO finalResponse7 = response;
        CompletableFuture totalDynamipsCf = CompletableFuture.runAsync(() -> {
            var result = new VirtualDynamips().statistics();
            int totalDynamips = 0;
            for (var v : result.getStatics().values()) {
                totalDynamips += v;
            }
            finalResponse7.setTotalDynamips(totalDynamips);
        });

        CompletableFuture allCf = CompletableFuture.allOf(totalInstanceCf, totalContainersCf, totalRealEquipmentsCf,
                totalRoutersCf, totalNpcsCf, totalFirewallsCf, totalNetworksCf, totalDynamipsCf);
        allCf.join();
        HypervisorStatisticsConvertModel hypervisorStatisticsConvertModel = hypervisorConverter.statistics();

        if(Objects.isNull(hypervisorStatisticsConvertModel)){
            return new Result<>(response);
        }

        HypervisorStatisticsConvertModel hypervisorStatistics = hypervisorStatisticsConvertModel;

        response = response.build(hypervisorStatistics.getCpus(), hypervisorStatistics.getMemoryMb()
                ,hypervisorStatistics.getLocalGb(),hypervisorStatistics.getCpusUsed(),hypervisorStatistics.getMemoryMbUsed()
                ,hypervisorStatistics.getLocalGbUsed(),hypervisorStatistics.getFreeRamMb(),hypervisorStatistics.getFreeDiskGb());
        return new Result<>(response);
    }

    
    @ApiOperation("节点管理")
    @GetMapping("/hypervisor/page")
    public PageResult<HypervisorPageVO> page(BasePageRequest request){
        BasePageBO<HypervisorBO> model = hypervisorConverter.page(new BasePageDTO(request.getLimit(),request.getOffset()));
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<HypervisorPageVO> hypervisorPageRespons =  model.getList().stream().map(hypervisor ->
                HypervisorPageVO.builder().name(hypervisor.getName())
                        .ip(hypervisor.getIp()).status(hypervisor.getStatus()).state(hypervisor.getState())
                        .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                        .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed()).totalInstances(hypervisor.getTotalInstances())
                        .diskUsed(hypervisor.getDiskUsed()).build()
        ).collect(Collectors.toList());

        
        CloudPlatformPageResult<ServiceStatus> serviceStatusModel = imagesConverter.listServiceStatus();

        hypervisorPageRespons.forEach(v -> {
            String name = v.getName();
            Optional<ServiceStatus> serviceStatus =
                    serviceStatusModel.getList().stream().filter(statusModel -> name.equals(statusModel.getHost())).findFirst();
            
            v.setServiceStatus(serviceStatus.isPresent() && serviceStatus.get().isStatus());

            HypervisorWeightAndMaxInstanceConvertModel weightAndMaxInstances = hypervisorConverter.getWeightAndMaxInstances(new HypervisorWeightAndMaxInstanceDTO(name));
            Integer totalContainers = containerConverter.getTotalContainers(new ContainerPageDTO(name));
            v.setTotalContainers(totalContainers);
            if (Objects.nonNull(weightAndMaxInstances)) {
                v.setWeight(weightAndMaxInstances.getWeight());
                v.setMaxInstances(weightAndMaxInstances.getMaxInstances());
            }
        });

        
        VirtualDynamips virtualDynamips = new VirtualDynamips();
        var result = virtualDynamips.statistics();
        hypervisorPageRespons.forEach(v -> {
            v.setTotalDynamips(result.getStatics().get(v.getName()));
        });
        return new PageResult<>(hypervisorPageRespons, model.getTotal());
    }

    
    @ApiOperation("节点管理-关机/重启")
    @GetMapping("/hypervisor/reboot-poweroff")
    public ServiceResult rebootOrPowerOff(HypervisorRequest request){
        if (StringUtils.isBlank(request.getAction()) || StringUtils.isBlank(request.getHostname())){
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        if(request.getAction().equals(HypervisorActionEnum.REBOOT.getAction())
                || request.getAction().equals(HypervisorActionEnum.POWEROFF.getAction())) {
            hypervisorConverter.rebootOrPowerOff(new HypervisorActionDTO(request));
        }
        return ResultCommon.success();
    }

    
    @ApiOperation("服务状态监控")
    @GetMapping("/service-state")
    public Result<List<HypervisorServiceStateVO>> serviceState(){
        List<HypervisorServiceStateConvertModel> modelList = hypervisorConverter.serviceState();
        List<HypervisorServiceStateVO> result = modelList.stream().map(m ->
                new HypervisorServiceStateVO(m.getName(),m.getAlive(),m.getState())).collect(Collectors.toList());
        return new Result<>(result);
    }

    
    @ApiOperation("虚实结合交换机状态")
    @GetMapping("/virtual-real-switch-state")
    public Result<List<VirtualRealSwitchStatusVO>> virtualRealSwitchStatus(){
        ListResult<VirtualRealSwitchBO> model = hypervisorConverter.listVirtualRealSwitch();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            return new Result<>();
        }
        List<VirtualRealSwitchStatusVO> list = model.getList().stream().map(v ->
                VirtualRealSwitchStatusVO.builder().name(v.getName()).managementIp(v.getManagementIp()).state(v.getState())
                        .usedPorts(v.getUsedPorts()).ports(v.getPorts()).build()
        ).collect(Collectors.toList());
        return new Result<>(list);
    }

    
    @ApiOperation("释放无效资源")
    @GetMapping("/release_resource")
    public ServiceResult<Boolean> releaseResource() {
        TaskCommon.executeTask(() -> clearService.clearUselessResource());
        return new ServiceResult<>(true);
    }

    
    @ApiOperation("云平台服务状态监控")
    @GetMapping("/cp_page")
    public ServiceResult<ListObjResult<ServiceMonitorDto>> page(CloudPlatformPageRequest request) {
        ListObjResult<ServiceMonitorDto> result = serviceMonitorService.cpPage(request);
        return ResultCommon.success(result);
    }

    
    @ApiOperation("服务重启")
    @ApiImplicitParam(name = "serverRestartRequest", value = "服务重启请求体", dataTypeClass = ServerRestartRequest.class)
    @PostMapping("/restart")
    public ServiceResult<Boolean> restartServer(@RequestBody @Validated ServerRestartRequest serverRestartRequest) {
        monitorScriptControlService.restart(serverRestartRequest.getHostname(), serverRestartRequest.getServiceList());
        return new ServiceResult<>(true);
    }

    
    @ApiOperation("设备运行监控")
    @GetMapping("/server/state")
    public ServiceResult<List<HypervisorPageVO>> page(AgentNameBasedRequest request){
        request.setPage(1);
        request.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStatesByAgentName(request);
        List<HypervisorPageVO> hypervisorPageVOList = new ArrayList<>();
        hostStates.forEach(hostState -> {

            
            Double downloadPercent = 0.00;
            Double uploadPercent = 0.00;
            Long maxNetSpeed = hostState.getMaxNetSpeed();
            if (Objects.isNull(maxNetSpeed) || maxNetSpeed.intValue() == 0) {
                maxNetSpeed = Long.valueOf(10000);
            }

            if (StringUtils.isNotBlank(hostState.getMaxDownloadSpeed())) {
                String downloadSpeed = hostState.getMaxDownloadSpeed();
                downloadSpeed = downloadSpeed.replace("KB/s", "");
                double ds = Double.parseDouble(downloadSpeed);
                BigDecimal decimal =  NumberUtil.round(ds * 8 / (maxNetSpeed * 1024), 2);

                if (ds > 0 && decimal.doubleValue() == 0.00) {
                    downloadPercent = 0.01;
                } else {
                    downloadPercent = decimal.doubleValue() * 100;
                    downloadPercent = downloadPercent > 100 ? 100.00 : NumberUtil.round(downloadPercent, 2).doubleValue();
                }
            }

            if (StringUtils.isNotBlank(hostState.getMaxUploadSpeed())) {
                String uploadSpeed = hostState.getMaxUploadSpeed();
                uploadSpeed = uploadSpeed.replace("KB/s", "");
                double us = Double.parseDouble(uploadSpeed);
                BigDecimal decimal =  NumberUtil.round(us * 8 / (maxNetSpeed * 1024), 2);

                if (us > 0 && decimal.doubleValue() == 0.00) {
                    uploadPercent = 0.01;
                } else {
                    uploadPercent = decimal.doubleValue() * 100;
                    uploadPercent = uploadPercent > 100 ? 100.00 : NumberUtil.round(uploadPercent, 2).doubleValue();
                }
            }

            HypervisorPageVO hypervisorPageVO = HypervisorPageVO.builder()
                    .name(hostState.getAgentName())
                    .cpu(hostState.getCpuCount())
                    .cpuPercent(Objects.isNull(hostState.getCpuPercent()) ? BigDecimal.ZERO : BigDecimal.valueOf(hostState.getCpuPercent()))
                    .ram(hostState.getMemoryTotal())
                    .ramPercent(Objects.isNull(hostState.getMemoryPercent()) ? BigDecimal.ZERO : BigDecimal.valueOf(hostState.getMemoryPercent()))
                    .disk(hostState.getDiskSpace())
                    .diskPercent(Objects.isNull(hostState.getDiskPercent()) ? BigDecimal.ZERO : BigDecimal.valueOf(hostState.getDiskPercent()))
                    .downloadSpeed(hostState.getDownloadSpeed())
                    .maxDownloadSpeed(hostState.getMaxDownloadSpeed())
                    .uploadSpeed(hostState.getUploadSpeed())
                    .maxUploadSpeed(hostState.getMaxUploadSpeed())
                    .diskReadBytes(hostState.getDiskReadBytes())
                    .diskWriteBytes(hostState.getDiskWriteBytes())
                    .diskIoPercent(hostState.getDiskIoPercent())
                    .downloadPercent(downloadPercent)
                    .uploadPercent(uploadPercent)
                    .build();

            hypervisorPageVOList.add(hypervisorPageVO);
        });
        return ResultCommon.success(hypervisorPageVOList);
    }


}
