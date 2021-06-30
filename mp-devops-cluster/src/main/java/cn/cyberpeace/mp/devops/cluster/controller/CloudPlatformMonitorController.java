package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.*;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorServiceStateResponse;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorStatisticsResponse;
import cn.cyberpeace.mp.devops.cluster.response.VirtualRealSwitchStatusResponse;
import cn.cyberpeace.mp.devops.cluster.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 云平台监控
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor")
public class CloudPlatformMonitorController {

    private HypervisorService hypervisorService;

    private InstanceService instanceService;

    private ContainerService containerService;

    private RouterService routerService;

    private RealEquipmentService realEquipmentService;

    private NpcService npcService;

    @Autowired
    public CloudPlatformMonitorController(HypervisorService hypervisorService, InstanceService instanceService,
                                          ContainerService containerService, RouterService routerService,
                                          RealEquipmentService realEquipmentService,NpcService npcService) {
        this.hypervisorService = hypervisorService;
        this.instanceService = instanceService;
        this.containerService = containerService;
        this.routerService = routerService;
        this.realEquipmentService =realEquipmentService;
        this.npcService = npcService;
    }

    /**
     * 资源统计
     * @return
     */
    @ApiOperation("资源统计")
    @GetMapping("/resources/statistics")
    public ServiceResult<HypervisorStatisticsResponse> statistics(){

        //虚拟机个数
        CompletableFuture<Integer> totalInstances = CompletableFuture.supplyAsync(() -> instanceService.getTotalInstances(new InstancePageRequest()));

        //容器个数
        CompletableFuture<Integer> totalContainers = CompletableFuture.supplyAsync(() -> containerService.getTotalContainers(new ContainerPageRequest()));

        //实装设备个数
        CompletableFuture<Integer> totalRealEquipments = CompletableFuture.supplyAsync(() -> realEquipmentService.getTotalRealEquipment());

        //NPC总数
        CompletableFuture<Integer> totalNPCs = CompletableFuture.supplyAsync(() -> npcService.getTotalNPCs());

        //路由个数
        CompletableFuture<Integer> totalRouters = CompletableFuture.supplyAsync(() -> routerService.getTotalRouters(new RouterPageRequest()));

        CompletableFuture<HypervisorStatisticsCloudPlatformModel> model = CompletableFuture.supplyAsync(() -> hypervisorService.statistics());

        CompletableFuture.allOf(totalInstances, totalContainers,totalRealEquipments,totalNPCs,totalRouters,model).join();

        //TODO 原计算方式不正确，云平台暂时不支持计算总的网络IO、磁盘IO
        //磁盘IO
        BigDecimal diskIo = BigDecimal.ZERO;
        //网络IO
        BigDecimal netIo = BigDecimal.ZERO;

        HypervisorStatisticsResponse response = null;
        try {
            response = HypervisorStatisticsResponse.builder().diskIo(diskIo).netIo(netIo)
                    .totalNpc(totalNPCs.get()).totalInstances(totalInstances.get())
                    .totalContainers(totalContainers.get()).totalRealEquipments(totalRealEquipments.get()).totalRouters(totalRouters.get()).build();
            if(Objects.isNull(model) || Objects.isNull(model.get().getHypervisor_statistics())){
                return new ServiceResult(response).asResult();
            }

            HypervisorStatisticsCloudPlatformModel.HypervisorStatistics hypervisorStatistics = model.get().getHypervisor_statistics();

            response = response.HypervisorStatisticsResponse(hypervisorStatistics.getVcpus(),hypervisorStatistics.getMemory_mb()
                    ,hypervisorStatistics.getLocal_gb(),hypervisorStatistics.getVcpus_used(),hypervisorStatistics.getMemory_mb_used()
                    ,hypervisorStatistics.getLocal_gb_used(),hypervisorStatistics.getFree_ram_mb(),hypervisorStatistics.getFree_disk_gb());
        } catch (InterruptedException e) {
           log.error("资源统计多线程异步合并请求异常信息:{}",e.getMessage());
            return new ServiceResult(response).asResult();
        } catch (ExecutionException e) {
            log.error("资源统计多线程异步合并请求异常信息:{}",e.getMessage());
            return new ServiceResult(response).asResult();
        }

        return new ServiceResult(response).asResult();
    }

    /**
     * 节点管理
     * @return
     */
    @ApiOperation("节点管理")
    @GetMapping("/hypervisor/page")
    public ListObjServiceResult<HypervisorPageResponse> page(HypervisorPageRequest request){
        HypervisorCloudPlatformModel model = hypervisorService.pageHypervisors(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getHypervisors())){
            return new ListObjServiceResult();
        }
        List<HypervisorCloudPlatformModel.Hypervisor> hypervisorList = model.getHypervisors();
        List<HypervisorPageResponse> hypervisorPageResponses = hypervisorList.stream().map(hypervisor ->
                HypervisorPageResponse.builder().name(hypervisor.getHostname())
                        .ip(hypervisor.getHost_ip()).status(hypervisor.getStatus()).state(hypervisor.getState())
                        .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                        .cpuUsed(hypervisor.getCpu_used()).ramUsed(hypervisor.getRam_used())
                        .diskUsed(hypervisor.getDisk_used()).build()
        ).collect(Collectors.toList());

        hypervisorPageResponses.stream().forEach(v -> {
            String name = v.getName();
            v.setTotalInstances(instanceService.getTotalInstances(new InstancePageRequest(name)));
            v.setTotalContainers(containerService.getTotalContainers(new ContainerPageRequest(name)));
            HypervisorWeightAndMaxInstanceCloudPlatformModel weightAndMaxInstances = hypervisorService.getWeightAndMaxInstances(new HypervisorWeightAndMaxInstanceRequest(name));
            if (Objects.nonNull(weightAndMaxInstances)) {
                v.setWeight(weightAndMaxInstances.getWeight());
                v.setMaxInstances(weightAndMaxInstances.getMax_instances());
            }
        });

        return new ListObjServiceResult(new ListObjResult(hypervisorPageResponses, model.getTotal())).asResult();
    }

    /**
     * 节点管理-关机/重启
     * @param request
     * @return
     */
    @ApiOperation("节点管理-关机/重启")
    @GetMapping("/hypervisor/reboot-poweroff")
    public ObjServiceResult rebootOrPowerOff(@Valid HypervisorRequest request){
        hypervisorService.rebootOrPowerOff(request);
        return new ObjServiceResult().asResult();
    }

    /**
     * 服务状态监控
     * @return
     */
    @ApiOperation("服务状态监控")
    @GetMapping("/service-state")
    public ServiceResult<HypervisorServiceStateResponse> serviceState(){
        List<HypervisorServiceStateResponse> responseList = hypervisorService.serviceState();
        return new ServiceResult(responseList).asResult();
    }

    /**
     * 虚实结合交换机状态
     * @return
     */
    @ApiOperation("虚实结合交换机状态")
    @GetMapping("/virtual-real-switch-state")
    public ServiceResult<VirtualRealSwitchStatusResponse> virtualRealSwitchStatus(){
        VirtualRealSwitchCloudPlatformModel model = hypervisorService.listVirtualRealSwitch();
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getSwitchs())){
            return new ServiceResult();
        }
        List<VirtualRealSwitchStatusResponse> list = model.getSwitchs().stream().map(v ->
                VirtualRealSwitchStatusResponse.builder().name(v.getName()).managementIp(v.getManagement_ip()).state(v.getStatus())
                        .usedPorts(Integer.valueOf(v.getInterfaces_used())).ports(Integer.valueOf(v.getInterfaces_summary())).build()
        ).collect(Collectors.toList());
        return new ServiceResult(list).asResult();
    }


}
