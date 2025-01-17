 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContextRegistry.java.
 // File size 798.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:16 CST 2025.
 // @Version 1.0.0
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
        // 设置优先级最高,http请求一进入就设置context
        filterRegistrationBean.setOrder(Integer.MIN_VALUE);
        return filterRegistrationBean;
    }
}
