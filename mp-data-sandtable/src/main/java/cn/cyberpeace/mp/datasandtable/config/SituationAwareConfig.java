package cn.cyberpeace.mp.datasandtable.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class SituationAwareConfig {

    @Value("${mp.datasandtable.group-url}")
    private String groupUrl;

    @Value("${mp.datasandtable.moloch.username}")
    private String userName;

    @Value("${mp.datasandtable.moloch.password}")
    private String password;

    @Value("${mp.datasandtable.wazuh-probe-manager.url}")
    private String wazuhManagerUrl;

    @Value("${mp.datasandtable.wazuh-probe-manager.agent-script-path}")
    private String scriptPath;
}
