package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;


import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WazuhLauncher {

    public static final String OS_TYPE_WINDOWS = "windows";
    public static final String OS_TYPE_LINUX = "linux";

    public static final String COMMAND_MATCHER_AR = "ar";
    public static final String COMMAND_MATCHER_CMD = "cmd";

    public static final String COMMAND_RAW_OUTPUT_KEY = "raw_output";

    public static final String CMD_OUTPUT_FORMAT_JSON = "json";
    public static final String CMD_OUTPUT_FORMAT_PLAIN = "plain";
    public static final String CMD_OUTPUT_FORMAT_NONE = "none";

    public static final String WAZUH_ALERT_ES_INDEX_PREFIX = "wazuh-alerts-4.x-";
    public static final DateTimeFormatter WAZUH_ALERT_ES_INDEX_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static final String WAZUH_ALERT_MONGO_DB = "wazuh";
    public static final String WAZUH_ALERT_MONGO_COLLECTION = "wazuh-alerts-4.x";

    private static Cache<String, LaunchAlert> runCommandResultPool = CacheBuilder.newBuilder()
            .maximumSize(120000)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    @Getter
    private WazuhClient wazuhClient;

    @Getter
    private List<RunCommandHttpCallback> httpCallbacks;
    @Getter
    private Boolean callbackSelf;

    public static void runCommandResultCall(LaunchAlert alertData) {
        log.info("Wazuh run command({}) result call", alertData.getData().getSicLaunchId());
        runCommandResultPool.put(alertData.getData().getSicLaunchId(), alertData);
    }

    private void runCommand(RunCommandItem runCommandItem) {
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
        launchInput.setExile(runCommandItem.getExile());
        launchInput.setCommand(runCommandItem.getCommand());

        var runCommandHttpCallbacks = runCommandItem.getHttpCallbacks();
        if (runCommandHttpCallbacks == null) {
            runCommandHttpCallbacks = this.httpCallbacks;
        }
        if (runCommandHttpCallbacks != null && !runCommandHttpCallbacks.isEmpty()) {
            launchInput.setHttpCallbacks(TextUtils.encodeBase64(JsonCommon.snakeStringify(runCommandHttpCallbacks)));
        }
        activeResponseHttpData.setArguments(List.of(TextUtils.encodeBase64(TextUtils.compress(JsonCommon.snakeStringify(launchInput))), "true"));

        log.info("Wazuh run command({}) on agent({}): {}", launchInput.getId(), runCommandItem.getAgentId(),
                launchInput.getCommand());
        var httpResult = wazuhClient.getWazuhHttpClient().put("/active-response", activeResponseHttpData,
                agentsHttpParam, wazuhClient.genWazuhAuthHeaders(), null);
        if (!httpResult.isSuccess()) {
            wazuhClient.handleWazuhErrorResult(httpResult);
        }

        WazuhClient.WazuhSuccessResponse<WazuhClient.WazuhSuccessData<String>> response;
        try {
            response = httpResult.snakeGetResultBean(new TypeReference<WazuhClient.WazuhSuccessResponse<WazuhClient.WazuhSuccessData<String>>>() {});
        } catch (Exception e) {
            log.error("Parse wazuh agents response failed: ", e);
            throw new MpRuntimeException("Parse wazuh agents response failed");
        }

        wazuhClient.handleWazuhFailResult(response);

        if (!Boolean.TRUE.equals(runCommandItem.getCheckOutput())) {
            return;
        }

        LaunchAlert launchAlert = null;
        var runCommandCallbackSelf = runCommandItem.getCallbackSelf();
        if (runCommandCallbackSelf == null) {
            runCommandCallbackSelf = this.callbackSelf;
        }
        if (Boolean.TRUE.equals(runCommandCallbackSelf)) {
            
            long timeout = runCommandItem.getCheckOutputTime() * (long) runCommandItem.getCheckOutputInterval();
            long checkConsumeMillis = 0;
            var step = 250;
            while (timeout >= checkConsumeMillis) {
                try {
                    Thread.sleep(step);
                } catch (InterruptedException e) {
                    log.error("Wait wazuh agent command output callback interrupted: ", e);
                }
                checkConsumeMillis = checkConsumeMillis + step;

                launchAlert = localCheckRunCommandLaunchAlert(runCommandItem.getRunId());
                if (launchAlert != null) {
                    break;
                }
            }

            
            if (launchAlert == null) {
                launchAlert = queryCommandLaunchAlert(runCommandItem.getAgentId(), runCommandItem.getRunId());
            }
        } else {
            
            for (var i = 0; i < runCommandItem.getCheckOutputTime(); i++) {
                try {
                    Thread.sleep(runCommandItem.getCheckOutputInterval());
                } catch (InterruptedException e) {
                    log.error("Wait wazuh agent command output interrupted: ", e);
                }
                launchAlert = queryCommandLaunchAlert(runCommandItem.getAgentId(), runCommandItem.getRunId());
                if (launchAlert != null) {
                    break;
                }
            }
        }

        if (launchAlert != null) {
            runCommandItem.setOutput(launchAlert.getData().getSicLaunchOutput());
            runCommandItem.setError(launchAlert.getData().getSicLaunchError());
        } else {
            runCommandItem.setError("Output not found, maybe you should wait a while");
        }
    }

    private LaunchAlert queryCommandLaunchAlert(String agentId, String runId) {
        if (wazuhClient.getMongoClient() != null) {
            return queryCommandLaunchAlertOnMongo(agentId, runId);
        }

        if (wazuhClient.getEsClient() != null) {
            return queryCommandLaunchAlertOnEs(agentId, runId);
        }

        return null;
    }

    private LaunchAlert queryCommandLaunchAlertOnEs(String agentId, String runId) {
        var currentDayTime = LocalDateTime.now();
        var currentDayAlertIndex = String.format("%s%s", WAZUH_ALERT_ES_INDEX_PREFIX,
                TimeUtils.localDateTimeToString(currentDayTime, WAZUH_ALERT_ES_INDEX_DATE_FORMAT));
        var esSearchRequest = new SearchRequest(currentDayAlertIndex);
        var esBoolQueryBuilder = QueryBuilders.boolQuery();
        esBoolQueryBuilder.filter(QueryBuilders.matchQuery("agent.id", agentId));
        esBoolQueryBuilder.filter(QueryBuilders.matchQuery("data.sic_launch_id", runId));

        var esSourceBuilder = new SearchSourceBuilder();
        esSearchRequest.source(esSourceBuilder.query(esBoolQueryBuilder));
        SearchHit[] esHits = null;
        try {
            esHits = wazuhClient.getEsClient().search(esSearchRequest, RequestOptions.DEFAULT).getHits().getHits();
        } catch (IOException e) {
            log.error("es query cmd output error: ", e);
        }
        if (esHits != null && esHits.length > 0) {
            return JsonCommon.snakeParse(esHits[0].getSourceAsString(), new TypeReference<LaunchAlert>() {});
        } else {
            return null;
        }
    }

    private LaunchAlert queryCommandLaunchAlertOnMongo(String agentId, String runId) {
        var db = wazuhClient.getMongoClient().getDatabase(WAZUH_ALERT_MONGO_DB);
        var collection = db.getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        var document = collection.find(Filters.eq("_source.agent.id", agentId))
                .filter(Filters.eq("_source.data.sic_launch_id", runId)).first();
        if (document != null && document.containsKey("_source")) {
            return JsonCommon.snakeParse(JsonCommon.stringify(document.get("_source")), new TypeReference<LaunchAlert>() {});
        } else {
            return null;
        }
    }

    private LaunchAlert localCheckRunCommandLaunchAlert(String runId) {
        var launchAlert = runCommandResultPool.getIfPresent(runId);
        if (launchAlert == null) {
            return null;
        }

        runCommandResultPool.invalidate(runId);

        return launchAlert;
    }

    public static class WazuhLauncherBuilder {
        private WazuhClient.WazuhClientBuilder wazuhClientBuilder;
        private List<RunCommandHttpCallback> httpCallbacks;
        private Boolean callbackSelf;

        private WazuhLauncherBuilder () {
            wazuhClientBuilder = WazuhClient.builder();
        }

        public WazuhLauncherBuilder configWazuh(String wazuhServer, String wazuhUser, String wazuhPass) {
            wazuhClientBuilder.configWazuh(wazuhServer, wazuhUser, wazuhPass);
            return this;
        }

        public WazuhLauncherBuilder configEs(String esServer) {
            wazuhClientBuilder.configEs(esServer);
            return this;
        }

        public WazuhLauncherBuilder configMongo(String mongoServer) {
            wazuhClientBuilder.configMongo(mongoServer);
            return this;
        }

        public WazuhLauncherBuilder configCallbackSelf(boolean callbackSelf) {
            this.callbackSelf = callbackSelf;
            return this;
        }

        public WazuhLauncherBuilder configHttpCallbacks(RunCommandHttpCallback... httpCallbacks) {
            this.httpCallbacks = Arrays.asList(httpCallbacks);
            return this;
        }

        public WazuhLauncher build() {
            return new WazuhLauncher(wazuhClientBuilder.build(), httpCallbacks, callbackSelf);
        }
    }

    private WazuhLauncher(WazuhClient wazuhClient, List<RunCommandHttpCallback> httpCallbacks, boolean callbackSelf) {
        this.wazuhClient = wazuhClient;
        this.httpCallbacks = httpCallbacks;
        this.callbackSelf = callbackSelf;
    }

    public static WazuhLauncherBuilder builder() {
        return new WazuhLauncherBuilder();
    }

    public List<RunCommandItem> runCommand(List<RunCommandItem> runCommandItems) {
        Set<String> queryIdNames = new HashSet<>();
        for (var runCommandItem : runCommandItems) {
            if (StringUtils.isEmpty(runCommandItem.getAgentId()) && !StringUtils.isEmpty(runCommandItem.getAgentName())) {
                queryIdNames.add(runCommandItem.getAgentName());
            }
        }
        Map<String, String> agentIds = wazuhClient.getAgentIds(new ArrayList<>(queryIdNames));

        Set<String> notfoundNames = new HashSet<>();
        List<Runnable> tasks = new ArrayList<>();
        List<RunCommandItem> result = new ArrayList<>();
        for (var runCommandItem : runCommandItems) {
            var agentId = runCommandItem.getAgentId();
            var agentName = runCommandItem.getAgentName();
            if (StringUtils.isEmpty(agentId)) {
                agentId = agentIds.getOrDefault(agentName, null);
            }

            if (StringUtils.isEmpty(agentId)) {
                notfoundNames.add(agentName);
            } else {
                runCommandItem.setAgentId(agentId);
                tasks.add(() -> runCommand(runCommandItem));
                result.add(runCommandItem);
            }
        }

        if (!notfoundNames.isEmpty()) {
            log.warn("Could not find those wazuh agents: {}, can not run command", notfoundNames);
        }

        TaskCommon.runTasks(tasks);

        return result;
    }

    @Data
    public static class LaunchAlert {
        private LaunchData data;
    }

    @Data
    public static class LaunchData {
        private String sicLaunchId;
        private String sicLaunchGroup;
        private String sicLaunchTime;
        private String sicLaunchStatus;
        private LaunchInput sicLaunchInput;
        private Map<String, Object> sicLaunchOutput;
        private Integer sicLaunchReturncode;
        private String sicLaunchError;
        private Long sicLaunchSeconds;
        private Long sicLaunchMilliseconds;
    }

    @Data
    public static class LaunchInput {
        private String id;
        private String group;
        private String matcher;
        private String command;
        private String outputFormat;
        private String httpCallbacks;
        private Boolean exile;
    }

    @Data
    public static class ActiveResponseHttpData implements SnakeHttpData {
        private String command;
        private List<String> arguments;
    }

    @Data
    public static class AgentsActiveResponseHttpParam implements SnakeHttpParam {
        private String agentsList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RunCommandHttpCallback {
        private String url;
        private Map<String, Object> headers;
    }

    @Data
    public static class RunCommandItem {
        
        private String agentName;
        
        private String osType;
        
        private String command;
        private String outputFormat;
        private Boolean exile;
        private Boolean checkOutput;
        private Integer checkOutputTime;
        private Integer checkOutputInterval;
        private List<RunCommandHttpCallback> httpCallbacks;
        private Boolean callbackSelf;

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

}
