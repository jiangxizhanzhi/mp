package cn.cyberpeace.mp.auth.config;

import cn.cyberpeace.mp.auth.handler.filter.RoleFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.config
 * @Author: hhuang
 * @CreateTime: 2023/03/25  16:33
 * @Description: 角色权限处理配置
 * @Version: 1.0
 */
@Configuration
public class RoleAuthConfig {

    @Autowired
    private ObjectMapper mapper;

    @Bean
    public FilterRegistrationBean roleAuthFilterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new RoleFilter(mapper));
        filterRegistrationBean.setOrder(3);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, new DispatcherType[]{DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC});
        return filterRegistrationBean;
    }

}
