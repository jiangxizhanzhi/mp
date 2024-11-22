package cn.cyberpeace.mp.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mp.thirdsso")
public class ThirdSsoConfig {
    private String clientId;
    private String responseType;
    private String redirectUri;
    private String pageUrl;
    private String clientSecret;
    private String getTokenUrl;
    private String getUserUrl;
    private String getCodeUrl;
}