package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.LocalFileMappingCommon;
import cn.cyberpeace.mp.base.constant.BaseSingleton;
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

    public static final String LINUX_DOWNLOAD_AGENT_FILE = "download_agent.sh";

    public static final String WAZUH_AGENT_LINUX_FILE_PREFIX = "agent-linux";

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
        String fdfsInnerServer = BaseSingleton.baseAppConfig.getFdfsInnerHttpServer();
        List<WazuhControl.ControlProbeParam> controlProbeParams = new ArrayList<>(controlParam.getProbeParams());
        List<WazuhControl.ControlProbeParam> defaultProbeParams;
        if (isContainer) {
            defaultProbeParams = getDefaultProbeParams(nameFileMap, fdfsInnerServer, agentName, wazuhProbeConfig);
        } else {
            defaultProbeParams = getDefaultProbeParams(nameFileMap, metadataFileServer, agentName, wazuhProbeConfig);
        }
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
                            String.format("unzip " + InstallerUtils.INSTALLER_REPLACEMENT + " -d %s\\agent-installer",
                                    windowsInstallDir),
                            String.format("cd %s\\agent-installer\\agent-windows", windowsInstallDir),
                            "call install.bat",
                            String.format("cd %s", windowsInstallDir),
                            "rd/s/q agent-installer",
                            "del/f/s/q " + InstallerUtils.INSTALLER_REPLACEMENT,
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
            resource.setFile(StringUtils.EMPTY);
            resource.setInstallCommand(
                    StringUtils.joinWith(CommandUtils.LINUX_LINE_BREAK,
                            String.format("unzip %s" + InstallerUtils.FILE_URL_SEPARATOR + WAZUH_AGENT_LINUX_FILE_NAME +
                                    " -d %s/agent-installer", linuxInstallDir, linuxInstallDir),
                            String.format("cd %s/agent-installer/agent-linux", linuxInstallDir),
                            "bash install.sh",
                            String.format("cd %s", linuxInstallDir),
                            "rm -rf agent-installer",
                            "rm -rf " + WAZUH_AGENT_LINUX_FILE_NAME,
                            String.format("export SIC_WAZUH_MANAGER_IP=%s SIC_WAZUH_AGENT_NAME=%s " +
                                            "SIC_WAZUH_MANAGER_REMOTE_PORT=%d SIC_WAZUH_MANAGER_AUTH_PORT=%d",
                                    wazuhHost, agentName, getWazuhRemotePort(), getWazuhAuthPort()),
                            "cd /opt/wazuha",
                            "bash config.sh",
                            probeCommand,
                            "bash start.sh"
                    ));
        }
        List<String> result = resource == null ? new ArrayList<>() :
                InstallerUtils.generateInstallerInstallCommand(systemType, List.of(resource));
        //容器不支持curl命令，需要添加一条复制wazuh-agent安装包文件的命令
        if (isContainer) {
            //将文件复制的命令加到创建目录的命令之后
            int copyFileIndex = 0;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).contains("mkdir") && result.get(i).contains(linuxInstallDir)) {
                    copyFileIndex = i + 1;
                    break;
                }
            }
            result.add(copyFileIndex, String.format(ContainerFileUtils.CONTAINER_COPY_FILE_PREFIX + " %s %s",
                    getAgentLinuxLocalFilepath(), linuxInstallDir));
        } else if (SystemTypeCommandUtils.isLinux(systemType)) {
            //Linux需要按需下载agent安装包
            String downloadUrl = StringUtils.joinWith(InstallerUtils.FILE_URL_SEPARATOR, metadataFileServer,
                    nameFileMap.getOrDefault(LINUX_DOWNLOAD_AGENT_FILE, StringUtils.EMPTY));
            //下载按需安装脚本并执行
            String downloadCommand = StringUtils.joinWith(CommandUtils.LINUX_LINE_BREAK,
                    String.format(InstallerUtils.LINUX_FILE_DOWNLOAD, linuxInstallDir + InstallerUtils.FILE_URL_SEPARATOR
                            + LINUX_DOWNLOAD_AGENT_FILE, downloadUrl),
                    String.format("bash %s/%s", linuxInstallDir, LINUX_DOWNLOAD_AGENT_FILE));
            //给执行的下载脚本添加参数
            String filePathParam = String.format(StringUtils.SPACE + "filepath=%s/%s", linuxInstallDir,
                    WAZUH_AGENT_LINUX_FILE_NAME);
            downloadCommand = downloadCommand.concat(filePathParam);
            List<String> linuxFiles = new ArrayList<>();
            nameFileMap.forEach((name, url) -> {
                if (name.startsWith(WAZUH_AGENT_LINUX_FILE_PREFIX)) {
                    linuxFiles.add(name);
                }
            });
            for (String file : linuxFiles) {
                String param = StringUtils.SPACE + file + "=" + metadataFileServer + InstallerUtils.FILE_URL_SEPARATOR
                        + nameFileMap.get(file);
                downloadCommand = downloadCommand.concat(param);
            }
            //将文件下载的命令放在创建目录之后
            int downloadFileIndex = 0;
            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).contains("mkdir") && result.get(i).contains(linuxInstallDir)) {
                    downloadFileIndex = i + 1;
                    break;
                }
            }
            result.add(downloadFileIndex, downloadCommand);
        }
        return result;
    }

    /**
     * 获取默认脚本探针参数集合
     *
     * @param nameFileMap        文件名Map
     * @param metadataFileServer 文件下载前缀
     * @param agentName
     * @param wazuhProbeConfig   默认脚本探针配置
     * @return 默认脚本探针参数集合
     */
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
