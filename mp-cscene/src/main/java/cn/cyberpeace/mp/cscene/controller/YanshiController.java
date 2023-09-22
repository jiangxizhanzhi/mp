package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.AlarmRequest;
import cn.cyberpeace.mp.cscene.controller.request.EsAlarmInfo;
import cn.cyberpeace.mp.cscene.dto.scene.AlarmInfo;
import cn.cyberpeace.mp.cscene.service.CsceneCommonService;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.situationaware.repository.WarningRuleConfigMapper;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Api(value = "告警数据查询接口", tags = "告警数据查询接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/alarm")
public class YanshiController {

    @Resource(name = "wazuhRestHighLevelClient")
    private RestHighLevelClient restHighLevelClient;

    @Resource
    private CsceneCommonService csceneCommonService;

    @Resource
    private DataCollectionService dataCollectionService;

    @Resource
    private WarningRuleConfigMapper warningRuleConfigMapper;

    @ApiOperation(value = "告警列表")
    @GetMapping("/page")
    public ListObjServiceResult<AlarmInfo> pageFlow(AlarmRequest alarmRequest) {
        var sceneIds = csceneCommonService.translateSceneIds(Arrays.asList(alarmRequest.getId()));
        if (CollectionUtils.isEmpty(sceneIds)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, alarmRequest.getNodeIds());
        if (CollectionUtils.isEmpty(batchSceneHandler.getProbeCollectors())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        List<String> agentName = new ArrayList<>(batchSceneHandler.getAllEid2ElementMap().keySet());
        Integer alarmType = alarmRequest.getType();
        if (alarmType != null && (alarmType > 4 || alarmType < 0)) {
            alarmType = null;
        }
        SearchRequest request = new SearchRequest();
        var indices = dataCollectionService.buildRequestIndex(batchSceneHandler.getEarliestStartTime(), LocalDateTime.now());
        request.indices(indices);

        var mergeQuery = QueryBuilders.boolQuery();
        // 文件
        if (alarmType == null || alarmType == 0 || alarmType == 3 || alarmType == 4) {
            var agentQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("agent.name", agentName))
                    .must(QueryBuilders.termQuery("rule.groups", "syscheck_file"));
            mergeQuery.should(agentQuery);
        }
        // 进程
        if (alarmType == null || alarmType == 1) {
            var agentQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("agent.name", agentName))
                    .must(QueryBuilders.termQuery("data.sic_launch_id", "38949dcf3829489d8dd9e0e63cc66541"));
            mergeQuery.should(agentQuery);
        }
        // 注册表
        if (alarmType == null || alarmType == 2) {
            var agentQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("agent.name", agentName))
                    .must(QueryBuilders.termQuery("rule.groups", "syscheck_registry"));
            mergeQuery.should(agentQuery);
        }

        var searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(mergeQuery);

        // 分页
        searchBuilder.from(0);
        searchBuilder.size(3000);
        // 排序
        searchBuilder.sort("@timestamp", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchBuilder);
        List<EsAlarmInfo> result = new ArrayList<>();
        try {
            var response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (var hit : response.getHits().getHits()) {
                var map = hit.getSourceAsMap();
                var data = JsonCommon.snakeMapToBean(map, EsData.class);
                Set<String> groupIds = new HashSet<>();
                if (data.getRule() != null && CollectionUtils.isNotEmpty(data.getRule().getGroups())) {
                    groupIds.addAll(data.getRule().getGroups());
                }
                if (!groupIds.isEmpty() && groupIds.contains("syscheck_registry")) {
                    // 注册表

                } else if (!groupIds.isEmpty() && groupIds.contains("syscheck_file")) {
                    // 文件
                    EsAlarmInfo.FileEsAlarmInfo fileEsAlarmInfo = new EsAlarmInfo.FileEsAlarmInfo();
                    fileEsAlarmInfo.setFileSize(data.getSyscheck().getSizeAfter());
                    fileEsAlarmInfo.setPath(data.getSyscheck().getPath());
                    fileEsAlarmInfo.setAgentName(data.getAgent().getName());
                    fileEsAlarmInfo.setAction(data.getSyscheck().getEvent());
                    result.add(fileEsAlarmInfo);
                } else {
                    // 进程
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
    }

    private void te() {
        var list = warningRuleConfigMapper.list(new HumanCondition());
        if (CollectionUtils.isNotEmpty(list)) {

        }
    }

    @Data
    public static class AlarmRules {
        private List<GuanJianXiTongFile> guanJianXiTongFiles;
    }

    @Data
    public static class GuanJianXiTongFile {
        private String name;
        private Integer level;
        private String action;
        private Set<String> paths;
        private Set<String> suffixes;
        private Integer size;

        private boolean hit(String path, Integer size) {
            boolean hit = false;
            if (!hit && CollectionUtils.isNotEmpty(paths)) {
                hit = path.contains(path);
            }
            if (!hit && CollectionUtils.isNotEmpty(suffixes)) {
                if (path.contains(".")) {
                    hit = false;
                } else {
                    String suffix = path.substring(path.lastIndexOf("."), path.length());
                }
            }
            return hit;
        }
    }

    @Data
    public static class EsData {
        private Agent agent = new Agent();
        private Rule rule = new Rule();
        private SicData data = new SicData();
        private Syscheck syscheck = new Syscheck();
    }

    @Data
    public static class Agent {
        private String name;
    }

    @Data
    public static class Rule {
        private List<String> groups;
        private String description;
        private Integer level;
    }

    @Data
    public static class SicData {
    }

    @Data
    private static class Syscheck {
        private String path;
        private String event;
        private String md5After;
        private Integer sizeAfter;
    }
}
