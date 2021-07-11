package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.context.http.HttpContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextRegistry {

    @Bean
    public FilterRegistrationBean httpContextFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new HttpContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }
}
