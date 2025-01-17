 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FrontBusinessConfiguration.java.
 // File size 1843.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.config;

import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.frontbusiness.filter.UrlACFilter;
import cn.cyberpeace.mp.frontbusiness.service.UrlACWrapperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

 /**
  * 前台业务配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Configuration
public class FrontBusinessConfiguration {

     /**
      * 消息来源
      */
     @Autowired
    private MessageSource messageSource;

     /**
      * urlacwrapper服务
      */
     @Autowired
    private UrlACWrapperService urlACWrapperService;

     /**
      * 人事处烫服务
      */
     @Autowired
    private OperateUserPermissionService opPermService;

     /**
      * 前面业务配置
      */
     @Autowired
    private FrontBusinessConfig frontBusinessConfig;

     /**
      * 映射器
      */
     @Autowired
    private ObjectMapper mapper;

     /**
      * Url验证过滤器注册bean
      *
      * @return {@link FilterRegistrationBean }
      */
     @Bean
    public FilterRegistrationBean urlAuthFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new UrlACFilter(urlACWrapperService, opPermService, frontBusinessConfig, mapper));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 50);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[]{DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC});
        return filterRegistrationBean;
    }


}
