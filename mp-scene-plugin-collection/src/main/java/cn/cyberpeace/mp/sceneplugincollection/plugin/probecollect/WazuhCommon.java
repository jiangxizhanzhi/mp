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
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.ContainerFileUtils;
import cn.cyberpeace.mp.sceneplugincollection.config.WazuhProbeConfig;
import cn.cyberpeace.mp.sceneplugincollection.constant.CollectionSingleton;
import cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh.WazuhControl;
import cn.hutool.core.collection.CollectionUtil;
import org.apache.commons.collections4.CollectionUtils;
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

    public static final String BIZ_SCRIPTS_GROUP = "biz_scripts_installer";

    public static final String WAZUH_AGENT_WINDOWS_FILE_NAME = "agent-windows.zip";

    public static final String WAZUH_AGENT_LINUX_FILE_NAME = "agent-linux.zip";

    public static final String LINUX_DOWNLOAD_AGENT_FILE = "download_agent.sh";

    public static final String WAZUH_AGENT_LINUX_FILE_PREFIX = "agent-linux";

    public static final String WINDOWS_INSTALL_DIR = "C:\\ossec-agent";
    public static final String WINDOWS_WAZUHA_DIR = "C:\\wazuha";
    public static final String WINDOWS_STOP_SCRIPT = WINDOWS_WAZUHA_DIR + "\\stop.bat";
    public static final String WINDOWS_UNINSTALL_SCRIPT = WINDOWS_WAZUHA_DIR + "\\uninstall.bat";

    public static final String LINUX_INSTALL_DIR = "/var/ossec";
    public static final String LINUX_WAZUHA_DIR = "/opt/wazuha";
    public static final String LINUX_STOP_SCRIPT = LINUX_WAZUHA_DIR + "/stop.sh";
    public static final String LINUX_UNINSTALL_SCRIPT = LINUX_WAZUHA_DIR + "/uninstall.sh";

    public static String agentLinuxLocalFilepath = null;

    public static String getAgentLinuxLocalFilepath() {
        if (!StringUtils.isEmpty(agentLinuxLocalFilepath)) {
            return agentLinuxLocalFilepath;
        }

        LocalFileMappingModel mapping = LocalFileMappingCommon.getMapping(WAZUH_AGENT_GROUP, WAZUH_AGENT_LINUX_FILE_NAME);
        agentLinuxLocalFilepath = mapping != null ? mapping.getLocalFile() : null;
        return agentLinuxLocalFilepath;
    }

    private WazuhCommon() {}

    public static List<String> generateWazuhAgentInstallScript(String systemType, String agentName, String wazuhHost,
                                                               WazuhControl.ControlConfigParam controlParam, boolean isContainer) {
        if (wazuhProbeConfig == null) {
            wazuhProbeConfig = BeanCommon.getBean(WazuhProbeConfig.class);
        }
        agentName = StringUtils.isEmpty(agentName) ? TextUtils.uuid() : agentName;
        List<LocalFileMappingModel> mappings = LocalFileMappingCommon.getMappings(WAZUH_AGENT_GROUP);
        Map<String, String> nameFileMap = mappings.stream().collect(Collectors.toMap(BaseObjMappingModel::getName,
                LocalFileMappingModel::getRemoteFile));

        String osType = SystemTypeCommandUtils.getOsType(systemType);
        String metadataFileServer = SceneSingleton.sceneAppConfig.getMetadataFileServer();
        List<WazuhControl.ControlProbeParam> controlProbeParams = new ArrayList<>(controlParam.getProbeParams());
        List<WazuhControl.ControlProbeParam> defaultProbeParams;
        defaultProbeParams = getDefaultProbeParams(nameFileMap, metadataFileServer, agentName, wazuhProbeConfig);
        controlProbeParams.addAll(defaultProbeParams);
        controlParam.setProbeParams(controlProbeParams);
        String probeCommand = WazuhControl.genControlConfigCommand(osType, controlParam);

        String windowsInstallDir = SceneSingleton.sceneAppConfig.getWindowsInstallerDir();
        String linuxInstallDir = SceneSingleton.sceneAppConfig.getLinuxInstallerDir();
        InstallerUtils.Installer resource = null;
        if (SystemTypeCommandUtils.isWindows(systemType)) {
            resource = new InstallerUtils.Installer();
            resource.setFile(nameFileMap.getOrDefault(WAZUH_AGENT_WINDOWS_FILE_NAME, StringUtils.EMPTY));
            resource.setInstallCommand(
                    StringUtils.joinWith(CommandUtils.WINDOWS_LINE_BREAK,
                            "call c:\\wazuha\\uninstall.bat",
                            String.format("set SIC_WAZUH_MANAGER_IP=%s", wazuhHost),
                            String.format("set SIC_WAZUH_AGENT_NAME=%s", agentName),
                            String.format("set SIC_WAZUH_MANAGER_REMOTE_PORT=%d", getWazuhRemotePort()),
                            String.format("set SIC_WAZUH_MANAGER_AUTH_PORT=%d", getWazuhAuthPort()),
                            String.format("unzip -q %s -d %s\\agent-installer", InstallerUtils.INSTALLER_REPLACEMENT,
                                    windowsInstallDir),
                            String.format("cd %s\\agent-installer\\agent-windows", windowsInstallDir),
                            "call install.bat",
                            String.format("cd %s", windowsInstallDir),
                            "rd/s/q agent-installer",
                            "del/f/s/q " + InstallerUtils.INSTALLER_REPLACEMENT,
                            "cd C:\\wazuha",
                            "call config.bat",
                            "call " + probeCommand,
                            "call start.bat"
                    ));
        } else if (SystemTypeCommandUtils.isLinux(systemType)) {
            resource = new InstallerUtils.Installer();
            resource.setFile(StringUtils.EMPTY);
            resource.setInstallCommand(
                    StringUtils.joinWith(CommandUtils.LINUX_LINE_BREAK,
                            "sh /opt/wazuha/stop.sh",
                            "sh /opt/wazuha/uninstall.sh",
                            String.format("export SIC_WAZUH_MANAGER_IP=%s SIC_WAZUH_AGENT_NAME=%s " +
                                            "SIC_WAZUH_MANAGER_REMOTE_PORT=%d SIC_WAZUH_MANAGER_AUTH_PORT=%d",
                                    wazuhHost, agentName, getWazuhRemotePort(), getWazuhAuthPort()),
                            String.format("unzip -o -q %s/%s -d %s/agent-installer", linuxInstallDir,
                                    WAZUH_AGENT_LINUX_FILE_NAME, linuxInstallDir),
                            String.format("cd %s/agent-installer/agent-linux", linuxInstallDir),
                            "sh install.sh",
                            String.format("cd %s", linuxInstallDir),
                            "rm -rf agent-installer",

                            "cd /opt/wazuha",
                            "sh config.sh",
                            probeCommand,
                            "sleep 10",
                            "sh start.sh"
                    ));
        }
        List<String> result = resource == null ? new ArrayList<>() :
                InstallerUtils.generateInstallerInstallCommand(systemType, List.of(resource));
        if (SystemTypeCommandUtils.isLinux(systemType)) {
            String addCommand;
            if (isContainer) {
                
                addCommand = String.format("%s %s %s", ContainerFileUtils.CONTAINER_COPY_FILE_PREFIX,
                        getAgentLinuxLocalFilepath(), linuxInstallDir);
            } else {
                
                String downloadUrl = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer,
                        nameFileMap.getOrDefault(LINUX_DOWNLOAD_AGENT_FILE, StringUtils.EMPTY));
                String downloadPath = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, linuxInstallDir,
                        LINUX_DOWNLOAD_AGENT_FILE);
                
                addCommand = StringUtils.joinWith(CommandUtils.LINUX_LINE_BREAK,
                        String.format(InstallerUtils.LINUX_FILE_DOWNLOAD, downloadPath, downloadUrl),
                        String.format("sh %s/%s ", linuxInstallDir, LINUX_DOWNLOAD_AGENT_FILE));
                
                String filePathParam = String.format("filepath=%s/%s", linuxInstallDir,
                        WAZUH_AGENT_LINUX_FILE_NAME);
                List<String> params = new ArrayList<>(CollectionUtil.toList(filePathParam));
                nameFileMap.forEach((name, url) -> {
                    if (name.startsWith(WAZUH_AGENT_LINUX_FILE_PREFIX)) {
                        String remoteUrl = String.format("%s/%s", metadataFileServer, url);
                        String param = String.format("%s=%s", name, remoteUrl);
                        params.add(param);
                    }
                });
                addCommand = addCommand.concat(StringUtils.joinWith(StringUtils.SPACE, params.toArray()));
            }
            
            int addCommandIndex = 0;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).contains("mkdir") && result.get(i).contains(linuxInstallDir)) {
                    addCommandIndex = i + 1;
                    break;
                }
            }
            result.add(addCommandIndex, addCommand);
        }
        return result;
    }

    public static void uninstallAgent(String agentName, String systemType) {
        
    }

    
    private static List<WazuhControl.ControlProbeParam> getDefaultProbeParams(Map<String, String> nameFileMap,
                                                                              String metadataFileServer, String agentName,
                                                                              WazuhProbeConfig wazuhProbeConfig) {
        if (wazuhProbeConfig == null || wazuhProbeConfig.getDefaultProbe() == null || CollectionUtils.isEmpty(wazuhProbeConfig.getDefaultProbe().getProbes())) {
            return new ArrayList<>();
        }
        List<WazuhControl.ControlProbeParam> defaultProbeParams = new ArrayList<>();
        wazuhProbeConfig.getDefaultProbe().getProbes().forEach(probe -> {
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
                defaultProbeParam.setInterval(wazuhProbeConfig.getDefaultProbe().getDefaultInterval());
            }
            defaultProbeParams.add(defaultProbeParam);
        });
        return defaultProbeParams;
    }

}
