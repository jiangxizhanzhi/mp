// Copyright (C) 2024
// All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AuthConfig.java.
 // File size 1671.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 身份验证配置
 *
 * @author 二竹
 * @date 2025/01/17
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

    private String mpIp;

    private String thirdIp;

    private String thirdPort;

    private String thirdClientId;

    private String thirdClientSecret;

    private String thirdTokenUrl;

    private String thirdUserInfoUrl;

    private String thirdCheckUrl;

    private String thirdGetUserInfoByTokenUrlV2;

    private String thirdLogoutUrlV2;

    private String homePageV2;

    private String thirdLoginPageV2;

    private String systemPage1;
    private String systemPage2;
    private String systemPage3;
}
