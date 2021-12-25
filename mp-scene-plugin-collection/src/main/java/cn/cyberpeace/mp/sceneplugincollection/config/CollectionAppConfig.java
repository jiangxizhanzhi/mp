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

    @Value("${mp.sceneplugincollection.wazuh_agent_installer_dir:}")
    private String wazuhAgentInstallerDir;

    @Value("${mp.sceneplugincollection.enable_flow_collect:true}")
    private Boolean enableFlowCollect;

    @Value("${mp.sceneplugincollection.enable_probe_collect:true}")
    private Boolean enableProbeCollect;

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
