package cn.cyberpeace.mp.collection.core.client.mongo;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.config.CollectionCacheConfig;
import cn.cyberpeace.mp.collection.config.EsDateFormatConfig;
import cn.cyberpeace.mp.collection.constant.SicLaunchConfig;
import cn.cyberpeace.mp.collection.core.common.ConvertModelUtil;
import cn.cyberpeace.mp.collection.core.model.*;
import cn.cyberpeace.mp.collection.core.model.sic.BaseChangeCountOutput;
import cn.cyberpeace.mp.collection.core.model.sic.BaseInput;
import cn.cyberpeace.mp.collection.core.model.sic.HostStateOutput;
import cn.cyberpeace.mp.collection.core.model.sic.Launch;
import cn.cyberpeace.mp.collection.core.request.WazuhEventRequest;
import cn.cyberpeace.mp.collection.core.request.WazuhHostMonitorPageRequest;
import cn.cyberpeace.mp.collection.core.response.MongoCommonResponse;
import cn.cyberpeace.mp.collection.core.response.WazuhSystemUserResponse;
import cn.cyberpeace.mp.collection.utils.AgentNameUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.collection.core.constant.CacheKeyConstant.*;

@CacheConfig(cacheManager = CollectionCacheConfig.CACHE_MANAGER)
public class MongoClientControl {
    private MongoClient mongoClient;

    private MongoClientControl(String mongoServer) {
        this.mongoClient = MongoClients.create(mongoServer);
    }

    public static class MongoClientControlBuilder {
        private String mongoServer;

        public void configMongo(String mongoServer) {
            this.mongoServer = mongoServer;
        }

        public MongoClientControl build() {
            return new MongoClientControl(mongoServer);
        }
    }

    public static MongoClientControlBuilder builder() {
        return new MongoClientControlBuilder();
    }

    private static final String WAZUH_ALERT_MONGO_DB = "wazuh";

    private static final String WAZUH_ALERT_MONGO_COLLECTION = "wazuh-alerts-4.x";

    private static final String MONGO_DB_SOURCE = "_source";

    private static final String SIC_LAUNCH_ID = MONGO_DB_SOURCE + ".data.sic_launch_id";

    private static final String AGGREGATION_NAME = "count";

    /**
     * 查询采集事件分页
     * @param request
     * @return ListObjResult<FileEventModel>
     */
    @Cacheable(value = CACHE_KEY_FILE_EVENTS)
    public ListObjResult<FileEvent> queryFileEvents(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        var findIterable = buildFindIterable(documents, bson, request);
        List<WazuhFileRoot> wazuhFileRootList = new ArrayList<>();
        for (var document : findIterable) {
            if (Objects.isNull(document) || !document.containsKey(MONGO_DB_SOURCE)) {
                continue;
            }
            var wazuhFileRoot = JSON.parseObject(JsonCommon.stringify(document.get(MONGO_DB_SOURCE)), WazuhFileRoot.class);
            wazuhFileRootList.add(wazuhFileRoot);
        }
        List<FileEvent> fileEventModelList = wazuhFileRootList.stream().map(ConvertModelUtil::getFileEvent).collect(Collectors.toList());

        long total = documents.countDocuments(bson);
        return new ListObjResult<>(fileEventModelList, BigDecimal.valueOf(total).intValue());
    }

    /**
     * 查询自定义探针采集数据分页
     * @param request
     * @return ListObjResult<WazuhProbeCommonModel>
     */
    @Cacheable(value = CACHE_KEY_CUSTOM_PROBES)
    public ListObjResult<WazuhProbeCommon> queryCustomProbeData(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        var findIterable = buildFindIterable(documents, bson, request);
        List<WazuhProbeCommon> wazuhModelList = new ArrayList<>();
        for (var document : findIterable) {
            if (Objects.isNull(document) || !document.containsKey(MONGO_DB_SOURCE)) {
                continue;
            }
            WazuhMonitorRoot<Launch<Object, BaseInput>> wazuhMonitorRoot = JSON.parseObject(JsonCommon.stringify(document.get(MONGO_DB_SOURCE)),
                    new TypeReference<>() {
                    });
            if (Objects.nonNull(wazuhMonitorRoot.getData()) && Objects.nonNull(wazuhMonitorRoot.getData().getOutput())) {
                String output = JsonCommon.stringify(wazuhMonitorRoot.getData().getOutput());
                WazuhProbeCommon wazuhProbeCommon = new WazuhProbeCommon();
                wazuhProbeCommon.setRawOutPut(output);
                wazuhProbeCommon.setAgentId(wazuhMonitorRoot.getAgent().getId());
                wazuhProbeCommon.setAgentName(wazuhMonitorRoot.getAgent().getName());
                wazuhProbeCommon.setTime(TimeUtils.dateToLocalDateTime(wazuhMonitorRoot.getTimestamp()));
                wazuhProbeCommon.setProbeId(wazuhMonitorRoot.getData().getId());
                wazuhModelList.add(wazuhProbeCommon);
            }
        }
        long total = documents.countDocuments(bson);
        return new ListObjResult(wazuhModelList, BigDecimal.valueOf(total).intValue());
    }

    /**
     * 查询漏洞信息分页
     * @param request
     * @return ListObjResult<WazuhVulnerabilityInfoModel>
     */
    @Cacheable(value = CACHE_KEY_VUL_INFOS)
    public ListObjResult<WazuhVulnerabilityInfo> queryVulnerabilityInfo(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(Collections.emptyList(), 0);
        }
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        var findIterable = buildFindIterable(documents, bson, request);
        List<WazuhVulnerabilityInfo> wazuhModelList = new ArrayList<>();
        for (var document : findIterable) {
            if (Objects.isNull(document) || !document.containsKey(MONGO_DB_SOURCE)) {
                continue;
            }
            WazuhMonitorRoot<WazuhVulnerabilityInfo> wazuhMonitorRoot = JSON.parseObject(JsonCommon.stringify(document.get(MONGO_DB_SOURCE)),
                    new TypeReference<>() {
                    });
            if (Objects.nonNull(wazuhMonitorRoot.getData())) {
                var vul = wazuhMonitorRoot.getData().getVulnerability();
                if (Objects.nonNull(vul)) {
                    WazuhVulnerabilityInfo vulnerability = new WazuhVulnerabilityInfo();
                    vulnerability.setVulnerability(vul);
                    vulnerability.setAgentId(wazuhMonitorRoot.getAgent().getId());
                    vulnerability.setAgentName(wazuhMonitorRoot.getAgent().getName());
                    vulnerability.setTime(TimeUtils.dateToLocalDateTime(wazuhMonitorRoot.getTimestamp()));
                    wazuhModelList.add(vulnerability);
                }
            }
        }
        long total = documents.countDocuments(bson);
        return new ListObjResult<>(wazuhModelList, BigDecimal.valueOf(total).intValue());
    }

    /**
     * 查询漏洞信息数量
     * @param request
     * @param fieldPath 字段所在路径
     * @param fieldName 字段名称
     * @return List<WazuhVulnerabilityInfoCountModel>
     */
    @Cacheable(value = CACHE_KEY_VUL_COUNTS)
    public List<WazuhVulnerabilityInfoCount> queryVulnerabilityCountInfo(WazuhEventRequest request,
                                                                         String fieldPath, String fieldName) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ArrayList<>();
        }
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        // 聚合查询
        var aggregateIterable = buildAggregateIterable(documents, bson, fieldPath, fieldName, request.getTopSize());
        List<WazuhVulnerabilityInfoCount> vulnerabilityInfoCounts = new ArrayList<>();
        for (var document : aggregateIterable) {
            if (Objects.isNull(document) || !document.containsKey(fieldName)) {
                continue;
            }
            WazuhVulnerabilityInfoCount infoCount = new WazuhVulnerabilityInfoCount();
            infoCount.setFieldName(document.get("_id").toString());
            infoCount.setCount((Integer) document.get(fieldName));
            vulnerabilityInfoCounts.add(infoCount);
        }
        return vulnerabilityInfoCounts;
    }

    /**
     * 查询主机状态信息
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_HOST_STATE)
    public ListObjResult<WazuhHostState> queryHostStateEventsWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return new ListObjResult<>(new ArrayList<>(), 0);
        }
        var response = querySICLaunchEventsWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_HOST_STATE);
        List<WazuhHostState> hostStates = new ArrayList<>();
        for (var document : response.getFindIterable()) {
            if (Objects.isNull(document) || !document.containsKey(MONGO_DB_SOURCE)) {
                continue;
            }
            WazuhMonitorRoot<Launch<HostStateOutput, BaseInput>> wazuhMonitorRoot = JSON.parseObject(JsonCommon.stringify(document.get(MONGO_DB_SOURCE)), new TypeReference<>() {
            });
            if (Objects.nonNull(wazuhMonitorRoot.getData()) && Objects.nonNull(wazuhMonitorRoot.getData().getOutput())) {
                HostStateOutput output = wazuhMonitorRoot.getData().getOutput();
                WazuhHostState wazuhHostState = ConvertModelUtil.convertHostStateOutput2WazuhHost(output);
                wazuhHostState.setAgentId(wazuhMonitorRoot.getAgent().getId());
                wazuhHostState.setAgentName(wazuhMonitorRoot.getAgent().getName());
                wazuhHostState.setAgentIp(wazuhMonitorRoot.getAgent().getIp());
                wazuhHostState.setTime(TimeUtils.dateToLocalDateTime(wazuhMonitorRoot.getTimestamp()));
                hostStates.add(wazuhHostState);
            }
        }
        return new ListObjResult<>(hostStates, BigDecimal.valueOf(response.getTotal()).intValue());
    }

    /**
     * 查询主机网络连接信息
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_NET_CONN)
    public List<WazuhNetworkConnection> queryNetConnEventsWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return Collections.emptyList();
        }
        // 每个agent取1条数据，做假分页处理
        request.setPageNum(1);
        request.setPageSize(request.getAgentNames().size());
        var response = querySICLaunchEventsWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response.getFindIterable());
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Collections.emptyList();
        }
        List<WazuhNetworkConnection> list = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray arr = jsonObject.getJSONArray("values");
            if (Objects.isNull(arr)) {
                continue;
            }
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data = arr.getJSONObject(i);
                WazuhNetworkConnection netConnModel = JSON.toJavaObject(data, WazuhNetworkConnection.class);
                netConnModel.setTime((LocalDateTime) jsonObject.get("time"));
                netConnModel.setDescription("CMD LAUNCH LOG");
                list.add(netConnModel);
            }
        }
        return list;
    }

    /**
     * 查询主机进程信息
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_PROCESS)
    public List<WazuhProcess> queryProcessesWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return Collections.emptyList();
        }
        // 每个agent取1条数据，做假分页处理
        request.setPageNum(1);
        request.setPageSize(request.getAgentNames().size());
        var response = querySICLaunchEventsWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_PROCESSES);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response.getFindIterable());
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return Collections.emptyList();
        }
        List<WazuhProcess> list = new ArrayList<>();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray arr = jsonObject.getJSONArray("values");
            if (Objects.isNull(arr)) {
                continue;
            }
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data = arr.getJSONObject(i);
                WazuhProcess processModel = JSON.toJavaObject(data, WazuhProcess.class);
                processModel.setTime((LocalDateTime) jsonObject.get("time"));
                processModel.setAgentIp((String) jsonObject.get("agent_ip"));
                processModel.setAgentName((String) jsonObject.get("agent_name"));
                processModel.setDescription("PROCESS");
                list.add(processModel);
            }
        }
        return list;
    }

    /**
     * 查询主机系统用户信息
     * @param
     * @return
     */
    @Cacheable(value = CACHE_KEY_USERS)
    public WazuhSystemUserResponse querySystemUserWithTimeRange(WazuhHostMonitorPageRequest wazuhMonitorEventsRequest) {
        if (AgentNameUtils.isAgentNamesEmpty(wazuhMonitorEventsRequest.getAgentNames())) {
            return null;
        }
        // 取agent个数条数据，做假分页处理
        wazuhMonitorEventsRequest.setPageNum(1);
        wazuhMonitorEventsRequest.setPageSize(wazuhMonitorEventsRequest.getAgentNames().size());
        var response = querySICLaunchEventsWithTimeRange(wazuhMonitorEventsRequest, SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS);
        List<JSONObject> jsonObjects = processBaseChangeOutput(response.getFindIterable());
        if (CollectionUtils.isEmpty(jsonObjects)) {
            return null;
        }
        WazuhSystemUserResponse systemUserResponse = new WazuhSystemUserResponse();
        for (JSONObject jsonObject : jsonObjects) {
            JSONArray values = jsonObject.getJSONArray("values");
            JSONArray modified = jsonObject.getJSONArray("modified");
            JSONArray deleted = jsonObject.getJSONArray("deleted_prev");
            JSONArray added = jsonObject.getJSONArray("added");
            systemUserResponse.setValues(ConvertModelUtil.processJSONArray(values, jsonObject));
            systemUserResponse.setModified(ConvertModelUtil.processJSONArray(modified, jsonObject));
            systemUserResponse.setDeletedPrev(ConvertModelUtil.processJSONArray(deleted, jsonObject));
            systemUserResponse.setAdded(ConvertModelUtil.processJSONArray(added, jsonObject));
        }
        return systemUserResponse;
    }

    /**
     * 一段时间内的环境变量数变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_ENV_VAR_CHANGED_COUNT)
    public int sumEnvVarChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_ENV_VAR);
    }

    /**
     * 一段时间内的用户数量变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_SYSTEM_USERS_CHANGED_COUNT)
    public int sumSysUsersChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_SYSTEM_USERS);
    }

    /**
     * 一段时间内的进程数量变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_PROCESS_CHANGED_COUNT)
    public int sumProcessesChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_PROCESSES);
    }

    /**
     * 一段时间内的网络连接数量变动
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_NET_CONNS_CHANGED_COUNT)
    public int sumNetConnsChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }
        return aggsChangedCountWithTimeRange(request, SicLaunchConfig.LAUNCH_ID_NET_CONNS);
    }

    /**
     * 统计每个节点的采集事件总数，并排序
     * 如：node5:100  node4:85  node3:60  node2:50  node1: 10
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_AGGS_BY_AGENTNAME)
    public Map<String, Long> countByAgentNamesBucket(WazuhEventRequest request) {
        Map<String, Long> map = new LinkedHashMap<>();
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return map;
        }
        request.getAgentNames().forEach(a -> map.put(a, 0L));
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        // 聚合查询
        var aggregateIterable = buildAggregateIterable(documents, bson, "agent.name", AGGREGATION_NAME, null);
        for (var document : aggregateIterable) {
            if (Objects.isNull(document) || !document.containsKey(AGGREGATION_NAME)) {
                continue;
            }
            String agentName = document.get("_id").toString();
            int count = (int) document.get(AGGREGATION_NAME);
            map.put(agentName, (long) count);
        }
        return map;
    }

    /**
     * 查询主机匹配到的所有rule id，最大数量100
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_AGGS_RULEIDS_BY_AGENTNAME)
    public Map<String, Long> aggsRuleIdsByAgentName(WazuhEventRequest request) {
        Map<String, Long> map = new LinkedHashMap<>();
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return map;
        }
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        // 聚合查询
        var aggregateIterable = buildAggregateIterable(documents, bson, "rule.id", AGGREGATION_NAME, 100);
        for (var document : aggregateIterable) {
            if (Objects.isNull(document) || !document.containsKey(AGGREGATION_NAME)) {
                continue;
            }
            String agentName = document.get("_id").toString();
            int count = (int) document.get(AGGREGATION_NAME);
            map.put(agentName, (long) count);
        }
        return map;
    }

    /**
     * 所有节点的采集事件总数
     * @param request
     * @return
     */
    @Cacheable(value = CACHE_KEY_COUNT_BY_AGENTNAME)
    public Long countByAgentNames(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0L;
        }
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        return documents.countDocuments(bson);
    }

    /**
     * 场景删除之后，删除对应mongo文档数据
     * @param request
     */
    public Long deleteDocByAgentNames(WazuhEventRequest request) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0L;
        }
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildFilterBson(request);
        return documents.deleteMany(bson).getDeletedCount();
    }

    /**
     * 查询数据变动数通用方法
     * @param request
     * @param launchId
     * @return
     */
    private int aggsChangedCountWithTimeRange(WazuhHostMonitorPageRequest request, String launchId) {
        if (AgentNameUtils.isAgentNamesEmpty(request.getAgentNames())) {
            return 0;
        }

        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildHostMonitorBson(request);
        if (StringUtils.isNotEmpty(launchId)) {
            bson = Filters.and(bson, Filters.eq(SIC_LAUNCH_ID, launchId));
        }
        // 聚合查询
        var aggregateIterable = buildAggregateIterable(documents, bson, "data.sic_launch_output.changed_count",
                AGGREGATION_NAME, null);

        int changeCount = 0;
        for (var document : aggregateIterable) {
            if (Objects.isNull(document) || !document.containsKey(AGGREGATION_NAME) || (int) document.get(AGGREGATION_NAME) == 0) {
                continue;
            }
            int count = (int) document.get(AGGREGATION_NAME);
            int frequency = Integer.parseInt(document.get("_id").toString());
            changeCount = changeCount + frequency * count;
        }
        return changeCount;
    }

    /**
     * 处理BaseChangeOutput通用方法
     * @param findIterable
     * @return
     */
    private List<JSONObject> processBaseChangeOutput(FindIterable<Document> findIterable) {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (var document : findIterable) {
            if (Objects.isNull(document) || !document.containsKey(MONGO_DB_SOURCE)) {
                continue;
            }
            WazuhMonitorRoot<Launch<BaseChangeCountOutput, BaseInput>> wazuhMonitorRoot = JSON.parseObject(JsonCommon.stringify(document.get(MONGO_DB_SOURCE)), new TypeReference<>() {
            });
            if (Objects.nonNull(wazuhMonitorRoot.getData()) && Objects.nonNull(wazuhMonitorRoot.getData().getOutput())) {
                BaseChangeCountOutput changedCntOutput = wazuhMonitorRoot.getData().getOutput();
                if (StringUtils.isNotBlank(changedCntOutput.getData())) {
                    byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(changedCntOutput.getData().trim(), "\""));
                    String output = TextUtils.decompress(raw);
                    JSONObject jsonObject = JSON.parseObject(output);

                    Date date = wazuhMonitorRoot.getTimestamp();
                    if (Objects.nonNull(date)) {
                        jsonObject.put("time", date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                    jsonObject.put("agent_ip", wazuhMonitorRoot.getAgent().getIp());
                    jsonObject.put("agent_name", wazuhMonitorRoot.getAgent().getName());
                    jsonObjects.add(jsonObject);
                }
            }
        }
        return jsonObjects;
    }

    /**
     * 查询LAUNCH_ID通用方法
     * @param request
     * @param launchId
     * @return
     */
    private MongoCommonResponse querySICLaunchEventsWithTimeRange(WazuhHostMonitorPageRequest request, String launchId) {
        var documents = mongoClient.getDatabase(WAZUH_ALERT_MONGO_DB).getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var bson = buildHostMonitorBson(request);
        if (StringUtils.isNotEmpty(launchId)) {
            bson = Filters.and(bson, Filters.eq(SIC_LAUNCH_ID, launchId));
        }
        MongoCommonResponse response = new MongoCommonResponse();
        response.setFindIterable(buildHostFindIterable(documents, bson, request));
        long total = documents.countDocuments(bson);
        response.setTotal(total);
        return response;
    }

    private Bson buildFilterBson(WazuhEventRequest request) {
        List<Bson> bsons = new ArrayList<>();
        // ruleGroups
        if (!CollectionUtils.isEmpty(request.getRuleGroups())) {
            buildRuleGroupQuery(request.getRuleGroups(), bsons);
        }

        // ruleIds
        if (!CollectionUtils.isEmpty(request.getRuleIds())) {
            buildRuleIdsQuery(request.getRuleIds(), bsons);
        }

        // agentNames
        if (!CollectionUtils.isEmpty(request.getAgentNames())) {
            buildAgentNameQuery(request.getAgentNames(), bsons);
        }

        // keywords
        if (!CollectionUtils.isEmpty(request.getKeyword())) {
            buildKeywordsQuery(request.getKeyword(), bsons);
        }

        // levels
        if (!CollectionUtils.isEmpty(request.getLevels())) {
            buildLevelQuery(request.getLevels(), bsons);
        }

        // path
        if (StringUtils.isNotEmpty(request.getPath())) {
            buildPathQuery(request.getPath(), bsons);
        }

        // probeIds
        if (CollectionUtils.isNotEmpty(request.getProbeIds())) {
            buildProbeIdQuery(request.getProbeIds(), bsons);
        }

        if (Objects.nonNull(request.getStartTime()) && Objects.nonNull(request.getEndTime())) {
            //时间查询
            bsons.add(Filters.gte("_source.timestamp", getUtcTime(request.getStartTime()).atZone(ZoneId.of("UTC")).format(EsDateFormatConfig.esDateTimeFormatter)));
            bsons.add(Filters.lte("_source.timestamp", getUtcTime(request.getEndTime()).atZone(ZoneId.of("UTC")).format(EsDateFormatConfig.esDateTimeFormatter)));
        }
        return Filters.and(bsons);
    }

    private LocalDateTime getUtcTime(LocalDateTime time) {
        return TimeUtils.utcToLocalDateTime(TimeUtils.localDateTimeToUtcString(time));
    }

    private Bson buildHostMonitorBson(WazuhHostMonitorPageRequest request) {
        List<Bson> bsons = new ArrayList<>();
        // agentNames
        if (CollectionUtils.isNotEmpty(request.getAgentNames())) {
            buildAgentNameQuery(request.getAgentNames(), bsons);
        }

        if (Objects.nonNull(request.getStartTime()) && Objects.nonNull(request.getEndTime())) {
            //构造时间条件
            bsons.add(Filters.gte("_source.timestamp", getUtcTime(request.getStartTime()).atZone(ZoneId.of("UTC")).format(EsDateFormatConfig.esDateTimeFormatter)));
            bsons.add(Filters.lte("_source.timestamp", getUtcTime(request.getEndTime()).atZone(ZoneId.of("UTC")).format(EsDateFormatConfig.esDateTimeFormatter)));
        }
        return Filters.and(bsons);
    }

    private void buildRuleGroupQuery(List<String> ruleGroups, List<Bson> bsons) {
        bsons.add(Filters.all("_source.rule.groups", ruleGroups));
    }

    private void buildRuleIdsQuery(List<String> ruleIds, List<Bson> bsons) {
        bsons.add(Filters.all("_source.rule.id", ruleIds));
    }

    private void buildAgentNameQuery(List<String> agentNames, List<Bson> bsons) {
        bsons.add(Filters.all("_source.agent.name", agentNames));
    }

    private void buildKeywordsQuery(List<String> keywords, List<Bson> bsons) {
        for (String keyword : keywords) {
            bsons.add(Filters.regex("_source.rule.description", "^.*" + QueryParserUtil.escape(keyword) + ".*$"));
        }
    }

    private void buildLevelQuery(List<Integer> levels, List<Bson> bsons) {
        bsons.add(Filters.all("_source.rule.level", levels));
    }

    private void buildPathQuery(String path, List<Bson> bsons) {
        bsons.add(Filters.regex("_source.syscheck.path", "^.*" + QueryParserUtil.escape(path) + ".*$"));
    }

    private void buildProbeIdQuery(List<String> probeIds, List<Bson> bsons) {
        bsons.add(Filters.all(SIC_LAUNCH_ID, probeIds));
    }

    private FindIterable<Document> buildFindIterable(MongoCollection<Document> documents, Bson bson, WazuhEventRequest request) {
        FindIterable<Document> findIterable = documents.find();
        findIterable.filter(bson);
        // 按时间排序
        if (request.isDesc()) {
            findIterable.sort(Filters.eq("_source.timestamp", -1));
        } else {
            findIterable.sort(Filters.eq("_source.timestamp", 1));
        }

        // 处理分页
        if (Objects.nonNull(request.getPageNum()) && Objects.nonNull(request.getPageSize())) {
            int offset = (request.getPageNum() - 1) * request.getPageSize();
            findIterable.skip(offset).limit(request.getPageSize());
        }
        return findIterable;
    }

    private FindIterable<Document> buildHostFindIterable(MongoCollection<Document> documents, Bson bson, WazuhHostMonitorPageRequest request) {
        FindIterable<Document> findIterable = documents.find();
        findIterable.filter(bson);
        // 按时间倒序
        findIterable.sort(Filters.eq("_source.timestamp", -1));

        // 处理分页
        if (Objects.nonNull(request.getPageNum()) && Objects.nonNull(request.getPageSize())) {
            int offset = (request.getPageNum() - 1) * request.getPageSize();
            findIterable.skip(offset).limit(request.getPageSize());
        }
        return findIterable;
    }

    private AggregateIterable<Document> buildAggregateIterable(MongoCollection<Document> documents, Bson bson, String fieldPath,
                                                               String fieldName, Integer topSize) {
        List<Bson> bsons = new ArrayList<>();
        bsons.add(Aggregates.match(bson));
        bsons.add(Aggregates.group("$" + MONGO_DB_SOURCE + "." + fieldPath, Accumulators.sum(fieldName, 1)));
        bsons.add(Aggregates.sort(Filters.eq(fieldName, -1)));
        if (Objects.nonNull(topSize)) {
            bsons.add(Aggregates.limit(topSize));
        }
        return documents.aggregate(bsons);
    }
}
