package cn.cyberpeace.mp.behavior.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mp.behavior")
public class BehaviorConfig {

    @Value("${mp.behavior.mp-server-30:http://127.0.0.1:8083}")
    private String mpServer30;
}
