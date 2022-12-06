package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.cscene.config.CPCloudConfig;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.request.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.dto.request.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.situationaware.model.wazuh.common.request.AgentRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.SystemUser;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.AgentService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 20:19
 */
@Slf4j
@Service
public class HostStateService {


    @Value("${mp.cscene.default.hours-range:24}")
    private Integer hoursRange;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private CsceneCommonService csceneCommonService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private CsceneAppConfig csceneAppConfig;

    public List<HostState> getHostStatesFromWazuh(String sceneId, String agentName, LocalDateTime startTime, LocalDateTime endTime) {
        List<CollectionHostStateBO> hostStateBOS = foo(agentName, startTime, endTime);

        List<HostState> hostStates = hostStateBOS.stream().map(hostStateBO -> {
            HostState hs = new HostState();
            hs.setAgentName(hostStateBO.getAgentName());
            hs.setCpuCount(hostStateBO.getCpuCount());
            hs.setCpuPercent(Objects.nonNull(hostStateBO.getCpuPercent()) ? NumberUtil.round(hostStateBO.getCpuPercent(), 2).doubleValue() : hostStateBO.getCpuPercent());
            hs.setMemoryFree(hostStateBO.getMemoryFree());
            hs.setMemoryTotal(hostStateBO.getMemoryTotal());
            // todo 节点内存利用率 统一为Double？
//            hs.setMemoryPercent(Objects.nonNull(hostStateBO.getMemoryPercent() ? NumberUtil.round(hostStateBO.getMemoryPercent(), 2).doubleValue() : hostStateBO.getMemoryPercent()));
            hs.setDiskSpace(hostStateBO.getDiskSpace());
            hs.setDiskPercent(hostStateBO.getDiskPercent());
            //hs.setDiskUsed(hostStateBO.getDiskUsed());
            hs.setDiskUnused(hostStateBO.getDiskUnused());
            hs.setSceneId(sceneId);
//            hs.setElementId(hostStateBO.getNodeId());
            hs.setTime(hostStateBO.getTime());
            return hs;
        }).collect(Collectors.toList());

        return hostStates;
    }



    private List<CollectionHostStateBO> foo(String agentName, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询参数
        var request = new WazuhMonitorEventsRequest();
        request.setPageNum(1);
        // todo 暂时最多取 10000 条
        request.setPageSize(1);
        request.setAgentNames(Arrays.asList(agentName));
        // 开始时间,默认为场景创建时间
        request.setStartTime(startTime);
        // 结束时间,默认为当前时间
        request.setEndTime(endTime);

        // 执行查询
        List<WazuhHostStateModel> result = dataCollectionService.queryHostStateEventsWithTimeRange(request);
        List<CollectionHostStateBO> hostStateBOs = result.stream().map(srcHostState -> {
            CollectionHostStateBO hostStateBO = new CollectionHostStateBO();
            hostStateBO.setCpuCount(srcHostState.getCpuCount());
            hostStateBO.setCpuPercent(srcHostState.getCpuTotalPercent());
            // 内存统一使用单位M
            hostStateBO.setMemoryTotal(this.byte2MB(srcHostState.getMemoryTotal()));
            hostStateBO.setMemoryFree(this.byte2MB(srcHostState.getMemoryFree()));
            // 磁盘统一使用单位G
            hostStateBO.setDiskSpace(this.byte2GB(srcHostState.getDiskTotal()));
            hostStateBO.setMemoryPercent(srcHostState.getMemoryPercent());
            hostStateBO.setTime(srcHostState.getTime());
            // todo 进程，端口，网络连接，磁盘使用率
//            var srcElement = agentName2Element.getOrDefault(srcHostState.getAgentName(), null);
//            if (srcElement != null) {
//                hostState.setNodeId(srcElement.getId());
//                hostState.setNodeName(srcElement.getName());
//            }
            return hostStateBO;
        }).collect(Collectors.toList());

        return hostStateBOs;
    }

    private long byte2MB(long num) {
        return num >> 20;
    }

    private long byte2GB(long num) {
        return num >> 30;
    }

    /**
     * 查询单个节点机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getElementHostStateTrend(HostStateTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));

        return processHostStateTrend(sceneIds, nodeIds, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    /**
     * 查询场景机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getSceneHostStateTrend(HostStateTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processHostStateTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }


    /**
     * 查询场景机器状态趋势
     *
     * @param trendRequest
     * @return
     */
    public List<HostState> getClusterHostStateTrend(HostStateTrendRequest trendRequest) {
        if (StringUtils.isBlank(csceneAppConfig.getCpIp()) || StringUtils.isBlank(csceneAppConfig.getCpPort())) {
            log.error("cp cloud ip or port is null...");
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        List<String> agentNames = getAgentNames();

        return processHostStateTrendByAgentNames(agentNames, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }

    private List<HostState> processHostStateTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
            if (CollectionUtils.isNotEmpty(sceneIds)) {
                var result = csceneSpecialService.getSceneStateInfo(sceneIds.get(0));
                if (!result.getSource().isRunning() && result.getSource().getSceneModel() != null) {
                    endTime = result.getSource().getSceneModel().getUpdateTime();
                }
            }
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔24小时
            startTime = endTime.minusHours(hoursRange);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalAvgHostCpuPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalAvgHostMemoryPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalAvgHostDiskPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());

        return listHostStates(timeList, cpuResult, memResult, diskResult);
    }

    private List<HostState> processHostStateTrendByAgentNames(List<String> agentNames, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔24小时
            startTime = endTime.minusHours(hoursRange);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalAvgPercentByAgentNames(agentNames, startTime, endTime, intervalSeconds.intValue(), (state) -> state.getCpuPercent());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalAvgPercentByAgentNames(agentNames, startTime, endTime, intervalSeconds.intValue(), (state) -> state.getMemoryPercent());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalAvgPercentByAgentNames(agentNames, startTime, endTime, intervalSeconds.intValue(), (state) -> state.getDiskPercent());

        return listHostStates(timeList, cpuResult, memResult, diskResult);
    }


    /**
     * 查询单个节点机器当前状态
     *
     * @return
     */
    // todo 老方法，待优化
    public List<HostState> listHostStates(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);
        String sceneId = pageRequest.getSceneId();
        List<String> elementIds = pageRequest.getElementIds();
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setNodeIds(elementIds);
        queryParams.setPage(pageRequest.getPage());
        queryParams.setPageSize(pageRequest.getPageSize());
//
        ListObjServiceResult<CollectionHostStateBO> listObjServiceResult = csceneInstanceService.listHostStates(sceneId, queryParams);

        List<CollectionHostStateBO> hostStateBOS = listObjServiceResult.getSource().getObjs();

        return collectionHostStateBOs2HostStates(hostStateBOS);
    }

    public List<HostState> listHostStatesByAgentName(AgentNameBasedRequest request) {
        request.ensure();
        if (CollectionUtil.isEmpty(request.getAgentNames())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.AGENT_NAME)));
        }
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setPage(request.getPage());
        queryParams.setPageSize(request.getPageSize());
//
        ListObjServiceResult<CollectionHostStateBO> listObjServiceResult = csceneInstanceService.listHostStatesByAgentNames(request.getAgentNames(), queryParams);

        List<CollectionHostStateBO> hostStateBOS = listObjServiceResult.getSource().getObjs();

        return collectionHostStateBOs2HostStates(hostStateBOS);
    }

    private List<HostState> collectionHostStateBOs2HostStates(List<CollectionHostStateBO> hostStateBOS) {
        List<HostState> hostStates = hostStateBOS.stream().map(hostStateBO -> {
            HostState hs = new HostState();
            hs.setAgentName(hostStateBO.getAgentName());
            hs.setCpuFreq(hostStateBO.getCpuFreq());
            hs.setCpuCount(hostStateBO.getCpuCount());
            hs.setCpuPercent(Objects.nonNull(hostStateBO.getCpuPercent()) ? NumberUtil.round(hostStateBO.getCpuPercent(), 2).doubleValue() : hostStateBO.getCpuPercent());

            hs.setMemoryFree(hostStateBO.getMemoryFree());
            hs.setMemoryTotal(hostStateBO.getMemoryTotal());
            hs.setMemoryPercent(Objects.nonNull(hostStateBO.getMemoryPercent()) ? NumberUtil.round(hostStateBO.getMemoryPercent(), 2).doubleValue() : hostStateBO.getMemoryPercent());

            hs.setDiskSpace(hostStateBO.getDiskSpace());
            hs.setDiskPercent(hostStateBO.getDiskPercent());
            //hs.setDiskUsed(hostStateBO.getDiskUsed());
            hs.setDiskUnused(hostStateBO.getDiskUnused());

            hs.setSceneId(hostStateBO.getSceneId());
            hs.setElementId(hostStateBO.getNodeId());
            hs.setTime(hostStateBO.getTime());

            hs.setDownloadSpeed(hostStateBO.getDownloadSpeed());
            hs.setUploadSpeed(hostStateBO.getUploadSpeed());
            hs.setDiskReadBytes(hostStateBO.getDiskReadBytes());
            hs.setDiskWriteBytes(hostStateBO.getDiskWriteBytes());
            return hs;
        }).collect(Collectors.toList());

        return hostStates;
    }


    private List<HostState> listHostStates(List<LocalDateTime> timeList, ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult, ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult, ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult) {
        List<IntervalSizeBO<LocalDateTime, Double>> intervalCpuPercentBOs = cpuResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalMemPercentBOs = memResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalDiskPercentBOs = diskResult.getSource();

        Map<LocalDateTime, HostState> hostStateMap = new LinkedHashMap<>();
        for (LocalDateTime time : timeList) {
            HostState hostState = new HostState();
            // 初始化
            hostState.setTime(time);
            hostState.setCpuPercent(0.0);
            hostState.setMemoryPercent(0.0);
            // todo diskPercent
            hostState.setDiskPercent(0.0);
            hostStateMap.put(time, hostState);
        }

        // cpu
        if (Objects.nonNull(intervalCpuPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalCpuPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setCpuPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }

        // 内存
        if (Objects.nonNull(intervalMemPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalMemPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setMemoryPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }

        // 磁盘
        if (Objects.nonNull(intervalDiskPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalDiskPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setDiskPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }

        List<HostState> hostStates = new ArrayList<>();
        hostStateMap.entrySet().forEach(entry -> {
            hostStates.add(entry.getValue());
        });
        hostStates.sort(Comparator.comparing(HostState::getTime));

        return hostStates;
    }

    public ListObjServiceResult<Port> pagePorts(ListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        return scenePluginCollectionSceneService.listPagePorts(listPageInfoQueryParams).asResult();
    }

    public ListObjServiceResult<Process> pageProcesses(ListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        ListObjServiceResult<Process> listResult = scenePluginCollectionSceneService.listPageProcess(listPageInfoQueryParams).asResult();
        List<Process> processes = listResult.getSource().getList();
        // KB -> MB
        processes.forEach(p -> {
            if (Objects.nonNull(p.getSize())) {
                Long megabytes = DataSize.ofKilobytes(p.getSize()).toMegabytes();
                p.setSize(megabytes);
            }

            if (Objects.nonNull(p.getVmSize())) {
                Long megabytes = DataSize.ofKilobytes(p.getVmSize()).toMegabytes();
                p.setVmSize(megabytes);
            }
        });

        return listResult;
    }


    public ListObjServiceResult<SystemUser> pageSystemUserEvents(ListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        ListObjServiceResult<SystemUser> listResult = scenePluginCollectionSceneService.listPageSystemUserEvents(listPageInfoQueryParams).asResult();
        return listResult;
    }

    /**
     * 分页查询网卡信息
     * @param listPageInfoQueryParams
     * @return
     */
    public ListObjServiceResult<Netiface> pageNetifaces(ListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        ListObjServiceResult<Netiface> netifacesResult = scenePluginCollectionSceneService.listNetifaces(listPageInfoQueryParams).asResult();
        //List<ElementNetiface> elementNetifaces = new ArrayList<>();
        //if (Objects.nonNull(netifacesResult.getSource())) {
        //    List<Netiface> netifaceList = netifacesResult.getSource().getList();
        //    netifaceList.forEach(n -> {
        //        ElementNetiface eNetiface = new ElementNetiface();
        //        BeanUtils.copyProperties(n, eNetiface);
        //        elementNetifaces.add(eNetiface);
        //    });
        //}

        //return new ListObjServiceResult<>(new ListObjResult<>(elementNetifaces, netifacesResult.getSource().getTotal()).asResult()).asResult();
        return netifacesResult;
    }


    private void checkListPageParams(ListPageInfoQueryParams params) {
        List<String> sceneIds = params.getSceneIds();
        if (CollectionUtil.isEmpty(sceneIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.SCENE_ID)));
        }

        List<String> nodeIds = params.getNodeIds();
        if (CollectionUtil.isEmpty(nodeIds)) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.ELEMENT_LIST)));
        }
    }

    private List<String> getAgentNames() {
        String url = "http://" + csceneAppConfig.getCpIp() + ":" + csceneAppConfig.getCpPort() + "/v1/hypervisors";
        String result = HttpRequest.get(url).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        Map<String, Object> data = (Map<String, Object>) JsonCommon.parseMap(result).get("data");
        List<Map<String, String>> list = (List<Map<String, String>>) data.get("list");
        List<String> agentNames = list.stream().map(map -> map.get("hostname")).collect(Collectors.toList());
        return agentNames;
    }

    /**
     * 包装请求头信息
     *
     * @param headers
     * @return
     */
    private Map<String, String> wrapperHeaders(Map<String, String> headers) {
        if (MapUtils.isEmpty(headers)) {
            headers = new HashMap<>();
        }
        headers.put("Accept-Language", ServletUtils.getAcceptLanguage());
        return headers;
    }
}
