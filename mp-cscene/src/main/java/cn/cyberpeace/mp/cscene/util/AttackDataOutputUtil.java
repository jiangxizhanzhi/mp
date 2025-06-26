package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.bo.AttackDataType;
import cn.cyberpeace.mp.base.bo.ImageToolRequest;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AttackDataOutputUtil {

    @Resource(name = "wazuhRestHighLevelClient")
    protected RestHighLevelClient restHighLevelClient;

    public void startOneAttackDataOutputTask(CollectionScenePluginHandler collectionScenePluginHandler, SceneElementBO source, SceneElementBO target, ImageToolRequest finalTool) {
        // 启动一个线程 查询攻击结果 每隔5秒查询一次es数据 ， 查最新的一条数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 30; i++) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
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
                    // 这里可以改成向外部推送数据
                    System.out.println(dataWrapper.getHostInfo().getCPUPercent());
                }
            }

        }).start();
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
        private HostStateOutput hostInfo;
        private List<OutProcess> processInfo;
        private List<FlowLogBO> flows;
        private List<OutConnect> connects;
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
