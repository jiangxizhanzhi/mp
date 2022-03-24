package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.IntUtils;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpResult;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WazuhControl {

    public static final String OS_TYPE_WINDOWS = "windows";
    public static final String OS_TYPE_LINUX = "linux";

    public static final String COMMAND_MATCHER_AR = "ar";
    public static final String COMMAND_MATCHER_CMD = "cmd";

    public static final String COMMAND_RAW_OUTPUT_KEY = "raw_output";

    public static final String CMD_OUTPUT_FORMAT_JSON = "json";
    public static final String CMD_OUTPUT_FORMAT_PLAIN = "plain";
    public static final String CMD_OUTPUT_FORMAT_NONE = "none";

    public static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";
    public static final DateTimeFormatter WAZUH_ALERT_INDEX_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static final String WINDOWS_CONTROL_SCRIPT_PATH = "C:\\ossec-agent\\probe-control.bat";
    public static final String LINUX_CONTROL_SCRIPT_PATH = "/var/ossec/bin/probe-control.py";

    public static final String WINDOWS_QUERY_SCRIPT_PATH = "C:\\ossec-agent\\probe-query.bat";
    public static final String LINUX_QUERY_SCRIPT_PATH = "/var/ossec/bin/probe-query.py";

    public static final String CONTROL_ACTION_ADD = "add";
    public static final String CONTROL_ACTION_REMOVE = "remove";
    public static final String CONTROL_ACTION_ENABLE = "enable";
    public static final String CONTROL_ACTION_DISABLE = "disable";
    public static final String CONTROL_ACTION_TYPE_SCRIPT = "script";
    public static final String CONTROL_ACTION_TYPE_LOG = "log";

    public static final String SCRIPT_DISABLED_NO = "no";
    public static final String SCRIPT_DISABLED_YES = "yes";

    public static final String SCRIPT_SUFFIX_PY = "py";
    public static final String SCRIPT_SUFFIX_BAT = "bat";
    public static final String SCRIPT_SUFFIX_SH = "sh";

    private final HttpClient httpClient;
    private final RestHighLevelClient esClient;

    private int tokenExpiredSeconds = 900;

    private String token;
    private LocalDateTime tokenExpiredTime;

    /**
     * TODO 缓存优化
     */
    private static final Map<String, String> agentNameIdMap = new ConcurrentHashMap<>();

    private Map<String, String> genBaseAuthHeaders() {
        // TODO 配置化
        var authUser = "wazuh";
        var authPass = "wazuh";
        return Map.of("Authorization", String.format("Basic %s",
                TextUtils.encodeBase64(String.format("%s:%s", authUser, authPass))));
    }

    private Map<String, String> genAuthHeaders() {
        return Map.of("Authorization", String.format("Bearer %s", token));
    }

    private void handleErrorResult(HttpResult httpResult) {
        var error = httpResult.getResultBean(new TypeReference<ErrorResponse>() {});
        throw new MpRuntimeException("Request wazuh error: " + error.getTitle());
    }

    private <T> void handleFailResult(SuccessResponse<SuccessData<T>> response) {
        var failedItems = response.getData().getFailedItems();
        if (!failedItems.isEmpty()) {
            log.warn("Request wazuh with failed items: {}", failedItems);
        }
    }

    private String getApiToken() {
        var httpResult = httpClient.get("/security/user/authenticate", null, genBaseAuthHeaders(), null);
        if (!httpResult.isSuccess()) {
            handleErrorResult(httpResult);
        }

        SuccessResponse<TokenData> response;
        try {
            response = httpResult.snakeGetResultBean(new TypeReference<SuccessResponse<TokenData>>() {});
        } catch (Exception e) {
            log.error("Parse wazuh token response failed: ", e);
            throw new MpRuntimeException("Parse wazuh token response failed");
        }

        return response.getData().getToken();
    }

    /**
     * TODO 简单示例，需要优化, 缓存...
     */
    private void syncApiToken() {
        if (tokenExpiredTime == null || LocalDateTime.now().compareTo(tokenExpiredTime) >= 0) {
            this.token = getApiToken();
            // 预留十秒，减少网络延迟导致的过期概率
            this.tokenExpiredTime = LocalDateTime.now().plusSeconds(tokenExpiredSeconds - 10L);
        }
    }

    private List<AgentItem> getAgents() {
        syncApiToken();

        var agentsQueryHttpParam = new AgentsQueryHttpParam();
        agentsQueryHttpParam.setStatus("active");
        var httpResult = httpClient.get("/agents", agentsQueryHttpParam, genAuthHeaders(), null);
        if (!httpResult.isSuccess()) {
            handleErrorResult(httpResult);
        }

        SuccessResponse<SuccessData<AgentItem>> response;
        try {
            response = httpResult.snakeGetResultBean(new TypeReference<SuccessResponse<SuccessData<AgentItem>>>() {});
        } catch (Exception e) {
            log.error("Parse wazuh agents response failed: ", e);
            throw new MpRuntimeException("Parse wazuh agents response failed");
        }

        handleFailResult(response);

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

    private String getAlertIndex(LocalDateTime time) {
        return String.format("%s%s", WAZUH_ALERT_INDEX_PREFIX,
                TimeUtils.localDateTimeToString(time, WAZUH_ALERT_INDEX_DATE_FORMAT));
    }

    private List<String> getRecentAlertIndices() {
        var currentDayTime = LocalDateTime.now();
//        var prevDayTime = currentDayTime.minusDays(1);
        return List.of(getAlertIndex(currentDayTime));
    }

    private void runCommand(RunCommandItem runCommandItem) {
        syncApiToken();

        runCommandItem.ensure();

        var agentsHttpParam = new AgentsActiveResponseHttpParam();
        agentsHttpParam.setAgentsList(runCommandItem.getAgentId());
        var activeResponseHttpData = new ActiveResponseHttpData();
        if (OS_TYPE_WINDOWS.equals(runCommandItem.getOsType())) {
            activeResponseHttpData.setCommand("!ar-launcher.bat");
        } else {
            activeResponseHttpData.setCommand("!ar-launcher.py");
        }
        var launchInput = new LaunchInput();
        launchInput.setMatcher(COMMAND_MATCHER_AR);
        launchInput.setGroup(launchInput.getMatcher());
        launchInput.setId(runCommandItem.getRunId());
        launchInput.setOutputFormat(runCommandItem.getOutputFormat());
        launchInput.setCommand(runCommandItem.getCommand());
        activeResponseHttpData.setArguments(List.of(TextUtils.encodeBase64(JsonCommon.snakeStringify(launchInput))));
        var httpResult = httpClient.put("/active-response", activeResponseHttpData, agentsHttpParam,
                genAuthHeaders(), null);
        if (!httpResult.isSuccess()) {
            handleErrorResult(httpResult);
        }

        SuccessResponse<SuccessData<String>> response;
        try {
            response = httpResult.snakeGetResultBean(new TypeReference<SuccessResponse<SuccessData<String>>>() {});
        } catch (Exception e) {
            log.error("Parse wazuh agents response failed: ", e);
            throw new MpRuntimeException("Parse wazuh agents response failed");
        }

        handleFailResult(response);

        if (Boolean.TRUE.equals(runCommandItem.getCheckOutput())) {
            for (var i = 0; i < runCommandItem.getCheckOutputTime(); i++) {
                try {
                    Thread.sleep(runCommandItem.getCheckOutputInterval());
                } catch (InterruptedException e) {
                    log.error("Wait wazuh agent command output interrupted: ", e);
                }

                var launchAlert = queryCommandLaunchAlert(runCommandItem.getAgentId(),
                        runCommandItem.getRunId());
                if (launchAlert != null) {
                    runCommandItem.setOutput(launchAlert.getData().getSicLaunchOutput());
                    runCommandItem.setError(launchAlert.getData().getSicLaunchError());
                    break;
                } else {
                    runCommandItem.setError("Output not found, maybe you should wait a while");
                }
            }
        }
    }

    private LaunchAlert queryCommandLaunchAlert(String agentId, String runId) {
        var esSearchRequest = new SearchRequest(getRecentAlertIndices().toArray(new String[0]));
        var esBoolQueryBuilder = QueryBuilders.boolQuery();
        esBoolQueryBuilder.filter(QueryBuilders.matchQuery("agent.id", agentId));
        esBoolQueryBuilder.filter(QueryBuilders.matchQuery("data.sic_launch_id", runId));

        var esSourceBuilder = new SearchSourceBuilder();
        esSearchRequest.source(esSourceBuilder.query(esBoolQueryBuilder));
        SearchHit[] esHits = null;
        try {
            esHits = esClient.search(esSearchRequest, RequestOptions.DEFAULT).getHits().getHits();
        } catch (IOException e) {
            log.error("es query cmd output error: ", e);
        }
        if (esHits != null && esHits.length > 0) {
            return JsonCommon.snakeParse(esHits[0].getSourceAsString(), new TypeReference<LaunchAlert>() {});
        } else {
            return null;
        }
    }

    private Map<String, String> getAgentIds(List<String> agentNames) {
        for (var agentName : agentNames) {
            if (!agentNameIdMap.containsKey(agentName)) {
                syncAgentInfo();
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

    public WazuhControl(String wazuhServer, String esServer) {
        httpClient = new HttpClient(wazuhServer, 60000, 200, 200);
        httpClient.setLogRequest(true);

        var esHostParts = esServer.split("://")[1].split(":");
        var restClientBuilder = RestClient.builder(new HttpHost(esHostParts[0], IntUtils.fromStr(esHostParts[1])));
        esClient = new RestHighLevelClient(restClientBuilder);
    }

    public static String genControlConfigCommand(String osType, List<ControlProbeParam> controlProbeParams,
                                                 List<ControlLogCollectParam> controlLogCollectParams) {
        return genControlConfigCommand(osType, controlProbeParams, controlLogCollectParams, false);
    }
    public static String genControlConfigCommand(String osType, List<ControlProbeParam> controlProbeParams,
                                                 List<ControlLogCollectParam> controlLogCollectParams,
                                                 boolean restart) {
        String controlScriptPath;
        if (OS_TYPE_WINDOWS.equals(osType)) {
            controlScriptPath = WINDOWS_CONTROL_SCRIPT_PATH;
        } else {
            controlScriptPath = LINUX_CONTROL_SCRIPT_PATH;
        }
        List<Map<String, Object>> scriptParams = new ArrayList<>();
        if (controlProbeParams != null) {
            for (var controlProbeParam : controlProbeParams) {
                scriptParams.add(JsonCommon.beanToSnakeMap(controlProbeParam));
            }
        }
        if (controlLogCollectParams != null) {
            for (var controlLogCollectParam : controlLogCollectParams) {
                scriptParams.add(JsonCommon.beanToSnakeMap(controlLogCollectParam));
            }
        }
        if (scriptParams.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String scriptParamsStr = TextUtils.encodeBase64(JsonCommon.stringify(scriptParams));
        if (restart) {
            return String.format("%s \"%s\" -r", controlScriptPath, scriptParamsStr);
        } else {
            return String.format("%s \"%s\"", controlScriptPath, scriptParamsStr);
        }
    }

    public static String genQueryConfigCommand(String osType) {
        String queryScriptPath;
        if (OS_TYPE_WINDOWS.equals(osType)) {
            queryScriptPath = WINDOWS_QUERY_SCRIPT_PATH;
        } else {
            queryScriptPath = LINUX_QUERY_SCRIPT_PATH;
        }
        return String.format("%s", queryScriptPath);
    }

    public List<RunCommandItem> runCommand(List<RunCommandItem> runCommandItems) {
        Map<String, List<RunCommandItem>> nameItemsMap = new HashMap<>();
        for (var runCommandItem : runCommandItems) {
            MapUtils.setDefault(nameItemsMap, runCommandItem.getAgentName(), new ArrayList<>()).add(runCommandItem);
        }
        Map<String, String> agentIds = getAgentIds(new ArrayList<>(nameItemsMap.keySet()));

        List<Runnable> tasks = new ArrayList<>();
        List<RunCommandItem> result = new ArrayList<>();
        for (var entry : agentIds.entrySet()) {
            var agentName = entry.getKey();
            var agentId = entry.getValue();
            for (var runCommandItem : nameItemsMap.remove(agentName)) {
                runCommandItem.setAgentId(agentId);
                result.add(runCommandItem);
                tasks.add(() -> runCommand(runCommandItem));
            }
        }
        if (!nameItemsMap.isEmpty()) {
            log.warn("Could not find those wazuh agents: {}, can not run command", nameItemsMap.keySet());
        }

        TaskCommon.runTasks(tasks);

        return result;
    }

    public RunCommandItem controlConfig(String agentName, String osType, List<ControlProbeParam> controlProbeParams,
                                        List<ControlLogCollectParam> controlLogCollectParams) {
        var runCommandItem = new RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genControlConfigCommand(osType, controlProbeParams, controlLogCollectParams, true));
        runCommand(List.of(runCommandItem));
        return runCommandItem;
    }

    public QueryConfigData queryConfig(String agentName, String osType) {
        var runCommandItem = new RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genQueryConfigCommand(osType));
        runCommandItem.setCheckOutput(true);
        runCommandItem.setCheckOutputTime(5);
        runCommandItem.setCheckOutputInterval(5000);
        runCommand(List.of(runCommandItem));

        var error = runCommandItem.getError();
        if (!StringUtils.isEmpty(error)) {
            log.error("Query agent({}) osType({}) config error: {}", agentName, osType, error);
        }

        var output = runCommandItem.getOutput();
        if (output == null) {
            return null;
        }
        var rawOutput = output.getOrDefault(COMMAND_RAW_OUTPUT_KEY, null);
        if (rawOutput == null) {
            return null;
        }

        QueryConfigData configData = null;
        try {
            configData = JsonCommon.snakeParse((String) rawOutput, new TypeReference<QueryConfigData>(){});
        } catch (Exception e) {
            log.error("Query agent({}) osType({}) config parse raw output({}) error: {}", agentName, osType, rawOutput,
                    e);
        }

        return configData;
    }

    @Data
    public static class QueryConfigData {
        private List<QueryConfigScriptInfo> scriptInfo;
        private List<QueryConfigLogInfo> logInfo;
    }

    @Data
    public static class QueryConfigScriptInfo {
        private String key;
        private String disabled;
        private String interval;
    }

    @Data
    public static class QueryConfigLogInfo {
        private String key;
        private String logFormat;
        private String location;
    }

    @Data
    public static class ControlProbeParam {
        private String id;
        private String action;
        private final String type = CONTROL_ACTION_TYPE_SCRIPT;
        private String group;
        private final String matcher = COMMAND_MATCHER_CMD;
        private String disabled = SCRIPT_DISABLED_NO;
        private String scriptUrl;
        private String scriptContent;
        private String scriptSuffix;
        private String scriptParam;
        private String outputFormat;
        private String interval;
        private Map<String, Object> extra;
    }

    @Data
    public static class ControlLogCollectParam {
        private String id;
        private String action;
        private final String type = CONTROL_ACTION_TYPE_LOG;
        private String logFormat;
        private String location;
    }

    @Data
    public static class LaunchAlert {
        private LaunchData data;
    }

    @Data
    public static class LaunchData {
        private String sicLaunchId;
        private String sicLaunchCommand;
        private Integer sicLaunchReturncode;
        private String sicLaunchStatus;
        private String sicLaunchOutputFormat;
        private Map<String, Object> sicLaunchOutput;
        private String sicLaunchError;
        private String sicLaunchTime;
    }

    @Data
    public static class LaunchInput {
        private String id;
        private String group;
        private String matcher;
        private String command;
        private String outputFormat;
    }

    @Data
    public static class ActiveResponseHttpData implements WazuhHttpData {
        private String command;
        private List<String> arguments;
    }

    @Data
    public static class AgentsQueryHttpParam implements WazuhHttpParam {
        private String status;
    }

    @Data
    public static class AgentsActiveResponseHttpParam implements WazuhHttpParam {
        private String agentsList;
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
    public static class RunCommandItem {
        /** required */
        private String agentName;
        /** required */
        private String osType;
        /** required */
        private String command;
        private String outputFormat;
        private Boolean checkOutput;
        private Integer checkOutputTime;
        private Integer checkOutputInterval;

        private String runId;
        private String agentId;
        private Map<String, Object> output;
        private String error;

        public void ensure() {
            if (StringUtils.isEmpty(runId)) {
                runId = TextUtils.uuid();
            }
            if (StringUtils.isEmpty(outputFormat)) {
                outputFormat = CMD_OUTPUT_FORMAT_NONE;
            }
            if (checkOutputTime == null) {
                checkOutputTime = 1;
            }
            if (checkOutputInterval == null) {
                checkOutputTime = 5000;
            }
        }
    }

    @Data
    public static class TokenData {
        private String token;
    }

    @Data
    public static class AgentItem {
        private String id;
        private String name;
    }

    @Data
    public static class SuccessResponse<T> {
        private T data;
        private String message;
        private Integer error;
    }

    @Data
    public static class SuccessData<T> {
        private List<T> affectedItems;
        private Integer totalAffectedItems;
        private Integer totalFailedItems;
        private List<Object> failedItems;
    }

    @Data
    public static class ErrorResponse {
        private String title;
        private String detail;
        private String remediation;
        private Integer error;
        private Object dapiErrors;
    }

}
