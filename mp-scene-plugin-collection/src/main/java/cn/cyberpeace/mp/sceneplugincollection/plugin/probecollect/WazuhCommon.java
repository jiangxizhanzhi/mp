package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WazuhCommon {

    private static Integer getWazuhRemotePort(){
        return CollectionSingleton.collectionAppConfig.getWazuhRemotePort();
    }

    private static Integer getWazuhAuthPort(){
        return CollectionSingleton.collectionAppConfig.getWazuhAuthPort();
    }

    public static final String WAZUH_AGENT_GROUP = "wazuh_agent_installer";

    public static final String WAZUH_AGENT_WINDOWS_FILE_NAME = "agent-windows.zip";

    public static final String WAZUH_AGENT_LINUX_FILE_NAME = "agent-linux.zip";

    public static final String WAZUH_PROBE_NAME = "get_virtual_info.py";

    public static final String INTERVAL = "180s";

    private WazuhCommon() {}

    public static String generateWazuhAgentInstallScript(String systemType, String agentName, String wazuhHost) {
        agentName = StringUtils.isEmpty(agentName) ? TextUtils.uuid() : agentName;
        List<LocalFileMappingModel> mappings = LocalFileMappingCommon.getMappings(WAZUH_AGENT_GROUP);
        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));

        String osType = SystemTypeCommandUtils.getOsType(systemType);
        String metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        String url = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer,
                nameFileMap.getOrDefault(WAZUH_PROBE_NAME, StringUtils.EMPTY));
        WazuhControl.ControlProbeParam controlProbeParam = new WazuhControl.ControlProbeParam();
        controlProbeParam.setId(TextUtils.uuid());
        controlProbeParam.setAction(WazuhControl.CONTROL_ACTION_ADD);
        controlProbeParam.setGroup(agentName);
        controlProbeParam.setScriptUrl(url);
        controlProbeParam.setScriptSuffix(WazuhControl.SCRIPT_SUFFIX_PY);
        controlProbeParam.setOutputFormat(WazuhControl.CMD_OUTPUT_FORMAT_PLAIN);
        controlProbeParam.setInterval(INTERVAL);
        List<WazuhControl.ControlProbeParam> controlProbeParams = new ArrayList<>();
        controlProbeParams.add(controlProbeParam);
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
                            "install.bat",
                            String.format("set SIC_WAZUH_MANAGER_IP=%s", wazuhHost),
                            String.format("set SIC_WAZUH_AGENT_NAME=%s", agentName),
                            String.format("set SIC_WAZUH_MANAGER_REMOTE_PORT=%d", getWazuhRemotePort()),
                            String.format("set SIC_WAZUH_MANAGER_AUTH_PORT=%d", getWazuhAuthPort()),
                            "cd C:\\wazuha",
                            "config.bat",
                            probeCommand,
                            "start.bat"
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
        return resource == null ? StringUtils.EMPTY :
                InstallerUtils.generateInstallerInstallCommand(systemType, List.of(resource));
    }

}
