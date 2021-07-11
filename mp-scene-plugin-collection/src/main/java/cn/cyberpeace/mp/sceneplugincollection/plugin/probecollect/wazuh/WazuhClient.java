package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WazuhClient {

    @Getter
    private HttpClient wazuhHttpClient;
    @Getter
    private RestHighLevelClient esClient;
    @Getter
    private MongoClient mongoClient;

    private int wazuhTokenExpiredSeconds = 900;

    private String wazuhUser;
    private String wazuhPass;

    private String wazuhToken;
    private LocalDateTime wazuhTokenExpiredTime;

    
    private static final Map<String, String> agentNameIdMap = new ConcurrentHashMap<>();

    private Map<String, String> genWazuhBaseAuthHeaders() {
        return Map.of("Authorization", String.format("Basic %s",
                TextUtils.encodeBase64(String.format("%s:%s", wazuhUser, wazuhPass))));
    }

    private String getWazuhApiToken() {
        var httpResult = wazuhHttpClient.get("/security/user/authenticate", null,
                genWazuhBaseAuthHeaders(), null);
        if (!httpResult.isSuccess()) {
            handleWazuhErrorResult(httpResult);
        }

        WazuhSuccessResponse<WazuhTokenData> response;
        try {
            response = httpResult.snakeGetResultBean(new TypeReference<WazuhSuccessResponse<WazuhTokenData>>() {});
        } catch (Exception e) {
            log.error("Parse wazuh token response failed: ", e);
            throw new MpRuntimeException("Parse wazuh token response failed");
        }

        return response.getData().getToken();
    }

    
    private void syncWazuhApiToken() {
        if (wazuhTokenExpiredTime == null || LocalDateTime.now().compareTo(wazuhTokenExpiredTime) >= 0) {
            this.wazuhToken = getWazuhApiToken();
            
            this.wazuhTokenExpiredTime = LocalDateTime.now().plusSeconds(wazuhTokenExpiredSeconds - 10L);
        }
    }

    private List<AgentItem> getAgents() {
        var agentsQueryHttpParam = new AgentsQueryWazuhHttpParam();
        agentsQueryHttpParam.setStatus("active");
        var httpResult = wazuhHttpClient.get("/agents", agentsQueryHttpParam, genWazuhAuthHeaders(), null);
        if (!httpResult.isSuccess()) {
            handleWazuhErrorResult(httpResult);
        }

        WazuhSuccessResponse<WazuhSuccessData<AgentItem>> response;
        try {
            response = httpResult.snakeGetResultBean(new TypeReference<WazuhSuccessResponse<WazuhSuccessData<AgentItem>>>() {});
        } catch (Exception e) {
            log.error("Parse wazuh agents response failed: ", e);
            throw new MpRuntimeException("Parse wazuh agents response failed");
        }

        handleWazuhFailResult(response);

        return response.getData().getAffectedItems();
    }

    
    private void syncAgentInfo() {
        var agents = getAgents();
        agentNameIdMap.clear();
        agents.forEach(agent -> {
            agentNameIdMap.put(agent.getName(), agent.getId());
        });
    }

    public static class WazuhClientBuilder {
        private String wazuhServer;
        private String wazuhUser;
        private String wazuhPass;
        private String esServer;
        private String mongoServer;

        public WazuhClientBuilder configWazuh(String wazuhServer, String wazuhUser, String wazuhPass) {
            this.wazuhServer = wazuhServer;
            this.wazuhUser = wazuhUser;
            this.wazuhPass = wazuhPass;
            return this;
        }

        public WazuhClientBuilder configEs(String esServer) {
            this.esServer = esServer;
            return this;
        }

        public WazuhClientBuilder configMongo(String mongoServer) {
            this.mongoServer = mongoServer;
            return this;
        }

        public WazuhClient build() {
            return new WazuhClient(wazuhServer, wazuhUser, wazuhPass, esServer, mongoServer);
        }
    }

    private WazuhClient(String wazuhServer, String wazuhUser, String wazuhPass, String esServer, String mongoServer) {
        this.wazuhHttpClient = HttpClient.builder()
                .configServer(wazuhServer)
                .configTimeout(60000)
                .configMaxConnPerRoute(200)
                .configMaxConnTotal(200)
                .build();
        this.wazuhHttpClient.setLogRequest(true);
        this.wazuhUser = wazuhUser;
        this.wazuhPass = wazuhPass;

        if (!StringUtils.isEmpty(esServer)) {
            var esHostParts = esServer.split("://")[1].split(":");
            var restClientBuilder = RestClient.builder(new HttpHost(esHostParts[0], IntUtils.fromStr(esHostParts[1])));
            this.esClient = new RestHighLevelClient(restClientBuilder);
        }

        if (!StringUtils.isEmpty(mongoServer)) {
            this.mongoClient = MongoClients.create(mongoServer);
        }
    }

    public static WazuhClientBuilder builder() {
        return new WazuhClientBuilder();
    }

    public Map<String, String> genWazuhAuthHeaders() {
        syncWazuhApiToken();
        return Map.of("Authorization", String.format("Bearer %s", wazuhToken));
    }

    public void handleWazuhErrorResult(HttpResult httpResult) {
        var error = httpResult.getResultBean(new TypeReference<WazuhErrorResponse>() {});
        throw new MpRuntimeException("Request wazuh error: " + error.getTitle());
    }

    public <T> void handleWazuhFailResult(WazuhSuccessResponse<WazuhSuccessData<T>> response) {
        var failedItems = response.getData().getFailedItems();
        if (!failedItems.isEmpty()) {
            log.warn("Request wazuh with failed items: {}", failedItems);
        }
    }

    public Map<String, String> getAgentIds(List<String> agentNames) {
        for (var agentName : agentNames) {
            if (!agentNameIdMap.containsKey(agentName)) {
                syncAgentInfo();
                break;
            }
        }
        Map<String, String> agentIds = new HashMap<>();
        for (var agentName : agentNames) {
            var agentId = agentNameIdMap.getOrDefault(agentName, null);
            if (agentId != null) {
                agentIds.put(agentName, agentId);
            }
        }
        return agentIds;
    }

    @Data
    public static class WazuhTokenData {
        private String token;
    }

    @Data
    public static class AgentItem {
        private String id;
        private String name;
    }

    @Data
    public static class WazuhSuccessResponse<T> {
        private T data;
        private String message;
        private Integer error;
    }

    @Data
    public static class WazuhSuccessData<T> {
        private List<T> affectedItems;
        private Integer totalAffectedItems;
        private Integer totalFailedItems;
        private List<Object> failedItems;
    }

    @Data
    public static class WazuhErrorResponse {
        private String title;
        private String detail;
        private String remediation;
        private Integer error;
        private Object dapiErrors;
    }

    @Data
    public static class AgentsQueryWazuhHttpParam implements SnakeHttpParam {
        private String status;
    }
}
