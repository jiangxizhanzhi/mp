 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBusinessWebMvcConfig.java.
 // File size 1325.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.config;

import cn.cyberpeace.mp.auth.extensions.security.interceptor.UrlAcInterceptor;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName FrontBusinessWebMvcConfig
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/10 21:15
 * @Version 1.0.0
 * @see WebMvcConfigurer
 */
@Configuration
public class FrontBusinessWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FrontBusinessConfig frontBusinessConfig;
    @Autowired
    private OperateUserPermissionService operateUserPermissionService;
    @Autowired
    private UserPermissionService userPermissionService;


    /**
     * addInterceptors
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UrlAcInterceptor(frontBusinessConfig, operateUserPermissionService, userPermissionService));
    }
}
