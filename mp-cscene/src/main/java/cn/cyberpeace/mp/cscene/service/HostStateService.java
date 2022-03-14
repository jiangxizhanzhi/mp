package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.request.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.WazuhModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 20:19
 */
@Slf4j
@Service
public class HostStateService {

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private SceneIdTranslationService sceneIdTranslationService;

    @Autowired
    private DataCollectionService dataCollectionService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    /**
     * 查询单个节点机器当前状态
     *
     * @return
     */
    // todo 老方法，待优化
    public List<HostState> getHostStates(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        String sceneId = baseRequest.getSceneId();
        List<String> elementIds = baseRequest.getElementIds();
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setNodeIds(elementIds);
        queryParams.setPage(1);
        queryParams.setPageSize(1);
//
        ListObjServiceResult<CollectionHostStateBO> listObjServiceResult = csceneInstanceService.listHostStates(sceneId, queryParams);

        List<CollectionHostStateBO> hostStateBOS = listObjServiceResult.getSource().getObjs();

        List<HostState> hostStates = hostStateBOS.stream().map(hostStateBO -> {
            HostState hs = new HostState();
            hs.setCpuCount(hostStateBO.getCpuCount());
            hs.setCpuPercent(Objects.nonNull(hostStateBO.getCpuPercent()) ? NumberUtil.round(hostStateBO.getCpuPercent(), 2).doubleValue() : hostStateBO.getCpuPercent());
            hs.setMemoryFree(hostStateBO.getMemoryFree());
            hs.setMemoryTotal(hostStateBO.getMemoryTotal());
            // todo 节点内存利用率 统一为Double？
//            hs.setMemoryPercent(Objects.nonNull(hostStateBO.getMemoryPercent() ? NumberUtil.round(hostStateBO.getMemoryPercent(), 2).doubleValue() : hostStateBO.getMemoryPercent()));
            hs.setDiskSpace(hostStateBO.getDiskSpace());
            hs.setSceneId(hostStateBO.getSceneId());
            hs.setElementId(hostStateBO.getNodeId());
            hs.setTime(hostStateBO.getTime());
            return hs;
        }).collect(Collectors.toList());

        return hostStates;
    }


    public List<HostState> getHostStatesFromWazuh(String sceneId, String agentName, LocalDateTime startTime, LocalDateTime endTime) {
        List<CollectionHostStateBO> hostStateBOS = foo(agentName, startTime, endTime);

        List<HostState> hostStates = hostStateBOS.stream().map(hostStateBO -> {
            HostState hs = new HostState();
            hs.setCpuCount(hostStateBO.getCpuCount());
            hs.setCpuPercent(Objects.nonNull(hostStateBO.getCpuPercent()) ? NumberUtil.round(hostStateBO.getCpuPercent(), 2).doubleValue() : hostStateBO.getCpuPercent());
            hs.setMemoryFree(hostStateBO.getMemoryFree());
            hs.setMemoryTotal(hostStateBO.getMemoryTotal());
            // todo 节点内存利用率 统一为Double？
//            hs.setMemoryPercent(Objects.nonNull(hostStateBO.getMemoryPercent() ? NumberUtil.round(hostStateBO.getMemoryPercent(), 2).doubleValue() : hostStateBO.getMemoryPercent()));
            hs.setDiskSpace(hostStateBO.getDiskSpace());
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
        log.info("dataCollection query start");
        List<WazuhModel> result = dataCollectionService.queryMonitorEventsWithTimeRange(request);
        log.info("dataCollection query end");
        List<CollectionHostStateBO> hostStateBOs = result.stream().map(srcHostState -> {
            CollectionHostStateBO hostStateBO = new CollectionHostStateBO();
            hostStateBO.setCpuCount(srcHostState.getCpuCount());
            hostStateBO.setCpuPercent(srcHostState.getCpuTotalPercent());
            // 内存统一使用单位M
            hostStateBO.setMemoryTotal(this.byte2MB(srcHostState.getMemoryTotal()));
            hostStateBO.setMemoryFree(this.byte2MB(srcHostState.getMemoryFree()));
            // 磁盘统一使用单位G
            hostStateBO.setDiskSpace(this.byte2GB(srcHostState.getDiskSpace()));
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
        log.info("{}", trendRequest);
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
        log.info("{}", trendRequest);
        BaseRequestUtil.checkBaseRequestWithSceneId(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());

        return processHostStateTrend(sceneIds, null, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }

    private List<HostState> processHostStateTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔24小时
            startTime = endTime.minusHours(24);
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        List<String> sceneIdList = sceneIdTranslationService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalAvgHostCpuPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalAvgHostMemoryPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalAvgHostDiskPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());

        List<IntervalSizeBO<LocalDateTime, Double>> intervalCpuPercentBOs = cpuResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalMemPercentBOs = memResult.getSource();
        List<IntervalSizeBO<LocalDateTime, Double>> intervalDiskPercentBOs = diskResult.getSource();

        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);
        Map<LocalDateTime, HostState> hostStateMap = new HashMap<>();
        for (LocalDateTime localDateTime : timeList) {
            HostState hostState = new HostState();
            // 初始化
            hostState.setTime(localDateTime);
            hostState.setCpuPercent(0.0);
            hostState.setMemoryPercent(0.0);
            // todo diskPercent
            hostState.setDiskPercent(0.0);
            hostStateMap.put(localDateTime, hostState);
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
                    // todo 在这造磁盘模拟数据
                    hostStateMap.get(bo.getStart()).setDiskPercent(30.0);
                }
            }
        }

        // 磁盘
        /*if (Objects.nonNull(intervalDiskPercentBOs)) {
            for (IntervalSizeBO<LocalDateTime, Double> bo : intervalDiskPercentBOs) {
                if (Objects.nonNull(hostStateMap.get(bo.getStart()))) {
                    hostStateMap.get(bo.getStart()).setDiskPercent(Objects.nonNull(bo.getSize()) ? NumberUtil.round(bo.getSize(), 2).doubleValue() : 0.0);
                }
            }
        }*/

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
        if (!checkCsceneIsRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }

        listPageInfoQueryParams.setSceneIds(sceneIdTranslationService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        return scenePluginCollectionSceneService.listPagePorts(listPageInfoQueryParams).asResult();
    }

    public ListObjServiceResult<Process> pageProcesses(ListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (!checkCsceneIsRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }

        listPageInfoQueryParams.setSceneIds(sceneIdTranslationService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        return scenePluginCollectionSceneService.listPageProcess(listPageInfoQueryParams).asResult();
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

    private boolean checkCsceneIsRunning(String csceneId) {
        ServiceResult<SceneStateInfo> result = csceneSpecialService.getSceneStateInfo(csceneId);
        SceneStateInfo sceneStateInfo = result.getSource();
        return sceneStateInfo.isRunning();
    }

}
