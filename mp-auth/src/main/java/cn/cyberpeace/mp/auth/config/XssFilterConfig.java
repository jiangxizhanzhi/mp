package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.extensions.security.xss.XssFilter;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/12 13:48
 */
@Configuration
@EnableConfigurationProperties(XssConfiguration.class)
public class XssFilterConfig {

    @Autowired
    private XssConfiguration xssConfiguration;

    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
        registrationBean.setFilter(new XssFilter());
        registrationBean.addUrlPatterns(StringUtils.split(xssConfiguration.getUrlPatterns(), ","));
        registrationBean.setName("XssFilter");
        registrationBean.setOrder(Integer.MAX_VALUE - 1);
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", xssConfiguration.getExcludes());
        initParameters.put("enabled", xssConfiguration.getEnabled());
        initParameters.put("blacklist", JSONObject.toJSONString(xssConfiguration.getBlacklist()));
        registrationBean.setInitParameters(initParameters);
        return registrationBean;
    }
}
