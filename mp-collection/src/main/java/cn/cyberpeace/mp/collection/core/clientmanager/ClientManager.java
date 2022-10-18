package cn.cyberpeace.mp.collection.core.clientmanager;

import cn.cyberpeace.mp.collection.bo.event.*;
import cn.cyberpeace.mp.collection.bo.host.*;
import cn.cyberpeace.mp.collection.core.constant.CollectionConstant;
import cn.cyberpeace.mp.collection.core.client.es.EsClient;
import cn.cyberpeace.mp.collection.core.client.mongo.MpMongoClient;
import cn.cyberpeace.mp.collection.core.client.wazuh.WazuhClient;
import cn.cyberpeace.mp.collection.core.model.es.event.Event;
import cn.cyberpeace.mp.collection.core.model.es.event.EventCount;
import cn.cyberpeace.mp.collection.core.model.es.event.RuleCount;
import cn.cyberpeace.mp.collection.core.model.es.event.WazuhProbeCommon;
import cn.cyberpeace.mp.collection.core.model.es.hostmonitor.WazuhHostState;
import cn.cyberpeace.mp.collection.core.model.es.hostmonitor.WazuhNetworkConnection;
import cn.cyberpeace.mp.collection.core.model.es.hostmonitor.WazuhProcess;
import cn.cyberpeace.mp.collection.core.model.es.vulnerability.WazuhVulnerabilityInfo;
import cn.cyberpeace.mp.collection.core.model.es.vulnerability.WazuhVulnerabilityInfoCount;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.core.request.WazuhEventRequest;
import cn.cyberpeace.mp.collection.core.request.WazuhHostMonitorPageRequest;
import cn.cyberpeace.mp.collection.core.request.wazuh.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.core.response.WazuhSystemUserResponse;
import cn.cyberpeace.mp.collection.request.CustomProbeEventRequest;
import cn.cyberpeace.mp.collection.request.EventRequest;
import cn.cyberpeace.mp.collection.request.HostMonitorPageRequest;
import cn.cyberpeace.mp.collection.request.ModifyEventRequest;
import cn.cyberpeace.mp.collection.request.base.BaseQueryPageRequest;
import cn.cyberpeace.mp.collection.response.SystemUserBOResponse;
import cn.cyberpeace.mp.collection.core.result.ListObjResult;
import cn.cyberpeace.mp.collection.util.ConvertBOUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ClientManager {
    private WazuhClient wazuhClient;

    private EsClient esClient;

    private MpMongoClient mpMongoClient;

    // expires in 10 minutes
    private static final TimedCache<String,String> TIMED_CACHE = CacheUtil.newTimedCache(10 * 60 * 1000L);

    private ClientManager(WazuhClient wazuhClient, EsClient esClient, MpMongoClient mpMongoClient) {
        this.wazuhClient = wazuhClient;
        this.esClient = esClient;
        this.mpMongoClient = mpMongoClient;
    }

    public static class ClientManagerBuilder {
        private WazuhClient.WazuhClientBuilder wazuhClientBuilder;
        private EsClient.EsClientBuilder esClientBuilder;
        private MpMongoClient.MpMongoClientBuilder mpMongoClientBuilder;

        private ClientManagerBuilder() {
            this.wazuhClientBuilder = WazuhClient.builder();
            this.esClientBuilder = EsClient.builder();
            this.mpMongoClientBuilder = MpMongoClient.builder();
        }

        public ClientManagerBuilder configWazuh(String wazuhUrl, String wazuhUsername, String wazuhPassword) {
            wazuhClientBuilder.configWazuh(wazuhUrl, wazuhUsername, wazuhPassword);
            return this;
        }

        public ClientManagerBuilder configEs(String esServer) {
            esClientBuilder.configEs(esServer);
            return this;
        }

        public ClientManagerBuilder configMgo(String mongoServer) {
            mpMongoClientBuilder.configMongo(mongoServer);
            return this;
        }

        public ClientManager build() {
            return new ClientManager(wazuhClientBuilder.build(), esClientBuilder.build(), mpMongoClientBuilder.build());
        }
    }

    public static ClientManagerBuilder builder() {
        return new ClientManagerBuilder();
    }

    /**
     * 分页查询事件
     * @param request
     * @return
     */
    public ListObjResult<EventBO> queryEvents(EventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(request.getAgentNames());
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        wazuhEventRequest.setRuleIds(request.getRuleIds());
        wazuhEventRequest.setKeyword(request.getKeyword());
        wazuhEventRequest.setLevels(request.getLevels());
        wazuhEventRequest.setPageNum(request.getPageNum());
        wazuhEventRequest.setPageSize(request.getPageSize());
        wazuhEventRequest.setTrackTotalHits(request.isTrackTotalHits());
        ListObjResult<Event> result = new ListObjResult<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryEvents(wazuhEventRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryEvents(wazuhEventRequest);
        }
        return new ListObjResult<>(ConvertBOUtil.convertEventBO(result.getList()), result.getTotal());
    }

    /**
     * 滚动分页查询事件
     * @param requestId 请求id,唯一
     * @param request
     * @return
     */
    public ListObjResult<EventBO> scrollFileEvents(String requestId, EventRequest request) {
        if (StringUtils.isEmpty(requestId) || Objects.isNull(esClient)) {
            return new ListObjResult<>();
        }
        String scrollId = TIMED_CACHE.get(requestId);
        ListObjResult<Event> result;
        if (StringUtils.isNotEmpty(scrollId)) {
            result = esClient.scrollEvents(scrollId);
            return new ListObjResult<>(ConvertBOUtil.convertEventBO(result.getList()), result.getTotal());
        }
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(request.getAgentNames());
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        wazuhEventRequest.setRuleIds(request.getRuleIds());
        wazuhEventRequest.setKeyword(request.getKeyword());
        wazuhEventRequest.setLevels(request.getLevels());
        wazuhEventRequest.setPageNum(request.getPageNum());
        wazuhEventRequest.setPageSize(request.getPageSize());
        wazuhEventRequest.setTrackTotalHits(request.isTrackTotalHits());
        wazuhEventRequest.setScroll(true);
        result = esClient.queryEvents(wazuhEventRequest);
        return new ListObjResult<>(ConvertBOUtil.convertEventBO(result.getList()), result.getTotal());
    }

    /**
     * 分页查询变更事件
     * @param type 变更事件类型 0:文件变更 1:注册表变更
     * @param request
     * @return
     */
    public ListObjResult<ModifyEventBO> queryModifyEvents(Integer type, ModifyEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(request.getAgentNames());
        wazuhEventRequest.setPageNum(request.getPageNum());
        wazuhEventRequest.setPageSize(request.getPageSize());
        wazuhEventRequest.setPath(request.getPath());
        wazuhEventRequest.setLevels(request.getLevels());
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        if (CollectionConstant.FILE.equals(type)) {
            wazuhEventRequest.setRuleGroups(CollectionConstant.FILE_MODIFY_GROUP);
        }
        if (CollectionConstant.REGISTRY.equals(type)) {
            wazuhEventRequest.setRuleGroups(CollectionConstant.REGISTRY_MODIFY_GROUP);
        }
        ListObjResult<Event> result = new ListObjResult<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryEvents(wazuhEventRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryEvents(wazuhEventRequest);
        }
        return new ListObjResult<>(ConvertBOUtil.convertModifyEventBO(result.getList()), result.getTotal());
    }

    public ListObjResult<CustomProbeEventBO> queryCustomProbeData(List<String> probeIds, CustomProbeEventRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(request.getAgentNames());
        wazuhEventRequest.setPageNum(request.getPageNum());
        wazuhEventRequest.setPageSize(request.getPageSize());
        wazuhEventRequest.setStartTime(request.getStartTime());
        wazuhEventRequest.setEndTime(request.getEndTime());
        wazuhEventRequest.setProbeIds(probeIds);
        ListObjResult<WazuhProbeCommon> result = new ListObjResult<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryCustomProbeData(wazuhEventRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryCustomProbeData(wazuhEventRequest);
        }
        return new ListObjResult<>(ConvertBOUtil.convertCustomProbeEventBO(result.getList()), result.getTotal());
    }

    public ListObjResult<VulnerabilityEventBO> queryVulnerabilityInfo(LocalDateTime startTime, LocalDateTime endTime,
                                                                      List<String> agentNames, BaseQueryPageRequest request) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setPageNum(request.getPageNum());
        wazuhEventRequest.setPageSize(request.getPageSize());
        wazuhEventRequest.setStartTime(startTime);
        wazuhEventRequest.setEndTime(endTime);
        wazuhEventRequest.setRuleGroups(CollectionConstant.VULNERABILITY_GROUP);
        ListObjResult<WazuhVulnerabilityInfo> result = new ListObjResult<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryVulnerabilityInfo(wazuhEventRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryVulnerabilityInfo(wazuhEventRequest);
        }
        return new ListObjResult<>(ConvertBOUtil.convertVulnerabilityEventBO(result.getList()), result.getTotal());
    }

    public List<VulnerabilityCountBO> queryVulnerabilityCountInfo(String fieldPath, String fieldName, LocalDateTime startTime,
                                                                  LocalDateTime endTime, Integer topSize, List<String> agentNames) {
        WazuhEventRequest wazuhEventRequest = new WazuhEventRequest();
        wazuhEventRequest.setAgentNames(agentNames);
        wazuhEventRequest.setStartTime(startTime);
        wazuhEventRequest.setEndTime(endTime);
        wazuhEventRequest.setTopSize(topSize);
        wazuhEventRequest.setRuleGroups(CollectionConstant.VULNERABILITY_GROUP);
        List<WazuhVulnerabilityInfoCount> result = new ArrayList<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryVulnerabilityCountInfo(wazuhEventRequest, fieldPath, fieldName);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryVulnerabilityCountInfo(wazuhEventRequest, fieldPath, fieldName);
        }
        return ConvertBOUtil.convertVulnerabilityCountBO(result);
    }

    public ListObjResult<HostStateBO> queryHostStateEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        pageRequest.setAgentNames(request.getAgentNames());
        pageRequest.setStartTime(request.getStartTime());
        pageRequest.setEndTime(request.getEndTime());
        pageRequest.setPageNum(request.getPageNum());
        pageRequest.setPageSize(request.getPageSize());
        ListObjResult<WazuhHostState> result = new ListObjResult<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryHostStateEventsWithTimeRange(pageRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryHostStateEventsWithTimeRange(pageRequest);
        }
        return new ListObjResult<>(ConvertBOUtil.convertHostStateBO(result.getList()), result.getTotal());
    }

    public ListObjResult<NetworkConnectionBO> queryNetConnEventsWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        pageRequest.setAgentNames(request.getAgentNames());
        pageRequest.setStartTime(request.getStartTime());
        pageRequest.setEndTime(request.getEndTime());
        pageRequest.setPageNum(request.getPageNum());
        pageRequest.setPageSize(request.getPageSize());
        List<WazuhNetworkConnection> result = new ArrayList<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryNetConnEventsWithTimeRange(pageRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryNetConnEventsWithTimeRange(pageRequest);
        }
        if (CollectionUtils.isEmpty(result)) {
            return new ListObjResult<>();
        }
        List<NetworkConnectionBO> res = ConvertBOUtil.convertNetworkConnectionBO(result);
        int offset = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();
        return new ListObjResult<>(res.stream()
                .skip(offset)
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList()), res.size());
    }

    public ListObjResult<ProcessBO> queryProcessesWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        pageRequest.setAgentNames(request.getAgentNames());
        pageRequest.setStartTime(request.getStartTime());
        pageRequest.setEndTime(request.getEndTime());
        pageRequest.setPageNum(request.getPageNum());
        pageRequest.setPageSize(request.getPageSize());
        List<WazuhProcess> result = new ArrayList<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.queryProcessesWithTimeRange(pageRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.queryProcessesWithTimeRange(pageRequest);
        }
        if (CollectionUtils.isEmpty(result)) {
            return new ListObjResult<>();
        }
        List<ProcessBO> res = ConvertBOUtil.convertProcessBO(result);
        int offset = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();
        return new ListObjResult<>(res.stream()
                .skip(offset)
                .limit(pageRequest.getPageSize())
                .collect(Collectors.toList()), res.size());
    }

    public SystemUserBOResponse querySystemUserWithTimeRange(HostMonitorPageRequest request) {
        WazuhHostMonitorPageRequest pageRequest = new WazuhHostMonitorPageRequest();
        pageRequest.setAgentNames(request.getAgentNames());
        pageRequest.setStartTime(request.getStartTime());
        pageRequest.setEndTime(request.getEndTime());
        pageRequest.setPageNum(request.getPageNum());
        pageRequest.setPageSize(request.getPageSize());
        WazuhSystemUserResponse result = new WazuhSystemUserResponse();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.querySystemUserWithTimeRange(pageRequest);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.querySystemUserWithTimeRange(pageRequest);
        }
        SystemUserBOResponse res = new SystemUserBOResponse();
        res.setValues(ConvertBOUtil.convertSystemUserBO(result.getValues()));
        res.setAdded(ConvertBOUtil.convertSystemUserBO(result.getAdded()));
        res.setDeletedPrev(ConvertBOUtil.convertSystemUserBO(result.getDeletedPrev()));
        res.setModified(ConvertBOUtil.convertSystemUserBO(result.getModified()));
        return res;
    }

    public int sumEnvVarChangedCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhHostMonitorPageRequest request = new WazuhHostMonitorPageRequest();
        request.setAgentNames(agentNames);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        if (Objects.nonNull(mpMongoClient)) {
            return mpMongoClient.sumEnvVarChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumEnvVarChangedCountByTimeRange(request);
        }
        return 0;
    }

    public int sumSysUsersChangedCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhHostMonitorPageRequest request = new WazuhHostMonitorPageRequest();
        request.setAgentNames(agentNames);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        if (Objects.nonNull(mpMongoClient)) {
            return mpMongoClient.sumSysUsersChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumSysUsersChangedCountByTimeRange(request);
        }
        return 0;
    }

    public int sumProcessesChangedCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhHostMonitorPageRequest request = new WazuhHostMonitorPageRequest();
        request.setAgentNames(agentNames);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        if (Objects.nonNull(mpMongoClient)) {
            return mpMongoClient.sumProcessesChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumProcessesChangedCountByTimeRange(request);
        }
        return 0;
    }

    public int sumNetConnsChangedCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhHostMonitorPageRequest request = new WazuhHostMonitorPageRequest();
        request.setAgentNames(agentNames);
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        if (Objects.nonNull(mpMongoClient)) {
            return mpMongoClient.sumNetConnsChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumNetConnsChangedCountByTimeRange(request);
        }
        return 0;
    }

    public List<EventCountBO> countEventByAgentNames(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        List<EventCount> result = new ArrayList<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.countEventByAgentNames(request);
        } else if (Objects.nonNull(esClient)) {
            result = esClient.countEventByAgentNames(request);
        }
        return ConvertBOUtil.convertEventCountBO(result);
    }

    public List<RuleCountBO> countRuleIdsByAgentName(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        List<RuleCount> result = new ArrayList<>();
        if (Objects.nonNull(mpMongoClient)) {
            result = mpMongoClient.countRuleIdByAgentName(request);
        }
        if (Objects.nonNull(esClient)) {
            result = esClient.countRuleIdByAgentName(request);
        }
        return ConvertBOUtil.convertRuleCountBO(result);
    }

    public Long countTotalEventByAgentNames(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        if (Objects.nonNull(mpMongoClient)) {
            return mpMongoClient.countTotalEventByAgentNames(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.countTotalEventByAgentNames(request);
        }
        return 0L;
    }

    public Long deleteDocByAgentNames(LocalDateTime startTime, LocalDateTime endTime, List<String> agentNames) {
        WazuhEventRequest request = new WazuhEventRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setAgentNames(agentNames);
        if (Objects.nonNull(mpMongoClient)) {
            return mpMongoClient.deleteDocByAgentNames(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.deleteDocByAgentNames(request);
        }
        return 0L;
    }

    public ListObjResult<PortBO> pagePorts(String agentName, BaseQueryPageRequest request) {
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        collectorBaseRequest.setAgentName(agentName);
        collectorBaseRequest.setPage(request.getPageNum());
        collectorBaseRequest.setPageSize(request.getPageSize());
        ListObjResult<Port> result = new ListObjResult<>();
        if (Objects.nonNull(wazuhClient)) {
            result = wazuhClient.pagePorts(collectorBaseRequest);
        }
        return new ListObjResult<>(ConvertBOUtil.convertPortBO(result.getList()), result.getTotal());
    }

    public ListObjResult<NetifaceBO> pageNetifaces(String agentName, BaseQueryPageRequest request) {
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        collectorBaseRequest.setAgentName(agentName);
        collectorBaseRequest.setPage(request.getPageNum());
        collectorBaseRequest.setPageSize(request.getPageSize());
        ListObjResult<Netiface> result = new ListObjResult<>();
        if (Objects.nonNull(wazuhClient)) {
            result = wazuhClient.pageNetifaces(collectorBaseRequest);
        }
        return new ListObjResult<>(ConvertBOUtil.convertNetifaceBO(result.getList()), result.getTotal());
    }

    public List<OperatingSystemBO> getOS(String agentName) {
        List<OS> result = new ArrayList<>();
        if (Objects.nonNull(wazuhClient)) {
            result = wazuhClient.getOS(agentName);
        }
        return ConvertBOUtil.convertOperatingSystemBO(result);
    }
}
