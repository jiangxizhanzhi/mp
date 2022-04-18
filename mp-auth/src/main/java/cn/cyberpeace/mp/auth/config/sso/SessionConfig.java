package cn.cyberpeace.mp.auth.config.sso;

import com.google.common.collect.Maps;
import com.unionpay.common.authorization.SessionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author caoya
 * @description: 银联sessionFilter
 * @date 2022/2/28 10:23
 */
@Configuration
public class SessionConfig {

    @Bean
    public FilterRegistrationBean sessionFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SessionFilter());
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/backend/user/testsso");
        Map<String, String> initParameters = Maps.newHashMap();
        initParameters.put("excludeContext", "web-console,invoker,jbossws,admin-console,jmx-console");
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }

}
