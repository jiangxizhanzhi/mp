package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpResult;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WazuhClient {

    @Getter
    private final HttpClient wazuhHttpClient;
    @Getter
    private final RestHighLevelClient esClient;

    private int wazuhTokenExpiredSeconds = 900;

    private String wazuhToken;
    private LocalDateTime wazuhTokenExpiredTime;

    /**
     * TODO 缓存优化
     */
    private static final Map<String, String> agentNameIdMap = new ConcurrentHashMap<>();

    private Map<String, String> genWazuhBaseAuthHeaders() {
        // TODO 配置化
        var authUser = "wazuh";
        var authPass = "wazuh";
        return Map.of("Authorization", String.format("Basic %s",
                TextUtils.encodeBase64(String.format("%s:%s", authUser, authPass))));
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

    /**
     * TODO 简单示例，需要优化, 缓存...
     */
    private void syncAgentInfo() {
        var agents = getAgents();
        agentNameIdMap.clear();
        agents.forEach(agent -> {
            agentNameIdMap.put(agent.getName(), agent.getId());
        });
    }

    public WazuhClient(String wazuhServer, String esServer) {
        wazuhHttpClient = new HttpClient(wazuhServer, 60000, 200, 200);
        wazuhHttpClient.setLogRequest(true);

        var esHostParts = esServer.split("://")[1].split(":");
        var restClientBuilder = RestClient.builder(new HttpHost(esHostParts[0], IntUtils.fromStr(esHostParts[1])));
        esClient = new RestHighLevelClient(restClientBuilder);
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

    public interface WazuhHttpParam extends HttpParam {
        @Override
        default String toPathString() {
            List<Field> fieldList = ParamUtils.getFieldsByObject(this);
            return ParamUtils.wrapperGetParamsByFieldsAndObject(fieldList, this, Boolean.TRUE);
        }
    }

    public interface WazuhHttpData extends HttpData {

        @Override
        default String toContent() {
            return JsonUtils.snakeStringify(this);
        }

        @Override
        default Map<String, Object> toMultipart() {
            Map<String, Object> multipart = new HashMap<>();
            final BeanWrapper wrapper = new BeanWrapperImpl(this);
            var propertyDescriptors = wrapper.getPropertyDescriptors();
            for (java.beans.PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                String fieldName = propertyDescriptor.getName();
                if (!StringUtils.equals("class", fieldName)) {
                    multipart.put(TextUtils.humpToLine(fieldName), wrapper.getPropertyValue(fieldName));
                }
            }
            return multipart;
        }

    }

    @Data
    public static class AgentsQueryWazuhHttpParam implements WazuhHttpParam {
        private String status;
    }
}
