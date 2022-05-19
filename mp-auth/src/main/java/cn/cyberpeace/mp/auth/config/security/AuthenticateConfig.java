package cn.cyberpeace.mp.auth.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 20:14
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.auth.authenticate")
public class AuthenticateConfig {

    // 需要认证的url
    private List<String> urls;

}
