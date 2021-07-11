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


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_PATH = ControllerConstant.BASE_API_PATH + "/login";
    private static final String LOGIN_MOBILE_PATH = ControllerConstant.BASE_API_PATH + "/login/mobile";
    private static final String LOGOUT_PATH = ControllerConstant.BASE_API_PATH + "/logout";

    private AuthUserDetailsService userDetailsService;
    private HttpAuthenticationEntryPoint authenticationEntryPoint;
    private HttpLogoutSuccessHandler logoutSuccessHandler;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private CustomAuthenticationFailureHandler authenticationFailureHandler;
    private ClientAuthenticationFilter clientAuthenticationFilter;
    private LoadAuthoritiesFilter loadAuthoritiesFilter;

    @Autowired
    private AuthConfig authConfig;

    @Resource(name = AuthCacheConfig.LOGIN_FAILURE)
    private Cache<String, Integer> loginFailureCache;

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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(PasswordUtils.getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        
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


        
        var customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        customUsernamePasswordAuthenticationFilter.setFilterProcessesUrl(LOGIN_PATH);
        customUsernamePasswordAuthenticationFilter.setLoginFailureCache(loginFailureCache);
        http.addFilterAt(customUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        
        var mobilePasswordAuthenticationFilter = new MobilePasswordAuthenticationFilter();
        mobilePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        mobilePasswordAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        mobilePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        mobilePasswordAuthenticationFilter.setFilterProcessesUrl(LOGIN_MOBILE_PATH);
        var mobilePasswordAuthenticationProvider = new MobilePasswordAuthenticationProvider();
        mobilePasswordAuthenticationProvider.setUserDetailsService(userDetailsService);

        http.authenticationProvider(mobilePasswordAuthenticationProvider).addFilterAt(mobilePasswordAuthenticationFilter, CustomUsernamePasswordAuthenticationFilter.class);

        










    }
}
