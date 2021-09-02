package cn.cyberpeace.mp.sceneplugincloud.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mp.sceneplugincloud")
@Data
public class CloudAppConfig {

    @Value("${mp.sceneplugincloud.cpcloud_server:http://127.0.0.1:10009}")
    private String cpcloudServer;

    @Value("${mp.sceneplugincloud.cpcloud_server_timeout:600")
    private Integer cpcloudServerTimeout;

}
