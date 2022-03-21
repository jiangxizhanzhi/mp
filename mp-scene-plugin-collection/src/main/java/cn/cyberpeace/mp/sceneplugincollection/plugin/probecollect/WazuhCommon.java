package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WazuhCommon {

    public static final String WAZUH_AGENT_GROUP = "wazuh_agent_installer";

    public static final String WAZUH_AGENT_WINDOWS_FILE_NAME = "agent-windows.zip";

    public static final String WAZUH_AGENT_LINUX_FILE_NAME = "agent-linux.zip";

    public static final int WAZUH_DEFAULT_REMOTE_PORT = 1514;

    public static final int WAZUH_DEFAULT_AUTH_PORT = 1515;

    private WazuhCommon() {}

    public static String generateWazuhAgentInstallScript(String systemType, String agentName, String wazuhHost) {
        agentName = StringUtils.isEmpty(agentName) ? TextUtils.uuid() : agentName;
        List<LocalFileMappingModel> mappings = LocalFileMappingCommon.getMappings(WAZUH_AGENT_GROUP);
        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));

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
                            String.format("set SIC_WAZUH_MANAGER_REMOTE_PORT=%d", WAZUH_DEFAULT_REMOTE_PORT),
                            String.format("set SIC_WAZUH_MANAGER_AUTH_PORT=%d", WAZUH_DEFAULT_AUTH_PORT),
                            "cd C:\\wazuha",
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
                                    wazuhHost, agentName, WAZUH_DEFAULT_REMOTE_PORT, WAZUH_DEFAULT_AUTH_PORT),
                            "cd /opt/wazuha",
                            "bash start.sh"
                    ));
        }
        return resource == null ? StringUtils.EMPTY :
                InstallerUtils.generateInstallerInstallCommand(systemType, List.of(resource));
    }

}
