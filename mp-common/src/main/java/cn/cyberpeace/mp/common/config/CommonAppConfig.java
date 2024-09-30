package cn.cyberpeace.mp.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp.common")
@Data
public class CommonAppConfig {
    @Value("${mp.common.mp-ip:10.100.7.1}")
    private String mpIp;

    @Value("${mp.common.mp-port:8001}")
    private String mpPort;
}
