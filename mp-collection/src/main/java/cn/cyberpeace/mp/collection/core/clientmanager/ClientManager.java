package cn.cyberpeace.mp.collection.core.clientmanager;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.collection.core.client.es.EsClient;
import cn.cyberpeace.mp.collection.core.model.*;
import cn.cyberpeace.mp.collection.core.model.wazuh.agent.Agent;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.collection.core.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.core.client.mongo.MongoClientControl;
import cn.cyberpeace.mp.collection.core.request.WazuhEventRequest;
import cn.cyberpeace.mp.collection.core.request.WazuhHostMonitorPageRequest;
import cn.cyberpeace.mp.collection.core.request.wazuh.AgentRequest;
import cn.cyberpeace.mp.collection.core.request.wazuh.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.core.response.WazuhSystemUserResponse;
import cn.cyberpeace.mp.collection.core.client.wazuh.WazuhClient;

import java.util.*;

public class ClientManager {
    private WazuhClient wazuhClient;

    private EsClient esClient;

    private MongoClientControl mongoClientControl;

    private ClientManager(WazuhClient wazuhClient, EsClient esClient, MongoClientControl mongoClientControl) {
        this.wazuhClient = wazuhClient;
        this.esClient = esClient;
        this.mongoClientControl = mongoClientControl;
    }

    public static class ClientManagerBuilder {
        private WazuhClient.WazuhClientBuilder wazuhClientBuilder;
        private EsClient.EsClientBuilder esClientBuilder;
        private MongoClientControl.MongoClientControlBuilder mongoClientControlBuilder;

        private ClientManagerBuilder() {
            this.wazuhClientBuilder = WazuhClient.builder();
            this.esClientBuilder = EsClient.builder();
            this.mongoClientControlBuilder = MongoClientControl.builder();
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
            mongoClientControlBuilder.configMongo(mongoServer);
            return this;
        }

        public ClientManager build() {
            return new ClientManager(wazuhClientBuilder.build(), esClientBuilder.build(), mongoClientControlBuilder.build());
        }
    }

    public static ClientManagerBuilder builder() {
        return new ClientManagerBuilder();
    }

    public ListObjResult<FileEvent> queryFileEvents(WazuhEventRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.queryFileEvents(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.queryFileEvents(request);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public ListObjResult<FileEvent> scrollFileEvents(String scrollId) {
        if (Objects.nonNull(esClient)) {
            return esClient.scrollFileEvents(scrollId);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public ListObjResult<WazuhProbeCommon> queryCustomProbeData(WazuhEventRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.queryCustomProbeData(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.queryCustomProbeData(request);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public ListObjResult<WazuhVulnerabilityInfo> queryVulnerabilityInfo(WazuhEventRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.queryVulnerabilityInfo(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.queryVulnerabilityInfo(request);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public List<WazuhVulnerabilityInfoCount> queryVulnerabilityCountInfo(WazuhEventRequest request,
                                                                         String fieldPath, String fieldName) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.queryVulnerabilityCountInfo(request, fieldPath, fieldName);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.queryVulnerabilityCountInfo(request, fieldPath, fieldName);
        }
        return new ArrayList<>();
    }

    public ListObjResult<WazuhHostState> queryHostStateEventsWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.queryHostStateEventsWithTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.queryHostStateEventsWithTimeRange(request);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public List<WazuhNetworkConnection> queryNetConnEventsWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.queryNetConnEventsWithTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.queryNetConnEventsWithTimeRange(request);
        }
        return new ArrayList<>();
    }

    public List<WazuhProcess> queryProcessesWithTimeRange(WazuhHostMonitorPageRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.queryProcessesWithTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.queryProcessesWithTimeRange(request);
        }
        return new ArrayList<>();
    }

    public WazuhSystemUserResponse querySystemUserWithTimeRange(WazuhHostMonitorPageRequest wazuhMonitorEventsRequest) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.querySystemUserWithTimeRange(wazuhMonitorEventsRequest);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.querySystemUserWithTimeRange(wazuhMonitorEventsRequest);
        }
        return null;
    }

    public int sumEnvVarChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.sumEnvVarChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumEnvVarChangedCountByTimeRange(request);
        }
        return 0;
    }

    public int sumSysUsersChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.sumSysUsersChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumSysUsersChangedCountByTimeRange(request);
        }
        return 0;
    }

    public int sumProcessesChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.sumProcessesChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumProcessesChangedCountByTimeRange(request);
        }
        return 0;
    }

    public int sumNetConnsChangedCountByTimeRange(WazuhHostMonitorPageRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.sumNetConnsChangedCountByTimeRange(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.sumNetConnsChangedCountByTimeRange(request);
        }
        return 0;
    }

    public Map<String, Long> countByAgentNamesBucket(WazuhEventRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.countByAgentNamesBucket(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.countByAgentNamesBucket(request);
        }
        return new HashMap<>();
    }

    public Map<String, Long> aggsRuleIdsByAgentName(WazuhEventRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.aggsRuleIdsByAgentName(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.aggsRuleIdsByAgentName(request);
        }
        return new HashMap<>();
    }

    public Long countByAgentNames(WazuhEventRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.countByAgentNames(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.countByAgentNames(request);
        }
        return 0L;
    }

    public Long deleteDocByAgentNames(WazuhEventRequest request) {
        if (Objects.nonNull(mongoClientControl)) {
            return mongoClientControl.deleteDocByAgentNames(request);
        }
        if (Objects.nonNull(esClient)) {
            return esClient.deleteDocByAgentNames(request);
        }
        return 0L;
    }

    public ListObjResult<Agent> pageAgents(AgentRequest agentRequest) {
        if (Objects.nonNull(wazuhClient)) {
            return wazuhClient.pageAgents(agentRequest);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public ListObjResult<Port> pagePorts(CollectorBaseRequest collectorBaseRequest) {
        if (Objects.nonNull(wazuhClient)) {
            return wazuhClient.pagePorts(collectorBaseRequest);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public ListObjResult<Netiface> pageNetifaces(CollectorBaseRequest collectorBaseRequest) {
        if (Objects.nonNull(wazuhClient)) {
            return wazuhClient.pageNetifaces(collectorBaseRequest);
        }
        return new ListObjResult<>(new ArrayList<>(), 0);
    }

    public List<OS> getOS(String agentName) {
        if (Objects.nonNull(wazuhClient)) {
            return wazuhClient.getOS(agentName);
        }
        return new ArrayList<>();
    }
}
