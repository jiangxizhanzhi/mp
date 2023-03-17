package cn.cyberpeace.mp.auth.config.sso;

import cn.cyberpeace.mp.auth.extensions.security.SSOLogoutFilter;
import cn.cyberpeace.mp.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caoya
 * @description: 银联LogingOutFilter
 * @date 2022/2/28 10:23
 */
@Configuration
public class LogingOutConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private SSOConfig ssoConfig;

    @Bean
    public FilterRegistrationBean loginOutFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SSOLogoutFilter(userService, ssoConfig));
        filterRegistrationBean.setOrder(-101);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/auth/logout");
        return filterRegistrationBean;
    }

}
