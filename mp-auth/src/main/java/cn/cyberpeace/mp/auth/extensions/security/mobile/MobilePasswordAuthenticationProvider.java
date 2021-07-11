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
        this.passwordEncoder = passwordEncoder;
    }

    public MobilePasswordAuthenticationProvider() {
        setPasswordEncoder(PasswordUtils.getPasswordEncoder());
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobilePasswordAuthenticationToken authenticationToken = (MobilePasswordAuthenticationToken) authentication;

        String mobile = (String) authenticationToken.getPrincipal();
        

        UserDetails userDetails = retrieveUser(mobile, null);
        preAuthenticationChecks.check(userDetails);
        additionalAuthenticationChecks(userDetails, (MobilePasswordAuthenticationToken) authentication);

        
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
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return MobilePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
