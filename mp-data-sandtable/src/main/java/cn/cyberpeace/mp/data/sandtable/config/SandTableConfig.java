package cn.cyberpeace.mp.data.sandtable.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp.scene")
@Data
public class SandTableConfig {

    @Value("data.sandTable.groupUrl")
    private String groupUrl;
}
