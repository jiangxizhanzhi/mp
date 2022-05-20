package cn.cyberpeace.mp.front.business.config;

import cn.cyberpeace.mp.auth.service.OperatePermissionService;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.front.business.controller.filter.UrlAuthFilter;
import cn.cyberpeace.mp.front.business.service.UrlACService;
import cn.cyberpeace.mp.front.business.service.UrlACWrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.annotation.PostConstruct;
import javax.servlet.DispatcherType;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/10 21:15
 */
@Configuration
public class FrontBusinessConfiguration {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UrlACWrapperService urlACWrapperService;

    @Autowired
    private OperateUserPermissionService opPermService;

    @Value("${mp.auth.url-ac-filter:true}")
    private Boolean enableUrlAcFilter;

    @PostConstruct
    public void addBaseNames() {
        ReloadableResourceBundleMessageSource msgSrc = (ReloadableResourceBundleMessageSource) messageSource;
        msgSrc.addBasenames("classpath:i18n/front_business");
    }


    @Bean
    public FilterRegistrationBean urlAuthFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new UrlAuthFilter(urlACWrapperService, opPermService, enableUrlAcFilter));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 50);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[]{DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC});
        return filterRegistrationBean;
    }


}
