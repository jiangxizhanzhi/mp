package cn.cyberpeace.mp.auth.extensions.security.mobile;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.extensions.security.check.CustomPostAuthenticationChecker;
import cn.cyberpeace.mp.auth.extensions.security.check.CustomPreAuthenticationChecker;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.hutool.core.lang.Validator;
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
import org.springframework.util.Assert;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class MobilePasswordAuthenticationProvider implements AuthenticationProvider {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private UserDetailsChecker preAuthenticationChecks = new CustomPreAuthenticationChecker();

    private UserDetailsChecker postAuthenticationChecks = new CustomPostAuthenticationChecker();

    private PasswordEncoder passwordEncoder;

    private AuthUserDetailsService userDetailsService;

    public void setUserDetailsService(AuthUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }

    public MobilePasswordAuthenticationProvider() {
        setPasswordEncoder(PasswordUtils.getPasswordEncoder());
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobilePasswordAuthenticationToken authenticationToken = (MobilePasswordAuthenticationToken) authentication;

        String mobile = (String) authenticationToken.getPrincipal();
        Assert.isTrue(Validator.isMobile(mobile), AuthConstant.ERROR_CODE_PREFIX + "mobile_invalid");

        UserDetails userDetails = retrieveUser(mobile, null);
        preAuthenticationChecks.check(userDetails);
        additionalAuthenticationChecks(userDetails, (MobilePasswordAuthenticationToken) authentication);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        MobilePasswordAuthenticationToken authenticationResult = new MobilePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;

    }

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

    protected void additionalAuthenticationChecks(UserDetails userDetails, MobilePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.info("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.info("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return MobilePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
