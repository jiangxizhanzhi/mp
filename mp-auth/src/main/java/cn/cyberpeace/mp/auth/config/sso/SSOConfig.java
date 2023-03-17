package cn.cyberpeace.mp.auth.config.sso;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/3/16 20:53
 */
@Component
@Data
@ConfigurationProperties(prefix = "mp.auth.sso")
public class SSOConfig {

    private String configFile;

}
