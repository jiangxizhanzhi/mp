package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.cscene.dto.scene.FlowEventData;
import cn.cyberpeace.mp.cscene.dto.scene.TaiShiEventInfo;
import cn.cyberpeace.mp.cscene.dto.scene.WazuhEventData;
import cn.cyberpeace.mp.cscene.request.common.TaiShiRequest;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.sceneplugincollection.plugin.CollectionScenePluginHandler;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaiShiGuidesService {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private DataCollectionService dataCollectionService;

    @Resource
    private CsceneCommonService csceneCommonService;

    public ListObjServiceResult<TaiShiEventInfo> getAttCkEventList(TaiShiRequest request) {
        var sceneIds = csceneCommonService.translateSceneIds(Arrays.asList(request.getSceneId()));
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ListObjServiceResult<>();
        }
        BatchSceneHandler sceneHandler = new BatchSceneHandler(sceneIds, new ArrayList<>());
        LocalDateTime start = request.getStartTime() == null ? sceneHandler.getEarliestStartTime() : request.getStartTime();
        LocalDateTime end = request.getEndTime() == null ? start.plusSeconds(10) : request.getEndTime();

        CollectionScenePluginHandler handler = sceneHandler.getCollectionScenePluginHandler(sceneIds.get(0));
        if (handler == null) {
            return new ListObjServiceResult<>();
        }
        // 取探针数据索引
        var indices = dataCollectionService.indexListBuilder(start, end);
        // 取流量数据索引
        String flowIndex = handler.getScene().getFlowCollector();
        if (StringUtils.isNotEmpty(flowIndex)) {
            flowIndex = flowIndex + "_session*";
            indices.add(flowIndex);
        }
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indices.toArray(new String[]{}));

        // 探针数据查询条件
        List<String> targetNodeEids = this.getEidsByNodeIds(sceneHandler, request.getTargetNodeIds());
        var agentQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("agent.name", targetNodeEids))
                .must(QueryBuilders.existsQuery("rule.mitre.tactic"));
        if (CollectionUtils.isNotEmpty(request.getDefenseTypes())) {
            agentQuery.must(QueryBuilders.termsQuery("rule.mitre.tactic", request.getDefenseTypes()));
        }

        // 流量查询条件
        var flowQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery("suricata"));
        // 流量数据既有源也有目的 可以使用ip来查下
        List<String> sourceIps = this.getIpsByNodeIds(sceneHandler, request.getSourceNodeIds());
        List<String> targetIps = this.getIpsByNodeIds(sceneHandler, request.getTargetNodeIds());
        flowQuery.must(QueryBuilders.termsQuery("source.ip", sourceIps));
        flowQuery.must(QueryBuilders.termsQuery("destination.ip", targetIps));
        if (CollectionUtils.isNotEmpty(request.getDefenseTypes())) {
            List<String> attackTypes = new ArrayList<>();
            for (var defenseType : request.getDefenseTypes()) {
                attackTypes.addAll(FlowAttackTypeConvert.getAttackTypes(defenseType));
            }
            flowQuery.must(QueryBuilders.termsQuery("suricata.category", attackTypes));
        }

        // 用或合并查询条件
        var searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(QueryBuilders.boolQuery()
                .should(agentQuery)
                .should(flowQuery)
        );
        // 分页
        searchBuilder.from((request.getPage() - 1) * request.getPageSize());
        searchBuilder.size(request.getPageSize());
        // 时间范围
        // 排序
        searchBuilder.sort("@timestamp", SortOrder.DESC);

        searchRequest.source(searchBuilder);
        List<TaiShiEventInfo> result = new ArrayList<>();
        int total = 0;
        try {
            var response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            total = (int) response.getHits().getTotalHits().value;
            var mac2Elements = sceneHandler.getAllMac2ElementMapForScene();
            var eid2Elements = sceneHandler.getAllEid2ElementMap();
            for (var hit : response.getHits().getHits()) {
                if (hit.getSourceAsMap().containsKey("agent")) {
                    var eventData = JsonCommon.snakeMapToBean(hit.getSourceAsMap(), WazuhEventData.class);
                    var taishi = this.getEventInfoFrom(eventData, eid2Elements);
                    result.add(taishi);
                } else {
                    var eventData = JsonCommon.mapToBean(hit.getSourceAsMap(), FlowEventData.class);
                    var taishi = this.getEventInfoFrom(eventData, mac2Elements);
                    result.add(taishi);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, total));
    }

    private TaiShiEventInfo getEventInfoFrom(WazuhEventData eventData, Map<String, SceneElementBO> eid2Elements) {
        TaiShiEventInfo taiShi = new TaiShiEventInfo();
        taiShi.setActionType("attack");
        taiShi.setTriggerType("host");
        taiShi.setAttCks(eventData.getRule().getMitre().getTactic());
        taiShi.setAttCkNames(this.getAttCkTypeNames(taiShi.getAttCks()));
        if (eventData.getAgent() != null && eid2Elements.containsKey(eventData.getAgent().getName())) {
            var element = eid2Elements.get(eventData.getAgent().getName());
            taiShi.setTargetNodeName(element.getName());
            taiShi.setTargetNodeIp(eventData.getAgent().getIp());
            taiShi.setTargetNodeId(element.getId());
        }
        return taiShi;
    }

    private TaiShiEventInfo getEventInfoFrom(FlowEventData eventData, Map<String, SceneElementBO> mac2Elements) {
        TaiShiEventInfo taiShi = new TaiShiEventInfo();
        taiShi.setActionType("attack");
        taiShi.setTriggerType("flow");
        if (eventData.getSource() != null) {
            var element = this.getElementBy(eventData.getSource().getMac(), mac2Elements);
            if (element != null) {
                taiShi.setSourceNodeId(element.getId());
                taiShi.setSourceNodeIp(eventData.getSource().getIp());
                taiShi.setSourceNodeName(element.getName());
                taiShi.setSourceNodePort(eventData.getSource().getPort());
            }
        }
        if (eventData.getDestination() != null) {
            var element = this.getElementBy(eventData.getDestination().getMac(), mac2Elements);
            if (element != null) {
                taiShi.setTargetNodeId(element.getId());
                taiShi.setTargetNodeIp(eventData.getDestination().getIp());
                taiShi.setTargetNodeName(element.getName());
                taiShi.setTargetNodePort(eventData.getDestination().getPort());
            }
        }
        taiShi.setAttCks(this.getAttCksByFlowAttackTypes(eventData.getSuricata().getCategory()));
        taiShi.setAttCkNames(this.getAttCkTypeNames(taiShi.getAttCks()));
        return taiShi;
    }

    private SceneElementBO getElementBy(List<String> onePortMac, Map<String, SceneElementBO> mac2Elements) {
        if (CollectionUtils.isEmpty(onePortMac)) {
            return null;
        }
        for (String mac : onePortMac) {
            if (mac2Elements.containsKey(mac)) {
                return mac2Elements.get(mac);
            }
        }
        return null;
    }

    private List<String> getAttCkTypeNames(List<String> attCks) {
        if (CollectionUtils.isEmpty(attCks)) {
            return new ArrayList<>();
        }
        return attCks.stream()
                .map(a -> this.getAttCkTypeName(a))
                .collect(Collectors.toList());
    }

    private String getAttCkTypeName(String attCk) {
        String key = attCk.replaceAll(" ", "_");
        return MessageCommon.get("cscene.attck." + key.toLowerCase());
    }

    public List<String> getAttCksByFlowAttackTypes(List<String> flowAttackTypes) {
        List<String> result = new ArrayList<>();
        for (var a : flowAttackTypes) {
            var attCk = this.getAttCkByFlowAttackType(a);
            if (attCk != null) {
                result.add(attCk);
            }
        }
        return result;
    }

    private String getAttCkByFlowAttackType(String flowAttackType) {
        return FlowAttackTypeConvert.attackType2AttCks.get(flowAttackType);
    }

    private List<String> getIpsByNodeIds(BatchSceneHandler sceneHandler, List<String> nodeIds) {
        if (CollectionUtils.isEmpty(nodeIds)) {
            return new ArrayList<>(sceneHandler.getAllIps());
        } else {
            List<String> result = new ArrayList<>();
            if (CollectionUtils.isEmpty(nodeIds)) {
                result.addAll(sceneHandler.getAllIps());
            } else {
                for (var nodeId : nodeIds) {
                    var ips = sceneHandler.getIpsByNodeId(nodeId);
                    if (CollectionUtils.isNotEmpty(ips)) {
                        result.addAll(ips);
                    }
                }
            }
            return result;
        }
    }

    private List<String> getEidsByNodeIds(BatchSceneHandler sceneHandler, List<String> nodeIds) {
        List<String> targetNodeEids = new ArrayList<>();
        // 探针数据 只有目标节点 没有源节点
        if (CollectionUtils.isEmpty(nodeIds)) {
            // 参数未指定 则查询全部节点
            targetNodeEids.addAll(sceneHandler.getAllEid2ElementMap().keySet());
        } else {
            // 参数指定 则转换一下eid
            for (var nodeId : nodeIds) {
                var node = sceneHandler.getNodeByNodeId(nodeId);
                if (node != null) {
                    targetNodeEids.add(node.getEid());
                }
            }
        }
        return targetNodeEids;
    }
}
