package cn.cyberpeace.mp.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.auth")
public class AuthConfig {
    /**
     * biz
     * 退出登录后是否重置tokenSecret
     */
    private Boolean logoutResetSecret = true;
    /**
     * 最大可通过excel导入的用户数量
     */
    private Integer maxImportUserSize;
    /**
     * 默认头像
     */
    private String defaultUserAvatar;

    private Integer authenticateCacheTime;

    private Boolean authenticateUseIndividualTokenSecret;

    private String authenticateJwtSecret;

    private Long authenticateJwtExpires;

    // 需要认证的url
    private List<String> authenticateUrls;

    private Integer authenticateEncoderIterations;
    private Integer authenticateEncoderHashWidth;
}
