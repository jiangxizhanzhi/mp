package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.controller.request.AlarmRequest;
import cn.cyberpeace.mp.cscene.controller.request.EsAlarmInfo;
import cn.cyberpeace.mp.cscene.dto.scene.AlarmInfo;
import cn.cyberpeace.mp.cscene.service.CsceneCommonService;
import cn.cyberpeace.mp.sceneplugincollection.plugin.BatchSceneHandler;
import cn.cyberpeace.mp.situationaware.model.WarningRuleConfigModel;
import cn.cyberpeace.mp.situationaware.repository.WarningRuleConfigMapper;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Objects;
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
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<AlarmInfo>(), 0)).asResult();
        }
        BatchSceneHandler batchSceneHandler = new BatchSceneHandler(sceneIds, alarmRequest.getNodeIds());
        if (CollectionUtils.isEmpty(batchSceneHandler.getProbeCollectors())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        List<String> agentName = new ArrayList<>(batchSceneHandler.getAllEid2ElementMap().keySet());
        Integer alarmType = alarmRequest.getType();
        SearchRequest request = new SearchRequest();
        var indices = dataCollectionService.buildRequestIndex(batchSceneHandler.getEarliestStartTime(), LocalDateTime.now());
        request.indices(indices);

        var mergeQuery = QueryBuilders.boolQuery();
        // 文件
        if (alarmType == null || alarmType == 1 || alarmType == 4 || alarmType == 5) {
            var agentQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("agent.name", agentName))
                    .must(QueryBuilders.termQuery("rule.groups", "syscheck_file"));
            mergeQuery.should(agentQuery);
        }
        // 进程
        if (alarmType == null || alarmType == 2) {
            var agentQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("agent.name", agentName))
                    .must(QueryBuilders.termQuery("data.sic_launch_id", "38949dcf3829489d8dd9e0e63cc66541"));
            mergeQuery.should(agentQuery);
        }
        // 注册表
        if (alarmType == null || alarmType == 3) {
            var agentQuery = QueryBuilders.boolQuery()
                    .must(QueryBuilders.termsQuery("agent.name", agentName))
                    .must(QueryBuilders.termQuery("rule.groups", "syscheck_registry"));
            mergeQuery.should(agentQuery);
        }

        var searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(mergeQuery);

        // 分页
        searchBuilder.from(0);
        searchBuilder.size(1500);
        // 排序
        searchBuilder.sort("@timestamp", SortOrder.DESC);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchBuilder);
        List<EsAlarmInfo> esAlarmInfoResult = new ArrayList<>();
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
                    EsAlarmInfo.RegistryAlarmInfo registryAlarmInfo = new EsAlarmInfo.RegistryAlarmInfo();
                    registryAlarmInfo.setAgentName(data.getAgent().getName());
                    registryAlarmInfo.setPath(data.getSyscheck().getPath());
                    registryAlarmInfo.setTime(map.get("@timestamp").toString());
                    registryAlarmInfo.setMd5(data.getSyscheck().getMd5After());
                    registryAlarmInfo.setAction(data.getSyscheck().getEvent());
                    esAlarmInfoResult.add(registryAlarmInfo);
                } else if (!groupIds.isEmpty() && groupIds.contains("syscheck_file")) {
                    // 文件
                    EsAlarmInfo.FileEsAlarmInfo fileEsAlarmInfo = new EsAlarmInfo.FileEsAlarmInfo();
                    fileEsAlarmInfo.setFileSize(data.getSyscheck().getSizeAfter());
                    fileEsAlarmInfo.setPath(data.getSyscheck().getPath());
                    fileEsAlarmInfo.setAgentName(data.getAgent().getName());
                    fileEsAlarmInfo.setAction(data.getSyscheck().getEvent());
                    fileEsAlarmInfo.setTime(map.get("@timestamp").toString());
                    fileEsAlarmInfo.setMd5(data.getSyscheck().getMd5After());
                    esAlarmInfoResult.add(fileEsAlarmInfo);
                } else if (data.getData() != null && data.getData().getSicLaunchOutput() != null) {
                    // 进程
                    var ss = data.getData().getSicLaunchOutput().getData();
                    if (StringUtils.isNotEmpty(ss)) {
                        byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(ss, "\""));
                        String output = TextUtils.decompress(raw);
                        JSONObject jsonObject = JSONObject.parseObject(output);
                        {
                            JSONArray arr = jsonObject.getJSONArray("added");
                            if (!Objects.isNull(arr)) {
                                for (int i = 0; i < arr.size(); i++) {

                                    var process = arr.getJSONObject(i);

                                    var command = process.getString("command");
                                    if (StringUtils.isNotEmpty(command) && command.contains("ossec")) {
                                        continue;
                                    }
                                    EsAlarmInfo.ProcessAlarmInfo processAlarmInfo = new EsAlarmInfo.ProcessAlarmInfo();
                                    processAlarmInfo.setAgentName(data.getAgent().getName());
                                    processAlarmInfo.setTime(data.getData().getSicLaunchOutput().getSicLaunchTime());

                                    processAlarmInfo.setCommand(process.getString("command"));
                                    processAlarmInfo.setName(process.getString("name"));
                                    processAlarmInfo.setTime(process.getString("create_time"));
                                    processAlarmInfo.setAction("added");

                                    esAlarmInfoResult.add(processAlarmInfo);
                                }
                            }
                        }

                        {
                            JSONArray arr = jsonObject.getJSONArray("deleted_prev");
                            if (!Objects.isNull(arr)) {
                                for (int i = 0; i < arr.size(); i++) {

                                    var process = arr.getJSONObject(i);

                                    var command = process.getString("command");
                                    if (StringUtils.isNotEmpty(command) && command.contains("ossec")) {
                                        continue;
                                    }

                                    EsAlarmInfo.ProcessAlarmInfo processAlarmInfo = new EsAlarmInfo.ProcessAlarmInfo();
                                    processAlarmInfo.setAgentName(data.getAgent().getName());
                                    processAlarmInfo.setTime(data.getData().getSicLaunchOutput().getSicLaunchTime());

                                    processAlarmInfo.setCommand(process.getString("command"));
                                    processAlarmInfo.setName(process.getString("name"));
                                    processAlarmInfo.setTime(process.getString("create_time"));
                                    processAlarmInfo.setAction("deleted");

                                    esAlarmInfoResult.add(processAlarmInfo);
                                }
                            }
                        }

                        {
                            JSONArray arr = jsonObject.getJSONArray("modified");
                            if (!Objects.isNull(arr)) {
                                for (int i = 0; i < arr.size(); i++) {

                                    var process = arr.getJSONObject(i);

                                    var command = process.getString("command");
                                    if (StringUtils.isNotEmpty(command) && command.contains("ossec")) {
                                        continue;
                                    }

                                    EsAlarmInfo.ProcessAlarmInfo processAlarmInfo = new EsAlarmInfo.ProcessAlarmInfo();
                                    processAlarmInfo.setAgentName(data.getAgent().getName());
                                    processAlarmInfo.setTime(data.getData().getSicLaunchOutput().getSicLaunchTime());

                                    processAlarmInfo.setCommand(process.getString("command"));
                                    processAlarmInfo.setName(process.getString("name"));
                                    processAlarmInfo.setTime(process.getString("create_time"));
                                    processAlarmInfo.setAction("modified");

                                    esAlarmInfoResult.add(processAlarmInfo);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 进程
//        if (alarmType == null || alarmType == 1) {
//            WazuhMonitorEventsRequest request1 = new WazuhMonitorEventsRequest();
//            request1.setStartTime(batchSceneHandler.getEarliestStartTime());
//            request1.setEndTime(LocalDateTime.now());
//            request1.setAgentNames(new ArrayList<>(batchSceneHandler.getAllEid2ElementMap().keySet()));
//            var lll = dataCollectionService.queryProcessesWithTimeRangeAA(request1);
//            if (CollectionUtils.isNotEmpty(lll)) {
//                for (var llll : lll) {
//                    EsAlarmInfo.ProcessAlarmInfo processAlarmInfo = new EsAlarmInfo.ProcessAlarmInfo();
//                }
//            }
//        }

        var rules = this.getRules(alarmType);
        List<AlarmInfo> alarmInfos = new ArrayList<>();
        for (var esData : esAlarmInfoResult) {
            String nodeName = batchSceneHandler.getNodeByEid(esData.getAgentName()).getName();
            if (esData instanceof EsAlarmInfo.FileEsAlarmInfo) {
                var dd = (EsAlarmInfo.FileEsAlarmInfo) esData;
                for (var r : rules.getFileTypeRules()) {
                    if (r.hit(dd.getPath(), dd.getFileSize(), dd.getAction())) {
                        AlarmInfo alarmInfo = new AlarmInfo();
                        alarmInfo.setType(r.getType());
                        alarmInfo.setNodeName(nodeName);
                        try {
                            String utcTime = esData.getTime();
                            utcTime = StringUtils.substringBeforeLast(utcTime, ".").concat("Z");
                            LocalDateTime time = TimeUtils.utcStringToLocalDateTime(utcTime);
                            alarmInfo.setTime(time);
                        } catch (Exception e) {

                        }
                        alarmInfo.setLevel(r.getLevel());
                        alarmInfo.setTarget(dd.getPath());
                        if ("added".equals(dd.getAction())) {
                            alarmInfo.setAction("新增");
                        } else if ("deleted".equals(dd.getAction())) {
                            alarmInfo.setAction("删除");
                        } else {
                            alarmInfo.setAction("修改");
                        }
                        if (StringUtils.isNotEmpty(dd.getMd5())) {
                            alarmInfo.setContent("内容:md5 " + dd.getMd5());
                        } else {
                            alarmInfo.setContent("-");
                        }
                        alarmInfo.setName(r.getName());
                        alarmInfos.add(alarmInfo);
                    }
                }
            } else if (esData instanceof EsAlarmInfo.RegistryAlarmInfo) {
                var dd = (EsAlarmInfo.RegistryAlarmInfo) esData;
                for (var r : rules.getRegistryRules()) {
                    if (r.hit(dd.getPath(), dd.getAction())) {
                        AlarmInfo alarmInfo = new AlarmInfo();
                        alarmInfo.setType(r.getType());
                        alarmInfo.setNodeName(nodeName);
                        try {
                            String utcTime = esData.getTime();
                            utcTime = StringUtils.substringBeforeLast(utcTime, ".").concat("Z");
                            LocalDateTime time = TimeUtils.utcStringToLocalDateTime(utcTime);
                            alarmInfo.setTime(time);
                        } catch (Exception e) {

                        }
                        alarmInfo.setLevel(r.getLevel());
                        alarmInfo.setTarget(dd.getPath());
                        if ("added".equals(dd.getAction())) {
                            alarmInfo.setAction("新增");
                        } else if ("deleted".equals(dd.getAction())) {
                            alarmInfo.setAction("删除");
                        } else {
                            alarmInfo.setAction("修改");
                        }
                        alarmInfo.setName(r.getName());
                        if (StringUtils.isNotEmpty(dd.getMd5())) {
                            alarmInfo.setContent("内容:md5 " + dd.getMd5());
                        } else {
                            alarmInfo.setContent("-");
                        }
                        alarmInfos.add(alarmInfo);
                    }
                }
            } else if (esData instanceof EsAlarmInfo.ProcessAlarmInfo) {
                var dd = (EsAlarmInfo.ProcessAlarmInfo) esData;
                for (var r : rules.getProcessRules()) {
                    if (r.hit(dd.getName(), dd.getCommand(), null, dd.getAction())) {
                        AlarmInfo alarmInfo = new AlarmInfo();
                        alarmInfo.setType(r.getType());
                        alarmInfo.setNodeName(nodeName);
                        alarmInfo.setName(dd.getName());
                        alarmInfo.setTarget(dd.getName());
                        if ("added".equals(dd.getAction())) {
                            alarmInfo.setAction("新增");
                        } else if ("deleted".equals(dd.getAction())) {
                            alarmInfo.setAction("删除");
                        } else {
                            alarmInfo.setAction("修改");
                        }
                        alarmInfo.setContent(dd.getCommand());
                        try {
                            String utcTime = esData.getTime();
                            utcTime = StringUtils.substringBeforeLast(utcTime, ".").concat("Z");
                            LocalDateTime time = TimeUtils.utcStringToLocalDateTime(utcTime);
                            alarmInfo.setTime(time);
                        } catch (Exception e) {

                        }
                        alarmInfo.setLevel(r.getLevel());
                        alarmInfos.add(alarmInfo);
                    }
                }
            }
        }

        if (StringUtils.isNotEmpty(alarmRequest.getKey())) {
            List<AlarmInfo> result = new ArrayList<>();
            for (var a : alarmInfos) {
                if (a.getContent() != null && a.getContent().contains(alarmRequest.getKey())) {
                    result.add(a);
                } else if (a.getTarget() != null && a.getTarget().contains(alarmRequest.getKey())) {
                    result.add(a);
                }
            }
            alarmInfos = result;
        }

        int from = (alarmRequest.getPage() - 1) * alarmRequest.getPageSize();
        int to = from + alarmRequest.getPageSize();
        if (from > alarmInfos.size()) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<AlarmInfo>(), alarmInfos.size())).asResult();
        } else {
            var sss = alarmInfos.subList(from, Math.min(to, alarmInfos.size()));
            return new ListObjServiceResult<>(new ListObjResult<>(sss, alarmInfos.size())).asResult();
        }
    }

    private AlarmRules getRules(Integer type) {

        var cond = new HumanCondition();
        if (type != null) {
            cond.filterEqual(WarningRuleConfigModel::getWarningTarget, type);
        }
        var list = warningRuleConfigMapper.list(cond);
        AlarmRules rules = new AlarmRules();
        if (CollectionUtils.isNotEmpty(list)) {
            for (var m : list) {
                if (m.getWarningTarget() == 1 || m.getWarningTarget() == 4 || m.getWarningTarget() == 5) {
                    FileTypeRule rule = null;
                    if (m.getWarningTarget() == 1) {
                        rule = new GuanJianXiTong();
                    } else if (m.getWarningTarget() == 4) {
                        rule = new XiTongRiZhi();
                    } else {
                        rule = new GuanJianRiZhi();
                    }
                    rule.setName(m.getWarningName());
                    if (2 == m.getWarningClass()) {
                        rule.setLevel("高危");
                    } else if (1 == m.getWarningClass()) {
                        rule.setLevel("中危");
                    } else {
                        rule.setLevel("低危");
                    }
                    if (StringUtils.isNotEmpty(m.getWarningMovements())) {
                        var a = m.getWarningMovements().split(",");
                        rule.setActions(new HashSet<>());
                        FileTypeRule finalG = rule;
                        Arrays.stream(a).forEach(aa -> {
                            if ("1".equals(aa)) {
                                finalG.getActions().add("added");
                            } else if ("2".equals(aa)) {
                                finalG.getActions().add("modified");
                            } else {
                                finalG.getActions().add("deleted");
                            }
                        });
                    } else {
                        rule.setActions(new HashSet<>() {{
                            this.add("added");
                            this.add("deleted");
                            this.add("modified");
                        }});
                    }
                    if (StringUtils.isNotEmpty(m.getFilePaths())) {
                        var a = m.getFilePaths().split(",");
                        rule.setPaths(new HashSet<>());
                        FileTypeRule finalG1 = rule;
                        Arrays.stream(a).forEach(aa -> finalG1.getPaths().add(aa));
                    }
                    if (StringUtils.isNotEmpty(m.getFileSuffixes())) {
                        var a = m.getFileSuffixes().split(",");
                        rule.setSuffixes(new HashSet<>());
                        FileTypeRule finalG2 = rule;
                        Arrays.stream(a).forEach(aa -> finalG2.getSuffixes().add(aa));
                    }
                    if (StringUtils.isNotEmpty(m.getFileSizeMin())) {
                        rule.setMinSize(Integer.parseInt(m.getFileSizeMin()));
                    }
                    rules.getFileTypeRules().add(rule);
                } else if (m.getWarningTarget() == 2) {
                    var rule = new RegistryRule();
                    rule.setName(m.getWarningName());
                    if (2 == m.getWarningClass()) {
                        rule.setLevel("高危");
                    } else if (1 == m.getWarningClass()) {
                        rule.setLevel("中危");
                    } else {
                        rule.setLevel("低危");
                    }
                    if (StringUtils.isNotEmpty(m.getWarningMovements())) {
                        var a = m.getWarningMovements().split(",");
                        rule.setActions(new HashSet<>());
                        RegistryRule finalG = rule;
                        Arrays.stream(a).forEach(aa -> {
                            if ("1".equals(aa)) {
                                finalG.getActions().add("added");
                            } else if ("2".equals(aa)) {
                                finalG.getActions().add("modified");
                            } else {
                                finalG.getActions().add("deleted");
                            }
                        });
                    } else {
                        rule.setActions(new HashSet<>() {{
                            this.add("added");
                            this.add("deleted");
                            this.add("modified");
                        }});
                    }
                    if (StringUtils.isNotEmpty(m.getRegisterPaths())) {
                        var a = m.getRegisterPaths().split(",");
                        rule.setPaths(new HashSet<>());
                        Arrays.stream(a).forEach(aa -> rule.getPaths().add(aa));
                    }
                    rules.getRegistryRules().add(rule);
                } else if (m.getWarningTarget() == 3) {
                    var rule = new ProcessRule();
                    rule.setName(m.getWarningName());
                    if (2 == m.getWarningClass()) {
                        rule.setLevel("高危");
                    } else if (1 == m.getWarningClass()) {
                        rule.setLevel("中危");
                    } else {
                        rule.setLevel("低危");
                    }

                    if (StringUtils.isNotEmpty(m.getWarningMovements())) {
                        var a = m.getWarningMovements().split(",");
                        rule.setActions(new HashSet<>());
                        ProcessRule finalG = rule;
                        Arrays.stream(a).forEach(aa -> {
                            if ("1".equals(aa)) {
                                finalG.getActions().add("added");
                            } else if ("2".equals(aa)) {
                                finalG.getActions().add("modified");
                            } else {
                                finalG.getActions().add("deleted");
                            }
                        });
                    } else {
                        rule.setActions(new HashSet<>() {{
                            this.add("added");
                            this.add("deleted");
                            this.add("modified");
                        }});
                    }

                    if (StringUtils.isNotEmpty(m.getProcessNames())) {
                        var a = m.getProcessNames().split(",");
                        rule.setProcessNames(new HashSet<>());
                        Arrays.stream(a).forEach(aa -> rule.getProcessNames().add(aa));
                    }
                    if (StringUtils.isNotEmpty(m.getProcessDetailFeatures())) {
                        var a = m.getProcessDetailFeatures().split(",");
                        rule.setCommands(new HashSet<>());
                        Arrays.stream(a).forEach(aa -> rule.getCommands().add(aa));
                    }
                    rules.getProcessRules().add(rule);
                }
            }
        }
        return rules;
    }

    @Data
    public static class AlarmRules {
        private List<FileTypeRule> fileTypeRules = new ArrayList<>();

        private List<RegistryRule> registryRules = new ArrayList<>();

        private List<ProcessRule> processRules = new ArrayList<>();
    }

    @Data
    public static class ProcessRule {
        private String name;
        private String type;
        private String level;
        private Set<String> processNames;
        private Set<String> commands;
        private Set<String> status;
        private Set<String> actions;

        public ProcessRule() {
            this.setType("系统进程");
        }

        private boolean hit(String name, String command, String status, String action) {
            boolean hit = false;
            if (actions.contains(action)) {
                if (!hit && CollectionUtils.isNotEmpty(processNames)) {
                    hit = this.processNames.contains(name);
                }
                if (!hit && CollectionUtils.isNotEmpty(this.status)) {
                    hit = this.status.contains(status);
                }
                if (!hit && (CollectionUtils.isNotEmpty(commands) && StringUtils.isNotEmpty(command))) {
                    for (var c : this.commands) {
                        if (command.contains(c)) {
                            hit = true;
                            break;
                        }
                    }
                }
            }
            return hit;
        }
    }

    @Data
    public static class RegistryRule {
        private String name;
        private String type;
        private String level;
        private Set<String> actions;
        private Set<String> paths;

        public RegistryRule() {
            this.setType("系统配置");
        }

        private boolean hit(String path, String action) {
            boolean hit = false;
            if (actions.contains(action)) {
                if (!hit && CollectionUtils.isNotEmpty(paths)) {
                    hit = this.paths.contains(path);
                }
            }
            return hit;
        }
    }

    @Data
    public static class FileTypeRule {
        private String name;
        private String type;
        private String level;
        private Set<String> actions;
        private Set<String> paths;
        private Set<String> suffixes;
        private Integer minSize;

        private boolean hit(String path, Integer size, String action) {
            boolean hit = false;
            if (actions.contains(action)) {
                if (!hit && CollectionUtils.isNotEmpty(paths)) {
                    hit = this.paths.contains(path);
                }
                if (!hit && CollectionUtils.isNotEmpty(suffixes)) {
                    if (!path.contains(".")) {
                        hit = false;
                    } else {
                        String suffix = path.substring(path.lastIndexOf("."));
                        hit = this.suffixes.contains(suffix);
                    }
                }
                if (!hit && this.minSize != null) {
                    hit = size > this.minSize;
                }
            }
            return hit;
        }
    }

    public static class GuanJianXiTong extends FileTypeRule {
        public GuanJianXiTong() {
            super();
            this.setType("关键系统文件");
        }
    }

    public static class XiTongRiZhi extends FileTypeRule {
        public XiTongRiZhi() {
            super();
            this.setType("系统日志");
        }
    }

    public static class GuanJianRiZhi extends FileTypeRule {
        public GuanJianRiZhi() {
            super();
            this.setType("关键日志");
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
        private SicOutputData sicLaunchOutput;
    }

    @Data
    public static class SicOutputData {
        private String valuesCount;
        private String data;
        private String sicLaunchTime;
    }

    @Data
    private static class Syscheck {
        private String path;
        private String event;
        private String md5After;
        private Integer sizeAfter;
    }
}
