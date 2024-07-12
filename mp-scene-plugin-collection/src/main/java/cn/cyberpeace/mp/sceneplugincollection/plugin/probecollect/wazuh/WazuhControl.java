 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhControl.java.
 // File size 35519.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @ClassName WazuhControl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
public class WazuhControl {
    // 定义Windows平台下控制脚本的路径
    public static final String WINDOWS_CONTROL_SCRIPT_PATH = "C:\\ossec-agent\\probe-control.bat";
    // 定义Linux平台下控制脚本的路径
    public static final String LINUX_CONTROL_SCRIPT_PATH = "/var/ossec/bin/probe-control.py";

    // 定义Windows平台下查询脚本的路径
    public static final String WINDOWS_QUERY_SCRIPT_PATH = "C:\\ossec-agent\\probe-query.bat";
    // 定义Linux平台下查询脚本的路径
    public static final String LINUX_QUERY_SCRIPT_PATH = "/var/ossec/bin/probe-query.py";

    // 定义Windows平台下特殊查询脚本的路径
    public static final String WINDOWS_SPEC_QUERY_SCRIPT_PATH = "C:\\ossec-agent\\spec-query.bat";
    // 定义Linux平台下特殊查询脚本的路径
    public static final String LINUX_SPEC_QUERY_SCRIPT_PATH = "/var/ossec/bin/spec-query.py";

    // 定义控制操作的添加动作
    public static final String CONTROL_ACTION_ADD = "add";
    // 定义控制操作的移除动作
    public static final String CONTROL_ACTION_REMOVE = "remove";
    // 定义控制操作的启用动作
    public static final String CONTROL_ACTION_ENABLE = "enable";
    // 定义控制操作的禁用动作
    public static final String CONTROL_ACTION_DISABLE = "disable";
    // 定义控制操作的脚本类型
    public static final String CONTROL_ACTION_TYPE_SCRIPT = "script";
    // 定义控制操作的日志类型
    public static final String CONTROL_ACTION_TYPE_LOG = "log";
    // 定义控制操作的系统检查类型
    public static final String CONTROL_ACTION_TYPE_SYSCHECK = "syscheck";
    // 定义控制操作的文件修改类型
    public static final String CONTROL_ACTION_TYPE_FILE_MODIFY = "file_modify";
    // 定义控制操作的注册表修改类型
    public static final String CONTROL_ACTION_TYPE_REGISTRY_MODIFY = "registry_modify";
    // 定义控制操作的系统收集器类型
    public static final String CONTROL_ACTION_TYPE_SYSCOLLECTOR = "syscollector";

    // 定义脚本禁用状态的否选项
    public static final String SCRIPT_DISABLED_NO = "no";
    // 定义脚本禁用状态的是选项
    public static final String SCRIPT_DISABLED_YES = "yes";
    // 定义系统检查禁用状态的否选项
    public static final String SYSCHECK_DISABLED_NO = "no";
    // 定义系统检查禁用状态的是选项
    public static final String SYSCHECK_DISABLED_YES = "yes";

    // 定义脚本的Python后缀
    public static final String SCRIPT_SUFFIX_PY = "py";
    // 定义脚本的批处理后缀
    public static final String SCRIPT_SUFFIX_BAT = "bat";
    // 定义脚本的Shell后缀
    public static final String SCRIPT_SUFFIX_SH = "sh";

    // 定义特殊查询操作获取日志收集文件的选项
    public static final String SPEC_QUERY_ACTION_GET_LOG_COLLECT_FILES = "get_log_collect_files";

    // 定义Windows平台下文件控制脚本的路径
    public static final String WINDOWS_FILE_CONTROL_SCRIPT_PATH = "C:\\ossec-agent\\file-control.bat";
    // 定义Linux平台下文件控制脚本的路径
    public static final String LINUX_FILE_CONTROL_SCRIPT_PATH = "/var/ossec/bin/file-control.py";

    // 定义文件控制操作的上传动作
    public static final String FILE_CONTROL_ACTION_UPLOAD = "upload";
    // 定义文件控制操作的下载动作
    public static final String FILE_CONTROL_ACTION_DOWNLOAD = "download";
    // 定义文件控制操作的读取动作
    public static final String FILE_CONTROL_ACTION_READ = "read";
    // 定义文件控制操作的写入动作
    public static final String FILE_CONTROL_ACTION_WRITE = "write";
    // 定义文件控制操作的追加动作
    public static final String FILE_CONTROL_ACTION_APPEND = "append";
    // 定义文件控制操作的移除动作
    public static final String FILE_CONTROL_ACTION_REMOVE = "remove";

    // 定义文件控制的远程类型为GOFDFS
    public static final String FILE_CONTROL_REMOTE_TYPE_GOFDFS = "godfs";


    /**
     * controlConfigOld
     *
     * @param agentName
     * @param osType
     * @param controlConfigParam
     */
    /**
     * 控制配置老版本代理的函数。
     * 此方法通过生成并执行针对指定操作系统类型和代理名称的控制配置命令，来更新老版本代理的配置。
     *
     * @param agentName 要操作的代理名称。
     * @param osType 代理所在操作系统的类型。
     * @param controlConfigParam 包含控制配置参数的对象。
     */
    public void controlConfigOld(String agentName, String osType, ControlConfigParam controlConfigParam) {
        WazuhLauncher.RunCommandItem runCommandItem = new WazuhLauncher.RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genControlConfigCommandOld(osType, controlConfigParam, true));
        wazuhLauncher.runCommand(List.of(runCommandItem));
    }

    /**
     * 生成控制配置命令（老版本）。
     * 根据给定的操作系统类型和控制配置参数，生成用于控制配置的命令。
     * 此方法是为不需立即执行命令的情境设计，与直接控制配置不同的是，它返回命令字符串而非直接执行。
     *
     * @param osType 代理所在操作系统的类型。
     * @param controlConfigParam 包含控制配置参数的对象。
     * @return 生成的控制配置命令字符串。
     */
    public static String genControlConfigCommandOld(String osType, ControlConfigParam controlConfigParam) {
        return genControlConfigCommandOld(osType, controlConfigParam, false);
    }


    /**
     * genControlConfigCommandOld
     *
     * @param osType
     * @param controlConfigParam
     * @param restart
     * @return
     */
    /**
     * 根据操作系统的类型生成控制配置的命令。
     * 该方法用于根据不同的操作系统类型（Windows或Linux）生成相应的控制脚本路径，并根据传入的控制配置参数构建命令行参数。
     * 如果需要重启，命令行中会包含重启标志。
     *
     * @param osType 操作系统的类型，用于确定控制脚本的路径。
     * @param controlConfigParam 包含各种控制配置参数的对象。
     * @param restart 表示是否需要重启，用于决定命令行中是否包含重启标志。
     * @return 生成的控制配置命令的字符串表示。
     */
    public static String genControlConfigCommandOld(String osType, ControlConfigParam controlConfigParam, boolean restart) {
        // 根据操作系统类型选择控制脚本的路径
        String controlScriptPath;
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            controlScriptPath = WINDOWS_CONTROL_SCRIPT_PATH;
        } else {
            controlScriptPath = LINUX_CONTROL_SCRIPT_PATH;
        }

        // 初始化脚本参数列表，用于存储转换后的参数对象
        List<Map<String, Object>> scriptParams = new ArrayList<>();

        // 将probe参数转换为snake命名规范的Map，并添加到脚本参数列表
        if (controlConfigParam.getProbeParams() != null) {
            for (ControlProbeParam probeParam : controlConfigParam.getProbeParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(probeParam));
            }
        }

        // 将log收集参数转换为snake命名规范的Map，并添加到脚本参数列表
        if (controlConfigParam.getLogCollectParams() != null) {
            for (ControlLogCollectParam logCollectParam : controlConfigParam.getLogCollectParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(logCollectParam));
            }
        }

        // 将syscheck参数转换为snake命名规范的Map，并添加到脚本参数列表
        if (controlConfigParam.getSyscheckParam() != null) {
            scriptParams.add(JsonCommon.beanToSnakeMap(controlConfigParam.getSyscheckParam()));
        }

        // 将file修改参数转换为snake命名规范的Map，并添加到脚本参数列表
        if (controlConfigParam.getFileModifyParams() != null) {
            for (ControlFileModifyParam fileModifyParam : controlConfigParam.getFileModifyParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(fileModifyParam));
            }
        }

        // 将registry修改参数转换为snake命名规范的Map，并添加到脚本参数列表
        if (controlConfigParam.getRegistryModifyParams() != null) {
            for (ControlRegistryModifyParam registryModifyParam : controlConfigParam.getRegistryModifyParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(registryModifyParam));
            }
        }

        // 将syscollector参数转换为snake命名规范的Map，并添加到脚本参数列表
        if (controlConfigParam.getSyscollectorParam() != null) {
            scriptParams.add(JsonCommon.beanToSnakeMap(controlConfigParam.getSyscollectorParam()));
        }

        // 如果脚本参数列表为空，返回空字符串
        if (scriptParams.isEmpty()) {
            return StringUtils.EMPTY;
        }

        // 将脚本参数列表转换为Base64编码的字符串
        // 把参数上传到文件服务器
        String scriptParamsStr = TextUtils.encodeBase64(JsonCommon.stringify(scriptParams));

        // 根据是否需要重启，构造并返回相应的命令行字符串
        if (restart) {
            return String.format("%s \"%s\" -r", controlScriptPath, scriptParamsStr);
        } else {
            return String.format("%s \"%s\"", controlScriptPath, scriptParamsStr);
        }
    }


    /**
     * genControlConfigCommand
     *
     * @param osType
     * @param controlConfigParam
     * @return
     */
    public static String genControlConfigCommand(String osType, ControlConfigParam controlConfigParam) {
        return genControlConfigCommand(osType, controlConfigParam, false);
    }

    /**
     * genControlConfigCommand
     *
     * @param osType
     * @param controlConfigParam
     * @param restart
     * @return
     */
    /**
     * 根据操作系统的类型生成控制配置的命令。
     * 该方法用于根据不同的操作系统类型（Windows或Linux）生成相应的控制脚本路径，并根据传入的控制配置参数生成相应的脚本参数，
     * 最后组合成一个完整的命令字符串。
     *
     * @param osType 操作系统的类型，用于确定控制脚本的路径。
     * @param controlConfigParam 包含各种控制配置参数的对象，如探针参数、日志收集参数等。
     * @param restart 标志位，表示是否需要重启，用于决定命令中是否包含重启参数。
     * @return 生成的控制配置命令字符串。
     */
    public static String genControlConfigCommand(String osType, ControlConfigParam controlConfigParam, boolean restart) {
        // 根据操作系统类型选择控制脚本路径
        String controlScriptPath;
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            controlScriptPath = WINDOWS_CONTROL_SCRIPT_PATH;
        } else {
            controlScriptPath = LINUX_CONTROL_SCRIPT_PATH;
        }

        // 初始化脚本参数列表
        List<Map<String, Object>> scriptParams = new ArrayList<>();

        // 将探针参数转换为脚本参数并添加到列表中
        if (controlConfigParam.getProbeParams() != null) {
            for (ControlProbeParam probeParam : controlConfigParam.getProbeParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(probeParam));
            }
        }

        // 将日志收集参数转换为脚本参数并添加到列表中
        if (controlConfigParam.getLogCollectParams() != null) {
            for (ControlLogCollectParam logCollectParam : controlConfigParam.getLogCollectParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(logCollectParam));
            }
        }

        // 将syscheck参数转换为脚本参数并添加到列表中
        if (controlConfigParam.getSyscheckParam() != null) {
            scriptParams.add(JsonCommon.beanToSnakeMap(controlConfigParam.getSyscheckParam()));
        }

        // 将文件修改参数转换为脚本参数并添加到列表中
        if (controlConfigParam.getFileModifyParams() != null) {
            for (ControlFileModifyParam fileModifyParam : controlConfigParam.getFileModifyParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(fileModifyParam));
            }
        }

        // 将注册表修改参数转换为脚本参数并添加到列表中
        if (controlConfigParam.getRegistryModifyParams() != null) {
            for (ControlRegistryModifyParam registryModifyParam : controlConfigParam.getRegistryModifyParams()) {
                scriptParams.add(JsonCommon.beanToSnakeMap(registryModifyParam));
            }
        }

        // 将syscollector参数转换为脚本参数并添加到列表中
        if (controlConfigParam.getSyscollectorParam() != null) {
            scriptParams.add(JsonCommon.beanToSnakeMap(controlConfigParam.getSyscollectorParam()));
        }

        // 如果脚本参数列表为空，返回空字符串
        if (scriptParams.isEmpty()) {
            return StringUtils.EMPTY;
        }

        // 将脚本参数列表转换为Base64编码的字符串
        // 把参数上传到文件服务器
        String scriptParamsStr = TextUtils.encodeBase64(JsonCommon.stringify(scriptParams));

        // 将脚本参数上传到文件服务器，并获取上传后的文件路径
        String result = FsCommon.uploadContent(scriptParamsStr);
        // 组合文件服务器地址和上传后的文件路径
        result = SceneSingleton.sceneAppConfig.getMetadataFileServer() + "/" + result;

        // 根据restart参数决定是否添加重启选项
        if (restart) {
            return String.format("%s \"%s\" -r", controlScriptPath, result);
        } else {
            return String.format("%s \"%s\"", controlScriptPath, result);
        }
    }

    /**
     * genQueryConfigCommand
     *
     * @param osType
     * @return
     */
    public static String genQueryConfigCommand(String osType) {
        String queryScriptPath;
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            queryScriptPath = WINDOWS_QUERY_SCRIPT_PATH;
        } else {
            queryScriptPath = LINUX_QUERY_SCRIPT_PATH;
        }
        return String.format("%s", queryScriptPath);
    }

    /**
     * genSpecQueryCommand
     *
     * @param osType
     * @param specQueryParam
     * @return
     */
    public static String genSpecQueryCommand(String osType, SpecQueryParam specQueryParam) {
        String controlScriptPath;
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            controlScriptPath = WINDOWS_SPEC_QUERY_SCRIPT_PATH;
        } else {
            controlScriptPath = LINUX_SPEC_QUERY_SCRIPT_PATH;
        }
        if (specQueryParam == null) {
            return StringUtils.EMPTY;
        }
        String scriptParamsStr = TextUtils.encodeBase64(JsonCommon.snakeStringify(specQueryParam));
        return String.format("%s \"%s\"", controlScriptPath, scriptParamsStr);
    }

    /**
     * genControlFileCommand
     *
     * @param osType
     * @param controlFileParams
     * @return
     */
    public static String genControlFileCommand(String osType, List<ControlFileParam> controlFileParams) {
        String controlScriptPath;
        if (WazuhLauncher.OS_TYPE_WINDOWS.equals(osType)) {
            controlScriptPath = WINDOWS_FILE_CONTROL_SCRIPT_PATH;
        } else {
            controlScriptPath = LINUX_FILE_CONTROL_SCRIPT_PATH;
        }
        List<Map<String, Object>> scriptParams = new ArrayList<>();
        if (controlFileParams != null) {
            for (ControlFileParam controlFileParam : controlFileParams) {
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

    public static class WazuhControlBuilder {
        private WazuhLauncher.WazuhLauncherBuilder wazuhLauncherBuilder;
        private String godfsServer;

        /**
         * WazuhControlBuilder
         */
        private WazuhControlBuilder () {
            wazuhLauncherBuilder = WazuhLauncher.builder();
        }

        /**
         * configWazuh
         *
         * @param wazuhServer
         * @param wazuhUser
         * @param wazuhPass
         * @return
         */
        public WazuhControlBuilder configWazuh(String wazuhServer, String wazuhUser, String wazuhPass) {
            wazuhLauncherBuilder.configWazuh(wazuhServer, wazuhUser, wazuhPass);
            return this;
        }

        /**
         * configEs
         *
         * @param esServer
         * @return
         */
        public WazuhControlBuilder configEs(String esServer) {
            wazuhLauncherBuilder.configEs(esServer);
            return this;
        }

        /**
         * configMongo
         *
         * @param mongoServer
         * @return
         */
        public WazuhControlBuilder configMongo(String mongoServer) {
            wazuhLauncherBuilder.configMongo(mongoServer);
            return this;
        }

        /**
         * configCallbackSelf
         *
         * @param callbackSelf
         * @return
         */
        public WazuhControlBuilder configCallbackSelf(boolean callbackSelf) {
            wazuhLauncherBuilder.configCallbackSelf(callbackSelf);
            return this;
        }

        /**
         * configHttpCallbacks
         *
         * @param httpCallbacks
         * @return
         */
        public WazuhControlBuilder configHttpCallbacks(WazuhLauncher.RunCommandHttpCallback... httpCallbacks) {
            wazuhLauncherBuilder.configHttpCallbacks(httpCallbacks);
            return this;
        }

        /**
         * configGodfs
         *
         * @param godfsServer
         * @return
         */
        public WazuhControlBuilder configGodfs(String godfsServer) {
            this.godfsServer = godfsServer;
            return this;
        }

        /**
         * build
         *
         * @return
         */
        public WazuhControl build() {
            return new WazuhControl(wazuhLauncherBuilder.build(), godfsServer);
        }
    }

    /**
     * WazuhControl
     *
     * @param wazuhLauncher
     * @param godfsServer
     */
    private WazuhControl(WazuhLauncher wazuhLauncher, String godfsServer) {
        this.wazuhLauncher = wazuhLauncher;
    }

    /**
     * builder
     *
     * @return
     */
    public static WazuhControlBuilder builder() {
        return new WazuhControlBuilder();
    }

    /**
     * controlConfig
     *
     * @param agentName
     * @param osType
     * @param controlConfigParam
     */
    public void controlConfig(String agentName, String osType, ControlConfigParam controlConfigParam) {
        WazuhLauncher.RunCommandItem runCommandItem = new WazuhLauncher.RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genControlConfigCommand(osType, controlConfigParam, true));
        wazuhLauncher.runCommand(List.of(runCommandItem));
    }

    /**
     * queryConfig
     *
     * @param agentName
     * @param osType
     * @return
     */
    public QueryConfigData queryConfig(String agentName, String osType) {
        WazuhLauncher.RunCommandItem runCommandItem = new WazuhLauncher.RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);
        runCommandItem.setCommand(genQueryConfigCommand(osType));
        runCommandItem.setCheckOutput(true);
        runCommandItem.setCheckOutputTime(5);
        runCommandItem.setCheckOutputInterval(5000);
        wazuhLauncher.runCommand(List.of(runCommandItem));

        String error = runCommandItem.getError();
        if (!StringUtils.isEmpty(error)) {
            log.error("Query agent({}) osType({}) config error: {}", agentName, osType, error);
        }

        Map<String, Object> output = runCommandItem.getOutput();
        if (output == null) {
            return null;
        }
        Object rawOutput = output.getOrDefault(WazuhLauncher.COMMAND_RAW_OUTPUT_KEY, null);
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

    /**
     * queryLogCollectFiles
     *
     * @param agentName
     * @param osType
     * @return
     */
    public List<QueryLogCollectFileData> queryLogCollectFiles(String agentName, String osType) {
        WazuhLauncher.RunCommandItem runCommandItem = new WazuhLauncher.RunCommandItem();
        runCommandItem.setAgentName(agentName);
        runCommandItem.setOsType(osType);

        SpecQueryParam specQueryParam = new SpecQueryParam();
        specQueryParam.setAction(SPEC_QUERY_ACTION_GET_LOG_COLLECT_FILES);
        runCommandItem.setCommand(genSpecQueryCommand(osType, specQueryParam));
        runCommandItem.setCheckOutput(true);
        runCommandItem.setCheckOutputTime(5);
        runCommandItem.setCheckOutputInterval(5000);
        wazuhLauncher.runCommand(List.of(runCommandItem));

        String error = runCommandItem.getError();
        if (!StringUtils.isEmpty(error)) {
            log.error("Query agent({}) osType({}) log collect files error: {}", agentName, osType, error);
        }

        List<QueryLogCollectFileData> result = new ArrayList<>();
        Map<String, Object> output = runCommandItem.getOutput();
        if (output == null) {
            return result;
        }
        Object rawOutput = output.getOrDefault(WazuhLauncher.COMMAND_RAW_OUTPUT_KEY, null);
        if (rawOutput == null) {
            return result;
        }

        try {
            result = JsonCommon.snakeParse(TextUtils.decompress(TextUtils.rawDecodeBase64(((String) rawOutput).trim())),
                    new TypeReference<List<QueryLogCollectFileData>>() {});
        } catch (Exception e) {
            log.error("Query agent({}) osType({}) log collect files parse raw output({}) error: {}", agentName, osType,
                    rawOutput, e);
        }

        return result;
    }

    /**
     * controlFile
     *
     * @param agentName
     * @param osType
     * @param controlFileParams
     * @param checkOutput
     * @param checkOutputTIme
     * @param checkOutputInterval
     * @return
     */
    public Map<String, ControlFileResult> controlFile(String agentName, String osType,
                                                      List<ControlFileParam> controlFileParams,
                                                      boolean checkOutput,
                                                      Integer checkOutputTIme,
                                                      Integer checkOutputInterval) {
        WazuhLauncher.RunCommandItem runCommandItem = new WazuhLauncher.RunCommandItem();
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

        String error = runCommandItem.getError();
        if (!StringUtils.isEmpty(error)) {
            log.error("Control agent({}) osType({}) file error: {}", agentName, osType, error);
        }

        Map<String, Object> output = runCommandItem.getOutput();
        if (output == null) {
            return null;
        }
        Object rawOutput = output.getOrDefault(WazuhLauncher.COMMAND_RAW_OUTPUT_KEY, null);
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
        private ControlSyscollectorParam syscollectorParam;
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
        private Map<String, String> extra;
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
        private Integer recursionLevel;
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
        private Integer recursionLevel;
        private List<String> ignores;
        private List<String> sregexIgnores;
    }

    @Data
    public static class ControlSyscollectorParam {
        private String id;
        private String action;
        private final String type = CONTROL_ACTION_TYPE_SYSCOLLECTOR;
        private String disabled;
        private String interval;
        private String hardware;
        private String os;
        private String network;
        private String packages;
        private String ports;
        private String portsAll;
        private String processes;
        private String hotfixes;
    }

    @Data
    public static class QueryConfigData {
        private List<QueryConfigScriptInfo> scriptInfo;
        private List<QueryConfigLogInfo> logInfo;
        private QuerySyscheckInfo syscheckInfo;
        private List<QueryFileModifyInfo> fileModifyInfo;
        private List<QueryRegistryModifyInfo> registryModifyInfo;
        private QuerySyscollectorInfo syscollectorInfo;
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
        private Integer recursionLevel;
        private List<String> ignores;
        private List<String> sregexIgnores;
        private List<String> nodiffs;
        private List<String> sregexNodiffs;
    }

    @Data
    public static class QueryRegistryModifyInfo {
        private String key;
        private List<String> targets;
        private Integer recursionLevel;
        private List<String> ignores;
        private List<String> sregexIgnores;
    }

    @Data
    public static class QuerySyscollectorInfo {
        private String disabled;
        private String interval;
        private String hardware;
        private String os;
        private String network;
        private String packages;
        private String ports;
        private String portsAll;
        private String processes;
        private String hotfixes;
    }

    @Data
    public static class SpecQueryParam {
        private String action;
    }

    @Data
    public static class QueryLogCollectFileData {
        private String key;
        private String logFormat;
        private String location;
        private List<QueryLogCollectRelatedFileInfo> relatedFiles;
    }

    @Data
    public static class QueryLogCollectRelatedFileInfo {
        private String path;
        private Long size;
        // linux下创建时间实际为changeTime，暂无法获取创建时间
        private String createTime;
        private String modifyTime;
        private String accessTime;
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

        // action: write, append
        private String fileContent;

        /**
         * ensureId
         */
        private void ensureId() {
            if (StringUtils.isEmpty(this.id)) {
                this.id = TextUtils.uuid();
            }
        }

        /**
         * configUploadParam
         *
         * @param filepath
         * @param uploadUrl
         */
        public void configUploadParam(String filepath, String uploadUrl) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_UPLOAD;
            this.filepath = filepath;
            this.remoteType = FILE_CONTROL_REMOTE_TYPE_GOFDFS;
            this.uploadUrl = uploadUrl;
        }

        /**
         * configDownloadParam
         *
         * @param filepath
         * @param downloadUrl
         */
        public void configDownloadParam(String filepath, String downloadUrl) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_DOWNLOAD;
            this.filepath = filepath;
            this.downloadUrl = downloadUrl;
        }

        /**
         * configReadParam
         *
         * @param filepath
         */
        public void configReadParam(String filepath) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_READ;
            this.filepath = filepath;
        }

        /**
         * configWriteParam
         *
         * @param filepath
         * @param fileContent
         */
        public void configWriteParam(String filepath, String fileContent) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_WRITE;
            this.filepath = filepath;
            this.fileContent = fileContent;
        }

        /**
         * configAppendParam
         *
         * @param filepath
         * @param fileContent
         */
        public void configAppendParam(String filepath, String fileContent) {
            ensureId();
            this.action = FILE_CONTROL_ACTION_APPEND;
            this.filepath = filepath;
            this.fileContent = fileContent;
        }

        /**
         * configRemoveParam
         *
         * @param filepath
         */
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
