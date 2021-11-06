package cn.cyberpeace.mp.front.admin.config;


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
@ConfigurationProperties(prefix = "mp.auth.activation")
public class ActivationConfig {

    public static String DEFAULT_LICENSE_DIR = "/home/mp/config/license";

    private boolean enable = false;

    private String licenseDir = DEFAULT_LICENSE_DIR;

}
