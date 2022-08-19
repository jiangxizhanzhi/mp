package cn.cyberpeace.mp.situationaware.context;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CollectContextRegistry {
    @Bean
    public FilterRegistrationBean collectHttpContextFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new CollectHttpContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        // 设置优先级最高,http请求一进入就设置context
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }
}
