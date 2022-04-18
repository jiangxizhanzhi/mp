package cn.cyberpeace.mp.auth.config.sso;

import com.google.common.collect.Maps;
import edu.yale.its.tp.cas.client.filter.CASFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author caoya
 * @description: 银联单点登录配置
 * @date 2022/2/23 14:15
 */
@Configuration
public class CASConfig {

    /**
     * CAS单点登陆过滤器
     */
    @Bean
    public FilterRegistrationBean casFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new CASFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/backend/user/ssoLogin");
        Map<String, String> initParameters = Maps.newHashMap();
        initParameters.put("excludeContext", "web-console,invoker,jbossws,admin-console,jmx-console");
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }
}
