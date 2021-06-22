package cn.cyberpeace.mp.auth.extensions.security.client;

import cn.cyberpeace.mp.auth.util.PasswordUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClientAuthentication2Filter extends AbstractAuthenticationProcessingFilter {

    public static final String CLIENT_ID_HEADER = "clientId";

    public static final String CLIENT_SECRET_HEADER = "clientSecret";

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/mobile",
            "POST");

//    private String clientIdHeader = CLIENT_ID_KEY;
//
//    private String passwordParameter = CLIENT_SECRET_KEY;

    private boolean postOnly = true;

    public ClientAuthentication2Filter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public ClientAuthentication2Filter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
    }

//    public final String getClientId() {
//        return this.CLIENT_ID_HEADER;
//    }
//
//    public final String getPasswordParameter() {
//        return this.passwordParameter;
//    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.error("client auth filter...");
        String clientId = request.getHeader(CLIENT_ID_HEADER);
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER);

        if(StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
            var authRequest = new ClientAuthenticationToken(clientId, clientSecret);
            setDetails(request, authRequest);

            setContinueChainBeforeSuccessfulAuthentication(true);
            // 认证
            log.error("client auth success...");
            return getAuthenticationManager().authenticate(authRequest);
        }
        // 继续后续认证流程
        setContinueChainBeforeSuccessfulAuthentication(true);
        return new ClientAuthenticationToken(null, null);
    }


    protected void setDetails(HttpServletRequest request, ClientAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
