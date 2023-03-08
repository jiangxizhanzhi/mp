package cn.cyberpeace.mp.auth.config.sso;

import edu.yale.its.tp.cas.client.filter.LogoutFilter;
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

    @Bean
    public FilterRegistrationBean loginOutFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new LogoutFilter());
        filterRegistrationBean.setOrder(3);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/auth/logout");
        return filterRegistrationBean;
    }

}
