 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MobilePasswordAuthenticationProvider.java.
 // File size 4942.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.extensions.security.mobile;

import cn.cyberpeace.mp.auth.extensions.security.check.CustomPostAuthenticationChecker;
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
  * 移动密码身份验证提供商
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
public class MobilePasswordAuthenticationProvider implements AuthenticationProvider {

     /**
      *
      */
     protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

     /**
      *
      */
     private UserDetailsChecker preAuthenticationChecks = new CustomPreAuthenticationChecker();

     /**
      *
      */
     private UserDetailsChecker postAuthenticationChecks = new CustomPostAuthenticationChecker();

     /**
      * 密码编码器
      */
     private PasswordEncoder passwordEncoder;

     /**
      * 用户细节服务
      */
     private AuthUserDetailsService userDetailsService;

     /**
      * 集用户细节服务
      *
      * @param userDetailsService 用户细节服务
      */
     public void setUserDetailsService(AuthUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

     /**
      * 设置密码编码器
      *
      * @param passwordEncoder 密码编码器
      */
     public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

     /**
      * 移动密码身份验证提供商
      */
     public MobilePasswordAuthenticationProvider() {
        setPasswordEncoder(PasswordUtils.getPasswordEncoder());
    }

     /**
      * 进行身份验证
      *
      * @param authentication 身份验证
      * @return {@link Authentication }
      * @throws AuthenticationException 身份验证异常
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
      * @param mobile
      * @param authentication 身份验证
      * @return {@link UserDetails }
      * @throws AuthenticationException 身份验证异常
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
      * @param userDetails
      * @param authentication 身份验证
      * @throws AuthenticationException 身份验证异常
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
      * @param authentication 身份验证
      * @return boolean
      */
     @Override
    public boolean supports(Class<?> authentication) {
        return MobilePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
