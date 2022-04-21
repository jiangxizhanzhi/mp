package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpClient;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class WazuhControl {
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
    public static final String CONTROL_ACTION_TYPE_SYSCHECK = "syscheck";
    public static final String CONTROL_ACTION_TYPE_FILE_MODIFY = "file_modify";
    public static final String CONTROL_ACTION_TYPE_REGISTRY_MODIFY = "registry_modify";

    public static final String SCRIPT_DISABLED_NO = "no";
    public static final String SCRIPT_DISABLED_YES = "yes";
    public static final String SYSCHECK_DISABLED_NO = "no";
    public static final String SYSCHECK_DISABLED_YES = "yes";

    public static final String SCRIPT_SUFFIX_PY = "py";
    public static final String SCRIPT_SUFFIX_BAT = "bat";
    public static final String SCRIPT_SUFFIX_SH = "sh";


    public static final String WINDOWS_FILE_CONTROL_SCRIPT_PATH = "C:\\ossec-agent\\file-control.bat";
    public static final String LINUX_FILE_CONTROL_SCRIPT_PATH = "/var/ossec/bin/file-control.py";

    public static final String FILE_CONTROL_ACTION_UPLOAD = "upload";
    public static final String FILE_CONTROL_ACTION_DOWNLOAD = "download";
    public static final String FILE_CONTROL_ACTION_READ = "read";
    public static final String FILE_CONTROL_ACTION_WRITE = "write";
    public static final String FILE_CONTROL_ACTION_APPEND = "append";
    public static final String FILE_CONTROL_ACTION_REMOVE = "remove";

    public static final String FILE_CONTROL_REMOTE_TYPE_GOFDFS = "godfs";

    @Getter
    private final HttpClient gofdsHttpClient;

    public static String genControlConfigCommand(String osType, ControlConfigParam controlConfigParam) {
        return genControlConfigCommand(osType, controlConfigParam, false);
    }

    public static String genControlConfigCommand(String osType, ControlConfigParam controlConfigParam, boolean restart) {
        String controlScriptPath;
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            controlScriptPath = WINDOWS_CONTROL_SCRIPT_PATH;
        } else {
            controlScriptPath = LINUX_CONTROL_SCRIPT_PATH;
        }
        List<Map<String, Object>> scriptParams = new ArrayList<>();
        if (controlConfigParam.getProbeParams() != null) {
            for (var probeParam : controlConfigParam.getProbeParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(probeParam));
            }
        }
        if (controlConfigParam.getLogCollectParams() != null) {
            for (var logCollectParam : controlConfigParam.getLogCollectParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(logCollectParam));
            }
        }
        if (controlConfigParam.getSyscheckParam() != null) {
            scriptParams.add(JsonCommon.beanToSnakeMap(controlConfigParam.getSyscheckParam()));
        }
        if (controlConfigParam.getFileModifyParams() != null) {
            for (var fileModifyParam : controlConfigParam.getFileModifyParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(fileModifyParam));
            }
        }
        if (controlConfigParam.getRegistryModifyParams() != null) {
            for (var registryModifyParam : controlConfigParam.getRegistryModifyParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(registryModifyParam));
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
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            queryScriptPath = WINDOWS_QUERY_SCRIPT_PATH;
        } else {
            queryScriptPath = LINUX_QUERY_SCRIPT_PATH;
        }
        return String.format("%s", queryScriptPath);
    }

    public static String genControlFileCommand(String osType, List<ControlFileParam> controlFileParams) {
        String controlScriptPath;
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            controlScriptPath = WINDOWS_FILE_CONTROL_SCRIPT_PATH;
        } else {
            controlScriptPath = LINUX_FILE_CONTROL_SCRIPT_PATH;
        }
        List<Map<String, Object>> scriptParams = new ArrayList<>();
        if (controlFileParams != null) {
            for (var controlFileParam : controlFileParams) {
                scriptParams.add(JsonCommon.beanToSnakeMap(controlFileParam));
            }
        }
        if (scriptParams.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String scriptParamsStr = TextUtils.encodeBase64(JsonCommon.stringify(scriptParams));
        return String.format("%s \"%s\"", controlScriptPath, scriptParamsStr);
    }

    @Getter
    private WazuhLauncher wazuhLauncher;

    public WazuhControl(String wazuhServer, String esServer, String gofdsServer) {
        wazuhLauncher = new WazuhLauncher(wazuhServer, esServer);

        gofdsHttpClient = new HttpClient(gofdsServer, 600000, 200, 200);
        gofdsHttpClient.setLogRequest(true);
    }

    public WazuhLauncher.RunCommandItem controlConfig(String agentName, String osType, ControlConfigParam controlConfigParam) {
        var runCommandItem = new WazuhLauncher.RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genControlConfigCommand(osType, controlConfigParam, true));
        wazuhLauncher.runCommand(List.of(runCommandItem));
        return runCommandItem;
    }

    public QueryConfigData queryConfig(String agentName, String osType) {
        var runCommandItem = new WazuhLauncher.RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genQueryConfigCommand(osType));
        runCommandItem.setCheckOutput(true);
        runCommandItem.setCheckOutputTime(5);
        runCommandItem.setCheckOutputInterval(5000);
        wazuhLauncher.runCommand(List.of(runCommandItem));

        var error = runCommandItem.getError();
        if (!StringUtils.isEmpty(error)) {
            log.error("Query agent({}) osType({}) config error: {}", agentName, osType, error);
        }

        var output = runCommandItem.getOutput();
        if (output == null) {
            return null;
        }
        var rawOutput = output.getOrDefault(WazuhLauncher.COMMAND_RAW_OUTPUT_KEY, null);
        if (rawOutput == null) {
            return null;
        }

        QueryConfigData configData = null;
        try {
            configData = JsonCommon.snakeParse(
                    TextUtils.decompress(TextUtils.rawDecodeBase64(((String) rawOutput).trim())),
                    new TypeReference<QueryConfigData>(){});
        } catch (Exception e) {
            log.error("Query agent({}) osType({}) config parse raw output({}) error: {}", agentName, osType, rawOutput,
                    e);
        }

        return configData;
    }

    public Map<String, ControlFileResult> controlFile(String agentName, String osType,
                                                      List<ControlFileParam> controlFileParams,
                                                      boolean checkOutput,
                                                      Integer checkOutputTIme,
                                                      Integer checkOutputInterval) {
        var runCommandItem = new WazuhLauncher.RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genControlFileCommand(osType, controlFileParams));
        if (checkOutput) {
            runCommandItem.setCheckOutput(true);
            if (checkOutputTIme != null) {
                runCommandItem.setCheckOutputTime(checkOutputTIme);
            }
            if (checkOutputInterval != null) {
                runCommandItem.setCheckOutputInterval(checkOutputInterval);
            }
        }
        wazuhLauncher.runCommand(List.of(runCommandItem));

        var error = runCommandItem.getError();
        if (!StringUtils.isEmpty(error)) {
            log.error("Control agent({}) osType({}) file error: {}", agentName, osType, error);
        }

        var output = runCommandItem.getOutput();
        if (output == null) {
            return null;
        }
        var rawOutput = output.getOrDefault(WazuhLauncher.COMMAND_RAW_OUTPUT_KEY, null);
        if (rawOutput == null) {
            return null;
        }

        Map<String, ControlFileResult> result = null;
        try {
            result = JsonCommon.snakeParse(TextUtils.decompress(TextUtils.rawDecodeBase64(((String) rawOutput).trim())),
                    new TypeReference<Map<String, ControlFileResult>>() {});
        } catch (Exception e) {
            log.error("Control agent({}) osType({}) file parse raw output({}) error: {}", agentName, osType, rawOutput,
                    e);
        }

        return result;
    }

    @Data
    public static class ControlConfigParam {
        private List<ControlProbeParam> probeParams;
        private List<ControlLogCollectParam> logCollectParams;
        private ControlSyscheckParam syscheckParam;
        private List<ControlFileModifyParam> fileModifyParams;
        private List<ControlRegistryModifyParam> registryModifyParams;
    }

    @Data
    public static class ControlProbeParam {
        private String id;
        private String action;
        private final String type = CONTROL_ACTION_TYPE_SCRIPT;
        private String group;
        private final String matcher = WazuhLauncher.COMMAND_MATCHER_CMD;
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
    public static class ControlSyscheckParam {
        private String id;
        private String action;
        private final String type = CONTROL_ACTION_TYPE_SYSCHECK;
        private String disabled;
        private String frequency;
        private String windowsAuditInterval;
    }

    @Data
    public static class ControlFileModifyParam {
        private String id;
        private String action;
        private final String type = CONTROL_ACTION_TYPE_FILE_MODIFY;
        private List<String> targets;
        private List<String> realtimeTargets;
        private List<String> ignores;
        private List<String> sregexIgnores;
        private List<String> nodiffs;
        private List<String> sregexNodiffs;
    }

    @Data
    public static class ControlRegistryModifyParam {
        private String id;
        private String action;
        private final String type = CONTROL_ACTION_TYPE_REGISTRY_MODIFY;
        private List<String> targets;
        private List<String> ignores;
        private List<String> sregexIgnores;
    }

    @Data
    public static class QueryConfigData {
        private List<QueryConfigScriptInfo> scriptInfo;
        private List<QueryConfigLogInfo> logInfo;
        private QuerySyscheckInfo syscheckInfo;
        private List<QueryFileModifyInfo> fileModifyInfo;
        private List<QueryRegistryModifyInfo> registryModifyInfo;
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
    public static class QuerySyscheckInfo {
        private String disabled;
        private String frequency;
        private String windowsAuditInterval;
    }

    @Data
    public static class QueryFileModifyInfo {
        private String key;
        private List<String> targets;
        private List<String> realtimeTargets;
        private List<String> ignores;
        private List<String> sregexIgnores;
        private List<String> nodiffs;
        private List<String> sregexNodiffs;
    }

    @Data
    public static class QueryRegistryModifyInfo {
        private String key;
        private List<String> targets;
        private List<String> ignores;
        private List<String> sregexIgnores;
    }

    @Data
    public static class ControlFileParam {
        private String id;
        private String action;
        private String filepath;

        // action: upload, download(remove)
        private String remoteType;

        // action: upload
        private String uploadUrl;

        // action: download
        private String downloadUrl;
        private String removeUrl; // download then remove

        // action: write, append
        private String fileContent;

        private void ensureId() {
            if (StringUtils.isEmpty(this.id)) {
                this.id = TextUtils.uuid();
            }
        }

        public void configUploadParam(String filepath, String uploadUrl) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_UPLOAD;
            this.filepath = filepath;
            this.remoteType = FILE_CONTROL_REMOTE_TYPE_GOFDFS;
            this.uploadUrl = uploadUrl;
        }

        public void configDownloadParam(String filepath, String downloadUrl) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_DOWNLOAD;
            this.filepath = filepath;
            this.downloadUrl = downloadUrl;
        }

        public void configDownloadParam(String filepath, String downloadUrl, String removeUrl) {
            configDownloadParam(filepath, downloadUrl);
            this.removeUrl = removeUrl;
            if (!StringUtils.isEmpty(this.removeUrl)) {
                this.remoteType = FILE_CONTROL_REMOTE_TYPE_GOFDFS;
            }
        }

        public void configReadParam(String filepath) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_READ;
            this.filepath = filepath;
        }

        public void configWriteParam(String filepath, String fileContent) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_WRITE;
            this.filepath = filepath;
            this.fileContent = fileContent;
        }

        public void configAppendParam(String filepath, String fileContent) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_APPEND;
            this.filepath = filepath;
            this.fileContent = fileContent;
        }

        public void configRemoveParam(String filepath) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_REMOVE;
            this.filepath = filepath;
        }

    }

    @Data
    public static class ControlFileResult {
        private Boolean success;
        private String error;
        private String warning;

        // action: upload
        private String fileSrc;

        // action: read
        private String fileContent;
    }

}
