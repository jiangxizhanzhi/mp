 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MobilePasswordAuthenticationProvider.java.
 // File size 5517.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.mobile;

import cn.cyberpeace.mp.auth.extensions.security.check.CustomPreAuthenticationChecker;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @ClassName MobilePasswordAuthenticationProvider
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see AuthenticationProvider
 */
@Slf4j
public class MobilePasswordAuthenticationProvider implements AuthenticationProvider {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private UserDetailsChecker preAuthenticationChecks = new CustomPreAuthenticationChecker();

    private PasswordEncoder passwordEncoder;

    private AuthUserDetailsService userDetailsService;

    /**
     * setUserDetailsService
     *
     * @param userDetailsService
     */
    public void setUserDetailsService(AuthUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * setPasswordEncoder
     *
     * @param passwordEncoder
     */
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * MobilePasswordAuthenticationProvider
     */
    public MobilePasswordAuthenticationProvider() {
        setPasswordEncoder(PasswordUtils.getPasswordEncoder());
    }


    /**
     * authenticate
     *
     * @param authentication
     * @return
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobilePasswordAuthenticationToken authenticationToken = (MobilePasswordAuthenticationToken) authentication;

        String mobile = (String) authenticationToken.getPrincipal();
        /*
        // todo 国际化手机号校验
        if (!PhoneUtil.isMobile(mobile)) {
            throw new MpRuntimeException(AuthErrorCode.MOBILE_INVALID);
        }
        */

        UserDetails userDetails = retrieveUser(mobile, null);
        preAuthenticationChecks.check(userDetails);
        additionalAuthenticationChecks(userDetails, (MobilePasswordAuthenticationToken) authentication);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        MobilePasswordAuthenticationToken authenticationResult = new MobilePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;

    }

    /**
     * retrieveUser
     *
     * @param mobile
     * @param authentication
     * @return
     */
    protected UserDetails retrieveUser(String mobile, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
            UserDetails loadedUser = userDetailsService.loadUserByMobile(mobile);
            if (loadedUser == null) {
                log.error("user not existed! mobile:{}", mobile);
                throw new IllegalArgumentException("UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    /**
     * additionalAuthenticationChecks
     *
     * @param userDetails
     * @param authentication
     */
    protected void additionalAuthenticationChecks(UserDetails userDetails, MobilePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }


    /**
     * supports
     *
     * @param authentication
     * @return boolean
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return MobilePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
