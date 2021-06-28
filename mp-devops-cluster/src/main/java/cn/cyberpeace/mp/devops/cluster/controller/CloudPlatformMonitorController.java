package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorComputeStatusOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorWeightAndMaxInstanceOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorServiceStateResponse;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorStatisticsResponse;
import cn.cyberpeace.mp.devops.cluster.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
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
        final Integer totalInstances = instanceService.getTotalInstances(new InstancePageRequest());
        //容器个数
        final Integer totalContainers = containerService.getTotalContainers(new ContainerPageRequest());
        //实装设备个数
        final Integer totalRealEquipments = realEquipmentService.getTotalRealEquipment();
        //NPC总数
        final Integer totalNPCs = npcService.getTotalNPCs();
        //路由个数
        final Integer totalRouters = routerService.getTotalRouters(new RouterPageRequest());

        //获取所有节点的磁盘信息、网络IO、内存信息、网络IO、CPU信息
        List<HypervisorComputeStatusOpenStackModel> hypervisorComputeStatusOpenStackModels = hypervisorService.computeStatus();
        //磁盘IO
        BigDecimal diskIo = hypervisorComputeStatusOpenStackModels.stream().filter(v -> MapUtils.isNotEmpty(v.getDisk_io())).map(v -> new BigDecimal(v.getDisk_io().get("read_bytes/write_byte").toString())).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        //网络IO
        BigDecimal netIo = hypervisorComputeStatusOpenStackModels.stream().filter(v -> MapUtils.isNotEmpty(v.getNet_io())).map(v -> new BigDecimal(v.getNet_io().get("bytes_sent/bytes_recv").toString())).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        HypervisorStatisticsResponse response = HypervisorStatisticsResponse.builder().diskIo(diskIo).netIo(netIo)
                .totalNpc(totalNPCs).totalInstances(totalInstances)
                .totalContainers(totalContainers).totalRealEquipments(totalRealEquipments).totalRouters(totalRouters).build();

        HypervisorStatisticsOpenStackModel model = hypervisorService.statistics();
        if(Objects.isNull(model) || Objects.isNull(model.getHypervisor_statistics())){
            return new ServiceResult(response).asResult();
        }

        HypervisorStatisticsOpenStackModel.HypervisorStatistics hypervisorStatistics = model.getHypervisor_statistics();

        response = response.HypervisorStatisticsResponse(hypervisorStatistics.getVcpus(),hypervisorStatistics.getMemory_mb()
        ,hypervisorStatistics.getLocal_gb(),hypervisorStatistics.getVcpus_used(),hypervisorStatistics.getMemory_mb_used()
        ,hypervisorStatistics.getLocal_gb_used(),hypervisorStatistics.getFree_ram_mb(),hypervisorStatistics.getFree_disk_gb());

        return new ServiceResult(response).asResult();
    }

    /**
     * 节点管理
     * @return
     */
    @ApiOperation("节点管理")
    @GetMapping("/hypervisor/page")
    public ServiceResult<HypervisorPageResponse> page(HypervisorPageRequest request){
        HypervisorOpenStackModel model = hypervisorService.pageHypervisors(request);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getHypervisors())){
            return new ServiceResult();
        }
        List<HypervisorOpenStackModel.Hypervisor> hypervisorList = model.getHypervisors();
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
            HypervisorWeightAndMaxInstanceOpenStackModel weightAndMaxInstances = hypervisorService.getWeightAndMaxInstances(new HypervisorWeightAndMaxInstanceRequest(name));
            if (Objects.nonNull(weightAndMaxInstances)) {
                v.setWeight(weightAndMaxInstances.getWeight());
                v.setMaxInstances(weightAndMaxInstances.getMax_instances());
            }
        });

        return new ServiceResult(hypervisorPageResponses).asResult();
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
    public ServiceResult virtualRealSwitchStatus(){
        return new ServiceResult().asResult();
    }


}
