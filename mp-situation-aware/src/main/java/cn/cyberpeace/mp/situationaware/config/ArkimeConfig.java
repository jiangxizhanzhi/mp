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
@ConfigurationProperties(prefix = "mp.situationaware.arkime")
public class ArkimeConfig {

    private String cpcloudContainerCreateUrl;

    private String cpcloudNetworkMirrorUrl;

    private String esHost;

    private String esPort;

    private String imageName;

    private String username;

    private String password;
}
