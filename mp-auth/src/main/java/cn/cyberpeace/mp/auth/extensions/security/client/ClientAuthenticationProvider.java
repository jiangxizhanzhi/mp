package cn.cyberpeace.mp.auth.extensions.security.client;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.extensions.security.check.CustomPostAuthenticationChecker;
import cn.cyberpeace.mp.auth.extensions.security.check.CustomPreAuthenticationChecker;
import cn.cyberpeace.mp.auth.extensions.security.service.AuthUserDetailsService;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.hutool.core.lang.Validator;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
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

import java.util.Objects;

@Slf4j
public class ClientAuthenticationProvider implements AuthenticationProvider {

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private ClientDetailsService clientDetailsService;

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ClientAuthenticationToken authenticationToken = (ClientAuthenticationToken) authentication;

        String clientId = (String) authenticationToken.getPrincipal();

        ClientDetailsModel clientDetails = retrieveClient(clientId, null);
        if (clientDetails.getType() != ClientDetailsModel.CLIENT_TYPE_UNAUTH) {
            // 不是免认证客户端，直接返回
            return authentication;
        }
        preAuthenticationCheck(clientDetails);
        additionalAuthenticationChecks(clientDetails, (ClientAuthenticationToken) authentication);

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        ClientAuthenticationToken authenticationResult = new ClientAuthenticationToken(clientId, authentication.getCredentials(), null);

        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;

    }

    protected ClientDetailsModel retrieveClient(String clientId, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        try {
           ClientDetailsModel clientDetails = clientDetailsService.getByClientId(clientId);
//            ClientDetailsModel clientDetails = result.getSource() == null ? null : result.getSource().getObj();
            if (clientDetails == null) {
                log.error("Client not existed! clientId:{}", clientId);
                throw new IllegalArgumentException("UserDetailsService returned null, which is an interface contract violation");
            }
            return clientDetails;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    protected void preAuthenticationCheck(ClientDetailsModel clientDetails) {
    }

    protected void additionalAuthenticationChecks(ClientDetailsModel clientDetails, ClientAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.info("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String secret = authentication.getCredentials().toString();
        if (!Objects.equals(clientDetails.getClientSecret(), secret)) {
            log.info("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return ClientAuthenticationToken.class.isAssignableFrom(authentication);
    }


}
