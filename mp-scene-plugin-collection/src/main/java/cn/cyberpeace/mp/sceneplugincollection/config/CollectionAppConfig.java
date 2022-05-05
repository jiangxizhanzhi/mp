package cn.cyberpeace.mp.sceneplugincollection.config;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.hutool.core.io.FileUtil;
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

    @Value("${mp.sceneplugincollection.wazuh_remote_port:8514}")
    private Integer wazuhRemotePort;

    @Value("${mp.sceneplugincollection.wazuh_auth_port:8515}")
    private Integer wazuhAuthPort;

    @Value("${mp.sceneplugincollection.wazuh_agent_installer_dir:}")
    private String wazuhAgentInstallerDir;

    @Value("${mp.sceneplugincollection.wazuh_api_server:http://127.0.0.1:55000}")
    private String wazuhApiServer;

    @Value("${mp.sceneplugincollection.wazuh_api_user:wazuh}")
    private String wazuhApiUser;

    @Value("${mp.sceneplugincollection.wazuh_api_pass:wazuh}")
    private String wazuhApiPass;

    @Value("${mp.sceneplugincollection.wazuh_es_server:http://127.0.0.1:9200}")
    private String wazuhEsServer;

    @Value("${mp.sceneplugincollection.wazuh_godfs_server:http://127.0.0.1:1888}")
    private String wazuhGodfsServer;

    @Value("${mp.sceneplugincollection.wazuh_run_command_http_callback_url:}")
    private String wazuhRunCommandHttpCallbackUrl;

    @Value("${mp.sceneplugincollection.enable_flow_collect:false}")
    private Boolean enableFlowCollect;

    @Value("${mp.sceneplugincollection.enable_probe_collect:false}")
    private Boolean enableProbeCollect;

    @Value("${mp.sceneplugincollection.collect_interval:180s}")
    private String collectInterval;

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
}
