package cn.cyberpeace.mp.auth.config;


import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "mp.auth")
public class AuthConfig {

    public static String DEFAULT_ACTIVATION_DIR = "/Users/sternberg/tmp/0811";

    private String activationDir = DEFAULT_ACTIVATION_DIR;

}
