package cn.cyberpeace.mp.situationaware.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SituationAwareConfig {

    @Value("${mp.situationaware.group-url}")
    private String groupUrl;

    @Value("${mp.situationaware.moloch.username}")
    private String userName;

    @Value("${mp.situationaware.moloch.password}")
    private String password;

    @Value("${mp.situationaware.wazuh-probe-manager.url}")
    private String wazuhManagerUrl;

    @Value("${mp.situationaware.wazuh-probe-manager.agent-script-linux-path}")
    private String scriptLinuxPath;

    @Value("${mp.situationaware.wazuh-probe-manager.agent-script-windows-path}")
    private String scripWindowsPath;

    @Value("${mp.situationaware.wazuh-probe-manager.task-enable}")
    private Boolean taskEnable;
}
