package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.*;
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.WazuhEventRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.model.WazuhVulnerabilityInfoCountModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.SysCollectorService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 场景节点监控Service
 * @Author zhouxinghuai
 * @Date：2021/11/23 14:45
 */
@Slf4j
@Service
public class SceneElementMonitorService {

    private static final String CPCLOUD_PLUGIN = "cpcloud";

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private SysCollectorService sysCollectorService;

    @Autowired
    private DataCollectionService dataCollectionService;

    /**
     * CsceneInstanceBO转为ElementMonitorInfo
     *
     * @return
     */
    public ElementMonitorBasicInfo getElementMonitorInfo(BaseSceneElementRequest baseRequest, boolean fetchRealTimeHostState) {
        log.info("baseRequest:{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        String sceneId = baseRequest.getSceneId();
        String elementId = baseRequest.getElementIds().get(0);

        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        ElementMonitorBasicInfo elementMonitorBasicInfo = new ElementMonitorBasicInfo();
        elementMonitorBasicInfo.setSceneId(sceneId);
        processTopologyElement(elementMonitorBasicInfo, csceneInstanceBO, elementId, fetchRealTimeHostState);
        processPluginElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);

        // todo 节点实时信息
        ElementRTInfo rtInfo = processRTInfo(baseRequest);

        elementMonitorBasicInfo.setRtInfo(rtInfo);

        return elementMonitorBasicInfo;
    }



    public ListObjServiceResult<ConnectionState> pageNetworkConnections(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);

        if (Objects.isNull(pageRequest.getFetchIfSceneInstanceShutdown()) || !pageRequest.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(pageRequest.getSceneId())) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setPage(pageRequest.getPage());
        listPageInfoQueryParams.setPageSize(pageRequest.getPageSize());
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollectionUtil.toList(pageRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(pageRequest.getElementIds());
        ListObjServiceResult<WazuhNetworkConnectionModel> netConns = scenePluginCollectionSceneService.listNetworkConnections(listPageInfoQueryParams).asResult();

        List<ConnectionState> connectionStates = new ArrayList<>();
        if (Objects.nonNull(netConns.getSource()) && CollectionUtil.isNotEmpty(netConns.getSource().getList())) {
            List<WazuhNetworkConnectionModel> list = netConns.getSource().getList();
            connectionStates = list.stream().map(model -> convertWazuhNetConn2ConnState(model)).collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(connectionStates, netConns.getSource().getTotal()).asResult()).asResult();
    }

    /**
     * 查询操作系统信息
     * @param baseRequest
     * @return
     */
    public OS getOSInfo(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        List<String> agentNames = csceneCommonService.getAgentNamesBySceneIdAndElementIds(baseRequest.getSceneId(), baseRequest.getElementIds());
        if (CollectionUtil.isEmpty(agentNames)) {
            return null;
        }
        String agentName = agentNames.get(0);
        List<OS> osList = sysCollectorService.getOS(agentName);
        if (CollectionUtil.isEmpty(osList)) {
            return null;
        }

        return osList.get(0);
    }


    public int sumEnvVarChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumEnvVarChangedCount(listPageInfoQueryParams);
        return sum;
    }

    public int sumProcessesChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumProcessesChangedCount(listPageInfoQueryParams);
        return sum;
    }


    public int sumSysUserChangedCount(BaseSceneElementRequest baseRequest) {
        ListPageInfoQueryParams listPageInfoQueryParams = buildListPageInfoQueryParams(baseRequest);
        int sum = scenePluginCollectionSceneService.sumSysUserChangedCount(listPageInfoQueryParams);
        return sum;
    }


    /**
     * 获取节点实时数据
     * @param baseRequest
     * @return
     */
    public ElementRTInfo getElementRTInfo(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        ElementRTInfo rtInfo = processRTInfo(baseRequest);

        return rtInfo;
    }


    public void processTopologyElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId, boolean fetchRealTimeHostState) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getTopology())) {
            return;
        }

        var topology = csceneInstanceBO.getScene().getTopology();
        var topologyElements = topology.getElements();
        Optional<SceneElementBO> tpoEleOptional = topologyElements.stream().filter(e -> Objects.equals(elementId, e.getId())).findFirst();

        if (tpoEleOptional.isPresent()) {
            SceneElementBO tpoEleBO = tpoEleOptional.get();
            sceneElementBO2ElementMonitorBasicInfo(basicInfo, tpoEleBO, csceneInstanceBO, fetchRealTimeHostState);
        }
    }

    public void processPluginElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return;
        }

        var scenePluginDataBO = csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN);
        var pluginElements = scenePluginDataBO.getElements();
        var pluginEleOptional = pluginElements.entrySet().stream().filter(entry -> Objects.equals(entry.getKey(), elementId)).map(Map.Entry::getValue).findFirst();

        if (pluginEleOptional.isPresent()) {
            CloudPluginSceneElementBO cloudPluginSceneElementBO = JsonCommon.snakeMapToBean(pluginEleOptional.get(), CloudPluginSceneElementBO.class);
            cpSceneElementBO2ElementMonitorBasicInfo(basicInfo, cloudPluginSceneElementBO);
        }
    }


    public void sceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, SceneElementBO sceneElementBO, CsceneInstanceBO csceneInstanceBO, boolean fetchRealTimeHostState) {
        log.info("fetchHostState:{}", fetchRealTimeHostState);
        // 获取hostState比较耗时，这里加个开关
        if (fetchRealTimeHostState) {
            HostState resource = basicInfo.getHostState();
            if (Objects.isNull(resource)) {
                resource = new HostState();
            }

            BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
            request.setSceneId(basicInfo.getSceneId());
            request.setElementIds(Arrays.asList(sceneElementBO.getId()));
            request.setPage(1);
            request.setPageSize(1);
            List<HostState> hostStates = hostStateService.getHostStates(request);
            if (CollectionUtil.isNotEmpty(hostStates)) {
                HostState hostState = hostStates.get(0);
                resource.setCpuCount(hostState.getCpuCount());
                resource.setMemoryTotal(hostState.getMemoryTotal());
                resource.setDiskSpace(hostState.getDiskSpace());
                //resource.setDiskUsed(hostState.getDiskUsed());
                resource.setDiskUnused(hostState.getDiskUnused());

                resource.setCpuPercent(Objects.nonNull(hostState.getCpuPercent()) ? NumberUtil.round(hostState.getCpuPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryPercent(Objects.nonNull(hostState.getMemoryPercent()) ? NumberUtil.round(hostState.getMemoryPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryFree(hostState.getMemoryFree());
                resource.setDiskPercent(Objects.nonNull(hostState.getDiskPercent()) ? NumberUtil.round(hostState.getDiskPercent(), 2).doubleValue() : 0.0);
            }
            basicInfo.setHostState(resource);
        }

        // ip地址
//        List<String> ipList = sceneElementBO.getPorts().stream().map(BaseSceneElementPortBO::getIp).collect(Collectors.toList());
//        basicInfo.setIps(ipList);

        // ID
        basicInfo.setId(sceneElementBO.getId());
        basicInfo.setEid(sceneElementBO.getEid());
        // 实例名称
        basicInfo.setName(sceneElementBO.getName());

        // 操作系统
        // 参考SystemTypeUtils
        if (StringUtils.isNotBlank(sceneElementBO.getSystemType())) {
            basicInfo.setSystemType(sceneElementBO.getSystemType());
            basicInfo.setSystemTypeName(SystemTypeUtils.getStandardSystemType(sceneElementBO.getSystemType()));
        }

        // ip, 用户名，登录方式
        var ports = sceneElementBO.getPorts();
        List<ElementPort> ePorts = ports.stream().map(p -> {
            ElementPort port = new ElementPort();
            port.setIp(p.getIp());
            port.setName(p.getName());
            port.setAccessModes(p.getAccessModes());
            return port;
        }).collect(Collectors.toList());
        basicInfo.setPorts(ePorts);

    }

    public void cpSceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, CloudPluginSceneElementBO cpSceneElementBO) {
        // 镜像名称
        basicInfo.setImageName(cpSceneElementBO.getImage());

        // 物理节点
        basicInfo.setHost(cpSceneElementBO.getHost());

        // 实例类型
        basicInfo.setVirtualType(cpSceneElementBO.getVirtualType());
    }

    public ListObjServiceResult<CollectionHostEventBO> getModifyData(ElementMonitorRequest pageRequest, List<String> ruleGroups,
                                                                     String path) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        if (pageRequest.getElementIds() == null || pageRequest.getElementIds().isEmpty()) {
            pageRequest.setElementIds(sceneHandler.getElementMap().values().stream().map(e -> e.getId()).collect(Collectors.toList()));
        }
        processRequest(pageRequest, sceneHandler);
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setNodeIds(pageRequest.getElementIds());
        hostEventQueryParams.setStartTime(TimeUtils.localDateTimeToString(pageRequest.getStartTime()));
        hostEventQueryParams.setEndTime(TimeUtils.localDateTimeToString(pageRequest.getEndTime()));
        hostEventQueryParams.setLevels(pageRequest.getLevels());
        hostEventQueryParams.setRuleGroups(ruleGroups);
        hostEventQueryParams.setPath(path);
        return csceneInstanceService.listHostEvents(pageRequest.getSceneId(), hostEventQueryParams);
    }

    public List<ElementHistogramInfo> getModifyHistogram(ElementMonitorRequest pageRequest, List<String> ruleGroups, Integer segmentCount) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        LocalDateTime startTime;
        LocalDateTime endTime;
        processRequest(pageRequest, sceneHandler);
        startTime = pageRequest.getStartTime();
        endTime = pageRequest.getEndTime();
        var result = getModifyData(pageRequest, ruleGroups, null);
        if (result == null || result.getSource() == null || CollectionUtil.isEmpty(result.getSource().getObjs())) {
            return new ArrayList<>();
        }
        result.getSource().getObjs().sort(Comparator.comparing(r -> r.getTime()));
        if (segmentCount == null) {
            //默认时间分15段
            segmentCount = 15;
        }
        long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<ElementHistogramInfo> histogramInfos = new ArrayList<>();
        Map<String, Integer> time2Count = new HashMap<>();
        for (int i = 1; i <= segmentCount; i++) {
            String time = TimeUtils.localDateTimeToString(startTime.plusSeconds(intervalSeconds * i));
            ElementHistogramInfo histogramInfo = new ElementHistogramInfo();
            histogramInfo.setTime(time);
            histogramInfo.setCount(0);
            histogramInfos.add(histogramInfo);
            time2Count.put(time, 0);
        }
        for (CollectionHostEventBO collectionData : result.getSource().getObjs()) {
            LocalDateTime nextTime = startTime.plusSeconds(intervalSeconds);
            String nextTimeString = TimeUtils.localDateTimeToString(nextTime);
            if (collectionData.getTime().isAfter(startTime) && collectionData.getTime().isBefore(nextTime)) {
                time2Count.put(nextTimeString, time2Count.get(nextTimeString) + 1);
            } else {
                time2Count.put(nextTimeString, time2Count.get(nextTimeString) + 1);
                startTime = startTime.plusSeconds(intervalSeconds);
            }
        }
        histogramInfos.forEach(h -> {
            h.setCount(time2Count.get(h.getTime()));
        });
        return histogramInfos;
    }

    public ListObjResult<ElementVulnerabilityInfo> getVulnerabilityInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        processRequest(pageRequest, sceneHandler);
        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Map<String, String> elementEid2Name = sceneHandler.getElementMap().values().stream().collect(Collectors.toMap(e -> e.getEid(), e -> e.getName()));
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler, cloudPlugin);
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(pageRequest.getStartTime());
        request.setEndTime(pageRequest.getEndTime());
        request.setPageNum(pageRequest.getPage());
        request.setPageSize(pageRequest.getPageSize());
        request.setAgentNames(agentNames);
        request.setRuleGroups(CollectionUtil.toList("vulnerability-detector"));
        var result = dataCollectionService.queryVulnerabilityInfo(request);
        List<ElementVulnerabilityInfo> vulnerabilityInfos = new ArrayList<>();
        result.getObjs().forEach(r -> {
            ElementVulnerabilityInfo vulnerabilityInfo = new ElementVulnerabilityInfo();
            vulnerabilityInfo.setNodeName(elementEid2Name.get(r.getAgentName()));
            vulnerabilityInfo.setCve(r.getVulnerability().getCve());
            vulnerabilityInfo.setSeverity(r.getVulnerability().getSeverity().toLowerCase());
            vulnerabilityInfo.setTitle(r.getVulnerability().getTitle());
            vulnerabilityInfo.setTime(r.getTime());
            vulnerabilityInfos.add(vulnerabilityInfo);
        });
        return new ListObjResult<>(vulnerabilityInfos, result.getTotal());
    }

    public List<ElementVulnerabilityPieChartInfo> getVulnerabilityPieChartInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        processRequest(pageRequest, sceneHandler);
        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler, cloudPlugin);
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(pageRequest.getStartTime());
        request.setEndTime(pageRequest.getEndTime());
        request.setAgentNames(agentNames);
        request.setRuleGroups(CollectionUtil.toList("vulnerability-detector"));
        var result = dataCollectionService.queryVulnerabilityCountInfo(request,
                false, "data.vulnerability.severity", "severity");
        if (CollectionUtil.isEmpty(result)) {
            return new ArrayList<>();
        }
        long i = 0;
        for (WazuhVulnerabilityInfoCountModel countModel : result) {
            i = countModel.getCount() + i;
        }
        long sum = i;
        List<ElementVulnerabilityPieChartInfo> pieChartInfos = new ArrayList<>();
        result.forEach(r -> {
            ElementVulnerabilityPieChartInfo pieChartInfo = new ElementVulnerabilityPieChartInfo();
            pieChartInfo.setSeverity(r.getFieldName().toLowerCase());
            if ("low".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(0);
            } else if ("medium".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(1);
            } else if ("high".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(2);
            } else if ("critical".equals(pieChartInfo.getSeverity())) {
                pieChartInfo.setSeverityLevel(3);
            }
            pieChartInfo.setPercent(BigDecimal.valueOf(r.getCount()).divide(BigDecimal.valueOf(sum), 4, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100)).doubleValue());
            pieChartInfos.add(pieChartInfo);
        });
        pieChartInfos.sort(Comparator.comparing(ElementVulnerabilityPieChartInfo::getSeverityLevel));
        return pieChartInfos;
    }

    public List<ElementVulnerabilityCountInfo> getVulnerabilityCountInfo(ElementMonitorRequest pageRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(pageRequest);
        var sceneId = csceneInstanceService.getSceneId(pageRequest.getSceneId());
        var sceneHandler = new SceneHandler(sceneId, null, false, null);
        processRequest(pageRequest, sceneHandler);
        CloudScenePluginHandler cloudPlugin = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        List<String> agentNames = getAgentNames(pageRequest, sceneHandler, cloudPlugin);
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(pageRequest.getStartTime());
        request.setEndTime(pageRequest.getEndTime());
        request.setAgentNames(agentNames);
        request.setRuleGroups(CollectionUtil.toList("vulnerability-detector"));
        var result = dataCollectionService.queryVulnerabilityCountInfo(request,
                true, "data.vulnerability.package.name", "package_name");
        List<ElementVulnerabilityCountInfo> countInfos = new ArrayList<>();
        result.forEach(r -> {
            ElementVulnerabilityCountInfo countInfo = new ElementVulnerabilityCountInfo();
            countInfo.setPackageName(r.getFieldName());
            countInfo.setCount((int) r.getCount());
            countInfos.add(countInfo);
        });
        return countInfos;
    }

    private void processRequest(ElementMonitorRequest pageRequest, SceneHandler sceneHandler) {
        if (pageRequest.getStartTime() == null) {
            pageRequest.setStartTime(sceneHandler.getScene().getCreateTime());
        }
        if (pageRequest.getEndTime() == null) {
            pageRequest.setEndTime(LocalDateTime.now());
        }
    }

    private List<String> getAgentNames(ElementMonitorRequest pageRequest, SceneHandler sceneHandler, CloudScenePluginHandler cloudPlugin) {
        List<String> agentNames;
        if (pageRequest.getElementIds() == null || pageRequest.getElementIds().isEmpty()) {
            agentNames = cloudPlugin.getElementMap().values().stream()
                    .filter(e -> e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER ||
                            e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE)
                    .map(e -> e.getSrcElement().getEid())
                    .collect(Collectors.toList());
        } else {
            agentNames = sceneHandler.getElementMap().values().stream()
                    .filter(e -> pageRequest.getElementIds().contains(e.getId()))
                    .map(e -> e.getEid())
                    .collect(Collectors.toList());
        }
        return agentNames;
    }

    private ConnectionState convertWazuhNetConn2ConnState(WazuhNetworkConnectionModel model) {
        ConnectionState connectionState = new ConnectionState();
        connectionState.setState(model.getStatus());
        connectionState.setProtocol(model.getProto());
        connectionState.setPid(model.getPid());

        connectionState.setLocalAddress(model.getLaddrIp());
        connectionState.setLocalPort(model.getLaddrPort());

        connectionState.setForeignAddress(model.getRaddrIp());
        connectionState.setForeignPort(model.getRaddrPort());

        return connectionState;
    }

    /**
     * 处理节点实时信息，用户，进程，端口，网络连接
     * @param baseRequest
     * @return
     */
    private ElementRTInfo processRTInfo(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        ElementRTInfo rtInfo = new ElementRTInfo();

        // 用户
        rtInfo.setUserCount(countNodeSessionUsers(baseRequest));

        ListPageInfoQueryParams portsParams = new ListPageInfoQueryParams();
        portsParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        portsParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Port> portsResult = hostStateService.pagePorts(portsParams);

        // pagePorts内部有sceneId转换动作，这里pageProcesses不能共用一个param
        ListPageInfoQueryParams processParams = new ListPageInfoQueryParams();
        processParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        processParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Process> processesResult = hostStateService.pageProcesses(processParams);

        BaseSceneElementPageRequest pageRequest = new BaseSceneElementPageRequest();
        pageRequest.setSceneId(baseRequest.getSceneId());
        pageRequest.setElementIds(baseRequest.getElementIds());
        ListObjServiceResult<ConnectionState> conns = pageNetworkConnections(pageRequest);

        if (Objects.nonNull(conns.getSource().getTotal())) {
            // 网络连接
            rtInfo.setConnectionCount(conns.getSource().getTotal());
        }

        if (Objects.nonNull(portsResult.getSource().getTotal())) {
            // 端口
            rtInfo.setPortsCount(portsResult.getSource().getTotal());
        }

        if (Objects.nonNull(processesResult.getSource().getTotal())) {
            // 进程
            rtInfo.setProcessCount(processesResult.getSource().getTotal());
        }

        // 文件
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<CollectionHostEventBO> fileResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            // 文件数量包含其他所有类型，所以这里不会剔除注册表数量
           rtInfo.setFileOpCount(fileResult.getSource().getTotal());
        }

        // 注册表
        hostEventQueryParams.setRuleGroups(CollectionUtil.toList("syscheck_registry"));
        ListObjServiceResult<CollectionHostEventBO> registryResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            // 注册表数量
            rtInfo.setRegistryOpCount(registryResult.getSource().getTotal());
        }

        // todo 文件操作，注册表变动，环境变量变动
        // todo 环境变量
        int envVarChangedCount = sumEnvVarChangedCount(baseRequest);
        rtInfo.setEnvOpCount(envVarChangedCount);

        return rtInfo;
    }

    /**
     * 统计节点当前登录用户数量
     * @param baseSceneElementRequest
     * @return
     */
    private int countNodeSessionUsers(BaseSceneElementRequest baseSceneElementRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(baseSceneElementRequest);
        ServiceResult<List<UserNodeSessionInfo>> result = csceneInstanceService.getNodeSessions(CollectionUtil.toList(baseSceneElementRequest.getSceneId()), baseSceneElementRequest.getElementIds(), null, null);
        if (CollectionUtil.isNotEmpty(result.getSource())) {
            return result.getSource().size();
        } else {
            return 0;
        }
    }

    private ListPageInfoQueryParams buildListPageInfoQueryParams(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollectionUtil.toList(baseRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(baseRequest.getElementIds());
        return listPageInfoQueryParams;
    }

}
