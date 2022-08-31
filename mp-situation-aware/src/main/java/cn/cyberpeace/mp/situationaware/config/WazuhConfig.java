package cn.cyberpeace.mp.situationaware.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.situationaware.wazuh")
public class WazuhConfig {

    /**
     * wazuh 开放接口地址
     */
    private String baseUrl;
    /**
     * wazuh 开放接口用户名
     */
    private String username;
    /**
     * wazuh 开放接口密码
     */
    private String password;
    private String flowTableUrl;
    private ProbeManager probeManager;

    @Data
    public static class ProbeManager {
        private String url;
        private String agentScriptLinuxPath;
        private String agentScriptWindowsPath;
        private String taskEnable;
    }
}
