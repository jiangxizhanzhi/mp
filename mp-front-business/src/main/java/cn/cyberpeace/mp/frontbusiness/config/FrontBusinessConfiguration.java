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


@Configuration
public class FrontBusinessConfiguration {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UrlACWrapperService urlACWrapperService;

    @Autowired
    private OperateUserPermissionService opPermService;

    @Autowired
    private FrontBusinessConfig frontBusinessConfig;

    @Autowired
    private ObjectMapper mapper;

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
