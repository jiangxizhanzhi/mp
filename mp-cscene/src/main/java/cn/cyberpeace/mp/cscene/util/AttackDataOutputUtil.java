package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.bo.AttackDataType;
import cn.cyberpeace.mp.base.bo.AttackType;
import cn.cyberpeace.mp.base.bo.ImageToolRequest;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.thread.ThreadUtils;
import cn.cyberpeace.mp.cscene.dto.common.AttackCommandExecuteInfoToThirdDto;
import cn.cyberpeace.mp.cscene.model.AttackCommandHistoryModel;
import cn.cyberpeace.mp.cscene.repository.AttackCommandHistoryMapper;
import cn.cyberpeace.mp.cscene.ws.WebSocketClientService;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.service.collect.base.WazuhResult;
import cn.cyberpeace.mp.situationaware.bo.FlowLogBO;
import cn.cyberpeace.mp.situationaware.model.WazuhFileRoot;
import cn.cyberpeace.mp.situationaware.model.sic.HostStateOutput;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AttackDataOutputUtil {

    private static final int INTERVAL_SECONDS = 5;

    @Resource(name = "wazuhRestHighLevelClient")
    protected RestHighLevelClient restHighLevelClient;

    @Autowired
    private WebSocketClientService webSocketClientService;

    public void startOneAttackDataOutputTask(CollectionScenePluginHandler collectionScenePluginHandler, SceneElementBO source, SceneElementBO target,
                                             ImageToolRequest finalTool, Long attackSconds, String taskId, String targetId, String cmdHistoryId, AttackType attackType) {
        // 启动一个线程 查询攻击结果 每隔5秒查询一次es数据 ， 查最新的一条数据

        new Thread(ThreadUtils.format(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < attackSconds / 5; i++) {
                    try {
                        Thread.sleep(INTERVAL_SECONDS*1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    DataWrapper dataWrapper = new DataWrapper();
                    dataWrapper.setTaskId(taskId);
                    dataWrapper.setSendTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
                    dataWrapper.setToolId(finalTool.getId());
                    dataWrapper.setTargetId(targetId);
                    String agentName = target.getEid();
                    for (var dataType : finalTool.getAttackDataTypes()) {
                        if (dataType == AttackDataType.HOSTINFO.getType()) {
                            // 主机信息
                            var hostInfo = getHostInfo(agentName);
                            dataWrapper.setHostInfo(hostInfo);
                        } else if (dataType == AttackDataType.PROCESS.getType()) {
                            // 进程信息
                            var processInfo = getProcessInfo(agentName);
                            dataWrapper.setProcessInfo(processInfo);
                        } else if (dataType == AttackDataType.FLOW.getType()) {
                            // 流量信息
                            String sourceIp = source.getPorts().get(0).getIp();
                            String targetIp = target.getPorts().get(0).getIp();
                            var flows = getFlow(collectionScenePluginHandler.getScene()
                                    .getFlowCollector(), sourceIp, targetIp);
                            dataWrapper.setFlows(flows);
                        } else if (dataType == AttackDataType.NETIFACE.getType()) {
                            // 网卡信息
                            var connects = getConnect(agentName);
                            dataWrapper.setConnects(connects);
                        }
                    }
                    AttackCommandHistoryMapper attackCommandHistoryMapper = BeanCommon.getBean(AttackCommandHistoryMapper.class);
                    AttackCommandHistoryModel attackCommandHistoryModel = attackCommandHistoryMapper.get(cmdHistoryId);
                    if (Objects.isNull(attackCommandHistoryModel)) {
                        log.error("cmdHistoryId:{} 获取不到命令历史详情",cmdHistoryId);
                        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("获取不到命令历史详情"));
                    }
                    dataWrapper.setCommandExecuteDetail(new AttackCommandExecuteInfoToThirdDto(attackType, attackCommandHistoryModel));
                    // 向320推送数据
                    JSONObject jsonObject = new JSONObject();
                    JSONObject dataJsonObject = new JSONObject();
                    dataJsonObject.put("data", dataWrapper);
                    jsonObject.put("Data", dataJsonObject.toJSONString());
                    jsonObject.put("Function", "AttackResultData");
                    jsonObject.put("ReceiveObjectName", "");
                    jsonObject.put("SendObjectName", "");
//        发送构建数据
                    String jsonString = jsonObject.toJSONString();
                    webSocketClientService.sendMessage(jsonString);
                    log.info("sendMessage320,taskId:{},toolId:{}, length {}, jsonString {}",taskId,finalTool.getId(),
                            jsonString.length(), jsonString);
                }
            }

        })).start();
    }

    /**
     * 查询3分钟内主机采集信息
     * @param target
     * @return
     */
    public  List<HostStateOutput> queryRecentHostInfo(SceneElementBO target, String toolId, String taskId, String targetId) {
        String agentName = target.getEid();
        BoolQueryBuilder finalQueryBuilder = QueryBuilders.boolQuery();
        // 指定节点
        finalQueryBuilder.must(QueryBuilders.termQuery("agent.name", agentName));
        // 指定端口数据的判断条件
        BoolQueryBuilder necessaryQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery("data.sic_launch_id", "eda72998ba654c8d8625f61eaa918eb3"));
        finalQueryBuilder.must(necessaryQuery);
        WazuhResult<WazuhFileRoot> result = queryWazuhData(0, 36, List.of(finalQueryBuilder));
        List<HostStateOutput> hostStateOutputList;
        if (CollectionUtils.isNotEmpty(result.getList())) {
            hostStateOutputList = result.getList().stream().map(xx -> {
                String content = JsonCommon.stringify(xx.getData().getSicLaunchOutput());
                HostStateOutput hostStateOutput = JSON.parseObject(content, HostStateOutput.class);
                hostStateOutput.setTimestamp(xx.getTimestamp());
                return hostStateOutput;
            }).collect(Collectors.toList());
        } else {
            log.error("未查询到主机信息");
            hostStateOutputList = new ArrayList<>();
        }

        // 向320推送数据
        JSONObject jsonObject = new JSONObject();
        JSONObject dataJsonObject = new JSONObject();
        JSONObject recentHostInfoList = new JSONObject();
        recentHostInfoList.put("recentHostInfoList", hostStateOutputList);
        recentHostInfoList.put("taskId",taskId);
        recentHostInfoList.put("toolId",toolId);
        recentHostInfoList.put("targetId",targetId);
        dataJsonObject.put("data", recentHostInfoList);
        jsonObject.put("Data", dataJsonObject.toJSONString());
        jsonObject.put("Function", "UniteRecentHostInfoData");
        jsonObject.put("ReceiveObjectName", "");
        jsonObject.put("SendObjectName", "");
//        发送构建数据
        String jsonString = jsonObject.toJSONString();
        webSocketClientService.sendMessage(jsonString);
        return hostStateOutputList;
    }

    public DataWrapper queryOneAttackData(CollectionScenePluginHandler collectionScenePluginHandler, SceneElementBO source, SceneElementBO target, ImageToolRequest finalTool) {
        DataWrapper dataWrapper = new DataWrapper();
        String agentName = target.getEid();
        for (var dataType : finalTool.getAttackDataTypes()) {
            if (dataType == AttackDataType.HOSTINFO.getType()) {
                // 主机信息
                var hostInfo = getHostInfo(agentName);
                dataWrapper.setHostInfo(hostInfo);
            } else if (dataType == AttackDataType.PROCESS.getType()) {
                // 进程信息
                var processInfo = getProcessInfo(agentName);
                dataWrapper.setProcessInfo(processInfo);
            } else if (dataType == AttackDataType.FLOW.getType()) {
                // 流量信息
                String sourceIp = source.getPorts().get(0).getIp();
                String targetIp = target.getPorts().get(0).getIp();
                var flows = getFlow(collectionScenePluginHandler.getScene()
                        .getFlowCollector(), sourceIp, targetIp);
                dataWrapper.setFlows(flows);
            } else if (dataType == AttackDataType.NETIFACE.getType()) {
                // 网卡信息
                var connects = getConnect(agentName);
                dataWrapper.setConnects(connects);
            }
        }
        return dataWrapper;
    }

    public HostStateOutput getHostInfo(String agentName) {
        BoolQueryBuilder finalQueryBuilder = QueryBuilders.boolQuery();
        // 指定节点
        finalQueryBuilder.must(QueryBuilders.termQuery("agent.name", agentName));
        // 指定端口数据的判断条件
        BoolQueryBuilder necessaryQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery("data.sic_launch_id", "eda72998ba654c8d8625f61eaa918eb3"));
        finalQueryBuilder.must(necessaryQuery);
        WazuhResult<WazuhFileRoot> result = queryWazuhData(0, 1, List.of(finalQueryBuilder));
        if (CollectionUtils.isNotEmpty(result.getList())) {
            var xx = result.getList().get(0);
            String content = JsonCommon.stringify(xx.getData().getSicLaunchOutput());
            HostStateOutput hostStateOutput = JSON.parseObject(content, HostStateOutput.class);
            hostStateOutput.setTimestamp(xx.getTimestamp());
            return hostStateOutput;
        } else {
            log.error("未查询到主机信息");
            return null;
        }
    }

    public List<OutConnect> getConnect(String agentName) {
        BoolQueryBuilder finalQueryBuilder = QueryBuilders.boolQuery();
        // 指定节点
        finalQueryBuilder.must(QueryBuilders.termQuery("agent.name", agentName));
        // 指定端口数据的判断条件
        BoolQueryBuilder necessaryQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery("data.sic_launch_id", "09ffcc4a67ef4accb407a1555629d0f8"));
        finalQueryBuilder.must(necessaryQuery);
        WazuhResult<WazuhFileRoot> result = queryWazuhData(0, 1, List.of(finalQueryBuilder));
        if (CollectionUtils.isNotEmpty(result.getList())) {
            var output = result.getList().get(0).getData().getSicLaunchOutput();
            String processInfoStr = output.get("data").toString();
            var decodeBytes = TextUtils.rawDecodeBase64(StringUtils.strip(processInfoStr, "\""));
            String decodeStr = TextUtils.decompress(decodeBytes);
            JSONObject decodeObject = JSONObject.parseObject(decodeStr);
            var valueStr = JsonCommon.stringify(decodeObject.get("values"));
            var conns = JsonCommon.snakeParse(valueStr, new TypeReference<List<OutConnect>>() {
            });
            return conns;
        } else {
            log.error("未查询到主机信息");
            return null;
        }
    }

    public List<OutProcess> getProcessInfo(String agentName) {
        BoolQueryBuilder finalQueryBuilder = QueryBuilders.boolQuery();
        // 指定节点
        finalQueryBuilder.must(QueryBuilders.termQuery("agent.name", agentName));
        // 指定端口数据的判断条件
        BoolQueryBuilder necessaryQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery("data.sic_launch_id", "38949dcf3829489d8dd9e0e63cc66541"));
        finalQueryBuilder.must(necessaryQuery);
        WazuhResult<WazuhFileRoot> result = queryWazuhData(0, 1, List.of(finalQueryBuilder));
        if (CollectionUtils.isNotEmpty(result.getList())) {
            var output = result.getList().get(0).getData().getSicLaunchOutput();
            String processInfoStr = output.get("data").toString();
            var decodeBytes = TextUtils.rawDecodeBase64(StringUtils.strip(processInfoStr, "\""));
            String decodeStr = TextUtils.decompress(decodeBytes);
            JSONObject decodeObject = JSONObject.parseObject(decodeStr);
            var valueStr = JsonCommon.stringify(decodeObject.get("values"));
            var processModels = JsonCommon.parse(valueStr, new TypeReference<List<OutProcess>>() {
            });
            return processModels;
        } else {
            log.error("未查询到主机信息");
            return null;
        }
    }

    public List<FlowLogBO> getFlow(String indices, String sourceIp, String targetIp) {
        BoolQueryBuilder finalQueryBuilder = QueryBuilders.boolQuery();
        finalQueryBuilder.must(QueryBuilders.termQuery("destination.ip", targetIp));
        finalQueryBuilder.must(QueryBuilders.existsQuery("suricata"));
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("@timestamp");
//        TODO 时间范围需要修改 查不到数据
//        rangeQueryBuilder.gte(LocalDateTime.now().minusSeconds(INTERVAL_SECONDS).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
//        rangeQueryBuilder.lte(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        finalQueryBuilder.must(rangeQueryBuilder);
        String index = indices + "_sessions3-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        var flows = this.queryArkimeData(index, 0, 10, List.of(finalQueryBuilder));
        return flows;
    }

    public List<FlowLogBO> queryArkimeData(String arkimeSession, int from, int size, List<BoolQueryBuilder> boolQueryBuilders) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder finalBuilder = QueryBuilders.boolQuery();
        for (BoolQueryBuilder queryBuilder : boolQueryBuilders) {
            finalBuilder.should(queryBuilder);
        }

        sourceBuilder.query(finalBuilder);
        sourceBuilder.from(from);
        sourceBuilder.size(size);

        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        sortBuilders.add(SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC));
        sourceBuilder.sort(sortBuilders);

        SearchRequest searchRequest = new SearchRequest(arkimeSession);
        searchRequest.source(sourceBuilder);
        log.info("query es data params: {}", searchRequest.source().toString());
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        try {

            RefreshRequest refreshRequest = new RefreshRequest();
            refreshRequest.indices(arkimeSession);
            refreshRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
            restHighLevelClient.indices().refresh(refreshRequest, RequestOptions.DEFAULT);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] searchHits = hits.getHits();
            var flowLogBOList = Arrays.stream(searchHits).map(hit -> {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                FlowLogBO flowLogBO = JSONObject.parseObject(hit.getSourceAsString(), FlowLogBO.class);
                flowLogBO.setTimestamp(Long.valueOf(sourceAsMap.get("@timestamp").toString()));
                if (flowLogBO.getSource() != null) {
                    flowLogBO.getSource().setMac(null);
                }
                if (flowLogBO.getDestination() != null) {
                    flowLogBO.getDestination().setMac(null);
                }
                return flowLogBO;
            }).collect(Collectors.toList());
            return flowLogBOList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public WazuhResult<WazuhFileRoot> queryWazuhData(int from, int size, List<BoolQueryBuilder> boolQueryBuilders) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder finalBuilder = QueryBuilders.boolQuery();
        for (BoolQueryBuilder queryBuilder : boolQueryBuilders) {
            finalBuilder.should(queryBuilder);
        }

        sourceBuilder.query(finalBuilder);
        sourceBuilder.size(size);
        sourceBuilder.from(from);

        List<SortBuilder<?>> sortBuilders = new ArrayList<>();
        sortBuilders.add(SortBuilders.fieldSort("timestamp").order(SortOrder.DESC));
        sourceBuilder.sort(sortBuilders);

        SearchRequest searchRequest = new SearchRequest("wazuh-alerts-4.x-*");
        searchRequest.source(sourceBuilder);
        searchRequest.indicesOptions(IndicesOptions.fromOptions(true, true, true, false));
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<WazuhFileRoot> wazuhFileRootList = new ArrayList<>();
            for (SearchHit hit : response.getHits().getHits()) {
                wazuhFileRootList.add(JSONObject.parseObject(hit.getSourceAsString(), WazuhFileRoot.class));
            }

            WazuhResult wazuhResult = new WazuhResult();
            wazuhResult.setList(wazuhFileRootList);
            wazuhResult.setTotal(response.getHits().getTotalHits().value);
            return wazuhResult;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @Setter
    public static class OutProcess {
        private String status;
        private String createTime;
        private String cwd;
        private String priority;
        private String command;
        private String username;
        private String pid;
        private String name;
    }

    @Getter
    @Setter
    public static class DataWrapper {
        private String taskId;
        private String toolId;
        private String targetId;
        // 方便观察 消息是否发送到320中，320观测软件无法展示全部的json数据；
        private String sendTime;
        private HostStateOutput hostInfo;
        private List<OutProcess> processInfo;
        private List<FlowLogBO> flows;
        private List<OutConnect> connects;
        private AttackCommandExecuteInfoToThirdDto commandExecuteDetail;
    }

    @Getter
    @Setter
    public static class OutConnect {
        private Integer laddrPort;
        private String proto;
        private Integer pid;
        private String status;
        private String laddrIp;
        private String raddrIp;
        private Integer raddrPort;
    }
}
