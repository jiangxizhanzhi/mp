package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.model.BaseObjMappingModel;
import cn.cyberpeace.mp.base.model.LocalFileMappingModel;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.constant.SceneSingleton;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.scene.util.CommandUtils;
import cn.cyberpeace.mp.scene.util.InstallerUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WazuhCommon {

    public static final String WAZUH_AGENT_GROUP = "wazuh_agent_installer";

    public static final String WAZUH_AGENT_WINDOWS_FILE_NAME = "agent-windows-installer.zip";

    public static final String WAZUH_AGENT_UBUNTU_FILE_NAME = "agent-ubuntu-installer.zip";

    public static final String WAZUH_AGENT_CENTOS_FILE_NAME = "agent-centos-installer.zip";

    private WazuhCommon() {}

    public static String generateWazuhAgentInstallScript(String systemType, String agentName, String wazuhHost) {
        agentName = StringUtils.isEmpty(agentName) ? TextUtils.uuid() : agentName;
        List<LocalFileMappingModel> mappings = LocalFileMappingCommon.getMappings(WAZUH_AGENT_GROUP);
        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));

        InstallerUtils.Installer resource = null;
        if (List.of(BaseSceneElementModel.SystemType.WINDOWS_10,
                BaseSceneElementModel.SystemType.WINDOWS_8,
                BaseSceneElementModel.SystemType.WINDOWS_7,
                BaseSceneElementModel.SystemType.WINDOWS_XP,
                BaseSceneElementModel.SystemType.WINDOWS_VISTA,
                BaseSceneElementModel.SystemType.WINDOWS_SERVER_2019,
                BaseSceneElementModel.SystemType.WINDOWS_SERVER_2016,
                BaseSceneElementModel.SystemType.WINDOWS_SERVER_2012,
                BaseSceneElementModel.SystemType.WINDOWS_SERVER_2010,
                BaseSceneElementModel.SystemType.WINDOWS_SERVER_2008,
                BaseSceneElementModel.SystemType.WINDOWS_SERVER_2003,
                BaseSceneElementModel.SystemType.WINDOWS_SERVER_2000,
                BaseSceneElementModel.SystemType.WINDOWS).contains(systemType)) {
            resource = new InstallerUtils.Installer();
            resource.setFile(nameFileMap.getOrDefault(WAZUH_AGENT_WINDOWS_FILE_NAME, StringUtils.EMPTY));
            resource.setInstallCommand(
                    StringUtils.joinWith(CommandUtils.WINDOWS_LINE_BREAK,
                    String.format("unzip {this} -d %s\\agent-installer",
                            SceneSingleton.sceneAppConfig.getWindowsInstallerDir()),
                    String.format("cd %s\\agent-installer\\windows",
                            SceneSingleton.sceneAppConfig.getWindowsInstallerDir()),
                    String.format(".\\wazuh-install.exe --server=%s --agent=%s", wazuhHost, agentName)
            ));
        } else if (List.of(BaseSceneElementModel.SystemType.CENTOS,
                BaseSceneElementModel.SystemType.LINUX).contains(systemType)) {
            resource = new InstallerUtils.Installer();
            resource.setFile(nameFileMap.getOrDefault(WAZUH_AGENT_CENTOS_FILE_NAME, StringUtils.EMPTY));
            resource.setInstallCommand(
                StringUtils.joinWith(CommandUtils.LINUX_LINE_BREAK,
                        String.format("unzip {this} -d %s/agent-installer",
                                SceneSingleton.sceneAppConfig.getLinuxInstallerDir()),
                        String.format("cd %s/agent-installer/centos",
                                SceneSingleton.sceneAppConfig.getLinuxInstallerDir()),
                        String.format("./wazuh-install --server=%s --agent=%s", wazuhHost, agentName)
                ));
        } else if (List.of(BaseSceneElementModel.SystemType.UBUNTU,
                BaseSceneElementModel.SystemType.KALI).contains(systemType)) {
            resource = new InstallerUtils.Installer();
            resource.setFile(nameFileMap.getOrDefault(WAZUH_AGENT_UBUNTU_FILE_NAME, StringUtils.EMPTY));
            resource.setInstallCommand(
                    StringUtils.joinWith(CommandUtils.LINUX_LINE_BREAK,
                            String.format("unzip {this} -d %s/agent-installer",
                                    SceneSingleton.sceneAppConfig.getLinuxInstallerDir()),
                            String.format("cd %s/agent-installer/ubuntu",
                                    SceneSingleton.sceneAppConfig.getLinuxInstallerDir()),
                            String.format("./wazuh-install --server=%s --agent=%s", wazuhHost, agentName)
                    ));
        }
        return resource == null ? StringUtils.EMPTY :
                InstallerUtils.generateInstallerInstallCommand(systemType, List.of(resource));
    }

}
