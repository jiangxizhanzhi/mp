 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WebSecurityConfig.java.
 // File size 9061.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.config.security;

import cn.cyberpeace.mp.auth.config.AuthCacheConfig;
import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.CustomAuthenticationFailureHandler;
import cn.cyberpeace.mp.auth.extensions.security.CustomAuthenticationSuccessHandler;
import cn.cyberpeace.mp.auth.extensions.security.HttpAuthenticationEntryPoint;
import cn.cyberpeace.mp.auth.extensions.security.HttpLogoutSuccessHandler;
import cn.cyberpeace.mp.auth.extensions.security.filter.ClientAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.filter.CustomUsernamePasswordAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.filter.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.filter.LoadAuthoritiesFilter;
import cn.cyberpeace.mp.auth.extensions.security.mobile.MobilePasswordAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.mobile.MobilePasswordAuthenticationProvider;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.Resource;

 /**
  * 网络安全配置
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
     /**
      * 登录路径
      */
     private static final String LOGIN_PATH = ControllerConstant.BASE_API_PATH + "/login";
     /**
      * 登录移动路径
      */
     private static final String LOGIN_MOBILE_PATH = ControllerConstant.BASE_API_PATH + "/login/mobile";
     /**
      *
      */
     private static final String LOGOUT_PATH = ControllerConstant.BASE_API_PATH + "/logout";

     /**
      * 用户细节服务
      */
     private AuthUserDetailsService userDetailsService;
     /**
      * 身份验证入口点
      */
     private HttpAuthenticationEntryPoint authenticationEntryPoint;
     /**
      * 注销成功处理程序
      */
     private HttpLogoutSuccessHandler logoutSuccessHandler;
     /**
      *
      */
     private JwtAuthenticationFilter jwtAuthenticationFilter;
     /**
      *
      */
     private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
     /**
      *
      */
     private CustomAuthenticationFailureHandler authenticationFailureHandler;
     /**
      *
      */
     private ClientAuthenticationFilter clientAuthenticationFilter;
     /**
      *
      */
     private LoadAuthoritiesFilter loadAuthoritiesFilter;

     /**
      * 身份验证配置
      */
     @Autowired
    private AuthConfig authConfig;

     /**
      *
      */
     @Resource(name = AuthCacheConfig.LOGIN_FAILURE)
    private Cache<String, Integer> loginFailureCache;

     /**
      * 网络安全配置
      *
      * @param userDetailsService           用户细节服务
      * @param authenticationEntryPoint     身份验证入口点
      * @param logoutSuccessHandler         注销成功处理程序
      * @param jwtAuthenticationFilter
      * @param authenticationSuccessHandler
      * @param authenticationFailureHandler
      * @param clientAuthenticationFilter
      * @param loadAuthoritiesFilter
      */
     @Autowired
    public WebSecurityConfig(AuthUserDetailsService userDetailsService,
                             HttpAuthenticationEntryPoint authenticationEntryPoint,
                             HttpLogoutSuccessHandler logoutSuccessHandler,
                             JwtAuthenticationFilter jwtAuthenticationFilter,
                             CustomAuthenticationSuccessHandler authenticationSuccessHandler,
                             CustomAuthenticationFailureHandler authenticationFailureHandler,
                             ClientAuthenticationFilter clientAuthenticationFilter,
                             LoadAuthoritiesFilter loadAuthoritiesFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.clientAuthenticationFilter = clientAuthenticationFilter;
        this.loadAuthoritiesFilter = loadAuthoritiesFilter;
    }

     /**
      * 配置
      *
      * @param auth 身份验证
      * @throws Exception 异常
      */
     @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(PasswordUtils.getPasswordEncoder());
    }

     /**
      * 配置
      *
      * @param http http
      * @throws Exception 异常
      */
     @Override
    protected void configure(HttpSecurity http) throws Exception {

        // custom authenticate urls
        String[] authenticateUrls = {};
        if (CollectionUtil.isNotEmpty(authConfig.getAuthenticateUrls())) {
            authenticateUrls = authConfig.getAuthenticateUrls().toArray(new String[authConfig.getAuthenticateUrls().size()]);
        }

        String[] authenticateExcludeUrls = {};
        if (CollectionUtil.isNotEmpty(authConfig.getAuthenticateExcludeUrls())) {
            authenticateExcludeUrls = authConfig.getAuthenticateExcludeUrls().toArray(new String[authConfig.getAuthenticateExcludeUrls().size()]);
        }

        http.authorizeRequests()
                .antMatchers(authenticateExcludeUrls).permitAll()
                .antMatchers(authenticateUrls).authenticated()
                .anyRequest().permitAll()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors()
                .and()
                .formLogin().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .logout()
                .permitAll()
                .logoutUrl(LOGOUT_PATH)
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(clientAuthenticationFilter, JwtAuthenticationFilter.class)
                .addFilterAfter(loadAuthoritiesFilter, JwtAuthenticationFilter.class);
//                .addFilterBefore(licenseFilter, ClientAuthenticationFilter.class);

        // usernamePassword
        var customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        customUsernamePasswordAuthenticationFilter.setFilterProcessesUrl(LOGIN_PATH);
        customUsernamePasswordAuthenticationFilter.setLoginFailureCache(loginFailureCache);
        http.addFilterAt(customUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        // mobilePassword
        var mobilePasswordAuthenticationFilter = new MobilePasswordAuthenticationFilter();
        mobilePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        mobilePasswordAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        mobilePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        mobilePasswordAuthenticationFilter.setFilterProcessesUrl(LOGIN_MOBILE_PATH);
        var mobilePasswordAuthenticationProvider = new MobilePasswordAuthenticationProvider();
        mobilePasswordAuthenticationProvider.setUserDetailsService(userDetailsService);

        http.authenticationProvider(mobilePasswordAuthenticationProvider).addFilterAt(mobilePasswordAuthenticationFilter, CustomUsernamePasswordAuthenticationFilter.class);

        // client
//        var clientAuthenticationFilter = new ClientAuthentication2Filter();
//        clientAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
//        clientAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
//        clientAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
//        clientAuthenticationFilter.setFilterProcessesUrl("/**");
//        var clientAuthenticationProvider = new ClientAuthenticationProvider();
//        clientAuthenticationProvider.setClientDetailsService(clientDetailsService);
//
//        http.authenticationProvider(clientAuthenticationProvider).addFilterAt(clientAuthenticationFilter, JwtAuthenticationFilter.class);

    }
}
