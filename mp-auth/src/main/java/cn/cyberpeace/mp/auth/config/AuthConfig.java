package cn.cyberpeace.mp.auth.config;


import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "mp.auth")
public class AuthConfig {

    public static String DEFAULT_ACTIVATION_FILE = "/Users/sternberg/tmp/0811/activation";

    private String activationFile = DEFAULT_ACTIVATION_FILE;

}
