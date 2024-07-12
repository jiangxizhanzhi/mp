 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhLauncher.java.
 // File size 18263.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;


import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName WazuhLauncher
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

    /**
     * runCommandResultCall
     *
     * @param alertData
     */
    public static void runCommandResultCall(LaunchAlert alertData) {
        log.info("Wazuh run command({}) result call", alertData.getData().getSicLaunchId());
        runCommandResultPool.put(alertData.getData().getSicLaunchId(), alertData);
    }

    /**
     * runCommand
     *
     * @param runCommandItem
     */
    private void runCommand(RunCommandItem runCommandItem) {
        runCommandItem.ensure();

        AgentsActiveResponseHttpParam agentsHttpParam = new AgentsActiveResponseHttpParam();
        agentsHttpParam.setAgentsList(runCommandItem.getAgentId());
        ActiveResponseHttpData activeResponseHttpData = new ActiveResponseHttpData();
        if (OS_TYPE_WINDOWS.equals(runCommandItem.getOsType())) {
            activeResponseHttpData.setCommand("!ar-launcher.bat");
        } else {
            activeResponseHttpData.setCommand("!ar-launcher.py");
        }

        LaunchInput launchInput = new LaunchInput();
        launchInput.setMatcher(COMMAND_MATCHER_AR);
        launchInput.setGroup(launchInput.getMatcher());
        launchInput.setId(runCommandItem.getRunId());
        launchInput.setOutputFormat(runCommandItem.getOutputFormat());
        launchInput.setExile(runCommandItem.getExile());
        launchInput.setCommand(runCommandItem.getCommand());

        List<RunCommandHttpCallback> runCommandHttpCallbacks = runCommandItem.getHttpCallbacks();
        if (runCommandHttpCallbacks == null) {
            runCommandHttpCallbacks = this.httpCallbacks;
        }
        if (runCommandHttpCallbacks != null && !runCommandHttpCallbacks.isEmpty()) {
            launchInput.setHttpCallbacks(TextUtils.encodeBase64(JsonCommon.snakeStringify(runCommandHttpCallbacks)));
        }
        activeResponseHttpData.setArguments(List.of(TextUtils.encodeBase64(TextUtils.compress(JsonCommon.snakeStringify(launchInput))), "true"));

        log.info("Wazuh run command({}) on agent({}): {}", launchInput.getId(), runCommandItem.getAgentId(),
                launchInput.getCommand());
        HttpResult httpResult = wazuhClient.getWazuhHttpClient().put("/active-response", activeResponseHttpData,
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
        Boolean runCommandCallbackSelf = runCommandItem.getCallbackSelf();
        if (runCommandCallbackSelf == null) {
            runCommandCallbackSelf = this.callbackSelf;
        }
        if (Boolean.TRUE.equals(runCommandCallbackSelf)) {
            // 轮询runCommandResultPool
            long timeout = runCommandItem.getCheckOutputTime() * (long) runCommandItem.getCheckOutputInterval();
            long checkConsumeMillis = 0;
            int step = 250;
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

            // 本地查不到，最后查一遍db
            if (launchAlert == null) {
                launchAlert = queryCommandLaunchAlert(runCommandItem.getAgentId(), runCommandItem.getRunId());
            }
        } else {
            // 轮询db
            for (int i = 0; i < runCommandItem.getCheckOutputTime(); i++) {
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

    /**
     * queryCommandLaunchAlert
     *
     * @param agentId
     * @param runId
     * @return
     */
    private LaunchAlert queryCommandLaunchAlert(String agentId, String runId) {
        if (wazuhClient.getMongoClient() != null) {
            return queryCommandLaunchAlertOnMongo(agentId, runId);
        }

        if (wazuhClient.getEsClient() != null) {
            return queryCommandLaunchAlertOnEs(agentId, runId);
        }

        return null;
    }

    /**
     * queryCommandLaunchAlertOnEs
     *
     * @param agentId
     * @param runId
     * @return
     */
    private LaunchAlert queryCommandLaunchAlertOnEs(String agentId, String runId) {
        LocalDateTime currentDayTime = LocalDateTime.now();
        String currentDayAlertIndex = String.format("%s%s", WAZUH_ALERT_ES_INDEX_PREFIX,
                TimeUtils.localDateTimeToString(currentDayTime, WAZUH_ALERT_ES_INDEX_DATE_FORMAT));
        SearchRequest esSearchRequest = new SearchRequest(currentDayAlertIndex);
        BoolQueryBuilder esBoolQueryBuilder = QueryBuilders.boolQuery();
        esBoolQueryBuilder.filter(QueryBuilders.matchQuery("agent.id", agentId));
        esBoolQueryBuilder.filter(QueryBuilders.matchQuery("data.sic_launch_id", runId));

        SearchSourceBuilder esSourceBuilder = new SearchSourceBuilder();
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

    /**
     * queryCommandLaunchAlertOnMongo
     *
     * @param agentId
     * @param runId
     * @return
     */
    private LaunchAlert queryCommandLaunchAlertOnMongo(String agentId, String runId) {
        MongoDatabase db = wazuhClient.getMongoClient().getDatabase(WAZUH_ALERT_MONGO_DB);
        MongoCollection<Document> collection = db.getCollection(WAZUH_ALERT_MONGO_COLLECTION);
        Document document = collection.find(Filters.eq("_source.agent.id", agentId))
                .filter(Filters.eq("_source.data.sic_launch_id", runId)).first();
        if (document != null && document.containsKey("_source")) {
            return JsonCommon.snakeParse(JsonCommon.stringify(document.get("_source")), new TypeReference<LaunchAlert>() {});
        } else {
            return null;
        }
    }

    /**
     * localCheckRunCommandLaunchAlert
     *
     * @param runId
     * @return
     */
    private LaunchAlert localCheckRunCommandLaunchAlert(String runId) {
        LaunchAlert launchAlert = runCommandResultPool.getIfPresent(runId);
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

        /**
         * WazuhLauncherBuilder
         */
        private WazuhLauncherBuilder () {
            wazuhClientBuilder = WazuhClient.builder();
        }

        /**
         * configWazuh
         *
         * @param wazuhServer
         * @param wazuhUser
         * @param wazuhPass
         * @return
         */
        public WazuhLauncherBuilder configWazuh(String wazuhServer, String wazuhUser, String wazuhPass) {
            wazuhClientBuilder.configWazuh(wazuhServer, wazuhUser, wazuhPass);
            return this;
        }

        /**
         * configEs
         *
         * @param esServer
         * @return
         */
        public WazuhLauncherBuilder configEs(String esServer) {
            wazuhClientBuilder.configEs(esServer);
            return this;
        }

        /**
         * configMongo
         *
         * @param mongoServer
         * @return
         */
        public WazuhLauncherBuilder configMongo(String mongoServer) {
            wazuhClientBuilder.configMongo(mongoServer);
            return this;
        }

        /**
         * configCallbackSelf
         *
         * @param callbackSelf
         * @return
         */
        public WazuhLauncherBuilder configCallbackSelf(boolean callbackSelf) {
            this.callbackSelf = callbackSelf;
            return this;
        }

        /**
         * configHttpCallbacks
         *
         * @param httpCallbacks
         * @return
         */
        public WazuhLauncherBuilder configHttpCallbacks(RunCommandHttpCallback... httpCallbacks) {
            this.httpCallbacks = Arrays.asList(httpCallbacks);
            return this;
        }

        /**
         * build
         *
         * @return
         */
        public WazuhLauncher build() {
            return new WazuhLauncher(wazuhClientBuilder.build(), httpCallbacks, callbackSelf);
        }
    }

    /**
     * WazuhLauncher
     *
     * @param wazuhClient
     * @param httpCallbacks
     * @param callbackSelf
     */
    private WazuhLauncher(WazuhClient wazuhClient, List<RunCommandHttpCallback> httpCallbacks, boolean callbackSelf) {
        this.wazuhClient = wazuhClient;
        this.httpCallbacks = httpCallbacks;
        this.callbackSelf = callbackSelf;
    }

    /**
     * builder
     *
     * @return
     */
    public static WazuhLauncherBuilder builder() {
        return new WazuhLauncherBuilder();
    }

    /**
     * runCommand
     *
     * @param runCommandItems
     * @return
     */
    public List<RunCommandItem> runCommand(List<RunCommandItem> runCommandItems) {
        Set<String> queryIdNames = new HashSet<>();
        for (RunCommandItem runCommandItem : runCommandItems) {
            if (StringUtils.isEmpty(runCommandItem.getAgentId()) && !StringUtils.isEmpty(runCommandItem.getAgentName())) {
                queryIdNames.add(runCommandItem.getAgentName());
            }
        }
        Map<String, String> agentIds = wazuhClient.getAgentIds(new ArrayList<>(queryIdNames));

        Set<String> notfoundNames = new HashSet<>();
        List<Runnable> tasks = new ArrayList<>();
        List<RunCommandItem> result = new ArrayList<>();
        for (RunCommandItem runCommandItem : runCommandItems) {
            String agentId = runCommandItem.getAgentId();
            String agentName = runCommandItem.getAgentName();
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
        /** required */
        private String agentName;
        /** required */
        private String osType;
        /** required */
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

        /**
         * ensure
         */
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
