package cn.cyberpeace.mp.front.admin.config;

import cn.cyberpeace.mp.front.admin.service.sys.SysCtlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import javax.annotation.PostConstruct;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/10 21:15
 */
@Configuration
public class FrontAdminConfiguration {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ActivationConfig activationConfig;

    @Autowired
    private SysCtlService sysCtlService;

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    @PostConstruct
    public void addBaseNames() {
        ReloadableResourceBundleMessageSource msgSrc = (ReloadableResourceBundleMessageSource) messageSource;
        msgSrc.addBasenames("classpath:i18n/admin", "classpath:i18n/admin_base");
    }


    /*@Bean
    public FilterRegistrationBean licenseFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SysAdminFilter(sysCtlService, converter));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[]{DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC});
        return filterRegistrationBean;
    }*/

    /*
    @Bean
    public FilterRegistrationBean licenseFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new LicenseFilter(activationConfig));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[]{DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC});
        return filterRegistrationBean;
    }*/

}
