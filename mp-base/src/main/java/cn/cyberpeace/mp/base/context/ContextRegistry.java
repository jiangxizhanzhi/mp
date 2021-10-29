package cn.cyberpeace.mp.base.context;

import cn.cyberpeace.mp.base.context.http.HttpContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/27
 */
@Configuration
public class ContextRegistry {

    @Bean
    public FilterRegistrationBean httpContextFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new HttpContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        // 设置优先级最高,http请求一进入就设置context
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }
}
