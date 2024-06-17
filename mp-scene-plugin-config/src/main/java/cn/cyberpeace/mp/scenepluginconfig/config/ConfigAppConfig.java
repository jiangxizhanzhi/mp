package cn.cyberpeace.mp.scenepluginconfig.config;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDevice;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mp.scenepluginconfig")
@Data
public class ConfigAppConfig {

    @Value("${mp.scenepluginconfig.create_pool_size:10}")
    private Integer createPoolSize;

    @Value("${mp.scenepluginconfig.create_hold_size:200000}")
    private Integer createHoldSize;

    @Value("${mp.scenepluginconfig.delete_pool_size:10}")
    private Integer deletePoolSize;

    @Value("${mp.scenepluginconfig.delete_hold_size:200000}")
    private Integer deleteHoldSize;

    @Value("${mp.scenepluginconfig.config_agent_name:agent_server}")
    private String configAgentName;

    @Value("${mp.scenepluginconfig.frame_host:http://172.30.102.19:8003}")
    private String frameHost;

    @Value("${mp.scenepluginconfig.time_sync_ip:172.29.104.114}")
    private String timeSyncIp;

    @Value("${mp.scenepluginconfig.config_agent_ram:4}")
    private Integer configAgentRam;

    @Value("${mp.scenepluginconfig.enable_config_create:true}")
    private Boolean enableConfigCreate;

    public String getConfigAgentTemplateId() {
        String configAgentTemplateId;
        try {
            configAgentTemplateId = new VirtualDevice().getTemplateId(configAgentName).getTemplateId();
        } catch (Exception e) {
            log.error("Try to get config agent template id  failed: ", e);
            throw e;
        }
        return configAgentTemplateId;
    }
}
