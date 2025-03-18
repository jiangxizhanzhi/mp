package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.extensions.security.filter.SSOLogoutFilter;
import cn.cyberpeace.mp.auth.service.SSOServiceFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.config
 * @Author: hhuang
 * @CreateTime: 2025/03/18  16:55
 * @Description: sso过滤器配置
 * @Version: 1.0
 */

@Configuration
public class SSOFilterConfig {

    @Resource
    private SSOServiceFactory ssoServiceFactory;


    @Bean
    public FilterRegistrationBean logOutFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SSOLogoutFilter(ssoServiceFactory));
        filterRegistrationBean.setOrder(-101);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/unauth/sso/logouts");
        return filterRegistrationBean;
    }
}
