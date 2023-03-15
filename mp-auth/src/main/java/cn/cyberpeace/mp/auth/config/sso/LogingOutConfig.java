package cn.cyberpeace.mp.auth.config.sso;

import edu.yale.its.tp.cas.client.filter.LogoutFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author caoya
 * @description: 银联LogingOutFilter
 * @date 2022/2/28 10:23
 */
@Configuration
public class LogingOutConfig {

    @Bean(name = "ssoLogoutFilter")
    public LogoutFilter logoutFilter() {
        return new LogoutFilter();
    }

}
