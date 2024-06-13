package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.*;
import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.VirtualRealSwitchBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorServiceStateConvertModel;
import cn.cyberpeace.mp.devopscluster.converter.model.HypervisorStatisticsConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.*;
import cn.cyberpeace.mp.devopscluster.enums.HypervisorActionEnum;
import cn.cyberpeace.mp.devopscluster.request.*;
import cn.cyberpeace.mp.devopscluster.service.MonitorScriptControlService;
import cn.cyberpeace.mp.devopscluster.service.ServiceMonitorService;
import cn.cyberpeace.mp.devopscluster.vo.HypervisorPageVO;
import cn.cyberpeace.mp.devopscluster.vo.HypervisorServiceStateVO;
import cn.cyberpeace.mp.devopscluster.vo.HypervisorStatisticsVO;
import cn.cyberpeace.mp.devopscluster.vo.VirtualRealSwitchStatusVO;
import cn.cyberpeace.mp.devopscluster.vo.common.ListResult;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.devopscluster.vo.common.Result;
import cn.cyberpeace.mp.situationaware.model.dto.monitor.ServiceMonitorDto;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 云平台监控
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor")
@Api(tags = "云平台监控")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.CLOUD_PLATFORM_MONITORING)
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

    /**
     * 资源统计
     * @return
     */
    @ApiOperation("资源统计")
    @GetMapping("/resources/statistics")
    public Result<HypervisorStatisticsVO> statistics(){

        HypervisorStatisticsVO response = new HypervisorStatisticsVO();
        response.setDiskIo(BigDecimal.ZERO);
        response.setNetIo(BigDecimal.ZERO);

        //虚拟机个数
        HypervisorStatisticsVO finalResponse = response;
        CompletableFuture totalInstanceCf = CompletableFuture.runAsync(() -> {
            Integer totalInstances = vmConverter.getTotalInstances(new InstancePageDTO());
            finalResponse.setTotalInstances(totalInstances);
        });

        //容器个数
        HypervisorStatisticsVO finalResponse1 = response;
        CompletableFuture totalContainersCf = CompletableFuture.runAsync(() -> {
            Integer totalContainers =  containerConverter.getTotalContainers(new ContainerPageDTO());
            finalResponse1.setTotalContainers(totalContainers);
        });

        //实装设备个数
        RealEquipmentPageRequest realEquipmentPageRequest = new RealEquipmentPageRequest();
        realEquipmentPageRequest.setPerPage(NumberUtils.INTEGER_ONE);

        HypervisorStatisticsVO finalResponse2 = response;
        CompletableFuture totalRealEquipmentsCf = CompletableFuture.runAsync(() -> {
            Integer totalRealEquipments = realEquipmentConverter.getTotalRealEquipment(realEquipmentPageRequest);
            finalResponse2.setTotalRealEquipments(totalRealEquipments);
        });

        //路由个数
        HypervisorStatisticsVO finalResponse3 = response;
        CompletableFuture totalRoutersCf = CompletableFuture.runAsync(() -> {
            Integer totalRouters = routerConverter.getTotalRouters(new RouterPageDTO());
            finalResponse3.setTotalRouters(totalRouters);
        });

        //NPC个数
        HypervisorStatisticsVO finalResponse4 = response;
        CompletableFuture totalNpcsCf = CompletableFuture.runAsync(() -> {
            finalResponse4.setTotalNpc(0);
        });

        // 防火墙个数
        HypervisorStatisticsVO finalResponse5 = response;
        CompletableFuture totalFirewallsCf = CompletableFuture.runAsync(() -> {
            finalResponse5.setTotalFireWall(0);
        });

        // 交换机个数 -> 替换为网络个数
        HypervisorStatisticsVO finalResponse6 = response;
        CompletableFuture totalNetworksCf = CompletableFuture.runAsync(() -> {
            Integer totalNetworks = networkConverter.getTotalNetwork(new NetworkPageDTO());
            finalResponse6.setTotalSwitch(totalNetworks);
        });

        // dynamips个数
        HypervisorStatisticsVO finalResponse7 = response;
        CompletableFuture totalDynamipsCf = CompletableFuture.runAsync(() -> {

            finalResponse7.setTotalDynamips(0);
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

    /**
     * 节点管理
     * @return
     */
    @AuthTarget({TargetEnum.CLOUD_PLATFORM_MONITORING, TargetEnum.DASHBOARD})
    @ApiOperation("节点管理")
    @GetMapping("/hypervisor/page")
    public PageResult<HypervisorPageVO> page(BasePageRequest request){
        BasePageBO<HypervisorBO> model = hypervisorConverter.page(new BasePageDTO(request.getLimit(),request.getOffset()));
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<HypervisorPageVO> hypervisorPageRespons = new ArrayList<>();
        for (HypervisorBO hypervisor : model.getList()) {
            HypervisorPageVO build = HypervisorPageVO.builder().name(hypervisor.getName())
                    .ip(hypervisor.getIp()).status(hypervisor.getStatus()).state(hypervisor.getState())
                    .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                    .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed()).totalInstances(hypervisor.getTotalInstances())
                    .diskUsed(hypervisor.getDiskUsed())
                    .status(hypervisor.getStatus())
                    .serviceStatus(true)
                    .build();
            hypervisorPageRespons.add(build);
        }

        // 获取所有物理主机服务状态

        for (HypervisorPageVO hypervisorPageRespon : hypervisorPageRespons) {
            String name = hypervisorPageRespon.getName();

            // 设置物理主机服务状态

            Integer totalContainers = containerConverter.getTotalContainers(new ContainerPageDTO(name));
            hypervisorPageRespon.setTotalContainers(totalContainers);
        }

        // 获取 dynamips 数量
        for (HypervisorPageVO v : hypervisorPageRespons) {
            v.setTotalDynamips(0);
        }
        return new PageResult<>(hypervisorPageRespons, model.getTotal());
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
            hypervisorConverter.rebootOrPowerOff(new HypervisorActionDTO(request));
        }
        return ResultCommon.success();
    }

    /**
     * 服务状态监控
     * @return
     */
    @ApiOperation("服务状态监控")
    @GetMapping("/service-state")
    public Result<List<HypervisorServiceStateVO>> serviceState(){
        List<HypervisorServiceStateConvertModel> modelList = hypervisorConverter.serviceState();
        List<HypervisorServiceStateVO> result = new ArrayList<>();
        for (HypervisorServiceStateConvertModel m : modelList) {
            HypervisorServiceStateVO hypervisorServiceStateVO = new HypervisorServiceStateVO(m.getName(), m.getAlive(), m.getState());
            result.add(hypervisorServiceStateVO);
        }
        return new Result<>(result);
    }

    /**
     * 虚实结合交换机状态
     * @return
     */
    @ApiOperation("虚实结合交换机状态")
    @GetMapping("/virtual-real-switch-state")
    public Result<List<VirtualRealSwitchStatusVO>> virtualRealSwitchStatus(){
        ListResult<VirtualRealSwitchBO> model = hypervisorConverter.listVirtualRealSwitch();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            return new Result<>();
        }
        List<VirtualRealSwitchStatusVO> list = new ArrayList<>();
        for (VirtualRealSwitchBO v : model.getList()) {
            VirtualRealSwitchStatusVO build = VirtualRealSwitchStatusVO.builder().name(v.getName()).managementIp(v.getManagementIp()).state(v.getState())
                    .usedPorts(v.getUsedPorts()).ports(v.getPorts()).build();
            list.add(build);
        }
        return new Result<>(list);
    }

    /**
     * 释放无效资源
     * @return
     */
    @ApiOperation("释放无效资源")
    @GetMapping("/release_resource")
    public ServiceResult<Boolean> releaseResource() {
        TaskCommon.executeTask(() -> clearService.clearUselessResource());
        return new ServiceResult<>(true);
    }

    /**
     * 云平台服务状态监控
     * @return
     */
    @ApiOperation("云平台服务状态监控")
    @GetMapping("/cp_page")
    public ServiceResult<ListObjResult<ServiceMonitorDto>> page(CloudPlatformPageRequest request) {
        ListObjResult<ServiceMonitorDto> result = serviceMonitorService.cpPage(request);
        return ResultCommon.success(result);
    }

    /**
     * 服务重启
     * @param serverRestartRequest
     * @return
     */
    @ApiOperation("服务重启")
    @ApiImplicitParam(name = "serverRestartRequest", value = "服务重启请求体", dataTypeClass = ServerRestartRequest.class)
    @PostMapping("/restart")
    public ServiceResult<Boolean> restartServer(@RequestBody @Validated ServerRestartRequest serverRestartRequest) {
        monitorScriptControlService.restart(serverRestartRequest.getHostname(), serverRestartRequest.getServiceList());
        return new ServiceResult<>(true);
    }

    /**
     * 设备运行监控
     * @param request
     * @return
     */
    @AuthTarget({TargetEnum.CLOUD_PLATFORM_MONITORING, TargetEnum.SYSTEM_ALARM})
    @ApiOperation("设备运行监控")
    @GetMapping("/server/state")
    public ServiceResult<List<HypervisorPageVO>> page(AgentNameBasedRequest request){
        request.setPage(1);
        request.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStatesByAgentName(request);
        List<HypervisorPageVO> hypervisorPageVOList = new ArrayList<>();
        for (HostState hostState : hostStates) {// todo 上下行带宽占比
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
                BigDecimal decimal = formatNumber(ds * 8 / (maxNetSpeed * 1024));

                if (ds > 0 && decimal.doubleValue() == 0.00) {
                    downloadPercent = 0.01;
                } else {
                    downloadPercent = decimal.doubleValue() * 100;
                    downloadPercent = downloadPercent > 100 ? 100.00 : formatNumber(downloadPercent).doubleValue();
                }
            }

            if (StringUtils.isNotBlank(hostState.getMaxUploadSpeed())) {
                String uploadSpeed = hostState.getMaxUploadSpeed();
                uploadSpeed = uploadSpeed.replace("KB/s", "");
                double us = Double.parseDouble(uploadSpeed);
                BigDecimal decimal = formatNumber(us * 8 / (maxNetSpeed * 1024));

                if (us > 0 && decimal.doubleValue() == 0.00) {
                    uploadPercent = 0.01;
                } else {
                    uploadPercent = decimal.doubleValue() * 100;
                    uploadPercent = uploadPercent > 100 ? 100.00 : formatNumber(uploadPercent).doubleValue();
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
        }
        return ResultCommon.success(hypervisorPageVOList);
    }

    private BigDecimal formatNumber(double number) {
        return BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_DOWN);
    }

}
