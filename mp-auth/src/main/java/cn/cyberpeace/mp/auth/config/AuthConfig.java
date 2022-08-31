package cn.cyberpeace.mp.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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
}
