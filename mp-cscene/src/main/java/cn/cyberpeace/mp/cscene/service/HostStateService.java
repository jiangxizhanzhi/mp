 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HostStateService.java.
 // File size 35961.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.bo.IntervalSizeBO;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.ServletUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.base.util.mptool.HttpRequest;
import cn.cyberpeace.mp.base.util.mptool.NumberUtil;
import cn.cyberpeace.mp.cscene.config.CsceneAppConfig;
import cn.cyberpeace.mp.cscene.constant.CsceneBasei18nCode;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.scene.SceneAttCkTypeCountInfo;
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.AttCkTypeCount;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostStateBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostStateQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.model.WazuhHostStateModel;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorEventsRequest;
import cn.cyberpeace.mp.situationaware.model.WazuhMonitorRoot;
import cn.cyberpeace.mp.situationaware.model.sic.BaseInput;
import cn.cyberpeace.mp.situationaware.model.sic.HostStateOutput;
import cn.cyberpeace.mp.situationaware.model.sic.Launch;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.SystemUser;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import cn.cyberpeace.mp.situationaware.service.wazuh.AgentService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

 /**
 * @ClassName HostStateService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/29 20:19
 * @Version 1.0.0
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
    private AgentService agentService;

    @Autowired
    private CsceneAppConfig csceneAppConfig;

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Autowired
    private CscenePluginDataRecordService dataRecordService;

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient ;


    /**
     * getHostStatesFromWazuh
     *
     * @param sceneId
     * @param agentName
     * @param startTime
     * @param endTime
     * @return
     */
    public List<HostState> getHostStatesFromWazuh(String sceneId, String agentName, LocalDateTime startTime, LocalDateTime endTime) {
        List<CollectionHostStateBO> hostStateBOS = foo(agentName, startTime, endTime);

        List<HostState> hostStates = new ArrayList<>();
        for (CollectionHostStateBO hostStateBO : hostStateBOS) {
            HostState hostState = getHostState(sceneId, hostStateBO);
            hostStates.add(hostState);
        }

        return hostStates;
    }

    /**
     * getHostState
     *
     * @param sceneId
     * @param hostStateBO
     * @return
     */
    private static HostState getHostState(String sceneId, CollectionHostStateBO hostStateBO) {
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
    }


    /**
     * foo
     *
     * @param agentName
     * @param startTime
     * @param endTime
     * @return
     */
    private List<CollectionHostStateBO> foo(String agentName, LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询参数
        WazuhMonitorEventsRequest request = new WazuhMonitorEventsRequest();
        request.setPageNum(1);
        // todo 设置最多读取最大条数
        request.setPageSize(situationAwareConfig.getWazuhEsMaxPageSize());
        request.setAgentNames(Arrays.asList(agentName));
        // 开始时间,默认为场景创建时间
        request.setStartTime(startTime);
        // 结束时间,默认为当前时间
        request.setEndTime(endTime);

        // 执行查询
        List<WazuhHostStateModel> result = dataCollectionService.queryHostStateEventsWithTimeRange(request);
        List<CollectionHostStateBO> hostStateBOs = new ArrayList<>();
        for (WazuhHostStateModel srcHostState : result) {
            CollectionHostStateBO collectionHostStateBO = getCollectionHostStateBO(srcHostState);
            hostStateBOs.add(collectionHostStateBO);
        }

        return hostStateBOs;
    }

    /**
     * getCollectionHostStateBO
     *
     * @param srcHostState
     * @return
     */
    private CollectionHostStateBO getCollectionHostStateBO(WazuhHostStateModel srcHostState) {
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

    public List<HostState> getElementHostStateTrendV2(HostStateTrendRequest trendRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(trendRequest);
        List<String> sceneIds = CollectionUtil.toList(trendRequest.getSceneId());
        List<String> nodeIds = CollectionUtil.toList(trendRequest.getElementIds().get(0));


        String sceneId = sceneIds.get(0);
        sceneId = this.csceneInstanceService.getSceneId(sceneId);
        String nodeId = nodeIds.get(0);
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(Arrays.asList(sceneId), null);
        CollectionScenePluginHandler pluginHandler = batchSceneHandler.getCollectionScenePluginHandler(sceneId);
        if (pluginHandler == null || pluginHandler.getElement(nodeId) == null || Boolean.FALSE.equals(pluginHandler.getElement(nodeId)
                                                                                                              .getEnableProbeCollect())) {
            // 没有采集插件 使用以前逻辑返回即可
            return this.getElementHostStateTrend(trendRequest);
        } else {
            // 有采集插件 直接从es里取最近的10条数据即可
            String agentName = batchSceneHandler.getNodeByNodeId(nodeId).getEid();
            // 查询的时间范围是 场景运行时最后的时间 - 24h
            var endTime = batchSceneHandler.getLatestSceneRunningTime();
            var startTime = endTime.minusHours(24);
            var indexes = DataCollectionService.buildRequestIndex(startTime,endTime);
            SearchRequest searchRequest = new SearchRequest(indexes);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery("agent.name",agentName))
                    .must(QueryBuilders.termQuery("data.sic_launch_id","eda72998ba654c8d8625f61eaa918eb3"));

            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.size(10);
            sourceBuilder.sort("timestamp", SortOrder.DESC);
            searchRequest.source(sourceBuilder);
            searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));

            SearchResponse response = null ;

            try {
                 response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new MpRuntimeException(BaseErrorCode.ERROR, ExceptionUtils.getStackTrace(e));
            }

            List<HostState> hostStates = new ArrayList<>();
            for(SearchHit hit : response.getHits().getHits()){
                WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>> launchWazuhMonitorRoot = JSONObject.parseObject(hit.getSourceAsString(), new TypeReference<>() {
                });
                var hostStateOutput =launchWazuhMonitorRoot.getData().getOutput();
                HostState hostState = new HostState();
                hostState.setCpuPercent(new BigDecimal(hostStateOutput.getCPUPercent()).setScale(2).doubleValue());
                BigDecimal diskTotal = new BigDecimal(hostStateOutput.getDiskTotal());
                BigDecimal diskUsed = new BigDecimal(hostStateOutput.getDiskUsed());
                hostState.setDiskPercent(diskUsed.divide(diskTotal, 2, RoundingMode.DOWN).doubleValue());
                hostState.setMemoryPercent(new BigDecimal(hostStateOutput.getMemoryUsedPercent()).setScale(2)
                                                 .doubleValue());
                hostState.setTime(TimeUtils.dateToLocalDateTime(launchWazuhMonitorRoot.getTimestamp()));
                hostStates.add(hostState);
            }
            Collections.reverse(hostStates);
            return hostStates;
        }
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
        List<String> agentNames = new ArrayList<>();
        for (String name : getAgentNames()) {
            if (Objects.nonNull(name) && name.contains("controller")) {
                agentNames.add(name);
            }
        }
        return processHostStateTrendByAgentNames(agentNames, trendRequest.getStartTime(), trendRequest.getEndTime(), trendRequest.getSegmentCount());
    }

    /**
     * processHostStateTrendByAgentNames
     *
     * @param agentNames
     * @param startTime
     * @param endTime
     * @param segmentCount
     * @return
     */
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
     * processHostStateTrend
     *
     * @param sceneIds
     * @param elementIds
     * @param startTime
     * @param endTime
     * @param segmentCount
     * @return
     */
    private List<HostState> processHostStateTrend(List<String> sceneIds, List<String> elementIds, LocalDateTime startTime, LocalDateTime endTime, Integer segmentCount) {
        // 默认值
        LocalDateTime sceneStartTime = null;
        if (Objects.isNull(endTime)) {
            endTime = LocalDateTime.now();
            if (CollectionUtils.isNotEmpty(sceneIds)) {
                ServiceResult<SceneStateInfo> result = csceneSpecialService.getSceneStateInfo(sceneIds.get(0));
                if (!result.getSource().isRunning() && result.getSource().getSceneModel() != null) {
                    endTime = result.getSource().getSceneModel().getUpdateTime();
                }
                if (result.getSource().getSceneModel() != null) {
                    sceneStartTime = result.getSource().getSceneModel().getCreateTime();
                }
            }
        }

        if (Objects.isNull(startTime)) {
            // todo 默认时间间隔24小时
            startTime = endTime.minusHours(hoursRange);
            if (sceneStartTime != null && sceneStartTime.isAfter(startTime)) {
                startTime = sceneStartTime;
            }
        }

        if (Objects.isNull(segmentCount)) {
            segmentCount = 10;
        }

        List<String> sceneIdList = csceneCommonService.translateSceneIds(sceneIds);
        Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, segmentCount);
        List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);

        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> cpuResult = scenePluginCollectionSceneService.timeIntervalLatestHostCpuPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> memResult = scenePluginCollectionSceneService.timeIntervalLatestHostMemoryPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());
        ServiceResult<List<IntervalSizeBO<LocalDateTime, Double>>> diskResult = scenePluginCollectionSceneService.timeIntervalLatestHostDiskPercent(sceneIdList, elementIds, startTime, endTime, intervalSeconds.intValue());

        return listHostStates(timeList, cpuResult, memResult, diskResult);
    }


    /**
     * 查询单个节点机器当前状态
     *
     * @param pageRequest
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

    /**
     * listHostStatesByAgentName
     *
     * @param request
     * @return
     */
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

    /**
     * mapHostStatesByAgentName
     *
     * @param request
     * @return
     */
    public Map<String, List<HostState>> mapHostStatesByAgentName(AgentNameBasedRequest request) {
        request.ensure();
        if (CollectionUtil.isEmpty(request.getAgentNames())) {
            throw new MpRuntimeException(CsceneErrorCode.PARAMS_SHOULD_NOT_EMPTY.format(MessageCommon.get(CsceneBasei18nCode.AGENT_NAME)));
        }
        HostStateQueryParams queryParams = new HostStateQueryParams();
        queryParams.setPage(request.getPage());
        queryParams.setPageSize(request.getPageSize());
        Map<String, List<CollectionHostStateBO>> map = csceneInstanceService.mapHostStatesByAgentNames(request.getAgentNames(), queryParams);
        Map<String, List<HostState>> convertedMap = new HashMap<>();
        for (Map.Entry<String, List<CollectionHostStateBO>> entry : map.entrySet()) {
            String k = entry.getKey();
            List<CollectionHostStateBO> v = entry.getValue();
            List<HostState> hostStates = collectionHostStateBOs2HostStates(v);
            convertedMap.put(k, hostStates);
        }

        return convertedMap;
    }


    /**
     * collectionHostStateBOs2HostStates
     *
     * @param hostStateBOS
     * @return
     */
    private List<HostState> collectionHostStateBOs2HostStates(List<CollectionHostStateBO> hostStateBOS) {
        List<HostState> hostStates = new ArrayList<>();
        for (CollectionHostStateBO hostStateBO : hostStateBOS) {
            HostState hostState = getHostState(hostStateBO);
            hostStates.add(hostState);
        }

        return hostStates;
    }

    /**
     * getHostState
     *
     * @param hostStateBO
     * @return
     */
    private static HostState getHostState(CollectionHostStateBO hostStateBO) {
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
        hs.setMaxDownloadSpeed(hostStateBO.getMaxDownloadSpeed());
        hs.setMaxUploadSpeed(hostStateBO.getMaxUploadSpeed());
        hs.setMaxNetSpeed(hostStateBO.getMaxNetSpeed());
        hs.setDiskReadBytes(hostStateBO.getDiskReadBytes());
        hs.setDiskWriteBytes(hostStateBO.getDiskWriteBytes());
        hs.setDiskIoPercent(hostStateBO.getDiskIoPercent());
        return hs;
    }


    /**
     * listHostStates
     *
     * @param timeList
     * @param cpuResult
     * @param memResult
     * @param diskResult
     * @return
     */
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
        for (Map.Entry<LocalDateTime, HostState> entry : hostStateMap.entrySet()) {
            hostStates.add(entry.getValue());
        }
        hostStates.sort(Comparator.comparing(HostState::getTime));

        return hostStates;
    }

    /**
     * pagePorts
     *
     * @param listPageInfoQueryParams
     * @return
     */
    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));

        if (listPageInfoQueryParams.getStartTime() != null || listPageInfoQueryParams.getEndTime() != null) {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
            LocalDateTime startTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getStartTime())) {
                startTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getStartTime());
            } else {
                startTime = batchSceneHandler.getEarliestStartTime();
            }
            LocalDateTime endTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getEndTime())) {
                endTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getEndTime());
            } else {
                endTime = LocalDateTime.now();
            }
            List<CollectionPluginSceneDataModel> dataModels = dataRecordService.getData(listPageInfoQueryParams, CollectionPluginSceneDataModel.Type.PORT, startTime, endTime);
            ArrayList<Object> ports = new ArrayList<>();
            for (CollectionPluginSceneDataModel d : dataModels) {
                Port parse = JsonCommon.parse(d.getData(), Port.class);
                ports.add(parse);
            }
            return new ListObjServiceResult(new ListObjResult<>(ports, ports.size()).asResult()).asResult();
        } else {
            return scenePluginCollectionSceneService.listPagePorts(listPageInfoQueryParams).asResult();
        }
    }

    /**
     * pageProcesses
     *
     * @param listPageInfoQueryParams
     * @return
     */
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
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
        for (Process p : processes) {
            if (Objects.nonNull(p.getSize())) {
                Long megabytes = DataSize.ofKilobytes(p.getSize()).toMegabytes();
                p.setSize(megabytes);
            }

            if (Objects.nonNull(p.getVmSize())) {
                Long megabytes = DataSize.ofKilobytes(p.getVmSize()).toMegabytes();
                p.setVmSize(megabytes);
            }
        }

        return listResult;
    }


    /**
     * pageSystemUserEvents
     *
     * @param listPageInfoQueryParams
     * @return
     */
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
     *
     * @param listPageInfoQueryParams
     * @return
     */
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        listPageInfoQueryParams.ensure();
        checkListPageParams(listPageInfoQueryParams);
        if (Objects.isNull(listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) || !listPageInfoQueryParams.getFetchIfSceneInstanceShutdown()) {
            if (!csceneCommonService.isCsceneInstanceRunning(listPageInfoQueryParams.getSceneIds().get(0))) {
                return new ListObjServiceResult(new ListObjResult()).asResult();
            }
        }

        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(listPageInfoQueryParams.getSceneIds()));
        if (listPageInfoQueryParams.getStartTime() != null || listPageInfoQueryParams.getEndTime() != null) {
            BatchSceneHandler batchSceneHandler = new BatchSceneHandler(listPageInfoQueryParams.getSceneIds(), null);
            LocalDateTime startTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getStartTime())) {
                startTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getStartTime());
            } else {
                startTime = batchSceneHandler.getEarliestStartTime();
            }
            LocalDateTime endTime = null;
            if (StringUtils.isNotEmpty(listPageInfoQueryParams.getEndTime())) {
                endTime = TimeUtils.stringToLocalDateTime(listPageInfoQueryParams.getEndTime());
            } else {
                endTime = LocalDateTime.now();
            }
            List<CollectionPluginSceneDataModel> dataModels = dataRecordService.getData(listPageInfoQueryParams, CollectionPluginSceneDataModel.Type.NETIFACE, startTime, endTime);
            ArrayList<Object> netifaces = new ArrayList<>();
            for (CollectionPluginSceneDataModel d : dataModels) {
                Netiface parse = JsonCommon.parse(d.getData(), Netiface.class);
                netifaces.add(parse);
            }
            return new ListObjServiceResult(new ListObjResult<>(netifaces, netifaces.size()).asResult()).asResult();
        } else {
            ListObjServiceResult<Netiface> netifacesResult = scenePluginCollectionSceneService.listNetifaces(listPageInfoQueryParams).asResult();
            return netifacesResult;
        }
    }

    /**
     * getSceneAttCkTypeCountTrend
     *
     * @param request
     * @param startTime
     * @param endTime
     * @return
     */
    public ServiceResult<List<IntervalSizeBO<LocalDateTime, Long>>> getSceneAttCkTypeCountTrend(BaseSceneElementRequest request,LocalDateTime startTime,LocalDateTime endTime) {
        List<IntervalSizeBO<LocalDateTime, Long>> result = new ArrayList<>();
        List<String> sceneIds = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ServiceResult<>(result);
        }

        ServiceResult<List<AttCkTypeCount>> listServiceResult = scenePluginCollectionSceneService.getSceneAttCkTypeCountTrend(sceneIds.get(0), startTime, endTime);
        List<AttCkTypeCount> list = listServiceResult.getSource();
        if (CollectionUtils.isNotEmpty(list)) {
            for (AttCkTypeCount count : list) {
                LocalDateTime time = TimeUtils.stringToLocalDateTime(count.getType(), TimeUtils.tzMillsDateTimeFormatter).atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
                IntervalSizeBO<LocalDateTime, Long> v = new IntervalSizeBO<>(time, time, count.getTimes());
                result.add(v);
            }
        }
        return new ServiceResult<>(result);
    }

    /**
     * getSceneAttCkTypeCountInfo
     *
     * @param request
     * @return
     */
    public ServiceResult<SceneAttCkTypeCountInfo> getSceneAttCkTypeCountInfo(BaseSceneElementRequest request) {
        List<String> sceneIds = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ServiceResult<>(new SceneAttCkTypeCountInfo()).asResult();
        }
        ServiceResult<List<AttCkTypeCount>> listServiceResult = scenePluginCollectionSceneService.getSceneAttCkTypeCountInfo(sceneIds.get(0));
        List<AttCkTypeCount> list = listServiceResult.getSource();
        SceneAttCkTypeCountInfo attCkTypeCountInfo = new SceneAttCkTypeCountInfo();
        attCkTypeCountInfo.setTotalTimes(0L);
        attCkTypeCountInfo.setList(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(list)) {
            for (AttCkTypeCount count : list) {
                attCkTypeCountInfo.setTotalTimes(attCkTypeCountInfo.getTotalTimes() + count.getTimes());
                AttCkTypeCount attCkTypeCount = new AttCkTypeCount();
                attCkTypeCount.setTimes(count.getTimes());
                attCkTypeCount.setType(count.getType().toLowerCase());
                attCkTypeCountInfo.getList().add(attCkTypeCount);
            }
        }
        return new ServiceResult<>(attCkTypeCountInfo);
    }

    /**
     * checkListPageParams
     *
     * @param params
     */
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

    /**
     * getAgentNames
     *
     * @return
     */
    private List<String> getAgentNames() {
        String url = "http://" + csceneAppConfig.getCpIp() + ":" + csceneAppConfig.getCpPort() + "/v1/hypervisors";
//        String result = HttpRequest.get(url).addHeaders(wrapperHeaders(new HashMap<>())).execute().body();
        String result = HttpRequest.sendGetRequest(url, wrapperHeaders(new HashMap<>()));
        if (StringUtils.isBlank(result)) {
            return new ArrayList<>();
        }
        Map<String, Object> resultMap = JsonCommon.parseMap(result);
        Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
        if (Objects.isNull(data) || !data.containsKey("list")) {
            return new ArrayList<>();
        }
        List<Map<String, String>> list = (List<Map<String, String>>) data.get("list");
        List<String> agentNames = new ArrayList<>();
        for (Map<String, String> map : list) {
            String hostname = map.getOrDefault("hostname", "");
            agentNames.add(hostname);
        }
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
