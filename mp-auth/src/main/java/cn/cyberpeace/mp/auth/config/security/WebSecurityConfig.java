package cn.cyberpeace.mp.auth.config.security;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.ClientAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.CustomAuthenticationFailureHandler;
import cn.cyberpeace.mp.auth.extensions.security.CustomAuthenticationSuccessHandler;
import cn.cyberpeace.mp.auth.extensions.security.CustomUsernamePasswordAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.HttpAuthenticationEntryPoint;
import cn.cyberpeace.mp.auth.extensions.security.HttpLogoutSuccessHandler;
import cn.cyberpeace.mp.auth.extensions.security.JwtAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.mobile.MobilePasswordAuthenticationFilter;
import cn.cyberpeace.mp.auth.extensions.security.mobile.MobilePasswordAuthenticationProvider;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
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

    @Autowired
    public WebSecurityConfig(AuthUserDetailsService userDetailsService,
                             HttpAuthenticationEntryPoint authenticationEntryPoint,
                             HttpLogoutSuccessHandler logoutSuccessHandler,
                             JwtAuthenticationFilter jwtAuthenticationFilter,
                             CustomAuthenticationSuccessHandler authenticationSuccessHandler,
                             CustomAuthenticationFailureHandler authenticationFailureHandler,
                             ClientAuthenticationFilter clientAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.clientAuthenticationFilter = clientAuthenticationFilter;
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
                .antMatchers("/systemOperation/**").authenticated()
                .antMatchers("/unauth/**").permitAll()
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
                .addFilterBefore(clientAuthenticationFilter, JwtAuthenticationFilter.class);

        // usernamePassword
        var customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter();
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManagerBean());
        customUsernamePasswordAuthenticationFilter.setFilterProcessesUrl(LOGIN_PATH);
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
