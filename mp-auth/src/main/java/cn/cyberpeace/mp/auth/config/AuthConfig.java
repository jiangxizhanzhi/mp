package cn.cyberpeace.mp.auth.config;


import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Component
@Data
@ConfigurationProperties(prefix = "mp.auth")
public class AuthConfig {

    public static String DEFAULT_ACTIVATION_DIR = "/home/mp/config/license";

    private String activationDir = DEFAULT_ACTIVATION_DIR;

}
