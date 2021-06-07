package cn.cyberpeace.mp.auth.config.security;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.*;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_PATH = ControllerConstant.BASE_API_PATH + "/login";
    private static final String LOGOUT_PATH = ControllerConstant.BASE_API_PATH + "/logout";

    private AuthUserDetailsService userDetailsService;
    private HttpAuthenticationEntryPoint authenticationEntryPoint;
    private HttpLogoutSuccessHandler logoutSuccessHandler;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private CustomAuthenticationSuccessHandler authenticationSuccessHandler;
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public WebSecurityConfig(AuthUserDetailsService userDetailsService,
                             HttpAuthenticationEntryPoint authenticationEntryPoint,
                             HttpLogoutSuccessHandler logoutSuccessHandler,
                             JwtAuthenticationFilter jwtAuthenticationFilter,
                             CustomAuthenticationSuccessHandler authenticationSuccessHandler,
                             CustomAuthenticationFailureHandler authenticationFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(PasswordUtils.getPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
//                .antMatchers("/login", "/logout").permitAll()  // login logout
//                .antMatchers("/doc.html", "/webjars/**").permitAll()  // swagger
                .antMatchers("/auth/**").authenticated()
                .anyRequest().permitAll()
                .and()
                    .csrf().disable()
                    .sessionManagement().disable()
                    .cors()
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
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        var customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        customUsernamePasswordAuthenticationFilter.setFilterProcessesUrl(LOGIN_PATH);
        http.addFilterAt(customUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
