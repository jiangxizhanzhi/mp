package cn.cyberpeace.mp.auth.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
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

    private Integer cacheTime;

    private Boolean useIndividualTokenSecret;

    private String jwtSecret;

    private Long jwtExpires;

    // 需要认证的url
    private List<String> urls;

    private Encoder encoder;


    @Data
    public static class Encoder {
        private Integer iterations;

        private Integer hashWidth;
    }

}
