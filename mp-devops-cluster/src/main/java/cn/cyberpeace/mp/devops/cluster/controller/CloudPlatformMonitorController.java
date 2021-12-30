package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.converter.*;
import cn.cyberpeace.mp.devops.cluster.converter.model.*;
import cn.cyberpeace.mp.devops.cluster.enums.HypervisorActionEnum;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor")
@Api(tags = "云平台监控")
public class CloudPlatformMonitorController {

    private InstanceConverter instanceConverter;

    private ContainerConverter containerConverter;

    private RouterConverter routerConverter;

    private RealEquipmentConverter realEquipmentConverter;

    private NpcConverter npcConverter;

    private HypervisorConverter hypervisorConverter;

    @Autowired
    public CloudPlatformMonitorController(InstanceConverter instanceConverter, ContainerConverter containerConverter,
                                          RouterConverter routerConverter,
                                          RealEquipmentConverter realEquipmentConverter,NpcConverter npcConverter,
                                          HypervisorConverter hypervisorConverter) {
        this.instanceConverter = instanceConverter;
        this.containerConverter = containerConverter;
        this.routerConverter = routerConverter;
        this.realEquipmentConverter = realEquipmentConverter;
        this.npcConverter = npcConverter;
        this.hypervisorConverter = hypervisorConverter;
    }

    /**
     * 资源统计
     * @return
     */
    @ApiOperation("资源统计")
    @GetMapping("/resources/statistics")
    public Result<HypervisorStatisticsResponse> statistics(){

        //虚拟机个数
        CompletableFuture<Integer> totalInstances = CompletableFuture.supplyAsync(() ->{
                    InstancePageRequest request = new InstancePageRequest();
                    request.setPerPage(NumberUtils.INTEGER_ONE);
                    return instanceConverter.getTotalInstances(request);
                }).handle((result, throwable) -> {
            // 如果异常存在,打印异常，并且返回默认值
            if (throwable != null) {
                log.info("=======statistics方法:getTotalInstances获取虚拟机个数异常信息：{}",throwable.getMessage());
                return NumberUtils.INTEGER_ZERO;
            } else {
                return result;
            }
        });

        //容器个数
        CompletableFuture<Integer> totalContainers = CompletableFuture.supplyAsync(() ->{
                    ContainerPageRequest request = new ContainerPageRequest();
                    request.setPerPage(NumberUtils.INTEGER_ONE);
                    return containerConverter.getTotalContainers(request);
                }).handle((result,throwable) -> {
            // 如果异常存在,打印异常，并且返回默认值
            if (throwable != null) {
                log.info("========statistics方法:getTotalContainers获取容器个数异常信息：{}",throwable.getMessage());
                return NumberUtils.INTEGER_ZERO;
            } else {
                return result;
            }
        });

        //实装设备个数
        CompletableFuture<Integer> totalRealEquipments = CompletableFuture.supplyAsync(() -> {
                    RealEquipmentPageRequest realEquipmentPageRequest = new RealEquipmentPageRequest();
                    realEquipmentPageRequest.setPerPage(NumberUtils.INTEGER_ONE);
                    return realEquipmentConverter.getTotalRealEquipment(realEquipmentPageRequest);
                }).handle((result,throwable) -> {
            // 如果异常存在,打印异常，并且返回默认值
            if (throwable != null) {
                log.info("========statistics方法:getTotalRealEquipment获取实装个数异常信息：{}",throwable.getMessage());
                return NumberUtils.INTEGER_ZERO;
            } else {
                return result;
            }
        });


        //NPC总数
        CompletableFuture<Integer> totalNPCs = CompletableFuture.supplyAsync(() ->
                npcConverter.getTotalNPCs()).handle((result,throwable) -> {
            // 如果异常存在,打印异常，并且返回默认值
            if (throwable != null) {
                log.info("========statistics方法:getTotalNPCs获取NPC个数异常信息：{}",throwable.getMessage());
                return NumberUtils.INTEGER_ZERO;
            } else {
                return result;
            }
        });

        //路由个数
        CompletableFuture<Integer> totalRouters = CompletableFuture.supplyAsync(() ->{
                    RouterPageRequest request = new RouterPageRequest();
                    request.setPerPage(NumberUtils.INTEGER_ONE);
                    return routerConverter.getTotalRouters(request);
                }).handle((result,throwable) -> {
            // 如果异常存在,打印异常，并且返回默认值
            if (throwable != null) {
                log.info("========statistics方法:getTotalRouters获取路由个数异常信息：{}",throwable.getMessage());
                return NumberUtils.INTEGER_ZERO;
            } else {
                return result;
            }
        });

        CompletableFuture<HypervisorStatisticsConvertModel> model = CompletableFuture.supplyAsync(() ->
                hypervisorConverter.statistics()).handle((result,throwable) -> {
            // 如果异常存在,打印异常，并且返回默认值
            if (throwable != null) {
                log.info("========statistics方法:statistics获取资源统计信息异常信息：{}",throwable.getMessage(),throwable);
                return null;
            } else {
                return result;
            }
        });

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
            if(Objects.isNull(model) || Objects.isNull(model.get()) || Objects.isNull(model.get())){
                return new Result<>(response);
            }

            HypervisorStatisticsConvertModel hypervisorStatistics = model.get();

            response = response.HypervisorStatisticsResponse(hypervisorStatistics.getCpus(), hypervisorStatistics.getMemoryMb()
                    ,hypervisorStatistics.getLocalGb(),hypervisorStatistics.getCpusUsed(),hypervisorStatistics.getMemoryMbUsed()
                    ,hypervisorStatistics.getLocalGbUsed(),hypervisorStatistics.getFreeRamMb(),hypervisorStatistics.getFreeDiskGb());
        } catch (InterruptedException | ExecutionException e) {
            log.error("资源统计多线程异步合并请求异常信息:{}",e.getMessage());
        }
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
                        .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed())
                        .diskUsed(hypervisor.getDiskUsed()).build()
        ).collect(Collectors.toList());

        hypervisorPageResponses.forEach(v -> {
            String name = v.getName();
            HypervisorWeightAndMaxInstanceConvertModel weightAndMaxInstances = hypervisorConverter.getWeightAndMaxInstances(new HypervisorWeightAndMaxInstanceRequest(name));
            Integer totalInstances = instanceConverter.getTotalInstances(new InstancePageRequest(name));
            Integer totalContainers = containerConverter.getTotalContainers(new ContainerPageRequest(name));
            v.setTotalInstances(totalInstances);
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
    public ServiceResult<?> rebootOrPowerOff(@Valid HypervisorRequest request){
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
