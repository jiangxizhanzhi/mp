 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthConfig.java.
 // File size 1372.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName AuthConfig
 * @Description
 * @Author shgang
 * @Date 2022/8/31
 * @Version 1.0.0
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

    private String logoutKeepSecretUser;

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

    // 不需要认证的url
    private List<String> authenticateExcludeUrls;

    // encoder循环次数
    private Integer authenticateEncoderIterations;

    // encoder hash长度
    private Integer authenticateEncoderHashWidth;

    // mq消息过期时间
    private Long mqMsgTimeout;
}
