package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;


import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.MapUtils;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final String WAZUH_ALERT_INDEX_PREFIX = "wazuh-alerts-4.x-";
    public static final DateTimeFormatter WAZUH_ALERT_INDEX_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Getter
    private WazuhClient wazuhClient;

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
        activeResponseHttpData.setArguments(List.of(TextUtils.encodeBase64(TextUtils.compress(JsonCommon.snakeStringify(launchInput))), "true"));
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

    public WazuhLauncher(String wazuhServer, String esServer) {
        wazuhClient = new WazuhClient(wazuhServer, esServer);
    }

    public List<RunCommandItem> runCommand(List<RunCommandItem> runCommandItems) {
        Map<String, List<RunCommandItem>> nameItemsMap = new HashMap<>();
        for (var runCommandItem : runCommandItems) {
            MapUtils.setDefault(nameItemsMap, runCommandItem.getAgentName(), new ArrayList<>()).add(runCommandItem);
        }
        Map<String, String> agentIds = wazuhClient.getAgentIds(new ArrayList<>(nameItemsMap.keySet()));

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
    public static class ActiveResponseHttpData implements WazuhClient.WazuhHttpData {
        private String command;
        private List<String> arguments;
    }

    @Data
    public static class AgentsActiveResponseHttpParam implements WazuhClient.WazuhHttpParam {
        private String agentsList;
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

}
