package cn.cyberpeace.mp.auth.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@Data
@ConfigurationProperties(prefix = "mp.auth.logout")
public class AuthLogoutConfig {

    private Boolean resetSecret = false;

}
