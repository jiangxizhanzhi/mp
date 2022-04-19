package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhProbeConfig;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WazuhCommon {

    private static WazuhProbeConfig wazuhProbeConfig;

    private static Integer getWazuhRemotePort(){
        return CollectionSingleton.collectionAppConfig.getWazuhRemotePort();
    }

    private static Integer getWazuhAuthPort(){
        return CollectionSingleton.collectionAppConfig.getWazuhAuthPort();
    }

    public static final String WAZUH_AGENT_GROUP = "wazuh_agent_installer";

    public static final String WAZUH_AGENT_WINDOWS_FILE_NAME = "agent-windows.zip";

    public static final String WAZUH_AGENT_LINUX_FILE_NAME = "agent-linux.zip";

    private WazuhCommon() {}

    public static List<String> generateWazuhAgentInstallScript(String systemType, String agentName, String wazuhHost,
                                                         List<WazuhControl.ControlProbeParam> customProbeParams) {
        if (wazuhProbeConfig == null) {
            wazuhProbeConfig = BeanCommon.getBean(WazuhProbeConfig.class);
        }
        agentName = StringUtils.isEmpty(agentName) ? TextUtils.uuid() : agentName;
        List<LocalFileMappingModel> mappings = LocalFileMappingCommon.getMappings(WAZUH_AGENT_GROUP);
        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));

        String osType = SystemTypeCommandUtils.getOsType(systemType);
        String metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        List<WazuhControl.ControlProbeParam> controlProbeParams = new ArrayList<>(customProbeParams);
        List<WazuhControl.ControlProbeParam> defaultProbeParams = getDefaultProbeParams(nameFileMap, metadataFileServer,
                agentName, wazuhProbeConfig);
        controlProbeParams.addAll(defaultProbeParams);
        String probeCommand = WazuhControl.genControlConfigCommand(osType, controlProbeParams, new ArrayList<>());

        InstallerUtils.Installer resource = null;
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            resource = new InstallerUtils.Installer();
            resource.setFile(nameFileMap.getOrDefault(WAZUH_AGENT_WINDOWS_FILE_NAME, StringUtils.EMPTY));
            resource.setInstallCommand(
                    StringUtils.joinWith(CommandUtils.WINDOWS_LINE_BREAK,
                            String.format("unzip " + InstallerUtils.INSTALLER_REPLACEMENT + " -d %s\\agent-installer",
                                    SceneSingleton.sceneAppConfig.getWindowsInstallerDir()),
                            String.format("cd %s\\agent-installer\\agent-windows",
                                    SceneSingleton.sceneAppConfig.getWindowsInstallerDir()),
                            "call install.bat",
                            String.format("set SIC_WAZUH_MANAGER_IP=%s", wazuhHost),
                            String.format("set SIC_WAZUH_AGENT_NAME=%s", agentName),
                            String.format("set SIC_WAZUH_MANAGER_REMOTE_PORT=%d", getWazuhRemotePort()),
                            String.format("set SIC_WAZUH_MANAGER_AUTH_PORT=%d", getWazuhAuthPort()),
                            "cd C:\\wazuha",
                            "call config.bat",
                            "call " + probeCommand,
                            "call start.bat"
                    ));
        } else if (SystemTypeCommandUtils.isLinux(systemType)) {
            resource = new InstallerUtils.Installer();
            resource.setFile(nameFileMap.getOrDefault(WAZUH_AGENT_LINUX_FILE_NAME, StringUtils.EMPTY));
            resource.setInstallCommand(
                    StringUtils.joinWith(CommandUtils.LINUX_LINE_BREAK,
                            String.format("unzip " + InstallerUtils.INSTALLER_REPLACEMENT + " -d %s/agent-installer",
                                    SceneSingleton.sceneAppConfig.getLinuxInstallerDir()),
                            String.format("cd %s/agent-installer/agent-linux",
                                    SceneSingleton.sceneAppConfig.getLinuxInstallerDir()),
                            "bash install.sh",
                            String.format("export SIC_WAZUH_MANAGER_IP=%s SIC_WAZUH_AGENT_NAME=%s " +
                                            "SIC_WAZUH_MANAGER_REMOTE_PORT=%d SIC_WAZUH_MANAGER_AUTH_PORT=%d",
                                    wazuhHost, agentName, getWazuhRemotePort(), getWazuhAuthPort()),
                            "cd /opt/wazuha",
                            "bash config.sh",
                            probeCommand,
                            "bash start.sh"
                    ));
        }
        return resource == null ? new ArrayList<>() :
                InstallerUtils.generateInstallerInstallCommand(systemType, List.of(resource));
    }

    private static List<WazuhControl.ControlProbeParam> getDefaultProbeParams(Map<String, String> nameFileMap,
                                                                              String metadataFileServer, String agentName,
                                                                              WazuhProbeConfig wazuhProbeConfig) {
        if (wazuhProbeConfig == null) {
            return new ArrayList<>();
        }
        List<WazuhControl.ControlProbeParam> defaultProbeParams = new ArrayList<>();
        wazuhProbeConfig.getProbes().forEach(probe -> {
            String url = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer,
                    nameFileMap.getOrDefault(probe.getProbeName(), StringUtils.EMPTY));
            WazuhControl.ControlProbeParam defaultProbeParam = new WazuhControl.ControlProbeParam();
            defaultProbeParam.setId(probe.getId());
            defaultProbeParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
            defaultProbeParam.setGroup(agentName);
            defaultProbeParam.setScriptUrl(url);
            if (probe.getProbeName().endsWith(WazuhControl.SCRIPT_SUFFIX_BAT)) {
                defaultProbeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
            } else if (probe.getProbeName().endsWith(WazuhControl.SCRIPT_SUFFIX_PY)) {
                defaultProbeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
            } else if (probe.getProbeName().endsWith(WazuhControl.SCRIPT_SUFFIX_SH)) {
                defaultProbeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_SH);
            }
            defaultProbeParam.setOutputFormat(probe.getOutputFormat());
            if (probe.getInterval() != null) {
                defaultProbeParam.setInterval(probe.getInterval());
            } else {
                defaultProbeParam.setInterval(wazuhProbeConfig.getDefaultInterval());
            }
            defaultProbeParams.add(defaultProbeParam);
        });
        return defaultProbeParams;
    }

}
