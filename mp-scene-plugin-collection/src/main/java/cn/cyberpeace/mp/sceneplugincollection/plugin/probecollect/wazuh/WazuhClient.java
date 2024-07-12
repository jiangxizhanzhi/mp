 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhClient.java.
 // File size 9837.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
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
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName WazuhClient
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

    /**
     * TODO 缓存优化
     */
    private static final Map<String, String> agentNameIdMap = new ConcurrentHashMap<>();

    /**
     * genWazuhBaseAuthHeaders
     *
     * @return
     */
    private Map<String, String> genWazuhBaseAuthHeaders() {
        return Map.of("Authorization", String.format("Basic %s",
                TextUtils.encodeBase64(String.format("%s:%s", wazuhUser, wazuhPass))));
    }

    /**
     * getWazuhApiToken
     *
     * @return
     */
    private String getWazuhApiToken() {
        HttpResult httpResult = wazuhHttpClient.get("/security/user/authenticate", null,
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

    /**
     * TODO 简单示例，需要优化, 缓存...
     */
    private void syncWazuhApiToken() {
        if (wazuhTokenExpiredTime == null || LocalDateTime.now().compareTo(wazuhTokenExpiredTime) >= 0) {
            this.wazuhToken = getWazuhApiToken();
            // 预留十秒，减少网络延迟导致的过期概率
            this.wazuhTokenExpiredTime = LocalDateTime.now().plusSeconds(wazuhTokenExpiredSeconds - 10L);
        }
    }

    /**
     * getAgents
     *
     * @return
     */
    private List<AgentItem> getAgents() {
        AgentsQueryWazuhHttpParam agentsQueryHttpParam = new AgentsQueryWazuhHttpParam();
        agentsQueryHttpParam.setStatus("active");
        HttpResult httpResult = wazuhHttpClient.get("/agents", agentsQueryHttpParam, genWazuhAuthHeaders(), null);
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

    /**
     * TODO 简单示例，需要优化, 缓存...
     */
    private void syncAgentInfo() {
        List<AgentItem> agents = getAgents();
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

        /**
         * configWazuh
         *
         * @param wazuhServer
         * @param wazuhUser
         * @param wazuhPass
         * @return
         */
        public WazuhClientBuilder configWazuh(String wazuhServer, String wazuhUser, String wazuhPass) {
            this.wazuhServer = wazuhServer;
            this.wazuhUser = wazuhUser;
            this.wazuhPass = wazuhPass;
            return this;
        }

        /**
         * configEs
         *
         * @param esServer
         * @return
         */
        public WazuhClientBuilder configEs(String esServer) {
            this.esServer = esServer;
            return this;
        }

        /**
         * configMongo
         *
         * @param mongoServer
         * @return
         */
        public WazuhClientBuilder configMongo(String mongoServer) {
            this.mongoServer = mongoServer;
            return this;
        }

        /**
         * build
         *
         * @return
         */
        public WazuhClient build() {
            return new WazuhClient(wazuhServer, wazuhUser, wazuhPass, esServer, mongoServer);
        }
    }

    /**
     * WazuhClient
     *
     * @param wazuhServer
     * @param wazuhUser
     * @param wazuhPass
     * @param esServer
     * @param mongoServer
     */
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
            String[] esHostParts = esServer.split("://")[1].split(":");
            RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(esHostParts[0], IntUtils.fromStr(esHostParts[1])));
            this.esClient = new RestHighLevelClient(restClientBuilder);
        }

        if (!StringUtils.isEmpty(mongoServer)) {
            this.mongoClient = MongoClients.create(mongoServer);
        }
    }

    /**
     * builder
     *
     * @return
     */
    public static WazuhClientBuilder builder() {
        return new WazuhClientBuilder();
    }

    /**
     * genWazuhAuthHeaders
     *
     * @return
     */
    public Map<String, String> genWazuhAuthHeaders() {
        syncWazuhApiToken();
        return Map.of("Authorization", String.format("Bearer %s", wazuhToken));
    }

    /**
     * handleWazuhErrorResult
     *
     * @param httpResult
     */
    public void handleWazuhErrorResult(HttpResult httpResult) {
        WazuhErrorResponse error = httpResult.getResultBean(new TypeReference<WazuhErrorResponse>() {});
        throw new MpRuntimeException("Request wazuh error: " + error.getTitle());
    }

    /**
     * handleWazuhFailResult
     *
     * @param response
     */
    public <T> void handleWazuhFailResult(WazuhSuccessResponse<WazuhSuccessData<T>> response) {
        List<Object> failedItems = response.getData().getFailedItems();
        if (!failedItems.isEmpty()) {
            log.warn("Request wazuh with failed items: {}", failedItems);
        }
    }

    /**
     * getAgentIds
     *
     * @param agentNames
     * @return
     */
    public Map<String, String> getAgentIds(List<String> agentNames) {
        for (String agentName : agentNames) {
            if (!agentNameIdMap.containsKey(agentName)) {
                syncAgentInfo();
                break;
            }
        }
        Map<String, String> agentIds = new HashMap<>();
        for (String agentName : agentNames) {
            String agentId = agentNameIdMap.getOrDefault(agentName, null);
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
