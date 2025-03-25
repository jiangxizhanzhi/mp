 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBusinessConfig.java.
 // File size 1198.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.config;

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
@ConfigurationProperties(prefix = "mp.frontbusiness")
public class FrontBusinessConfig {

    private Boolean thirdRoleEnabled;

    /**
     * 拦截器是否开启，默认开启
     */
    private Boolean urlAcFilterEnabled = true;

    /**
     * url请求方法是否拦截，默认开启
     */
    private Boolean urlAcFilterMethodMatch = true;

    /**
     * url权限是否拦截，默认开启
     */
    private Boolean urlAcFilterAuthorityMatch = true;

    /**
     * unauth（未经过认证）的请求是否拦截，默认不跳过
     */
    private Boolean urlAcFilterIgnoreIfUnauth = false;

    /**
     * 如果请求url在规则表中没有定义，是否跳过拦截，默认不跳过
     */
    private Boolean urlAcFilterIgnoreIfNotDefined = false;

    /**
     * 不需要拦截的url
     */
    private List<String> urlAcFilterExcludeUrls;
}
