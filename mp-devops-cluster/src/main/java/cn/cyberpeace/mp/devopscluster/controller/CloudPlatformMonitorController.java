package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.cron.clearjob.ClearService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
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
import cn.cyberpeace.mp.devopscluster.model.PhysicNodeModel;
import cn.cyberpeace.mp.devopscluster.model.common.ServiceStatus;
import cn.cyberpeace.mp.devopscluster.repository.PhysicNodeMapper;
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
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GuacamoleCreateConnectionData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamipsStatisticsResult;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
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

    private final CsceneService csceneService;

    private final Remote REMOTE = new Remote();

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
            try {
                Integer totalInstances = vmConverter.getTotalInstances(new InstancePageDTO());
                finalResponse.setTotalInstances(totalInstances);
            } catch (Exception e) {
                log.error("failed to get vm instances", e);
                finalResponse.setTotalInstances(0);
            }
        });

        //容器个数
        HypervisorStatisticsVO finalResponse1 = response;
        CompletableFuture totalContainersCf = CompletableFuture.runAsync(() -> {
            try {
                Integer totalContainers = containerConverter.getTotalContainers(new ContainerPageDTO());
                finalResponse1.setTotalContainers(totalContainers);
            } catch (Exception e) {
                log.error("failed to get container instances", e);
                finalResponse1.setTotalContainers(0);
            }
        });

        //实装设备个数
        RealEquipmentPageRequest realEquipmentPageRequest = new RealEquipmentPageRequest();
        realEquipmentPageRequest.setPerPage(NumberUtils.INTEGER_ONE);

        HypervisorStatisticsVO finalResponse2 = response;
        CompletableFuture totalRealEquipmentsCf = CompletableFuture.runAsync(() -> {
            try {
                Integer totalRealEquipments = realEquipmentConverter.getTotalRealEquipment(realEquipmentPageRequest);
                finalResponse2.setTotalRealEquipments(totalRealEquipments);
            }catch (Exception e){
                log.error("failed to get RealEquipments instances", e);
                finalResponse2.setTotalRealEquipments(0);
            }
        });

        //路由个数
        HypervisorStatisticsVO finalResponse3 = response;
        CompletableFuture totalRoutersCf = CompletableFuture.runAsync(() -> {
            try {
                Integer totalRouters = routerConverter.getTotalRouters(new RouterPageDTO());
                finalResponse3.setTotalRouters(totalRouters);
            }catch (Exception e){
                log.error("failed to get totalRouters instances", e);
                finalResponse3.setTotalRouters(0);
            }
        });

        //NPC个数
        HypervisorStatisticsVO finalResponse4 = response;
        CompletableFuture totalNpcsCf = CompletableFuture.runAsync(() -> {
            try {
                Integer totalNpcs = npcConverter.getTotalNpcs(new BasePageDTO());
                finalResponse4.setTotalNpc(totalNpcs);
            }catch (Exception e){
                log.error("failed to get totalNpcs instances", e);
                finalResponse4.setTotalNpc(0);
            }
        });

        // 防火墙个数
        HypervisorStatisticsVO finalResponse5 = response;
        CompletableFuture totalFirewallsCf = CompletableFuture.runAsync(() -> {
            try {
                Integer totalFirewalls = firewallConverter.getTotalFirewall(new BasePageDTO());
                finalResponse5.setTotalFireWall(totalFirewalls);
            }catch (Exception e){
                log.error("failed to get totalFirewalls instances", e);
                finalResponse5.setTotalFireWall(0);
            }
        });

        // 交换机个数 -> 替换为网络个数
        HypervisorStatisticsVO finalResponse6 = response;
        CompletableFuture totalNetworksCf = CompletableFuture.runAsync(() -> {
            try {
                Integer totalNetworks = networkConverter.getTotalNetwork(new NetworkPageDTO());
                finalResponse6.setTotalSwitch(totalNetworks);
            }catch (Exception e){
                log.error("failed to get totalFirewalls instances", e);
                finalResponse6.setTotalSwitch(0);
            }
        });

        // dynamips个数
        HypervisorStatisticsVO finalResponse7 = response;
        CompletableFuture totalDynamipsCf = CompletableFuture.runAsync(() -> {
            try {

                VirtualDynamipsStatisticsResult result = new VirtualDynamips().statistics();
                int totalDynamips = 0;
                for (Integer v : result.getStatics().values()) {
                    totalDynamips += v;
                }
                finalResponse7.setTotalDynamips(totalDynamips);
            }catch (Exception e){
                log.error("failed to get TotalDynamips instances", e);
                finalResponse7.setTotalDynamips(0);
            }
        });

        HypervisorStatisticsVO finalResponse8 = response;
        CompletableFuture hypervisorStatisticsCf = CompletableFuture.runAsync(() -> {
            try {
                HypervisorStatisticsConvertModel hypervisorStatistics = hypervisorConverter.statistics();
                if(hypervisorStatistics != null){
                    finalResponse8.build(hypervisorStatistics.getCpus(), hypervisorStatistics.getMemoryMb()
                            ,hypervisorStatistics.getLocalGb(),hypervisorStatistics.getCpusUsed(),hypervisorStatistics.getMemoryMbUsed()
                            ,hypervisorStatistics.getLocalGbUsed(),hypervisorStatistics.getFreeRamMb(),hypervisorStatistics.getFreeDiskGb());
                }
            }catch (Exception e){
                log.error("failed to get TotalDynamips instances", e);
                finalResponse8.build(0, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
            }
        });

        CompletableFuture allCf = CompletableFuture.allOf(totalInstanceCf, totalContainersCf, totalRealEquipmentsCf,
                totalRoutersCf, totalNpcsCf, totalFirewallsCf, totalNetworksCf, totalDynamipsCf, hypervisorStatisticsCf);
        allCf.join();
        return new Result<>(response);
    }


    @ApiOperation("云平台集群节点ssh登录")
    @PostMapping("/hypervisor/node/connection_url")
    public ServiceResult<String> getConnectionUrl(@RequestBody NodeConnectionReq nodeConnectionReq) {
        synchronized (nodeConnectionReq.getNodeName().intern()) {
            PhysicNodeMapper physicNodeMapper = BeanCommon.getBean(PhysicNodeMapper.class);
            String nodeName = nodeConnectionReq.getNodeName();
            if (StringUtils.isEmpty(nodeName)) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("节点信息异常"));
            }
            var physicNodes =  physicNodeMapper.list(new HumanCondition().filterEqual(PhysicNodeModel::getNodeName, nodeConnectionReq.getNodeName()));
            if (CollectionUtils.isEmpty(physicNodes)) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("不存在当前物理节点"));
            } else {
                if (StringUtils.isNotEmpty(physicNodes.get(0).getConnectionUrl())) {
                    return new ServiceResult<>(physicNodes.get(0).getConnectionUrl()).asResult();
                } else {

                    try {
                        GuacamoleCreateConnectionData guacamoleCreateConnectionData = new
                                GuacamoleCreateConnectionData();
                        guacamoleCreateConnectionData.setUsername(TextUtils.uuid());
                        guacamoleCreateConnectionData.setHost(nodeConnectionReq.getNodeName());
                        guacamoleCreateConnectionData.setFlag(false);
                        GuacamoleCreateConnectionData.ConnectionParams connectionParams = new GuacamoleCreateConnectionData.ConnectionParams();
                        connectionParams.setProtocol("ssh");
                        connectionParams.setHostname(nodeConnectionReq.getNodeIp());
                        connectionParams.setUsername("woodpecker");
                        connectionParams.setPassword("woodpecker");
                        connectionParams.setPort("22");
                        guacamoleCreateConnectionData.setConnectionParams(connectionParams);
                        String url = REMOTE.createConnection(guacamoleCreateConnectionData).getUrl();
                        physicNodes.get(0).setConnectionUrl(url);
                        physicNodeMapper.update(physicNodes.get(0));
                        return new ServiceResult<>(url).asResult();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("获取ssh链接失败"));
                    }
                }
            }
        }
    }


    @ApiOperation("云平台集群节点物理信息更新")
    @PostMapping("/hypervisor/node/rack_position")
    public ServiceResult<Boolean> updatePhysicNode(@RequestBody PhysicNodeModel physicNodeModel) {
        PhysicNodeMapper physicNodeMapper = BeanCommon.getBean(PhysicNodeMapper.class);
        String nodeName = physicNodeModel.getNodeName();
        if (StringUtils.isEmpty(nodeName)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("节点信息异常"));
        }
        var physicNodes =  physicNodeMapper.list(new HumanCondition().filterEqual(PhysicNodeModel::getNodeName, physicNodeModel.getNodeName()));
        if (CollectionUtils.isEmpty(physicNodes)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("不存在当前物理节点"));
        } else {
            physicNodes.forEach(physicNode -> {
                physicNode.setRackPosition(physicNodeModel.getRackPosition());
                physicNode.setCpuInfo(physicNodeModel.getCpuInfo());
                physicNode.setMemoryInfo(physicNodeModel.getMemoryInfo());
                physicNode.setDiskInfo(physicNodeModel.getDiskInfo());
                physicNode.setNicInfo(physicNodeModel.getNicInfo());
            });
            physicNodeMapper.bulkUpdate(physicNodes);
            return new ServiceResult<>(true).asResult();
        }
    }






    /**
     * 节点管理
     * @return
     */
    @AuthTarget({TargetEnum.CLOUD_PLATFORM_MONITORING, TargetEnum.DASHBOARD})
    @ApiOperation("节点管理")
    @GetMapping("/hypervisor/page")
    public PageResult<HypervisorPageVO> page(HypervisorPageRequest request){
        if (Boolean.TRUE.equals(request.getOnlyHostname())) {
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
                        .diskUsed(hypervisor.getDiskUsed()).build();
                hypervisorPageRespons.add(build);
            }

            addPhysicInfoToHypervisorPageRespons(hypervisorPageRespons);


            return new PageResult<>(hypervisorPageRespons, model.getTotal());
        }

        BasePageBO<HypervisorBO> model = hypervisorConverter.pageUnCache(new BasePageDTO(request.getLimit(),request.getOffset()));
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            return new PageResult<>(Lists.newArrayList(), NumberUtils.INTEGER_ZERO);
        }
        List<HypervisorPageVO> hypervisorPageRespons = new ArrayList<>();
        for (HypervisorBO hypervisor : model.getList()) {
            HypervisorPageVO build = HypervisorPageVO.builder().name(hypervisor.getName())
                    .ip(hypervisor.getIp()).status(hypervisor.getStatus()).state(hypervisor.getState())
                    .cpu(hypervisor.getCpu()).ram(hypervisor.getRam()).disk(hypervisor.getDisk())
                    .cpuUsed(hypervisor.getCpuUsed()).ramUsed(hypervisor.getRamUsed()).totalInstances(hypervisor.getTotalInstances())
                    .diskUsed(hypervisor.getDiskUsed()).build();
            hypervisorPageRespons.add(build);
        }

        // 获取所有物理主机服务状态
        CloudPlatformPageResult<ServiceStatus> serviceStatusModel = imagesConverter.listServiceStatus();
        for (HypervisorPageVO hypervisorPageRespon : hypervisorPageRespons) {
            String name = hypervisorPageRespon.getName();
            Optional<ServiceStatus> serviceStatus = Optional.empty();
            for (ServiceStatus statusModel : serviceStatusModel.getList()) {
                if (name.equals(statusModel.getHost())) {
                    serviceStatus = Optional.of(statusModel);
                    break;
                }
            }
            // 设置物理主机服务状态
            hypervisorPageRespon.setServiceStatus(serviceStatus.isPresent() && serviceStatus.get().isStatus());
        }

        // 调用接口次数太多，异步批量处理
        List<Runnable> queryTasks = new ArrayList<>();
        Map<String, HypervisorWeightAndMaxInstanceConvertModel> weightAndMaxInstancesMap = new ConcurrentHashMap<>();
        Map<String, Integer> totalContainersMap = new ConcurrentHashMap<>();
        Map<String, Integer> totalDynamipsMap = new ConcurrentHashMap<>();

        for (HypervisorPageVO hypervisorPageRespon : hypervisorPageRespons) {
            String name = hypervisorPageRespon.getName();

            queryTasks.add(() -> {
                HypervisorWeightAndMaxInstanceConvertModel weightAndMaxInstances = hypervisorConverter.getWeightAndMaxInstances(new HypervisorWeightAndMaxInstanceDTO(name));
                weightAndMaxInstancesMap.put(name, weightAndMaxInstances);
            });

            queryTasks.add(() -> {
                ContainerPageRequest req = new ContainerPageRequest();
                req.setHost(name);
                req.setLimit(1);
                req.setOffset(1);
                Integer totalContainers = containerConverter.getTotalContainers(new ContainerPageDTO(req));
                totalContainersMap.put(name, totalContainers);
            });
        }

        queryTasks.add(() -> {
            // 获取 dynamips 数量
            VirtualDynamips virtualDynamips = new VirtualDynamips();
            VirtualDynamipsStatisticsResult result = virtualDynamips.statistics();
            for (HypervisorPageVO v : hypervisorPageRespons) {
                if (result.getStatics() == null || result.getStatics().isEmpty() || !result.getStatics()
                        .containsKey(v.getName())) {
                    totalDynamipsMap.put(v.getName(), 0);
                } else {
                    totalDynamipsMap.put(v.getName(), result.getStatics().get(v.getName()));
                }
            }
        });

        // 批量执行
        TaskCommon.runTasks(queryTasks);

        // 回写数据
        for (HypervisorPageVO hypervisorPageRespon : hypervisorPageRespons) {
            hypervisorPageRespon.setTotalDynamips(totalDynamipsMap.get(hypervisorPageRespon.getName()));
            hypervisorPageRespon.setTotalContainers(totalContainersMap.get(hypervisorPageRespon.getName()));
            var weight = weightAndMaxInstancesMap.get(hypervisorPageRespon.getName());
            if(weight != null){
                hypervisorPageRespon.setWeight(weight.getWeight());
                hypervisorPageRespon.setMaxInstances(weight.getMaxInstances());
            }
        }
        addPhysicInfoToHypervisorPageRespons(hypervisorPageRespons);

        return new PageResult<>(hypervisorPageRespons, model.getTotal());
    }


    private void addPhysicInfoToHypervisorPageRespons(List<HypervisorPageVO> hypervisorPageRespons) {
        List<String> hostNames = hypervisorPageRespons.stream().map(HypervisorPageVO::getName).collect(Collectors.toList());
        PhysicNodeMapper physicNodeMapper = BeanCommon.getBean(PhysicNodeMapper.class);
        Map<String, PhysicNodeModel> nameToPhysicNodeMap = physicNodeMapper.list(new HumanCondition().filterIn(PhysicNodeModel::getNodeName, hostNames.toArray()))
                .stream().collect(Collectors.toMap(PhysicNodeModel::getNodeName, Function.identity(), (a, b) -> a));
        hypervisorPageRespons.forEach(hypervisorPageVO -> {
            var physicNode = nameToPhysicNodeMap.get(hypervisorPageVO.getName());
            if (Objects.isNull(physicNode)) {
                return;
            }
            hypervisorPageVO.setRackPosition(physicNode.getRackPosition());
            hypervisorPageVO.setOs(physicNode.getOs());
            hypervisorPageVO.setCpuInfo(physicNode.getCpuInfo());
            hypervisorPageVO.setMemoryInfo(physicNode.getMemoryInfo());
            hypervisorPageVO.setDiskInfo(physicNode.getDiskInfo());
            hypervisorPageVO.setNicInfo(physicNode.getNicInfo());
        });
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
        try {
            NodeLimitUtils.getAndRefreshCount();
        } catch (Exception e) {
            log.error("failed to refresh node limit count", e);
        }
        TaskCommon.executeTask(() -> {
            clearService.clearUselessResource();
        });
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
