package cn.cyberpeace.mp.frontbusiness.config;

import cn.cyberpeace.mp.auth.extensions.security.interceptor.UrlAcInterceptor;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/10 21:15
 */
@Configuration
public class FrontBusinessWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private FrontBusinessConfig frontBusinessConfig;
    @Autowired
    private OperateUserPermissionService operateUserPermissionService;
    @Autowired
    private UserPermissionService userPermissionService;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UrlAcInterceptor(frontBusinessConfig, operateUserPermissionService, userPermissionService));
    }
}
