package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.sceneplugincollection")
@Data
public class CollectionAppConfig {

    @Value("${mp.sceneplugincollection.create_pool_size:10}")
    private Integer initiatePoolSize;

    @Value("${mp.sceneplugincollection.create_hold_size:200000}")
    private Integer initiateHoldSize;

    @Value("${mp.sceneplugincollection.create_pool_size:10}")
    private Integer createPoolSize;

    @Value("${mp.sceneplugincollection.create_hold_size:200000}")
    private Integer createHoldSize;

    @Value("${mp.sceneplugincollection.delete_pool_size:10}")
    private Integer deletePoolSize;

    @Value("${mp.sceneplugincollection.delete_hold_size:200000}")
    private Integer deleteHoldSize;

    @Value("${mp.sceneplugincollection.wazuh_server_host:127.0.0.1}")
    private String wazuhServerHost;

    @Value("${mp.sceneplugincollection.azurePublic.wazuh-server-host:127.0.0.1}")
    private String wazuhPublicServerHost;

    @Value("${mp.sceneplugincollection.azurePrivate.wazuh-server-host:127.0.0.1}")
    private String wazuhPrivateServerHost;

    @Value("${mp.sceneplugincollection.azureMixed.wazuh-server-host:127.0.0.1}")
    private String wazuhMixedServerHost;

    @Value("${mp.sceneplugincollection.wazuh_remote_port:8514}")
    private Integer wazuhRemotePort;

    @Value("${mp.sceneplugincollection.azurePublic.wazuh-remote-port:9200}")
    private Integer wazuhPublicRemotePort;

    @Value("${mp.sceneplugincollection.azurePrivate.wazuh-remote-port:9200}")
    private Integer wazuhPrivateRemotePort;

    @Value("${mp.sceneplugincollection.azureMixed.wazuh-remote-port:9200}")
    private Integer wazuhMixedRemotePort;

    @Value("${mp.sceneplugincollection.wazuh_auth_port:8515}")
    private Integer wazuhAuthPort;

    @Value("${mp.sceneplugincollection.azurePublic.wazuh_auth_port:9200}")
    private Integer wazuhPublicAuthPort;

    @Value("${mp.sceneplugincollection.azurePrivate.wazuh_auth_port:9200}")
    private Integer wazuhPrivateAuthPort;

    @Value("${mp.sceneplugincollection.azureMixed.wazuh_auth_port:9200}")
    private Integer wazuhMixedAuthPort;

    @Value("${mp.sceneplugincollection.wazuh_vulnerability_scan_interval:6}")
    private Integer wazuhVulnerabilityScanInterval;

    @Value("${mp.sceneplugincollection.wazuh_agent_installer_dir:}")
    private String wazuhAgentInstallerDir;

    @Value("${mp.sceneplugincollection.biz_scripts_installer_dir:}")
    private String bizScriptsInstallerDir;

    @Value("${mp.sceneplugincollection.wazuh_api_server:http://127.0.0.1:55000}")
    private String wazuhApiServer;

    @Value("${mp.sceneplugincollection.public_wazuh_api_server:http://20.205.18.81:15500}")
    private String publicWazuhApiServer;

    @Value("${mp.sceneplugincollection.wazuh_api_user:wazuh}")
    private String wazuhApiUser;

    @Value("${mp.sceneplugincollection.wazuh_api_pass:wazuh}")
    private String wazuhApiPass;

    @Value("${mp.sceneplugincollection.wazuh_mongo_server:}")
    private String wazuhMongoServer;

    @Value("${mp.sceneplugincollection.wazuh_es_server:}")
    private String wazuhEsServer;

    @Value("${mp.sceneplugincollection.wazuh_godfs_server:http://127.0.0.1:1888}")
    private String wazuhGodfsServer;

    @Value("${mp.sceneplugincollection.wazuh-godfs-outer-http-server:/mp_scene_collection_godfs}")
    private String wazuhGodfsOuterHttpServer;

    @Value("${mp.sceneplugincollection.wazuh_run_command_http_callback_url:}")
    private String wazuhRunCommandHttpCallbackUrl;

    @Value("${mp.sceneplugincollection.enable_flow_collect:false}")
    private Boolean enableFlowCollect;

    @Value("${mp.sceneplugincollection.enable_probe_collect:false}")
    private Boolean enableProbeCollect;

    @Value("${mp.sceneplugincollection.collect_interval:180s}")
    private String collectInterval;

    @Value("${mp.sceneplugincollection.syscheck_frequency:60}")
    private String syscheckFrequency;

    @Value("${mp.sceneplugincollection.syscollector_interval:15m}")
    private String syscollectorInterval;

    @Value("${mp.sceneplugincollection.recursion_level:3}")
    private Integer fileRecursionLevel;

    @Value("${mp.sceneplugincollection.recursion_level:5}")
    private Integer registryRecursionLevel;

    @Value("${mp.sceneplugincollection.default.hours-range:24}")
    private Integer hoursRange;

    @Autowired
    private BaseAppConfig baseAppConfig;

    public String getWazuhAgentInstallerDir() {
        if (!StringUtils.isEmpty(wazuhAgentInstallerDir)) {
            return wazuhAgentInstallerDir;
        }

        wazuhAgentInstallerDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR,
                baseAppConfig.getResourcesDir(), "scene_plugin_collection/wazuh_agent");
        return wazuhAgentInstallerDir;
    }


    public String getBizScriptsInstallerDir() {
        if (!StringUtils.isEmpty(bizScriptsInstallerDir)) {
            return bizScriptsInstallerDir;
        }

        bizScriptsInstallerDir = StringUtils.joinWith(FileUtil.FILE_SEPARATOR,
                baseAppConfig.getResourcesDir(), "scene_plugin_collection/biz_scripts");
        return bizScriptsInstallerDir;
    }
}
