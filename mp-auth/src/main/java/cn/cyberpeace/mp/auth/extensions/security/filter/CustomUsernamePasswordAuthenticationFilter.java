package cn.cyberpeace.mp.auth.extensions.security.filter;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Slf4j
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    @SuppressWarnings("unchecked")
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!Objects.equals(request.getMethod(), "POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        if (Objects.equals(request.getContentType(), MediaType.APPLICATION_JSON_VALUE)) {
            Map<String, String> loginData = new HashMap<>();
            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                log.error("Parse login data from request error: {}", e.getMessage());
            }
            String username = loginData.getOrDefault(getUsernameParameter(), StringUtils.EMPTY);
            String decryptedPwd = loginData.getOrDefault(getPasswordParameter(), StringUtils.EMPTY);
            String originPwd = PasswordUtils.desDecrypt(decryptedPwd);
            username = username.trim();

            if (StringUtils.isNotBlank(username)) {
                var userService = BeanCommon.getBean(UserService.class);
                var userModel = userService.getByUserName(username);
                if (Objects.nonNull(userModel) && Objects.equals(userModel.getType(), UserModel.USER_TYPE_AD)) {
                    // todo LDAP认证
                    boolean ldapAuthenticateSuccess = ldapAuthenticate(username, originPwd);
                    // Ldap认证成功，使用默认密码
                    if (ldapAuthenticateSuccess) {
                        originPwd = AuthConstant.LDAP_USER_DEFAULT_PWD;
                    }
                }
            }

            var authRequest = new UsernamePasswordAuthenticationToken(username, originPwd);
            setDetails(request, authRequest);
            // 认证
            return getAuthenticationManager().authenticate(authRequest);
        } else {
            return super.attemptAuthentication(request, response);
        }
    }


    public static boolean ldapAuthenticate(String userName, String password) {
        var authConfig = BeanCommon.getBean(AuthConfig.class);
        String DN = "cn=" + userName + "," +  authConfig.getLdapBaseDn();
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.SECURITY_AUTHENTICATION, "simple"); // use simple
        props.put(Context.SECURITY_CREDENTIALS, password);
        props.put(Context.SECURITY_PRINCIPAL, DN);
        props.put(Context.PROVIDER_URL, authConfig.getLdapUrl());
        try {
            DirContext ctx = new InitialDirContext(props);
            log.info("ldapAuthenticate success! DN:{}", DN);
            return true;
        } catch (Exception ex) {
            log.error("ldapAuthenticate failed! DN: {}, url:{}\nex:{}", DN, authConfig.getLdapUrl(), ex);
            return false;
        }

    }

}
